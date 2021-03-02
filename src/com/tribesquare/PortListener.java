package com.tribesquare;

import com.tribesquare.connection.Db;
import javafx.animation.Timeline;
import javafx.application.Platform;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import org.joda.time.DateTime;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;

public class PortListener implements SerialPortEventListener {

    SerialPort port;
    byte count = 0;
    String tx_date, tx_type;
    byte tx_no;

    Timeline timeline;
    Timing timer;

    public PortListener (SerialPort port) throws SQLException, ClassNotFoundException {
        this.port = port;
//        timeline = new Timeline();
//        timer = new Timing(timeline);
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
//        System.out.println("Port listened: " + port + " " + event.isRXCHAR());

        if(event.isRXCHAR()){
//            if(event.getEventValue() == 27) {
                try {
//                    System.out.println("In try block");
                    String bufferHex = port.readHexString();
//                    System.out.println("Buffer Hex: " + bufferHex);

                    getSignalData(bufferHex, count);

                    count++;

                } catch (SerialPortException ex) {
//                    System.out.println(ex.getMessage());
                } catch (SQLException | ClassNotFoundException throwables) {
//                    System.out.println(throwables.getMessage());
                    throwables.printStackTrace();
                }
//            }

        } else if(event.isCTS()){ // CTS line has changed state
            if(event.getEventValue() == 1){ // line is ON
                System.out.println("CTS - ON");
            } else {
                System.out.println("CTS - OFF");
            }
        } else if(event.isDSR()){ // DSR line has changed state
            if(event.getEventValue() == 1){ // line is ON
                System.out.println("DSR - ON");
            } else {
                System.out.println("DSR - OFF");
            }
        } else {
            System.out.println("What signal is this?");
        }

        if (count == 2) count = 0;


    }

    private void getSignalData(String signal, byte count) throws SQLException, ClassNotFoundException {
        if (count == 0) {
            tx_no = Byte.parseByte(signal.substring(21, 26).replace(" ", ""));
            tx_date = signal.substring(30, 38);
            tx_type = getSignalType(signal.substring(63, 74));

//            System.out.println("Signal tx number: " + tx_no);
            insertData();

        }
    }

    private String getSignalType(String substring) {
        String data = null;
        switch (substring) {
            case "43 41 4C 4C" :
                data = "CALL";
                break;
            case "42 49 4C 4C" :
                data = "BILL";
                break;
            case  "44 4F 4E 45":
                data = "DONE";
                break;
        }

        return data;
    }

    public void insertData() throws SQLException, ClassNotFoundException {
        HashMap<String, Object> tx = new HashMap<>();
        String status = "active";

        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();

        tx.put("tx_no", tx_no);
        tx.put("tx_type", tx_type);
        tx.put("tx_date", date);
        tx.put("tx_time", time);
        tx.put("tx_state", status);

        var time_tx = date+"T"+time;
//        System.out.println("Calling Port listener: " + tx_no);

        Db connect = new Db("tx_display");
        ResultSet resultSet = connect.get(
                "  WHERE tx_no = " + tx_no + " ORDER BY id ASC LIMIT 1 "
        );

        resultSet.next();

        var tx_status = "active";
        if (resultSet.getRow() != 0) {
            var tx_time = resultSet.getString("tx_date")+"T"+resultSet.getString("tx_time");
            var id = resultSet.getInt("id");

            if (resultSet.getString("tx_type").equals("CALL") && !tx_type.equals("CALL")) {
                if (tx_type.equals("DONE")) {
                    var respTime = calculateResponseTime(tx_time);
                    tx.put("response_time", respTime);
                    tx.put("tx_state", "attended");
                    tx_status = "attended";

                }

                connect.update(tx, id);
                connect.insert(tx, "tx");

            } else if (resultSet.getString("tx_type").equals("BILL") && tx_type.equals("DONE") ) {
                var respTime = calculateResponseTime(tx_time);
                tx.put("response_time", respTime);
                tx.put("tx_state", "attended");
                tx_status = "attended";

                connect.update(tx, id);
                connect.insert(tx, "tx");

            } else if (resultSet.getString("tx_type").equals("DONE") && !tx_type.equals("DONE")) {
                connect.update(tx, id);
                connect.insert(tx, "tx");

            }
        } else {
            connect.insert(tx);
            connect.insert(tx, "tx");
        }

        updateLayoutUI(time_tx, tx_status);

    }

    private String calculateResponseTime(String tx_time) {
        var startTime = DateTime.parse(tx_time);
        var responseTime = DateTime.now().minusMillis(startTime.getMillisOfDay());
        var stringedTime = String.valueOf(responseTime.toLocalTime());

        return stringedTime;

    }

    public void updateLayoutUI(String time_tx, String tx_state) throws SQLException, ClassNotFoundException {
        Platform.runLater(new Runnable() {
            @Override public void run() {
                //Update UI here
//                CallerNavigator.loadVista(CallerNavigator.TX);
                var caller = new CallerNavigator();
                caller.loadTx(caller.TX2, timeline);
            }
        });




//        DateTime txTime = DateTime.parse(time_tx);
//        timer.startWarningCountdown(txTime, tx_state);

    }


}

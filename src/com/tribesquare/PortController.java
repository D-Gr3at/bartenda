package com.tribesquare;

import com.tribesquare.connection.Db;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;

public class PortController {

    protected SerialPort port;
    protected StringProperty portStatus;
    protected ObjectProperty portStatusColor;
    boolean isPortOpen;
    PortConfiguration portConfig;

    public PortController() throws SQLException, ClassNotFoundException {
        PortConfiguration p = new PortConfiguration();
        portConfig = p.getConfig();

        getPorts();

        String portValue = portConfig.port;
        port = new SerialPort(portValue);
        portStatus = new SimpleStringProperty();
        portStatusColor = new SimpleObjectProperty<>();
    }

    public StringProperty portStatusProperty() {
        return portStatus;
    }

    public void setPortStatus(String portStatus) {
        this.portStatus.set(portStatus);
    }

    protected String[] getPorts() {
        return SerialPortList.getPortNames();
    }

    public boolean connect() throws SQLException, ClassNotFoundException {
        try {
            port.openPort();
            var parity = portConfig.parity == "" ? null : Integer.parseInt(portConfig.parity);
            port.setParams(Integer.parseInt(portConfig.baud), Integer.parseInt(portConfig.data), Integer.parseInt(portConfig.stop), parity);
            int mask = SerialPort.MASK_RXCHAR + SerialPort.MASK_CTS + SerialPort.MASK_DSR;
            port.setEventsMask(mask);
            port.addEventListener(new PortListener(port) /* defined below */);

            isPortOpen = true;

        } catch (SerialPortException e) {
            if (e.getExceptionType() == "TYPE_PORT_NOT_OPENED" || e.getExceptionType() == "TYPE_PORT_NOT_OPENED") {
                isPortOpen = false;
            }
        } catch (NullPointerException e) {
            //
        }

        return isPortOpen;

    }

    public void insertData() throws SQLException, ClassNotFoundException {
        Db connect = new Db("tx");
        HashMap<String, Object> tx = new HashMap<>();
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();
        tx.put("tx_no", "1");
        tx.put("tx_type", "CALL");
        tx.put("tx_date", date);
        tx.put("tx_time", time);
        String status = "active";
        tx.put("tx_state", status);

        connect.insert(tx);
    }

    public String getPortStatus() throws SQLException, ClassNotFoundException {
        String s = "Not Connected";
        if (isPortOpen) s = "Connected";

        return s;
    }

    public Paint getPortStatusColor() throws SQLException, ClassNotFoundException {
        Paint s = Color.RED;
        if (isPortOpen) s = Color.valueOf("#36FF19");

        return s;
    }

}


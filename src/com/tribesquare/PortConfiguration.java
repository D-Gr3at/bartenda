package com.tribesquare;

import com.tribesquare.connection.Db;
import javafx.event.ActionEvent;
import javafx.scene.control.ChoiceBox;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class PortConfiguration extends ConfigurationsController {

    String port, baud, data, stop, parity, flux;
    int id;

    public PortConfiguration() throws SQLException, ClassNotFoundException {
        super();
    }

    public PortConfiguration(
            int id,
            String port,
            String baud,
            String data,
            String stop,
            String parity,
            String flux
    ) throws SQLException, ClassNotFoundException {
        super();
        this.id = id;
        this.port = port;
        this.baud = baud;
        this.data = data;
        this.stop = stop;
        this.parity = parity;
        this.flux = flux;
    }

    protected void discardEntry(ActionEvent e) {
    }

    protected void savePortEntry(
            ChoiceBox port,
            ChoiceBox baud,
            ChoiceBox data,
            ChoiceBox stop,
            ChoiceBox parity,
            ChoiceBox flux
    )
    {
        try {
            Db connect = new Db("port_config");
            HashMap<String, Object> config = new HashMap<>();

            config.put("port", port.getValue());
            config.put("baud", baud.getValue());
            config.put("data_bit", data.getValue());
            config.put("stop", stop.getValue());
            config.put("parity", parity.getValue());
            config.put("flux", flux.getValue());

            connect.update(config, 1);

            CallerNavigator.loadVista(CallerNavigator.TX);

//            var caller = new CallerNavigator();
//            caller.loadTx(caller.TX2);

        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
    }

    public PortConfiguration getConfig() throws SQLException, ClassNotFoundException {
        Db connect = new Db("port_config");
        ResultSet result = connect.get();
        result.next();
        return new PortConfiguration(
                result.getInt("id"),
                result.getString("port"),
                result.getString("baud"),
                result.getString("data_bit"),
                result.getString("stop"),
                result.getString("parity"),
                result.getString("flux")
        );
    }
}

package com.tribesquare;

import com.tribesquare.connection.Db;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class LayoutConfiguration {

    int id;
    String customer_name;
    String record_count;
    String active_threshold;
    String first_threshold;
    String second_threshold;
    String final_threshold;

    Color first_background,
            first_message,
            second_background,
            second_message,
            final_background,
            final_message,
            no_answer_background,
            no_answer_message,
            attended_background,
            attended_message,
            active_background,
            active_message,
            inactive_background,
            inactive_message;

    public LayoutConfiguration() {}

    public LayoutConfiguration(
            int id,
            String customer_name,
            String record_count,
            Color first_background,
            Color first_message,
            String first_threshold,
            Color second_background,
            Color second_message,
            String second_threshold,
            Color final_background,
            Color final_message,
            String final_threshold,
            Color no_answer_background,
            Color no_answer_message,
            Color attended_background,
            Color attended_message,
            String active_threshold,
            Color active_background,
            Color active_message,
            Color inactive_background,
            Color inactive_message
    ) {
        this.id = id;
        this.customer_name = customer_name;
        this.record_count = record_count;
        this.first_background = first_background;
        this.first_message = first_message;
        this.first_threshold = first_threshold;
        this.second_background = second_background;
        this.second_message = second_message;
        this.second_threshold = second_threshold;
        this.final_background = final_background;
        this.final_message = final_message;
        this.final_threshold = final_threshold;
        this.no_answer_background = no_answer_background;
        this.no_answer_message = no_answer_message;
        this.attended_background = attended_background;
        this.attended_message = attended_message;
        this.active_threshold = active_threshold;
        this.active_background = active_background;
        this.active_message = active_message;
        this.inactive_background = inactive_background;
        this.inactive_message = inactive_message;
    }

    public LayoutConfiguration getConfig() throws SQLException, ClassNotFoundException {
        Db connect = new Db("layout_config");
        ResultSet result = connect.get();
        result.next();

        return new LayoutConfiguration(
                result.getInt("id"),
                result.getString("customer_name"),
                result.getString("record_count"),
                parseColor(result.getString("first_background")),
                parseColor(result.getString("first_message")),
                result.getString("first_threshold"),
                parseColor(result.getString("second_background")),
                parseColor(result.getString("second_message")),
                result.getString("second_threshold"),
                parseColor(result.getString("final_background")),
                parseColor(result.getString("final_message")),
                result.getString("final_threshold"),
                parseColor(result.getString("no_answer_background")),
                parseColor(result.getString("no_answer_message")),
                parseColor(result.getString("attended_background")),
                parseColor(result.getString("attended_message")),
                result.getString("active_threshold"),
                parseColor(result.getString("active_background")),
                parseColor(result.getString("active_message")),
                parseColor(result.getString("inactive_background")),
                parseColor(result.getString("inactive_message"))
        );
    }

    public String getRecord_count() {
        return record_count;
    }

    private Color parseColor(String background) {
        return Color.web(background, 1.0);
    }

    public void saveLayoutEntry(
            TextField customer_name,
            TextField record_count,
            ColorPicker first_background,
            ColorPicker first_message,
            Spinner first_threshold,
            ColorPicker second_background,
            ColorPicker second_message,
            Spinner second_threshold,
            ColorPicker final_background,
            ColorPicker final_message,
            Spinner final_threshold,
            ColorPicker no_answer_background,
            ColorPicker no_answer_message,
            ColorPicker attended_background,
            ColorPicker attended_message,
            Spinner active_threshold,
            ColorPicker active_background,
            ColorPicker active_message,
            ColorPicker inactive_background,
            ColorPicker inactive_message
    ) {
        try {
            Db connect = new Db("layout_config");
            HashMap<String, Object> config = new HashMap<>();

            config.put("customer_name", customer_name.getText());
            config.put("record_count", record_count.getText());
            config.put("first_background", first_background.getValue());
            config.put("first_message", first_message.getValue());
            config.put("second_background", second_background.getValue());
            config.put("second_message", second_message.getValue());
            config.put("final_background", final_background.getValue());
            config.put("final_message", final_message.getValue());
            config.put("no_answer_background", no_answer_background.getValue());
            config.put("no_answer_message", no_answer_message.getValue());
            config.put("attended_background", attended_background.getValue());
            config.put("attended_message", attended_message.getValue());
            config.put("active_background", active_background.getValue());
            config.put("active_message", active_message.getValue());
            config.put("inactive_background", inactive_background.getValue());
            config.put("inactive_message", inactive_message.getValue());
            config.put("active_threshold", active_threshold.getEditor().getText());
            config.put("first_threshold", first_threshold.getEditor().getText());
            config.put("second_threshold", second_threshold.getEditor().getText());
            config.put("final_threshold", final_threshold.getEditor().getText());

            connect.update(config, 1);

//            CallerNavigator.loadVista(CallerNavigator.TX);

            var caller = new CallerNavigator();
            caller.loadTx(caller.TX2);

        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
    }
}

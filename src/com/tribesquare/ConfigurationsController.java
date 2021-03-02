package com.tribesquare;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ConfigurationsController implements Initializable {

    @FXML
    protected ChoiceBox<String> port, baud, data, stop, parity, flux, first_warning_repeat, second_warning_repeat, final_warning_repeat;

    @FXML
    protected AnchorPane portPane;

    @FXML
    protected Button savePort;
    @FXML
    protected Button saveLayout;
    @FXML
    protected Button saveSystem;
    @FXML
    protected Button choose_first_tone;
    @FXML
    protected Button choose_second_tone;
    @FXML
    protected Button choose_final_tone;
    @FXML
    protected Button play_first_tone;
    @FXML
    protected Button stop_first_tone;
    @FXML
    protected Button play_second_tone;
    @FXML
    protected Button stop_second_tone;
    @FXML
    protected Button play_final_tone;
    @FXML
    protected Button stop_final_tone;

    @FXML
    protected TextField customer_name, record_count, first_warning_tone, second_warning_tone, final_warning_tone;

    @FXML
    protected ColorPicker
            first_background,
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

    @FXML
    protected Spinner<String> active_threshold;
    @FXML
    protected Spinner<String> first_threshold;
    @FXML
    protected Spinner<String> second_threshold;
    @FXML
    protected Spinner<String> final_threshold;

    @FXML
    protected CheckBox autorun, topside, taskbar_icon, pop_up, stop_counting;

    public ConfigurationsController() throws SQLException, ClassNotFoundException {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            configurePort();
            configureLayout();
            configureSystem();
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
    }

    private void configureSystem() throws SQLException, ClassNotFoundException {
        SystemConfiguration systemConfiguration = new SystemConfiguration();
        var sConfig = systemConfiguration.getConfig();

//        boolean bool = (sConfig.autorun == 1);
//        autorun.selectedProperty().setValue(bool);
//        autorun.setOnAction(systemConfiguration::updateCheckboxConfig);
//
//        bool = (sConfig.topside == 1);
//        topside.selectedProperty().setValue(bool);
//        topside.setOnAction(systemConfiguration::updateCheckboxConfig);
//
//        bool = (sConfig.taskbar_icon == 1);
//        taskbar_icon.selectedProperty().setValue(bool);
//        taskbar_icon.setOnAction(systemConfiguration::updateCheckboxConfig);
//
//        bool = (sConfig.pop_up == 1);
//        pop_up.selectedProperty().setValue(bool);
//        pop_up.setOnAction(systemConfiguration::updateCheckboxConfig);
//
//        bool = (sConfig.stop_counting == 1);
//        stop_counting.selectedProperty().setValue(bool);
//        stop_counting.setOnAction(systemConfiguration::updateCheckboxConfig);

        String fTone = sConfig.first_warning_tone;
        if(fTone != null) first_warning_tone.setText(fTone);

        String sTone = sConfig.second_warning_tone;
        if(sTone != null) second_warning_tone.setText(sTone);

        String flTone = sConfig.final_warning_tone;
        if(flTone != null) final_warning_tone.setText(flTone);

        first_warning_repeat.setItems(FXCollections.observableArrayList("0", "1", "2", "3"));
        first_warning_repeat.getSelectionModel().select(sConfig.first_warning_repeat);
        first_warning_repeat.setOnAction(systemConfiguration::updateChoiceBoxConfig);

        second_warning_repeat.setItems(FXCollections.observableArrayList("0", "1", "2", "3"));
        second_warning_repeat.getSelectionModel().select(sConfig.second_warning_repeat);
        second_warning_repeat.setOnAction(systemConfiguration::updateChoiceBoxConfig);

        final_warning_repeat.setItems(FXCollections.observableArrayList("0", "1", "2", "3"));
        final_warning_repeat.getSelectionModel().select(sConfig.second_warning_repeat);
        final_warning_repeat.setOnAction(systemConfiguration::updateChoiceBoxConfig);

        choose_first_tone.setOnAction(e -> systemConfiguration.updateFile(first_warning_tone));
        choose_second_tone.setOnAction(e -> systemConfiguration.updateFile(second_warning_tone));
        choose_final_tone.setOnAction(e -> systemConfiguration.updateFile(final_warning_tone));

        play_first_tone.setOnAction(e -> systemConfiguration.playAudio(sConfig.first_warning_tone));
        play_second_tone.setOnAction(e -> systemConfiguration.playAudio(sConfig.second_warning_tone));
        play_final_tone.setOnAction(e -> systemConfiguration.playAudio(sConfig.final_warning_tone));

        stop_first_tone.setOnAction(e -> systemConfiguration.stopAudio());
        stop_second_tone.setOnAction(e -> systemConfiguration.stopAudio());
        stop_final_tone.setOnAction(e -> systemConfiguration.stopAudio());


        saveSystem.setOnAction(e -> systemConfiguration.saveSystemEntry(
//                autorun,
//                topside,
//                taskbar_icon,
//                pop_up,
//                stop_counting,
                first_warning_tone,
                second_warning_tone,
                final_warning_tone,
                first_warning_repeat,
                second_warning_repeat,
                final_warning_repeat
        ));
    }

    private void configureLayout() throws SQLException, ClassNotFoundException {
        LayoutConfiguration layout = new LayoutConfiguration();
        var lConfig = layout.getConfig();

        customer_name.setText(lConfig.customer_name);
        record_count.setText(lConfig.record_count);

        first_background.setValue(lConfig.first_background);
        first_message.setValue(lConfig.first_message);
        second_background.setValue(lConfig.second_background);
        second_message.setValue(lConfig.second_message);
        final_background.setValue(lConfig.final_background);
        final_message.setValue(lConfig.final_message);
        no_answer_background.setValue(lConfig.no_answer_background);
        no_answer_message.setValue(lConfig.no_answer_message);
        attended_background.setValue(lConfig.attended_background);
        attended_message.setValue(lConfig.attended_message);
        active_background.setValue(lConfig.active_background);
        active_message.setValue(lConfig.active_message);
        inactive_background.setValue(lConfig.inactive_background);
        inactive_message.setValue(lConfig.inactive_message);

        ObservableList activeList = FXCollections.observableArrayList();
        activeList.addAll("00:30", "00:45", "01:00", "01:30", "01:45", "02:00", "03:00", "04:00", "05:00", "10:00");
        SpinnerValueFactory<String> activeThresholdFactory = //
                new SpinnerValueFactory.ListSpinnerValueFactory<String>(activeList);
        active_threshold.setValueFactory(activeThresholdFactory);
        active_threshold.getEditor().setText(lConfig.active_threshold);

        ObservableList firstList = FXCollections.observableArrayList();
        firstList.addAll("00:30", "00:45", "01:00", "01:30", "01:45", "02:00", "03:00", "04:00", "05:00", "10:00");
        SpinnerValueFactory<String> firstThresholdFactory = //
                new SpinnerValueFactory.ListSpinnerValueFactory<String>(firstList);
        first_threshold.setValueFactory(firstThresholdFactory);
        first_threshold.getEditor().setText(lConfig.first_threshold);

        ObservableList secondList = FXCollections.observableArrayList();
        secondList.addAll("00:30", "00:45", "01:00", "01:30", "01:45", "02:00", "03:00", "04:00", "05:00", "10:00");
        SpinnerValueFactory<String> secondThresholdFactory = //
                new SpinnerValueFactory.ListSpinnerValueFactory<String>(secondList);
        second_threshold.setValueFactory(secondThresholdFactory);
        second_threshold.getEditor().setText(lConfig.second_threshold);

        ObservableList finalList = FXCollections.observableArrayList();
        finalList.addAll("00:30", "00:45", "01:00", "01:30", "01:45", "02:00", "03:00", "04:00", "05:00", "10:00");
        SpinnerValueFactory<String> finalThresholdFactory = //
                new SpinnerValueFactory.ListSpinnerValueFactory<String>(finalList);
        final_threshold.setValueFactory(finalThresholdFactory);
        final_threshold.getEditor().setText(lConfig.final_threshold);

        saveLayout.setOnAction(e -> layout.saveLayoutEntry(
                customer_name,
                record_count,
                first_background,
                first_message,
                first_threshold,
                second_background,
                second_message,
                second_threshold,
                final_background,
                final_message,
                final_threshold,
                no_answer_background,
                no_answer_message,
                attended_background,
                attended_message,
                active_threshold,
                active_background,
                active_message,
                inactive_background,
                inactive_message
        ));
    }

    public void configurePort() throws SQLException, ClassNotFoundException {

        PortConfiguration p = new PortConfiguration();
        var portConfig = p.getConfig();

        PortController set = new PortController();
        String[] ports = set.getPorts();
        port.setItems(FXCollections.observableArrayList(ports));
        port.getSelectionModel().select(portConfig.port);

        baud.setItems(FXCollections.observableArrayList("1200", "2400", "4800", "9600", "19200", "38400", "57600", "115200"));
        baud.getSelectionModel().select(portConfig.baud);

        data.setItems(FXCollections.observableArrayList("5", "6", "7", "8", "9"));
        data.getSelectionModel().select(portConfig.data);

        stop.setItems(FXCollections.observableArrayList("1", "2"));
        stop.getSelectionModel().select(portConfig.stop);

        parity.setItems(FXCollections.observableArrayList("", "0", "1"));
        parity.getSelectionModel().select(portConfig.parity);

        flux.setItems(FXCollections.observableArrayList("1"));
        flux.getSelectionModel().select(portConfig.flux);


        savePort.setOnAction(e -> p.savePortEntry(port, baud, data, stop, parity, flux));


    }
}

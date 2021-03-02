package com.tribesquare;

import com.tribesquare.connection.Db;
import javafx.event.ActionEvent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class SystemConfiguration extends ConfigurationsController {

    int id, autorun, topside, taskbar_icon, pop_up, stop_counting;
    String first_warning_repeat, second_warning_repeat, final_warning_repeat, first_warning_tone, second_warning_tone, final_warning_tone;
    boolean playStatus = false;

    AudioPlayer audioPlayer;

    public SystemConfiguration() throws SQLException, ClassNotFoundException {
        super();
    }

    public SystemConfiguration(
            int id,
            int autorun,
            int topside,
            int taskbar_icon,
            int pop_up,
            int stop_counting,
            String first_warning_repeat,
            String second_warning_repeat,
            String final_warning_repeat,
            String first_warning_tone,
            String second_warning_tone,
            String final_warning_tone
    ) throws SQLException, ClassNotFoundException {
        super();
        this.autorun = autorun;
        this.topside = topside;
        this.taskbar_icon = taskbar_icon;
        this.pop_up = pop_up;
        this.stop_counting = stop_counting;
        this.first_warning_repeat = first_warning_repeat;
        this.first_warning_tone = first_warning_tone;
        this.second_warning_repeat = second_warning_repeat;
        this.second_warning_tone = second_warning_tone;
        this.final_warning_repeat = final_warning_repeat;
        this.final_warning_tone = final_warning_tone;
    }

    public SystemConfiguration getConfig() throws SQLException, ClassNotFoundException {
        Db connect = new Db("system_config");
        ResultSet result = connect.get();
        result.next();

        return new SystemConfiguration(
                result.getInt("id"),
                result.getInt("autorun"),
                result.getInt("topside"),
                result.getInt("taskbar_icon"),
                result.getInt("pop_up"),
                result.getInt("stop_counting"),
                result.getString("first_warning_repeat"),
                result.getString("second_warning_repeat"),
                result.getString("final_warning_repeat"),
                result.getString("first_warning_tone"),
                result.getString("second_warning_tone"),
                result.getString("final_warning_tone")
        );
    }

    public void saveSystemEntry(
//            CheckBox autorun,
//            CheckBox topside,
//            CheckBox taskbar_icon,
//            CheckBox pop_up,
//            CheckBox stop_counting,
            TextField first_warning_tone,
            TextField second_warning_tone,
            TextField final_warning_tone,
            ChoiceBox<String> first_warning_repeat,
            ChoiceBox<String> second_warning_repeat,
            ChoiceBox<String> final_warning_repeat
    ) {
        try {
            Db connect = new Db("system_config");
            HashMap<String, Object> config = new HashMap<>();

//            config.put("autorun", autorun.getText());
//            config.put("topside", topside.getText());
//            config.put("taskbar_icon", taskbar_icon.getText());
//            config.put("pop_up", pop_up.getText());
//            config.put("stop_counting", stop_counting.getText());
            config.put("first_warning_tone", first_warning_tone.getText());
            config.put("second_warning_tone", second_warning_tone.getText());
            config.put("final_warning_tone", final_warning_tone.getText());
            config.put("first_warning_repeat", first_warning_repeat.getValue());
            config.put("second_warning_repeat", second_warning_repeat.getValue());
            config.put("final_warning_repeat", final_warning_repeat.getValue());

            connect.update(config, 1);

//            CallerNavigator.loadVista(CallerNavigator.TX);
            var caller = new CallerNavigator();
            caller.loadTx(caller.TX2);

        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
    }

    public void updateCheckboxConfig(ActionEvent event) {
        var item = (CheckBox) event.getSource();
        var name = item.getId();
        var value = item.selectedProperty().getValue() == true ? '1' : '0';

        updateConfig(name, value);
    }

    public void updateChoiceBoxConfig(ActionEvent event) {
        var item = (ChoiceBox) event.getSource();
        var name = item.getId();
        var value = item.getValue();

        updateConfig(name, value);
    }

    public void updateFile(TextField warning_tone) {
        Stage stage = new Stage();
        final FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser);
        File fileName = fileChooser.showOpenDialog(stage);
        if (fileName != null) {
            var name = warning_tone.getId();
            updateConfig(name, fileName.getAbsoluteFile());
            warning_tone.setText(fileName.getAbsolutePath());
            CallerNavigator.loadVista(CallerNavigator.CONFIGS, "2");
        }
    }

    private static void configureFileChooser(final FileChooser fileChooser) {
        fileChooser.setTitle("View Tones");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Audio Files", "*.wav")
        );
    }

    private void updateConfig(String name, Object value) {
        try {
            Db connect = new Db("system_config");
            HashMap<String, Object> config = new HashMap<>();

            config.put(name, value);
            connect.update(config, 1);

        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
    }

    public void playAudio(String warning_tone) {
        try {
            if (!playStatus) {
                audioPlayer = new AudioPlayer(warning_tone);
                audioPlayer.play();
                playStatus = true;
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void stopAudio() {
        try {
            if (playStatus) {
                audioPlayer.stop();
                playStatus = false;
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
        playStatus = false;
    }

}

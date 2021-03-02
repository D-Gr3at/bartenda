package com.tribesquare;

import com.tribesquare.connection.Db;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class Permissions extends UsersController {

    int userIDs;
    CheckBox config_settings, layout_settings, staff_settings, analytics_settings;
    Boolean bool;

    Permissions(int userIDs) {
        this.userIDs = userIDs;
    }

    public void display() throws SQLException, ClassNotFoundException {
        Db connect = new Db("permissions");
        ResultSet result = connect.get("WHERE user_id = " + userIDs);
        result.next();

        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Permissions Management");

        config_settings = new CheckBox("Configuration Settings");
        bool = (result.getString("config_settings").equals("true"));
        config_settings.selectedProperty().setValue(bool);

        layout_settings = new CheckBox("Layout Settings");
        bool = (result.getString("layout_settings").equals("true"));
        layout_settings.selectedProperty().setValue(bool);

        staff_settings = new CheckBox("Staff Settings");
        bool = (result.getString("staff_settings").equals("true"));
        staff_settings.selectedProperty().setValue(bool);

        analytics_settings = new CheckBox("Analytics Settings");
        bool = (result.getString("analytics_settings").equals("true"));
        analytics_settings.selectedProperty().setValue(bool);

        VBox vBox1 = new VBox();
        vBox1.getChildren().addAll(config_settings, layout_settings, staff_settings, analytics_settings);
        vBox1.setPrefSize(350, 450);
        vBox1.setSpacing(25);

        Button save = new Button("Save");
        save.setId("saveUserEntry");
        save.setOnAction(this::setPermissions);
        save.setPrefSize(150, 40);

        Button cancel = new Button("Cancel");
        cancel.setId("discardEntry");
        cancel.setOnAction(this::discardEntry);
        cancel.setPrefSize(150, 40);

        VBox vBox2 = new VBox();
        vBox2.getChildren().addAll(save, cancel);
        vBox2.setSpacing(25);

        HBox hLayout = new HBox();
        hLayout.setSpacing(25);
        hLayout.setPadding(new Insets(40, 40, 40, 40));
        hLayout.getChildren().addAll(vBox1, vBox2);


        Scene scene = new Scene(hLayout, 550, 450);
        window.setScene(scene);
        window.show();

    }

    private void discardEntry(ActionEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();

    }

    private void setPermissions(ActionEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();

        try {
            Db connect = new Db("permissions");

            HashMap<String, Object> permissions = new HashMap<>();
//
            permissions.put("config_settings", config_settings.selectedProperty().getValue());
            permissions.put("layout_settings", layout_settings.selectedProperty().getValue());
            permissions.put("staff_settings", staff_settings.selectedProperty().getValue());
            permissions.put("analytics_settings", analytics_settings.selectedProperty().getValue());
            permissions.put("user_id", userIDs);

            int status = connect.update(permissions, "user_id", userIDs);

            System.out.println("Response from insert statement: " + status);

            if (status == 1) {
                stage.close();
                CallerNavigator.loadVista(CallerNavigator.USERS);
            }

        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
    }
}

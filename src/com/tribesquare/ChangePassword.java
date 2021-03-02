package com.tribesquare;

import com.tribesquare.connection.Db;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class ChangePassword extends UsersController {

    TextField newpass, oldpass;
    HashMap<String, Object> password;
    int userIDs;

    ChangePassword(int userIDs) {
        this.userIDs = userIDs;
    }

    public void display() throws SQLException, ClassNotFoundException {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Change Password");

        Label newLabel = new Label("New Password*");
        newpass = new TextField();
        newpass.setPrefSize(300, 40);

        Label oldLabel = new Label("Confirm Password*");
        oldpass = new TextField();
        oldpass.setPrefSize(300, 40);

        Button save = new Button("Save");
        save.setId("saveUserEntry");
        save.setOnAction(this::saveUserEntry);
        save.setPrefSize(150, 40);


        Button cancel = new Button("Cancel");
        cancel.setId("discardEntry");
        cancel.setOnAction(this::discardEntry);
        cancel.setPrefSize(150, 40);


        HBox hLayout = new HBox();
        hLayout.setSpacing(15);
        hLayout.setPadding(new Insets(30, 0, 0, 0));
        hLayout.getChildren().addAll(save, cancel);

        VBox vLayout = new VBox();
        vLayout.getChildren().addAll(newLabel, newpass, oldLabel, oldpass, hLayout);
        vLayout.setAlignment(Pos.CENTER_LEFT);
        vLayout.setSpacing(10);
        vLayout.setPadding(new Insets(25));

        Scene scene = new Scene(vLayout, 400, 300);
        window.setScene(scene);
        window.show();

    }

    private void discardEntry(ActionEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();

    }

    private void saveUserEntry(ActionEvent event) {
        Db connect = null;

        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();

        System.out.println("Changing Password: " + userIDs);

        try {
            connect = new Db("users");
            password = new HashMap<>();

            String oldPass = oldpass.getText();
            String newPass = newpass.getText();
            if (oldPass.isEmpty() || newPass.isEmpty()) {
                AlertBox.display("Error", "Password cannot be blank.");
                return;
            }
            if (oldPass.equals(newPass)) {
                password.put("password", newPass);
                connect.update(password, userIDs);
                stage.close();
            }else {
                AlertBox.display("Error", "Passwords do not match.");
                return;
            }

        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        } finally {
//            connect.close(null);
        }
    }
}

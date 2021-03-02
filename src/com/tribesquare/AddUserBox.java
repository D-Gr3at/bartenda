package com.tribesquare;

import com.tribesquare.connection.Db;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class AddUserBox extends UsersController {

    TextField name, phone, staffNumber;
    ChoiceBox<String> role;
    ChoiceBox<String> assigned;
    HashMap<String, Object> user, permissions, assignments;

    public void display() throws SQLException, ClassNotFoundException {
        Db connect = new Db("layouts");
        ResultSet resultSet = connect.get();
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Create New User");

        Label staffLabel = new Label("Staff Number*");
        staffNumber = new TextField();
        staffNumber.setPrefSize(300, 40);

        Label nameLabel = new Label("Full Name*");
        name = new TextField();
        name.setPrefSize(300, 40);

        ObservableList<String> assignedList = FXCollections.observableArrayList();
        while(resultSet.next()) {
            assignedList.add(resultSet.getString("table_name"));
        }

        Label assignedLabel = new Label("Table assigned to");
        assigned = new ChoiceBox<>();
        assigned.setItems(assignedList);
        assigned.setPrefSize(300, 40);
        assignedLabel.setPadding(new Insets(15, 0, 0, 0));

        Label phoneLabel = new Label("Phone Number*");
        phoneLabel.setPadding(new Insets(15, 0, 0, 0));
        phone = new TextField();
        phone.setPrefSize(300, 40);

        Label roleLabel = new Label("Role*");
        role = new ChoiceBox<>(FXCollections.observableArrayList("Staff", "Manager"));
        role.getSelectionModel().selectFirst();
        role.setPrefSize(300, 40);
        roleLabel.setPadding(new Insets(15, 0, 0, 0));


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
        vLayout.getChildren().addAll(staffLabel, staffNumber, nameLabel, name, phoneLabel, phone, roleLabel, role, assignedLabel, assigned, hLayout);
        vLayout.setAlignment(Pos.CENTER_LEFT);
        vLayout.setSpacing(10);
        vLayout.setPadding(new Insets(25));

        Scene scene = new Scene(vLayout, 500, 600);
        window.setScene(scene);
        window.show();

    }

    public void updateBox(UserData selection) {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Update User");

        Label staffLabel = new Label("Staff Number*");
        staffNumber = new TextField(selection.getStaffNumber());
        staffNumber.setPrefSize(300, 40);

        Label nameLabel = new Label("Full Name*");
        name = new TextField(selection.getName());
        name.setPrefSize(300, 40);

        Label phoneLabel = new Label("Phone Number*");
        phoneLabel.setPadding(new Insets(15, 0, 0, 0));
        phone = new TextField(selection.getPhone());
        phone.setPrefSize(300, 40);

        Label roleLabel = new Label("Role*");
        role = new ChoiceBox<>(FXCollections.observableArrayList("Staff", "Manager"));
        role.getSelectionModel().select(selection.getRole());
        role.setPrefSize(300, 40);
        roleLabel.setPadding(new Insets(15, 0, 0, 0));


        Button save = new Button("Save");
        save.setId("saveUserEntry");
        save.setOnAction(e -> updateEntry(e, selection.getId()));
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
        vLayout.getChildren().addAll(staffLabel, staffNumber, nameLabel, name, phoneLabel, phone, roleLabel, role, hLayout);
        vLayout.setAlignment(Pos.CENTER_LEFT);
        vLayout.setSpacing(10);
        vLayout.setPadding(new Insets(25));

        Scene scene = new Scene(vLayout, 500, 600);
        window.setScene(scene);
        window.show();

    }

    private void discardEntry(ActionEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();

    }

    private void saveUserEntry(ActionEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();

        try {
            Db connect = new Db("users");
            user = new HashMap<>();

            String roleValue = role.getValue();
            String nameValue = name.getText();
            user.put("name", nameValue);
            user.put("role", roleValue);
            user.put("phone", phone.getText());
            user.put("staff_no", staffNumber.getText());
            user.put("password", "000000");

            if (staffNumber.getText().isEmpty()){
                AlertBox.display("Error", "Staff number cannot be blank.");
                return;
            }else if (nameValue.isEmpty()){
                AlertBox.display("Error", "Full name cannot be blank.");
                return;
            }else if (roleValue.isEmpty()){
                AlertBox.display("Error", "Role cannot be blank.");
                return;
            }else if (phone.getText().isEmpty()){
                AlertBox.display("Error", "Phone number cannot be blank.");
                return;
            }

            String assignedValue = assigned.getValue();

            try {
                connect.autoCommit();
                System.out.println(user);

                ResultSet status = connect.insertReturnKey(user);
                int userIDs = status.getInt(1);
                System.out.println(userIDs);
                System.out.println("Response from insert statement: " + status.getInt(1));

                if (userIDs > 0) {

                    //Permissions
                    permissions = new HashMap<>();

                    Boolean permValue = !roleValue.equals("Staff");
                    permissions.put("config_settings", permValue);
                    permissions.put("layout_settings", permValue);
                    permissions.put("staff_settings", permValue);
                    permissions.put("analytics_settings", permValue);
                    permissions.put("user_id", userIDs);

//                    System.out.println("permissions ====> "+permissions);

                    connect = new Db("permissions");
                    int permCount = connect.insert(permissions);
                    System.out.println("PermCount: " + permCount);

                    System.out.println("Assigned Value: " + assignedValue);
                    if (assignedValue != null) {
                        //Assignment
                        assignments = new HashMap<>();

                        assignments.put("user_id", nameValue);
                        assignments.put("table_id", assignedValue);

                        connect = new Db("assignments");
                        int assignCount = connect.insert(assignments);
                        System.out.println("Assign Count: " + assignCount);
                    }
                }

                connect.commit();

                stage.close();
                CallerNavigator.loadVista(CallerNavigator.USERS);

            } catch(SQLException se){
                se.printStackTrace();
                connect.rollback();
            }


        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
    }

    private void updateEntry(ActionEvent event, int userID) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();

        try {
            Db connect = new Db("users");
            user = new HashMap<>();

            String roleValue = role.getValue();
            String nameValue = name.getText();
            user.put("name", nameValue);
            user.put("role", roleValue);
            user.put("phone", phone.getText());
            user.put("staff_no", staffNumber.getText());

            if (staffNumber.getText().isEmpty()){
                AlertBox.display("Error", "Staff number cannot be blank.");
                return;
            }else if (nameValue.isEmpty()){
                AlertBox.display("Error", "Full name cannot be blank.");
                return;
            }else if (roleValue.isEmpty()){
                AlertBox.display("Error", "Role cannot be blank.");
                return;
            }else if (phone.getText().isEmpty()){
                AlertBox.display("Error", "Phone number cannot be blank.");
                return;
            }

            try {
                connect.update(user, userID);
                stage.close();
                CallerNavigator.loadVista(CallerNavigator.USERS);

            } catch(SQLException se){
                connect.rollback();
            }

        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
    }
}

package com.tribesquare;

import com.tribesquare.connection.Db;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;

public class LayoutController {

    TextField tableName, txNo, floorName;
    ChoiceBox<String> floor;
    ChoiceBox<String> assignedTo;
    HashMap<String, Object> assignments;

    @FXML
    public TableView<LayoutData> tableView;

    @FXML
    TableColumn<Object, Object> tableColumn, floorColumn, assignedColumn, txColumn;

    public void initialize() throws SQLException, ClassNotFoundException {
        ObservableList layoutData = getLayout();

        tableColumn.setCellValueFactory(new PropertyValueFactory<>("tableName"));
        floorColumn.setCellValueFactory(new PropertyValueFactory<>("floorName"));
        assignedColumn.setCellValueFactory(new PropertyValueFactory<>("userID"));
        txColumn.setCellValueFactory(new PropertyValueFactory<>("txNo"));

        tableView.getItems().setAll(layoutData);

    }

    public ObservableList getLayout() throws SQLException {
        Db connect = null;
        ResultSet result = null;
        ObservableList layouts = FXCollections.observableArrayList();
        try {
            connect = new Db("layouts");
            result = connect.get(
                    "LEFT JOIN assignments ON layouts.table_name = assignments.table_id "
            );
            while (result.next()) {
                layouts.add(
                        new LayoutData(
                                result.getInt("id"),
                                result.getString("table_name"),
                                result.getString("tx_no"),
                                result.getString("floor"),
                                result.getString("user_id")
                        )
                );
            }
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        } finally {
//            connect.close(result);
        }

        return layouts;
    }

    public void createNewFloor() {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Create New Floor");

        Label floorLabel = new Label("Floor Name");
        floorName = new TextField();
        floorName.setPrefSize(300, 40);

        Button save = new Button("Create");
        save.setId("saveFloorEntry");
        save.setOnAction(this::createFloor);
        save.setPrefSize(150, 40);

        Button cancel = new Button("Cancel");
        cancel.setId("discardEntry");
        cancel.setOnAction(e-> discardEntry(window));
        cancel.setPrefSize(150, 40);

        VBox vLayout = new VBox();
        HBox hLayout = new HBox();

        hLayout.getChildren().addAll(save, cancel);
        hLayout.setSpacing(15);
        hLayout.setPadding(new Insets(30, 0, 0, 0));

        vLayout.getChildren().addAll(floorLabel, floorName, hLayout);
        vLayout.setAlignment(Pos.CENTER_LEFT);
        vLayout.setSpacing(10);
        vLayout.setPadding(new Insets(25));

        Scene scene = new Scene(vLayout, 350, 200);
        window.setScene(scene);
        window.show();
    }

    public void createNewLayout() {
        Db connect = null;
        ResultSet resultSet = null;
        try {
            Stage window = new Stage();

            window.initModality(Modality.APPLICATION_MODAL);
            window.setTitle("Create New Table");

            Label nameLabel = new Label("Table Name");
            tableName = new TextField();
            tableName.setPrefSize(300, 40);

            connect = new Db("floors");
            resultSet = connect.get();


            Label txLabel = new Label("Tx number");
            txNo = new TextField();
            txNo.setPrefSize(300, 40);

            ObservableList<String> floorList = FXCollections.observableArrayList();
            while(resultSet.next()) {
                floorList.add(resultSet.getString("floor_name"));
            }

            Label floorLabel = new Label("Floor plan");
            floor = new ChoiceBox<>();
            floor.setItems(floorList);
            floor.getSelectionModel().selectFirst();
            floor.setPrefSize(300, 40);

            connect = new Db("users");
            resultSet = connect.get();

            ObservableList<String> layoutList = FXCollections.observableArrayList();
            while(resultSet.next()) {
                layoutList.add(resultSet.getString("name"));
            }

            Label assignedLabel = new Label("User assigned to");
            assignedTo = new ChoiceBox<>();
            assignedTo.setItems(layoutList);
            assignedTo.getSelectionModel().selectFirst();
            assignedTo.setPrefSize(300, 40);

            Button save = new Button("Create");
            save.setId("saveLayoutEntry");
            save.setOnAction(this::createLayout);
            save.setPrefSize(150, 40);

            Button cancel = new Button("Cancel");
            cancel.setId("discardEntry");
            cancel.setOnAction(e-> discardEntry(window));
            cancel.setPrefSize(150, 40);

            VBox vLayout = new VBox();
            HBox hLayout = new HBox();

            hLayout.getChildren().addAll(save, cancel);
            hLayout.setSpacing(15);
            hLayout.setPadding(new Insets(30, 0, 0, 0));

            vLayout.getChildren().addAll(nameLabel, tableName, txLabel, txNo, floorLabel, floor, assignedLabel, assignedTo, hLayout);
            vLayout.setAlignment(Pos.CENTER_LEFT);
            vLayout.setSpacing(10);
            vLayout.setPadding(new Insets(25));

            Scene scene = new Scene(vLayout, 550, 450);
            window.setScene(scene);
            window.show();
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        } finally {
//            connect.close(resultSet);
        }
    }

    private void discardEntry(Stage stage) {
        stage.close();
    }

    private void createLayout(ActionEvent event) {
        Db connect = null;

        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();

        try {
            connect = new Db("layouts");
            HashMap<String, Object> layouts = new HashMap<>();

            layouts.put("table_name", tableName.getText());
            layouts.put("tx_no", txNo.getText());
            layouts.put("floor", floor.getValue());
            if (tableName.getText().isEmpty()){
                AlertBox.display("Error", "Table name cannot be blank");
                return;
            }else if (txNo.getText().isEmpty()){
                AlertBox.display("Error", "TX number cannot be blank");
                return;
            }
            ResultSet result = connect.get("WHERE tx_no = "+txNo.getText());
            result.next();
            if (result.getRow() > 0){
                AlertBox.display("Error", "TX number has already been assigned to another table.");
                return;
            }else {
                connect.insert(layouts);
            }


            if (assignedTo != null) {
                //Assignment
                assignments = new HashMap<>();
                assignments.put("user_id", assignedTo.getValue());
                assignments.put("table_id", tableName.getText());

                connect = new Db("assignments");
                connect.insert(assignments);
            }

            stage.close();
            CallerNavigator.loadVista(CallerNavigator.LAYOUTS);

        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        } finally {
//            connect.close(null);
        }

    }

    private void createFloor(ActionEvent event) {
        Db connect = null;

        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();

        try {
            connect = new Db("floors");
            HashMap<String, Object> floors = new HashMap<>();

            floors.put("floor_name", floorName.getText());
            if (floorName.getText().isEmpty()){
                AlertBox.display("Error", "Floor name cannot be blank.");
                return;
            }
            int status = connect.insert(floors);
            if (status == 1) {
                stage.close();
                CallerNavigator.loadVista(CallerNavigator.TX);
            }

        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        } finally {
//            connect.close(null);
        }
    }

    public void updateLayout() {
        var selectionModel = tableView.getSelectionModel().getSelectedItem();
        try {
            if (selectionModel == null) throw new EmptyStackException();
            updateBox(selectionModel);

        } catch (EmptyStackException | ClassNotFoundException e) {
            System.out.println("Error says: " + e);
        }
    }

    private void updateBox(LayoutData selection) throws ClassNotFoundException {
        Db connect = null;
        ResultSet resultSet = null;

        try {
            Stage window = new Stage();

            window.initModality(Modality.APPLICATION_MODAL);
            window.setTitle("Create New Table");

            Label nameLabel = new Label("Table Name");
            tableName = new TextField(selection.getTableName());
            tableName.setPrefSize(300, 40);

            connect = new Db("floors");
            resultSet = connect.get();


            Label txLabel = new Label("Tx number");
            txNo = new TextField(selection.getTxNo());
            txNo.setPrefSize(300, 40);


            ObservableList<String> floorList = FXCollections.observableArrayList();
            while(resultSet.next()) {
                floorList.add(resultSet.getString("floor_name"));
            }

            Label floorLabel = new Label("Floor plan");
            floor = new ChoiceBox<>();
            floor.setItems(floorList);
            floor.getSelectionModel().select(selection.getFloorName());
            floor.setPrefSize(300, 40);

            connect = new Db("users");
            resultSet = connect.get();

            ObservableList<String> layoutList = FXCollections.observableArrayList();
            while(resultSet.next()) {
                layoutList.add(resultSet.getString("name"));
            }

            Label assignedLabel = new Label("User assigned to");
            assignedTo = new ChoiceBox<>();
            assignedTo.setItems(layoutList);
            assignedTo.getSelectionModel().select(selection.getUserID());
            assignedTo.setPrefSize(300, 40);

            Button save = new Button("Update");
            save.setId("saveLayoutEntry");
            save.setOnAction(e-> {
                try {
                    update(e, selection.getId());
                } catch (ClassNotFoundException throwables) {
                    throwables.printStackTrace();
                }
            });
            save.setPrefSize(150, 40);

            Button cancel = new Button("Cancel");
            cancel.setId("discardEntry");
            cancel.setOnAction(e-> discardEntry(window));
            cancel.setPrefSize(150, 40);

            VBox vLayout = new VBox();
            HBox hLayout = new HBox();

            hLayout.getChildren().addAll(save, cancel);
            hLayout.setSpacing(15);
            hLayout.setPadding(new Insets(30, 0, 0, 0));

            vLayout.getChildren().addAll(nameLabel, tableName, txLabel, txNo, floorLabel, floor, assignedLabel, assignedTo, hLayout);
            vLayout.setAlignment(Pos.CENTER_LEFT);
            vLayout.setSpacing(10);
            vLayout.setPadding(new Insets(25));

            Scene scene = new Scene(vLayout, 550, 450);
            window.setScene(scene);
            window.show();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
//            connect.close(resultSet);
        }
    }

    private void update(ActionEvent actionEvent, int id) throws ClassNotFoundException {
        Db connect = null;
        ResultSet checkResult = null;

        Node node = (Node) actionEvent.getSource();
        Stage stage = (Stage) node.getScene().getWindow();

        try {
            connect = new Db("layouts");
            HashMap<String, Object> layouts = new HashMap<>();

            layouts.put("table_name", tableName.getText());
            layouts.put("tx_no", txNo.getText());
            layouts.put("floor", floor.getValue());

            connect.update(layouts, id);

            if (assignedTo != null) {
                //Assignment
                assignments = new HashMap<>();

                var table_id = tableName.getText();
                assignments.put("user_id", assignedTo.getValue());
                assignments.put("table_id", table_id);

                connect = new Db("assignments");
                checkResult = connect.get("WHERE table_id = '" + table_id + "'");

                if (!!checkResult.next()) connect.update(assignments, "table_id", table_id);
                else connect.insert(assignments);

                stage.close();
                CallerNavigator.loadVista(CallerNavigator.LAYOUTS);
            }
        } catch (SQLException se){
            se.printStackTrace();
        } finally {
//            connect.close(checkResult);
        }
    }

    public void deleteLayout() {
        ResultSet checkResult = null;
        Db connect = null;

        try {
            connect = new Db("layouts");


            ArrayList<Integer> layoutID = new ArrayList<>();
            var selectionModel = tableView.getSelectionModel().getSelectedItem();
            if (selectionModel == null) throw new EmptyStackException();
            layoutID.add(selectionModel.getId());

            var delete = connect.delete(layoutID);

            if (delete > 0) {
                connect = new Db("assignments");

                var table_id = selectionModel.getTableName();
                checkResult = connect.get("WHERE table_id = '" + table_id + "'");
                checkResult.next();

                layoutID.add(checkResult.getInt("id"));
                connect.delete(layoutID);

                CallerNavigator.loadVista(CallerNavigator.LAYOUTS);
            }

        } catch (EmptyStackException | SQLException | ClassNotFoundException e) {
            System.out.println("Error says: " + e);
        } finally {
//            connect.close(checkResult);
        }
    }

    public void deleteFloor(String floor) throws ClassNotFoundException {
        ResultSet checkResult = null;
        Db connect = null;

        try {
            ArrayList<Integer> layoutID = new ArrayList<>();
            if (floor == null) throw new EmptyStackException();

            connect = new Db("floors");
            checkResult = connect.get("WHERE floor_name = '" + floor + "'");
            checkResult.next();
            layoutID.add(checkResult.getInt("id"));
            connect.delete(layoutID);

            CallerNavigator.loadVista(CallerNavigator.LAYOUTS);

        } catch (EmptyStackException | SQLException e) {
            System.out.println("Error says: " + e);
        } finally {
//            connect.close(checkResult);
        }
    }

}

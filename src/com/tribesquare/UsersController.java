package com.tribesquare;

import com.tribesquare.connection.Db;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;

public class UsersController {
    @FXML
    private TableView<UserData> tableView;

    @FXML
    private TableColumn<UserData, Integer> id;

    @FXML
    private TableColumn<UserData, String> name, phone, role, staffNumber;


    public void initialize() throws SQLException, ClassNotFoundException {
        ObservableList<UserData> users = getUsers();

        id.setCellValueFactory(new PropertyValueFactory<>("id"));

        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        name.setCellFactory(TextFieldTableCell.forTableColumn());
        name.setOnEditCommit(e -> editCell(e, "name"));

        phone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        phone.setCellFactory(TextFieldTableCell.forTableColumn());
        phone.setOnEditCommit(e -> editCell(e, "phone"));

        role.setCellValueFactory(new PropertyValueFactory<>("role"));

        staffNumber.setCellValueFactory(new PropertyValueFactory<>("staffNumber"));
        staffNumber.setCellFactory(TextFieldTableCell.forTableColumn());
        staffNumber.setOnEditCommit(e -> editCell(e, "staffNumber"));

        tableView.getItems().setAll(users);
        tableView.setEditable(true);
        tableView.getSelectionModel().setSelectionMode(
                SelectionMode.MULTIPLE
        );

    }

    public void createUserPane() throws SQLException, ClassNotFoundException {
        AddUserBox userBox = new AddUserBox();
        userBox.display();
    }

    public ObservableList<UserData> getUsers() throws SQLException, ClassNotFoundException {
        ObservableList users = FXCollections.observableArrayList();
        Db connect = new Db("users");
        ResultSet result = connect.get(
                "LEFT JOIN assignments ON users.name = assignments.user_id "
        );
        while (result.next()) {

            users.add(
                    new UserData(
                            result.getInt("id"),
                            result.getString("phone"),
                            result.getString("name"),
                            result.getString("role"),
                            result.getString("staff_no")
                    )
            );
        }
        return users;
    }

    public void deleteUsers() throws SQLException, ClassNotFoundException {
        Db connect = new Db("users");
        ArrayList<Integer> userIDs = new ArrayList<>();
        var selectionModel = tableView.getSelectionModel().getSelectedItems();

        try {
            if (selectionModel.size() == 0) throw new EmptyStackException();

            for (UserData i : selectionModel) {
                userIDs.add(i.getId());
            }

            var delete = connect.delete(userIDs);
            if (delete > 0) {
                initialize();
            }

        } catch (EmptyStackException e) {
            System.out.println("Error says: " + e);
        }

    }

    private void editCell(TableColumn.CellEditEvent<UserData, String> e, String type) {
        try {
            Db connect = new Db("users");
            var id = e.getTableView().getItems().get(e.getTablePosition().getRow()).getId();

            HashMap<String, Object> user = new HashMap<>();
            user.put(type, e.getNewValue());
            connect.update(user, id);

            initialize();
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }

    }

    public void updateUsers() {
        var selectionModel = tableView.getSelectionModel().getSelectedItem();

        try {
            if (selectionModel == null) throw new EmptyStackException();

            AddUserBox userBox = new AddUserBox();
            userBox.updateBox(selectionModel);
        } catch (EmptyStackException e) {
            System.out.println("Error says: " + e);
        }
    }

    public void getPermissions() throws SQLException, ClassNotFoundException {
        var selectionModel = tableView.getSelectionModel().getSelectedItem();

        try {
            if (selectionModel == null) throw new EmptyStackException();

            int userIDs = selectionModel.getId();
            Permissions permissions = new Permissions(userIDs);
            permissions.display();

        } catch (EmptyStackException e) {
            System.out.println("Error says: " + e);
        }
    }

    public void changePassword() throws SQLException, ClassNotFoundException {
        var selectionModel = tableView.getSelectionModel().getSelectedItem();

        try {
            if (selectionModel == null) throw new EmptyStackException();

            int userIDs = selectionModel.getId();
            ChangePassword pass = new ChangePassword(userIDs);
            pass.display();

        } catch (EmptyStackException e) {
            System.out.println("Error says: " + e);
        }

    }
}

package com.tribesquare;

import com.tribesquare.connection.Db;
import com.tribesquare.connection.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController extends MainController {
    @FXML
    TextField username;

    @FXML
    TextField password;

    @FXML
    AnchorPane loginPane;

    public Button submitLoginButton;

    public Parent display() throws Exception {
        return FXMLLoader.load(getClass().getResource("login.fxml"));
    }

    public Boolean checkAuth() throws SQLException, ClassNotFoundException {
        UserSession session = new UserSession();
        String role = session.getUserID();
        return role != null && !role.equals("");
    }

    public void submit() {
        Db connect = null;
        ResultSet resultSet = null;
        if (username.getText().isEmpty()){
            AlertBox.display("Error", "Username cannot be blank.");
            return;
        }else if (password.getText().isEmpty()){
            AlertBox.display("Error", "Password cannot be blank.");
            return;
        }
        try {
            connect = new Db("users");
            resultSet = connect.get(" WHERE name = '" + username.getText().replace(username.getText().charAt(0), username.getText().toUpperCase().charAt(0)) + "' AND password = '" + password.getText() + "'");
            resultSet.next();
            if (resultSet.getRow() == 1) {
                String userID = resultSet.getString("id");
                UserSession.getInstance(userID);

                Stage loginStage = (Stage) loginPane.getScene().getWindow();
                loginStage.close();

                Stage stage = new Stage();
                stage.setTitle("Dashboard");
                stage.setScene(
                        createScene(
                                loadMainPane()
                        )
                );

                stage.show();

                //            CallerNavigator.loadVista(CallerNavigator.TX);
                var caller = new CallerNavigator();
                caller.loadTx(caller.TX2);


            }else if (resultSet.getRow() < 1){
                AlertBox.display("Error", "Username or password incorrect.");
            }

        } catch (SQLException | ClassNotFoundException | IOException exception) {
            exception.printStackTrace();
        } finally {
//            connect.close(resultSet);
        }
    }
}

package com.tribesquare;

import com.tribesquare.connection.Db;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.commons.dbutils.DbUtils;

import java.io.IOException;
import java.sql.SQLException;

public class MainController extends Application {

    public boolean authenticated = true;

    /**
     * Main point of entry*
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
    *Launches Java FX application*
    */
    @Override
    public void start(Stage primaryStage) throws Exception{
        boolean status = setUpDatabase();

        /*St up port connection*/
//        setUpConnection();

        if (status) {
            LoginController loginController = new LoginController();

            try {
                if (authenticated != loginController.checkAuth()) {
                    Parent loginLayout = loginController.display();
                    Scene loginScene = new Scene(loginLayout);
                    primaryStage.setTitle("Login");
                    primaryStage.setScene(loginScene);
                } else {
                    primaryStage.setTitle("Dashboard");
                    primaryStage.setScene(
                            createScene(
                                    loadMainPane()
                            )
                    );

                    CallerNavigator.loadVista(CallerNavigator.TX);
                }
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }

            primaryStage.show();
        }

    }

    /**
     * Initial Database setup*
     */
    private Boolean setUpDatabase() throws SQLException, ClassNotFoundException {
        Db connect = new Db("users");
        boolean status;

        try {
            connect.setTable("floors");
            connect.setTable("layouts");
            connect.setTable("permissions");
            connect.setTable("port_config");
            connect.setTable("layout_config");
            connect.setTable("system_config");
            connect.setTable("assignments");
            connect.setTable("tx");
            connect.setTable("tx_display");

            status = true;
        } catch (SQLException ex) {
            status = false;
            DbUtils.closeQuietly(connect.connection);
        }

        return status;
    }

    /**
     * Loads the main fxml layout.
     * Sets up the vista switching VistaNavigator.
     * Loads the first vista into the fxml layout.
     *
     * @return the loaded pane.
     * @throws IOException if the pane could not be loaded.
     */
    protected AnchorPane loadMainPane() throws IOException, SQLException, ClassNotFoundException {
        FXMLLoader loader = new FXMLLoader();

        AnchorPane mainPane = loader.load(
                getClass().getResourceAsStream(
                        CallerNavigator.MAIN
                )
        );

        Controller mainController = loader.getController();
        CallerNavigator.setMainController(mainController);

        return mainPane;
    }


    /**
     * Creates the main application scene.
     *
     * @param mainPane the main application layout.
     *
     * @return the created scene.
     */
    protected Scene createScene(AnchorPane mainPane) {

        return new Scene(
                mainPane
        );
    }

}

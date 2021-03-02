package com.tribesquare;

import com.tribesquare.connection.Db;
import com.tribesquare.connection.UserSession;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    /** Holder of a switchable vista. */
    @FXML
    private StackPane screenHolder;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private Menu configPane, staffPane, layoutPane, historyPane;

    @FXML
    private Text connectionStatus;

    @FXML
    private Circle connectionColour;

    String config_settings, layout_settings, staff_settings, analytics_settings;

    Timeline timeline;

    public void connect() throws SQLException, ClassNotFoundException {

        PortController set = new PortController();
        set.connect();
        connectionStatus.textProperty().bind(Bindings.createStringBinding(set::getPortStatus, set.portStatus));
        connectionStatus.fillProperty().bind(Bindings.createObjectBinding(set::getPortStatusColor, set.portStatusColor));
        connectionColour.fillProperty().bind(Bindings.createObjectBinding(set::getPortStatusColor, set.portStatusColor));

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        timeline = new Timeline();
        try {
            connect();
            Db connect = new Db("permissions");

            String userID = UserSession.getInstance(null).userID;
            System.out.println("Type of user: " + " " + userID);
            if (userID != null) {
                ResultSet resultSet = connect.get(" WHERE user_id = " + userID);
                resultSet.next();

                config_settings = resultSet.getString("config_settings");
                setPermissions("config_settings", config_settings);

                layout_settings = resultSet.getString("layout_settings");
                setPermissions("layout_settings", layout_settings);

                staff_settings = resultSet.getString("staff_settings");
                setPermissions("staff_settings", staff_settings);

                analytics_settings = resultSet.getString("analytics_settings");
                setPermissions("analytics_settings", analytics_settings);

            }


        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
//            System.out.println("An error occured: " + throwables);
        }

    }

    public void setPermissions (String settings, String value) {
        boolean bool = value.equals("true");

//        System.out.println("Getting permissions value: " + bool + " " + value);

        if (settings.equals("config_settings")) {
            configPane.setVisible(bool);
        }

        if (settings.equals("layout_settings")) {
            layoutPane.setVisible(bool);
        }

        if (settings.equals("staff_settings")) {
            staffPane.setVisible(bool);
        }

        if (settings.equals("analytics_settings")) {
            historyPane.setVisible(bool);
        }

    }

    /**
     * Replaces the vista displayed in the vista holder with a new vista.
     *
     * @param node the vista node to be swapped in.
     */
    public void setVista(Node node) {
//        System.out.println("Checking Vista: " + screenHolder);
        screenHolder.getChildren().setAll(node);
    }

    public void setVista(Node node, String tab) {
        var id = Integer.parseInt(tab);
        screenHolder.getChildren().setAll(node);
        TabPane tabPane = (TabPane) node;
        tabPane.getSelectionModel().select(id);
    }

    public void setTx(FXMLLoader loader) throws IOException {
        TxController controller = new TxController();
        controller.setTimeline();
        loader.setController(controller);
        var node = (Node) loader.load();
        screenHolder.getChildren().setAll(node);
    }

    public void setTx(FXMLLoader loader, Timeline timeline) throws IOException {
        TxController controller = new TxController();
        controller.setTimeline(timeline);
        loader.setController(controller);
        var node = (Node) loader.load();
        screenHolder.getChildren().setAll(node);
    }

    public void logout() throws IOException {
        UserSession.cleanUserSession();

        //close current parent
        Stage stage = (Stage) mainPane.getScene().getWindow();
        stage.close();


        //show login
        Stage loginStage = new Stage();
        Parent loginLayout = FXMLLoader.load(getClass().getResource("login.fxml"));

        Scene loginScene = new Scene(loginLayout);
        loginStage.setTitle("Login");
        loginStage.setScene(loginScene);
        loginStage.show();

    }

    public void triggerAboutPane() {
        new About();
    }

    public void triggerLayoutPane(ActionEvent actionEvent) {
        var id = ((MenuItem) actionEvent.getTarget()).getId();
        System.out.println("TabPane Tab: " + id );
        CallerNavigator.loadVista(CallerNavigator.CONFIGS, id);
    }

    public void triggerUsersPane() {
        CallerNavigator.loadVista(CallerNavigator.USERS);
    }

    public void triggerHistoryPane() {
        CallerNavigator.loadVista(CallerNavigator.HISTORY);
    }


    public void createNewFloor() throws SQLException, ClassNotFoundException {
        LayoutController layoutController = new LayoutController();
        layoutController.createNewFloor();
    }

    public void createNewLayout() throws SQLException, ClassNotFoundException {
        LayoutController layoutController = new LayoutController();
        layoutController.createNewLayout();
    }

    public void goHome() {
        var caller = new CallerNavigator();
        caller.loadTx(caller.TX2, timeline);
//        CallerNavigator.loadVista(CallerNavigator.TX);

    }

    public void createTx() throws SQLException, ClassNotFoundException {
        PortController portController = new PortController();
        portController.insertData();
    }

    public void viewLayout() {
        CallerNavigator.loadVista(CallerNavigator.LAYOUTS);
    }
}

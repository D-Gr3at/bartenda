package com.tribesquare;

import com.tribesquare.connection.Db;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.joda.time.DateTime;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;


public class TxController implements Initializable {
    TreeView<String> tree;
    TreeItem<String> root, layoutBranch;
    ComboBox<String> layoutOption;

    String selectedFloor;

    Text secText, minText, hrText;

    @FXML
    TabPane tabPane;

    @FXML
    AnchorPane myAnchor;

    @FXML
    private TableView<TxData> callStatusTable, unsettledCallTable;

    @FXML
    private TableColumn<TxData, String> date, date2, time, time2, type, type2, staff, staff2, responseTime, responseTime2, status, status2;

    LayoutConfiguration layout;

    Timeline timeline;

    public void setTimeline() {
//        timeline = new Timeline();
    }

    public void setTimeline(Timeline timeline) {
//        this.timeline = timeline;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {

            layout = new LayoutConfiguration().getConfig();
            timeline = new Timeline();


            populateTree();
            callStatus();
            unsettledCall();

        } catch (SQLException | ClassNotFoundException | FileNotFoundException throwables) {
            throwables.printStackTrace();
        }

    }

    private void callStatus() throws SQLException, ClassNotFoundException {
        ObservableList tx = getCallStatus();

        date.setCellValueFactory(new PropertyValueFactory<>("txDate"));
        date.setCellFactory(TextFieldTableCell.forTableColumn());

        time.setCellValueFactory(new PropertyValueFactory<>("txTime"));
        time.setCellFactory(TextFieldTableCell.forTableColumn());

        type.setCellValueFactory(new PropertyValueFactory<>("txType"));
        type.setCellFactory(TextFieldTableCell.forTableColumn());

        staff.setCellValueFactory(new PropertyValueFactory<>("staff"));
        staff.setCellFactory(TextFieldTableCell.forTableColumn());

        responseTime.setCellValueFactory(new PropertyValueFactory<>("responseTime"));
        responseTime.setCellFactory(TextFieldTableCell.forTableColumn());

        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        status.setCellFactory(TextFieldTableCell.forTableColumn());

        callStatusTable.getItems().setAll(tx);
    }

    private void unsettledCall() throws SQLException, ClassNotFoundException {
        ObservableList tx = getUnsettlesCalls();

        date2.setCellValueFactory(new PropertyValueFactory<>("txDate"));
        date2.setCellFactory(TextFieldTableCell.forTableColumn());

        time2.setCellValueFactory(new PropertyValueFactory<>("txTime"));
        time2.setCellFactory(TextFieldTableCell.forTableColumn());

        type2.setCellValueFactory(new PropertyValueFactory<>("txType"));
        type2.setCellFactory(TextFieldTableCell.forTableColumn());

        staff2.setCellValueFactory(new PropertyValueFactory<>("staff"));
        staff2.setCellFactory(TextFieldTableCell.forTableColumn());

        responseTime2.setCellValueFactory(new PropertyValueFactory<>("responseTime"));
        responseTime2.setCellFactory(TextFieldTableCell.forTableColumn());

        status2.setCellValueFactory(new PropertyValueFactory<>("status"));
        status2.setCellFactory(TextFieldTableCell.forTableColumn());

        unsettledCallTable.getItems().setAll(tx);
    }

    public ObservableList getCallStatus() throws SQLException, ClassNotFoundException {
        Db connect = new Db("tx");
        ResultSet statusResult = connect.get(
                "LEFT JOIN layouts ON layouts.tx_no = tx.tx_no " +
                        "LEFT JOIN assignments ON layouts.table_name = assignments.table_id " +
//                        "ORDER BY id DESC LIMIT " + layout.record_count
                        "ORDER BY id DESC LIMIT " + 10
        );

        return getTx(statusResult);
    }

    public ObservableList getUnsettlesCalls() throws SQLException, ClassNotFoundException {
        Db connect = new Db("tx");
        ResultSet result = connect.get(
                "LEFT JOIN layouts ON layouts.tx_no = tx.tx_no " +
                        "LEFT JOIN assignments ON layouts.table_name = assignments.table_id " +
                        "WHERE tx_state = 'no_answer' " +
//                        "ORDER BY id DESC LIMIT " + layout.record_count
                        "ORDER BY id DESC LIMIT " + 10

        );

        return getTx(result);
    }

    private ObservableList getTx(ResultSet resultSet) throws SQLException {
        ObservableList tx = FXCollections.observableArrayList();

        while (resultSet.next()) {
            tx.add(
                    new TxData(
                            resultSet.getInt("id"),
                            resultSet.getString("tx_no"),
                            resultSet.getString("tx_type"),
                            resultSet.getString("tx_date"),
                            resultSet.getString("tx_time"),
                            resultSet.getString("response_time"),
                            resultSet.getString("tx_state"),
                            resultSet.getString("user_id")
                    )
            );
        }
        return tx;
    }

    private void populateTree() throws SQLException, ClassNotFoundException, FileNotFoundException {
        root = new TreeItem<>();
        root.setExpanded(true);

        Db connect = new Db("floors");
        ResultSet result = connect.get();

        layoutBranch = makeBranch("Floor Plan", root);
        while (result.next()) {
            makeBranch(result.getString("floor_name"), layoutBranch);
        }

        tree = new TreeView<>(root);
        tree.setShowRoot(false);
        tree.setPrefWidth(200);
        tree.setPrefHeight(550);
        tree.setEditable(true);

        var firstItem = tree.getTreeItem(1);
        tree.getSelectionModel().select(firstItem);
        selectedFloor = firstItem.getValue();

        switchDisplay();

        final ContextMenu contextMenu = new ContextMenu();
        MenuItem create = new MenuItem("New");
        MenuItem delete = new MenuItem("Delete");
        contextMenu.getItems().addAll(create, delete);
        tree.setContextMenu(contextMenu);

        create.setOnAction(this::floorAction);
        delete.setOnAction(this::floorAction);

        tree.setCellFactory(tree -> {
            TreeCell<String> cell = new TreeCell<>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(item);
                    }
                }
            };
            cell.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    if (!cell.isEmpty()) {
                        TreeItem<String> treeItem = cell.getTreeItem();
                        selectedFloor = treeItem.getValue();
                        switchDisplay();
                    }
                } else {
                    System.out.println(MouseButton.SECONDARY + "Button Clicked!!!");
                }
            });
            return cell ;
        });

        SplitPane splitPane = (SplitPane) myAnchor.getChildren().get(0);
        ScrollPane scrollPane = (ScrollPane) splitPane.getItems().get(0);

        scrollPane.setContent(tree);
    }

    private void floorAction(ActionEvent event) {
        var target = (MenuItem) event.getSource();
        var action = target.getText();

        var selectedItem = tree.getSelectionModel().getSelectedItem().getValue();
        var layouts = new LayoutController();

        if (action.equals("Delete")) {
            try {
                layouts.deleteFloor(selectedItem);
            } catch (ClassNotFoundException throwables) {
                throwables.printStackTrace();
            }
        } else if (action.equals("New")) {
            layouts.createNewFloor();
        }
    }

    public TreeItem<String> makeBranch (String title, TreeItem<String> parent) {
        TreeItem<String> treeItem = new TreeItem<>(title);
        treeItem.setExpanded(true);
        parent.getChildren().add(treeItem);
        return treeItem;
    }

    private void switchDisplay() {
        if (layoutOption == null) {
            layoutOption = new ComboBox<>(FXCollections.observableArrayList("By Table", "By Waiter"));
            layoutOption.getSelectionModel().selectFirst();
        }

        String option = layoutOption.getValue();
        try {
            if (option.equals("By Waiter")) displayByStaff();
            else  display();
        } catch (ClassNotFoundException | FileNotFoundException | SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void displayByStaff() throws SQLException, ClassNotFoundException, FileNotFoundException {
        Db connect = new Db("users");

        ResultSet result =
                connect.getCustom(" *, tx_display.id as tx_id FROM users " +
                    " LEFT JOIN assignments ON users.name = assignments.user_id " +
                    " LEFT JOIN layouts ON layouts.table_name = assignments.table_id " +
                    " LEFT JOIN tx_display ON layouts.tx_no = tx_display.tx_no " +
                    "WHERE layouts.floor = '" + selectedFloor + "' " +
                    " GROUP BY layouts.table_name, tx_display.tx_no "
            );

        ArrayList<VBox> rectList = new ArrayList<>();
        ArrayList<String> countArr = new ArrayList<>();
        ArrayList<HashMap<String, String>> resultSetArr = new ArrayList<>();

        while (result.next()) {
            String name = result.getString("user_id");
            if (!countArr.contains(name)) {
                countArr.add(name);
            }

            HashMap<String, String> userResult = new HashMap<>();

            userResult.put("name", result.getString("name"));
            userResult.put("table_id", result.getString("table_id"));
            userResult.put("tx_state", result.getString("tx_state"));
            userResult.put("tx_id", result.getString("tx_no"));
            userResult.put("tx_time", result.getString("tx_time"));
            userResult.put("tx_date", result.getString("tx_date"));
            userResult.put("tx_type", result.getString("tx_type"));
            userResult.put("floor_id", result.getString("floor"));

            resultSetArr.add(userResult);

        }

        ArrayList<KeyFrame> keyFrames = new ArrayList<>();

        for (String assigned : countArr) {
            ArrayList<Group> groupList = new ArrayList<>();
            HBox userBox = new HBox();
            VBox nameLayout = new VBox();

            for (HashMap<String, String> stringStringHashMap : resultSetArr) {
                String name = stringStringHashMap.get("name");

                String time = stringStringHashMap.get("tx_time");
                String tx_date = stringStringHashMap.get("tx_date");
                String tx_id = stringStringHashMap.get("tx_id");
                String tx_type = stringStringHashMap.get("tx_type");
                String tx_state = stringStringHashMap.get("tx_state");

                time = time != null ? time : "00:00:00";
                tx_date = tx_date != null ? tx_date : "1970-01-01";

                Timing timer = new Timing(timeline);

                DateTime txTime = DateTime.parse(tx_date + "T" + time);
                keyFrames.add(new KeyFrame(Duration.seconds(1), actionEvent -> timer.updateCountDown()));
                this.timeline.getKeyFrames().setAll(keyFrames);
                this.timeline.setCycleCount(Timeline.INDEFINITE);
                timer.startWarningCountdown(txTime, tx_state, tx_id);

                if (assigned.equals(name)) {
                    String table = stringStringHashMap.get("table_id");

                    ImageView imageView = new ImageView();
                    if (tx_type != null) {
                        if (tx_type.equals("BILL")) {
                            FileInputStream input = new FileInputStream("src/com/tribesquare/images/bill.png");
                            Image image = new Image(input);
                            imageView.setImage(image);
                            imageView.setX(100);
                            imageView.setY(5);
                        }
                    }

                    Rectangle rect1 = new Rectangle();
                    rect1.setWidth(150);
                    rect1.setHeight(150);
                    rect1.setStroke(Color.WHITE);
                    rect1.setStrokeWidth(3);
                    rect1.fillProperty().bind(Bindings.createObjectBinding(timer::background, timer.backgroundColor));

                    Text tableText1 = new Text(table);
                    tableText1.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 18));
                    tableText1.setX(10);
                    tableText1.setY(25);
                    tableText1.fillProperty().bind(Bindings.createObjectBinding(timer::message, timer.messageColor));

                    secText = new Text();
                    secText.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 25));
                    secText.textProperty().bind(Bindings.createStringBinding(() -> timer.getSecondFromDuration(tx_id, tx_state), timer.secLeft));
                    secText.setX(90);
                    secText.setY(85);
                    secText.fillProperty().bind(Bindings.createObjectBinding(timer::message, timer.messageColor));

                    minText = new Text();
                    minText.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 25));
                    minText.textProperty().bind(Bindings.createStringBinding(() -> timer.getMinuteFromDuration(tx_id, tx_state), timer.minLeft));
                    minText.setX(45);
                    minText.setY(85);
                    minText.fillProperty().bind(Bindings.createObjectBinding(timer::message, timer.messageColor));

                    hrText = new Text();
                    hrText.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 25));
                    hrText.textProperty().bind(Bindings.createStringBinding(() -> timer.getHourFromDuration(tx_id, tx_state), timer.hrLeft));
                    hrText.setX(10);
                    hrText.setY(85);
                    hrText.fillProperty().bind(Bindings.createObjectBinding(timer::message, timer.messageColor));

                    Group timeGroup = new Group(hrText, minText, secText);

                    Text nameText1 = new Text(assigned);
                    nameText1.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 12));
                    nameText1.setX(10);
                    nameText1.setY(130);
                    nameText1.fillProperty().bind(Bindings.createObjectBinding(timer::message, timer.messageColor));

                    Group group = new Group(rect1, tableText1, timeGroup, nameText1, imageView);

                    groupList.add(group);
                    keyFrames.add(new KeyFrame(Duration.seconds(1), actionEvent -> timer.updateCountDown()));

                }
                    //
                    //                this.timeline.getKeyFrames().setAll(keyFrames);
                    //                this.timeline.setCycleCount(Timeline.INDEFINITE);
            }

            userBox.getChildren().addAll(groupList);
            nameLayout.getChildren().addAll(userBox);
            rectList.add(nameLayout);

        }

        layoutOption = new ComboBox<>(FXCollections.observableArrayList("By Table", "By Waiter"));
        layoutOption.getSelectionModel().select("By Waiter");
        layoutOption.setPrefWidth(100);
        layoutOption.setOnAction(e -> switchDisplay());

        HBox hBox = new HBox();
        hBox.getChildren().add(layoutOption);
        hBox.setAlignment(Pos.TOP_RIGHT);
        hBox.setPadding(new Insets(5, 35, 5, 0));

        VBox vBox = new VBox();
        vBox.getChildren().addAll(rectList);
        vBox.setPrefHeight(550);
        vBox.setPrefWidth(800);

        VBox vBox1 = new VBox();
        vBox1.getChildren().addAll(hBox, vBox);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(vBox1);

        TabPane parent = tabPane;
        parent.getTabs().get(0).setContent(scrollPane);

    }

    public void display() throws ClassNotFoundException, FileNotFoundException, SQLException {
        Db connect = new Db("layouts");
        ResultSet results =
                connect.getCustom(" *, tx_display.id as tx_id FROM layouts " +
                    "LEFT JOIN tx_display ON layouts.tx_no = tx_display.tx_no " +
                    "LEFT JOIN assignments ON layouts.table_name = assignments.table_id " +
                    "WHERE layouts.floor = '" + selectedFloor + "' " +
                    "ORDER BY layouts.table_name "
            );

        ArrayList<Group> rectList = new ArrayList<>();
        ArrayList<HashMap<String, String>> resultSetArr = new ArrayList<>();

        while (results.next()) {
            HashMap<String, String> userResult = new HashMap<>();

            userResult.put("table_name", results.getString("table_name"));
            userResult.put("floor", results.getString("floor"));
            userResult.put("user_id", results.getString("user_id"));
            userResult.put("tx_state", results.getString("tx_state"));
            userResult.put("tx_id", results.getString("tx_no"));
            userResult.put("tx_time", results.getString("tx_time"));
            userResult.put("tx_date", results.getString("tx_date"));
            userResult.put("tx_type", results.getString("tx_type"));

            resultSetArr.add(userResult);

        }

        ArrayList<KeyFrame> keyFrames = new ArrayList<>();

        for (HashMap<String, String> result : resultSetArr) {
            String table = result.get("table_name");
            String assigned = result.get("user_id");
            String tx_state = result.get("tx_state");
            String tx_id = result.get("tx_id");
            String tx_type = result.get("tx_type");

            String time = result.get("tx_time");
            String tx_date = result.get("tx_date");
            time = time != null ? time : "00:00:00";
            tx_date = tx_date != null ? tx_date : "1970-01-01";

            Timing timer = new Timing(timeline);

            DateTime txTime = DateTime.parse(tx_date + "T" + time);

            keyFrames.add(new KeyFrame(Duration.seconds(1), actionEvent -> timer.updateCountDown()));
            this.timeline.getKeyFrames().setAll(keyFrames);
            this.timeline.setCycleCount(Timeline.INDEFINITE);

            timer.startWarningCountdown(txTime, tx_state, tx_id);

            ImageView imageView = new ImageView();
            if (tx_type != null) {
                if (tx_type.equals("BILL")) {
                    FileInputStream input = new FileInputStream("src/com/tribesquare/images/bill.png");
                    Image image = new Image(input);
                    imageView.setImage(image);
                    imageView.setX(100);
                    imageView.setY(5);
                }
            }

            Rectangle rect1 = new Rectangle();
            rect1.setWidth(150);
            rect1.setHeight(150);
            rect1.setStroke(Color.WHITE);
            rect1.setStrokeWidth(3);
            rect1.fillProperty().bind(Bindings.createObjectBinding(timer::background, timer.backgroundColor));

            Text tableText1 = new Text(table);
            tableText1.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 18));
            tableText1.setX(10);
            tableText1.setY(25);
            tableText1.fillProperty().bind(Bindings.createObjectBinding(timer::message, timer.messageColor));

            secText = new Text();
            secText.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 25));
            secText.textProperty().bind(Bindings.createStringBinding(() -> timer.getSecondFromDuration(tx_id, tx_state), timer.secLeft));
            secText.setX(90);
            secText.setY(85);
            secText.fillProperty().bind(Bindings.createObjectBinding(timer::message, timer.messageColor));

            minText = new Text();
            minText.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 25));
            minText.textProperty().bind(Bindings.createStringBinding(() -> timer.getMinuteFromDuration(tx_id, tx_state), timer.minLeft));
            minText.setX(45);
            minText.setY(85);
            minText.fillProperty().bind(Bindings.createObjectBinding(timer::message, timer.messageColor));

            hrText = new Text();
            hrText.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 25));
            hrText.textProperty().bind(Bindings.createStringBinding(() -> timer.getHourFromDuration(tx_id, tx_state), timer.hrLeft));
            hrText.setX(10);
            hrText.setY(85);
            hrText.fillProperty().bind(Bindings.createObjectBinding(timer::message, timer.messageColor));

            Group timeGroup = new Group(hrText, minText, secText);

            Text nameText1 = new Text(assigned);
            nameText1.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 12));
            nameText1.setX(10);
            nameText1.setY(130);
            nameText1.fillProperty().bind(Bindings.createObjectBinding(timer::message, timer.messageColor));

            Group group = new Group(rect1, tableText1, timeGroup, nameText1, imageView);

            rectList.add(group);

            timeline.getKeyFrames().setAll(keyFrames);
            timeline.setCycleCount(Timeline.INDEFINITE);

        }

        layoutOption = new ComboBox<>(FXCollections.observableArrayList("By Table", "By Waiter"));
        layoutOption.getSelectionModel().select("By Table");
        layoutOption.setPrefWidth(100);
        layoutOption.setOnAction(e -> switchDisplay());

        HBox hBox = new HBox();
        hBox.getChildren().add(layoutOption);
        hBox.setAlignment(Pos.TOP_RIGHT);
        hBox.setPadding(new Insets(5, 35, 5, 0));

        FlowPane flowPane = new FlowPane();
        flowPane.getChildren().addAll(rectList);
        flowPane.setPrefHeight(550);
        flowPane.setPrefWidth(800);

        VBox vBox = new VBox();
        vBox.getChildren().addAll(hBox, flowPane);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(vBox);

        TabPane parent = tabPane;
        parent.getTabs().get(0).setContent(scrollPane);

    }

}

package com.tribesquare;

import com.tribesquare.connection.Db;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class HistoryController implements Initializable {

    private LineChart<String,Number> lineChart;

    @FXML
    private AnchorPane chartPane;

    @FXML
    private HBox hBoxPane;

    ArrayList<HashMap> resultSetArr;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // defining the axes
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Staff Name");
        yAxis.setLabel("Number/Time of Call");

        //creating the chart
        lineChart = new LineChart<>(xAxis,yAxis);
        lineChart.setTitle("Staff Activity Chart");

        ComboBox<String> menuOption = new ComboBox<>(FXCollections.observableArrayList(
                "Total Number Of Calls",
                "Number Of Completed",
                "Number of Incompleted"
//                "Total Time of Calls",
//                "Average time Of Calls"
        ));

        menuOption.getSelectionModel().selectFirst();
        menuOption.setPrefSize(150, 40);

        hBoxPane.getChildren().add(menuOption);
        hBoxPane.setPadding(new Insets(5));
        hBoxPane.setSpacing(5);

        try {
            display(menuOption);
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }

        menuOption.setOnAction(e -> {
           try {
               display(menuOption);
           } catch (SQLException | ClassNotFoundException throwables) {
               throwables.printStackTrace();
           }
        });


    }

    private void display(ComboBox<String> menuOption) throws SQLException, ClassNotFoundException {
        var menuOptionIndex = menuOption.getSelectionModel().getSelectedIndex();
        var attended = getData(menuOptionIndex);

        //defining a series
        XYChart.Series series = new XYChart.Series();
        series.setName(menuOption.getSelectionModel().getSelectedItem());

        System.out.println("Response: " + attended);

        //populating the series with data

        for (HashMap map: attended ) {
            series.getData().add(new XYChart.Data(map.get("staff"),  map.get("count")));
        }
        lineChart.getData().setAll(series);
        lineChart.setPrefSize(1000, 500);

        chartPane.getChildren().setAll(lineChart);
    }

    private ArrayList<HashMap> getData(int data) throws SQLException, ClassNotFoundException {
        System.out.println("Menu button change" + data);

        ArrayList<HashMap> attended;
        switch (data) {
            case 1:
                attended = noOfCompleted();
                break;
            case 2:
                attended = noOfInCompleted();
                break;
            case 3:
                attended = totalTime();
                break;
            case 4:
                attended = averageTime();
                break;
            default:
                attended = totalNumber();
                break;

        }

        return attended;
    }

    public ArrayList<HashMap> totalNumber() throws SQLException, ClassNotFoundException {
        Db connect = new Db("users");
        ResultSet result = connect.count(" " +
                "LEFT JOIN assignments ON users.name = assignments.user_id " +
                "LEFT JOIN layouts ON layouts.table_name = assignments.table_id " +
                "LEFT JOIN tx ON layouts.tx_no = tx.tx_no " +
                "GROUP BY users.name "
        );

        return getHashMaps(result);
    }

    public ArrayList<HashMap> totalTime() throws SQLException, ClassNotFoundException {
        Db connect = new Db("users");
        ResultSet result = connect.get(" " +
                "LEFT JOIN assignments ON users.name = assignments.user_id " +
                "LEFT JOIN layouts ON layouts.table_name = assignments.table_id " +
                "LEFT JOIN tx ON layouts.tx_no = tx.tx_no " +
                "GROUP BY users.name "
        );

        return getHashMaps(result);
    }

    public ArrayList<HashMap> averageTime() throws SQLException, ClassNotFoundException {
        Db connect = new Db("users");
        ResultSet result = connect.count(" " +
                "LEFT JOIN assignments ON users.name = assignments.user_id " +
                "LEFT JOIN layouts ON layouts.table_name = assignments.table_id " +
                "LEFT JOIN tx ON layouts.tx_no = tx.tx_no " +
                "GROUP BY users.name "
        );

        return getHashMaps(result);
    }

    public ArrayList<HashMap> noOfCompleted() throws SQLException, ClassNotFoundException {
        Db connect = new Db("users");
        ResultSet result = connect.count(" " +
                "LEFT JOIN assignments ON users.name = assignments.user_id " +
                "LEFT JOIN layouts ON layouts.table_name = assignments.table_id " +
                "LEFT JOIN tx ON layouts.tx_no = tx.tx_no " +
                "WHERE tx_state = 'attended' " +
                "GROUP BY users.name "
        );

        return getHashMaps(result);
    }

    public ArrayList<HashMap> noOfInCompleted() throws SQLException, ClassNotFoundException {
        Db connect = new Db("users");
        ResultSet result = connect.count(" " +
                "LEFT JOIN assignments ON users.name = assignments.user_id " +
                "LEFT JOIN layouts ON layouts.table_name = assignments.table_id " +
                "LEFT JOIN tx ON layouts.tx_no = tx.tx_no " +
                "WHERE tx_state = 'no_answer' " +
                "GROUP BY users.name "
        );

        return getHashMaps(result);
    }

    private ArrayList<HashMap> getHashMaps(ResultSet result) throws SQLException {
        resultSetArr = new ArrayList<>();

        while (result.next()) {
            HashMap<String, Object> attended= new HashMap<>();

            attended.put("staff", result.getString("name"));
            attended.put("count", result.getInt("count"));

            resultSetArr.add(attended);
        }

        return resultSetArr;
    }

}

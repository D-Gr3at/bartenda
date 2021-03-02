package com.tribesquare;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AlertBox {

    public static void display(String title, String message){
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setWidth(400);
        window.initStyle(StageStyle.UNDECORATED);
        window.setHeight(150);

        Label label = new Label();
        label.setText(message);
        label.setFont(Font.font("san serif", FontWeight.BOLD, 14.0));
        label.setTextFill(Paint.valueOf("red"));
        Button closeButton = new Button("Close");
        closeButton.setPrefSize(70, 30);
        closeButton.setOnAction(actionEvent -> {
//            window.close();
            ((Node) actionEvent.getSource()).getScene().getWindow().hide();//close currentstage
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, closeButton);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }
}

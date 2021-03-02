package com.tribesquare;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class About extends ScrollPane {
    public About () {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("About");

        Text text = new Text(
                "Welcome to Table Caller Ltd, the developers of the Table caller business solution for hospitality business in Nigeria, and all over the world. Welcome to Table Caller Ltd, the developers of the Table caller business solution for hospitality business in Nigeria, and all over the world. Welcome to Table Caller Ltd, the developers of the Table caller business solution for hospitality business in Nigeria, and all over the world. Welcome to Table Caller Ltd, the developers of the Table caller business solution for hospitality business in Nigeria, and all over the world. Welcome to Table Caller Ltd, the developers of the Table caller business solution for hospitality business in Nigeria, and all over the world."
        );

        text.setLineSpacing(5);
        text.setFont(Font.font("verdana", FontWeight.NORMAL, FontPosture.REGULAR, 14));
        text.setX(10);
        text.setY(25);
        text.wrappingWidthProperty().set(450);

        AnchorPane pane = new AnchorPane();
        pane.getChildren().add(text);
        pane.setPadding(new Insets(20));

        Scene scene = new Scene(pane, 500, 300);
        window.setScene(scene);
        window.show();
    }
}

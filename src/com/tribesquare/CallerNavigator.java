package com.tribesquare;

import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;

import java.io.IOException;

public class CallerNavigator {
    /**
     * Convenience constants for fxml layouts managed by the navigator.
     */
    public static final String MAIN    = "main.fxml";
    public static final String CONFIGS = "configurations.fxml";
    public static final String TX = "tx.fxml";
    public final String TX2 = "tx.fxml";
    public static final String LAYOUTS = "layouts.fxml";
    public static final String USERS = "users.fxml";
    public static final String HISTORY = "history.fxml";
    public static final String ANALYTICS = "analytics.fxml";

    /** The main application layout controller. */
    private static Controller mainController;

    /**
     * Stores the main controller for later use in navigation tasks.
     *
     * @param mainController the main application layout controller.
     */
    public static void setMainController(Controller mainController) {
        CallerNavigator.mainController = mainController;
    }

    /**
     * Loads the vista specified by the fxml file into the
     * vistaHolder pane of the main application layout.
     *
     * Previously loaded vista for the same fxml file are not cached.
     * The fxml is loaded anew and a new vista node hierarchy generated
     * every time this method is invoked.
     *
     * A more sophisticated load function could potentially add some
     * enhancements or optimizations, for example:
     *   cache FXMLLoaders
     *   cache loaded vista nodes, so they can be recalled or reused
     *   allow a user to specify vista node reuse or new creation
     *   allow back and forward history like a browser
     *
     * @param fxml the fxml file to be loaded.
     */
    public static void loadVista(String fxml) {
        try {
            mainController.setVista(
                    FXMLLoader.load(
                            CallerNavigator.class.getResource(
                                    fxml
                            )
                    )
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadVista(String fxml, String arg) {
        try {
            mainController.setVista(
                    FXMLLoader.load(
                            CallerNavigator.class.getResource(
                                    fxml
                            )
                    ),
                    arg
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadTx(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            fxml
                    )
            );

            mainController.setTx(
                    loader
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadTx(String fxml, Timeline timeline) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    CallerNavigator.class.getResource(
                            fxml
                    )
            );

            mainController.setTx(
                    loader, timeline
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

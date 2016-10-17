package com.harambe;


import com.harambe.communication.ServerCommunication;
import com.harambe.database.DatabaseConnector;
import com.harambe.game.SessionVars;
import com.harambe.gui.MasterController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import javax.swing.*;
import java.sql.SQLException;
import java.util.Optional;

/**
 * TODO: insert documentation here
 */
public class App extends Application {

    public static DatabaseConnector db;
    public static ServerCommunication sC;
    public static Stage stage; //used for alert ownership

    public static final String MENU_SCREEN = "startScreen";
    public static final String MENU_SCREEN_FILE = "/scenes/menu.fxml";
    public static final String MAIN_SCREEN = "main";
    public static final String MAIN_SCREEN_FILE = "/scenes/main.fxml";
    public static final String REPLAY_SCREEN = "replay";
    public static final String REPLAY_SCREEN_FILE = "/scenes/replay.fxml";
    public static final String STATISTICS_SCREEN = "statistics";
    public static final String STATISTICS_SCREEN_FILE = "/scenes/statistics.fxml";




    public static void main(String[] args) {
        try {
            db = new DatabaseConnector();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        launch(args);
    }


    public void start(Stage stage) throws Exception {
        App.stage = stage;
        stage.getIcons().add(
                new Image(getClass().getClassLoader().getResourceAsStream("img/harambe.png")));

        MasterController mainContainer = new MasterController();
        mainContainer.loadAndSetScreen(App.MENU_SCREEN, App.MENU_SCREEN_FILE, false);


        StackPane root = new StackPane();
        root.getChildren().addAll(mainContainer);
        Scene scene = new Scene(root, 1920, 1080);
        stage.setTitle("Harambe Wins!");
        stage.setScene(scene);
        scene.addEventHandler(KeyEvent.KEY_RELEASED, t -> {
            if(t.getCode()== KeyCode.ESCAPE) {
                stage.setFullScreen(true);
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.initOwner(stage);
                alert.setTitle("Confirmation Dialog");
                alert.setHeaderText("Do you really want to exit?");
                ButtonType toMenuScreen = new ButtonType("Menu Screen", ButtonBar.ButtonData.CANCEL_CLOSE);
                alert.getButtonTypes().setAll(ButtonType.YES, toMenuScreen ,ButtonType.CANCEL);
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.YES){
                    Platform.exit();
                } else if (result.get() == toMenuScreen) {
                    mainContainer.loadAndSetScreen(MENU_SCREEN, MENU_SCREEN_FILE, false);
                }
            } else if(t.getCode() == KeyCode.M) {
                if(com.harambe.gui.Stage.player != null) {
                    com.harambe.gui.Stage.player.setMute(!com.harambe.gui.Stage.player.isMute());
                }
            }
        });

        stage.show();

        stage.setFullScreenExitHint("");
        stage.setFullScreenExitKeyCombination(new KeyCharacterCombination("x"));
        stage.setFullScreen(true);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        System.out.println("Application stopped");
        db.shutdown();
    }

}
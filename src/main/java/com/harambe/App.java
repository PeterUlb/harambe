package com.harambe;


import com.harambe.communication.ServerCommunication;
import com.harambe.database.DatabaseConnector;
import com.harambe.gui.MainController;
import com.harambe.gui.MasterController;
import com.harambe.gui.MenuController;
import com.harambe.tools.I18N;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import com.harambe.database.model.BoardEvalModel;

import java.awt.*;
import java.net.URL;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * TODO: insert documentation here
 */
public class App extends Application {

    public static DatabaseConnector db;
    public static ServerCommunication sC;
    public static Stage stage; //used for alert ownership

    public static final String MENU_SCREEN = "startScreen";
    public static final String MENU_SCREEN_FILE = "/scenes/menu.fxml";
    public static final String CHARACTER_SELECTION_SCREEN = "character_selection";
    public static final String CHARACTER_SELECTION_SCREEN_FILE = "/scenes/characterSelection.fxml";
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
        stage.setTitle(I18N.getString("harambe_wins"));
        stage.setScene(scene);
        scene.addEventHandler(KeyEvent.KEY_RELEASED, t -> {
            if(t.getCode()== KeyCode.ESCAPE) {
                if (stage.isFullScreen()) {
                    stage.setFullScreen(true);
                }
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.initOwner(stage);
                alert.setTitle(I18N.getString("confirmation.dialog"));
                alert.setHeaderText(I18N.getString("alert.exit.question"));
                ButtonType toMenuScreen = new ButtonType(I18N.getString("menu.screen"), ButtonBar.ButtonData.CANCEL_CLOSE);
                alert.getButtonTypes().setAll(ButtonType.YES, toMenuScreen ,ButtonType.CANCEL);
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.YES){
                    Platform.exit();
                } else if (result.get() == toMenuScreen) {
                    MenuController.themePlayer.stop();
                    mainContainer.loadAndSetScreen(MENU_SCREEN, MENU_SCREEN_FILE, true);
                }
            } else if(t.getCode() == KeyCode.M) {
                if(com.harambe.gui.Stage.player != null) {
                    com.harambe.gui.Stage.player.setMute(!com.harambe.gui.Stage.player.isMute());
                }
                if(MenuController.themePlayer != null) {
                    MenuController.themePlayer.setMute(!MenuController.themePlayer.isMute());
                }
            } else if (t.getCode() == KeyCode.F) {
                stage.setFullScreen(!stage.isFullScreen());
            }
        });


        stage.show();

        stage.setFullScreenExitHint("");
        stage.setFullScreenExitKeyCombination(new KeyCharacterCombination("´"));

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        if (screenSize.getHeight() == 1080 && screenSize.getWidth() == 1920) {
            stage.setFullScreen(true);
        }

    }

    @Override
    public void stop() throws Exception {
        super.stop();
        System.out.println("Application stopped");
        db.shutdown();
    }



}
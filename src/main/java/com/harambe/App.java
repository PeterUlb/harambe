package com.harambe;


import com.harambe.gui.MasterController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.net.URL;

public class App extends Application {

 //   public static Pane root;
    public static String screen1ID = "startScreen";
    public static String screen1File = "/scenes/startScreen.fxml";
    public static String screen2ID = "main";
    public static String screen2File = "/scenes/main.fxml";

    public static void main(String[] args) {
        launch(args);
    }


    public void start(Stage stage) throws Exception {

        MasterController mainContainer = new MasterController();
        mainContainer.loadScreen(App.screen1ID, App.screen1File);
        mainContainer.loadScreen(App.screen2ID, App.screen2File);

        //Load FXML & set name
       // rootStart = FXMLLoader.load(getClass().getClassLoader().getResource("scenes/startScreen.fxml"));
        //stage.setTitle("Harambe Wins!");


        // music player
        final URL resource = getClass().getResource("/audio/mainTheme.mp3");
        final Media media = new Media(resource.toString());
        final MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();


       // rootStart.getChildren();
       // stage.setScene(new Scene(rootStart, 1920, 1080));
       // stage.show();

        mainContainer.setScreen(App.screen1ID);

        Group root = new Group();
        root.getChildren().addAll(mainContainer);
        Scene scene = new Scene(root, 1920, 1080);
        stage.setScene(scene);
        stage.show();

        //stage.setFullScreen(true);
    }

}
package com.harambe;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class App extends Application {

    //public static Stage stage;
    public static Pane root;


    public static void main(String[] args) {
            launch(args);
    }


    public void start(Stage stage) throws Exception {


        //Load FXML & set name
        root = FXMLLoader.load(getClass().getClassLoader().getResource("scenes/main.fxml"));
        stage.setTitle("Harambe Wins!");


        // music player
        final URL resource = getClass().getResource("/audio/mainTheme.mp3");
        final Media media = new Media(resource.toString());
        final MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();


        root.getChildren();
        stage.setScene(new Scene(root, 1920, 1080));
        stage.show();

        //stage.setFullScreen(true);
    }

}
package com.harambe;


import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.net.URL;

public class App extends Application {

    Stage window;
    @FXML
    private Pane fg;

    public static void main(String[] args) {
        launch(args);
    }


    public void start(Stage stage) throws Exception {


        //Load FXML & set name
        Pane root = FXMLLoader.load(getClass().getClassLoader().getResource("scenes/main.fxml"));
        stage.setTitle("Harambe Wins!");


        // music player
        final URL resource = getClass().getResource("/audio/testsound.mp3");
        final Media media = new Media(resource.toString());
        final MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();


        stage.setScene(new Scene(root, 1920, 1080));
        stage.show();


        //stage.setFullScreen(true);
    }

}
package com.harambe;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class App extends Application {

    Stage window;
    Button button;

    public static void main(String[] args) {
        launch(args);
    }


    public void start(Stage stage) throws Exception {
        Pane root = (Pane) FXMLLoader.load(getClass().getClassLoader().getResource("scenes/main.fxml"));
        Scene scene = new Scene(root, 1920, 1080);
        stage.setTitle("test");
        stage.setScene(scene);
        stage.show();
        stage.setFullScreen(true);
    }

}
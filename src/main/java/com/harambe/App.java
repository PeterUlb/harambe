package com.harambe;


import com.harambe.database.DatabaseConnector;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.sql.SQLException;


public class App extends Application {

    //public static Stage stage;
    public static Pane root;
    public static DatabaseConnector db;

    public static void main(String[] args) {
        try {
            db = new DatabaseConnector();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        launch(args);
    }


    public void start(Stage stage) throws Exception {


        //Load FXML & set name
        root = FXMLLoader.load(getClass().getClassLoader().getResource("scenes/main.fxml"));
        stage.setTitle("Harambe Wins!");


        root.getChildren();
        stage.setScene(new Scene(root, 1920, 1080));
        stage.show();

        //stage.setFullScreen(true);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        System.out.println("Application stopped");
        db.shutdown();
    }

}
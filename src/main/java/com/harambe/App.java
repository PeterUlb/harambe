package com.harambe;


import com.harambe.communication.ServerCommunication;
import com.harambe.database.DatabaseConnector;
import com.harambe.game.SessionVars;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import javax.swing.*;
import java.sql.SQLException;


public class App extends Application {

    //public static Stage stage;
    public static Pane root;
    public static DatabaseConnector db;
    public static ServerCommunication sC;

    public static void main(String[] args) {
        // TODO replace this with proper user interface (radio buttons etc)
        String input = JOptionPane.showInputDialog("[F]ile, [P]usher, against [A]I or [V]ersus?");
        if(input == null) {
            System.exit(0);
        } else if(input.equalsIgnoreCase("F")) {
            SessionVars.ourSymbol = JOptionPane.showInputDialog("O or X?").toUpperCase().charAt(0);
            SessionVars.useFileInterface = true;
//            SessionVars.fileInterfacePath = "C:\\Users\\USERNAME\\Desktop\\server";
            if(SessionVars.fileInterfacePath == null) {
                JOptionPane.showMessageDialog(null, "Set SessionVars.fileInterfacePath!!", "ERROR", 1);
                System.exit(-1);
            }
        } else if (input.equalsIgnoreCase("P")) {
            JOptionPane.showMessageDialog(null, "Not implemented!", "ERROR", 1);
//            SessionVars.ourSymbol = JOptionPane.showInputDialog("O or X?").charAt(0);
            SessionVars.usePusherInterface = true;
            System.exit(-1);
        } else if (input.equalsIgnoreCase("A")) {
            SessionVars.soloVsAI = true;
        } else if (input.equalsIgnoreCase("V")) {
            // do not set any flag
        } else {
            JOptionPane.showMessageDialog(null, "F P A or V only.....", "ERROR", 1);
            System.exit(-1);
        }

        try {
            db = new DatabaseConnector();
        } catch (SQLException | ClassNotFoundException e) {
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
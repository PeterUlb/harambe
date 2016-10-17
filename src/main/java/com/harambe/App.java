package com.harambe;


import com.harambe.communication.ServerCommunication;
import com.harambe.database.DatabaseConnector;
import com.harambe.game.SessionVars;
import com.harambe.gui.MasterController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import javax.swing.*;
import java.sql.SQLException;

/**
 * TODO: insert documentation here
 */
public class App extends Application {

    public static DatabaseConnector db;
    public static ServerCommunication sC;

    public static final String MENU_SCREEN = "startScreen";
    public static final String MENU_SCREEN_FILE = "/scenes/menu.fxml";
    public static final String MAIN_SCREEN = "main";
    public static final String MAIN_SCREEN_FILE = "/scenes/main.fxml";



    public static void main(String[] args) {
        // TODO replace this with proper user interface (radio buttons etc)
        Object[] options = { "File", "Pusher", "Human vs AI", "Human vs Human" };
        int x = JOptionPane.showOptionDialog(null, "Choose a play type", "Selection",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);
        if(x == -1) {
            System.exit(0);
        } else if(x == 0) {
            int symbolChoice = JOptionPane.showOptionDialog(null, "O or X?", "Selection",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, new Object[] {"O", "X"}, "O");
            if (symbolChoice == 0) {
                SessionVars.ourSymbol = 'O';
            } else if (symbolChoice == 1) {
                SessionVars.ourSymbol = 'X';
            } else {
                System.exit(0);
            }
            SessionVars.useFileInterface = true;
//            SessionVars.fileInterfacePath = "C:\\Users\\USERNAME\\Desktop\\server";
            if(SessionVars.fileInterfacePath == null) {
                SessionVars.fileInterfacePath = JOptionPane.showInputDialog(null, "Set communication path", "C:\\Users\\USERNAME\\Desktop\\server");
                if(SessionVars.fileInterfacePath == null) {
                    System.exit(0);
                }
            }

        } else if (x == 1) {
            JOptionPane.showMessageDialog(null, "Not implemented!", "ERROR", 1);
//            SessionVars.ourSymbol = JOptionPane.showInputDialog("O or X?").charAt(0);
            SessionVars.usePusherInterface = true;
            System.exit(-1);
        } else if (x == 2) {
            SessionVars.soloVsAI = true;
        } else if (x == 3) {
            // do not set any flag
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
        stage.getIcons().add(
                new Image(getClass().getClassLoader().getResourceAsStream("img/harambe.png")));

        MasterController mainContainer = new MasterController();
        mainContainer.loadScreen(App.MENU_SCREEN, App.MENU_SCREEN_FILE);

        //TODO remove when out of prototype stage
        mainContainer.loadScreen(App.MAIN_SCREEN, App.MAIN_SCREEN_FILE);
        mainContainer.setScreen(App.MAIN_SCREEN);
        // end remove

        //set main menu as first screen TODO uncomment when out of prototype
        //mainContainer.setScreen(App.MENU_SCREEN);

        StackPane root = new StackPane();
        root.getChildren().addAll(mainContainer);
        Scene scene = new Scene(root, 1920, 1080);
        stage.setTitle("Harambe Wins!");
        stage.setScene(scene);
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
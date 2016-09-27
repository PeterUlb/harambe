package com.harambe.gui;

import com.harambe.App;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

//import static com.harambe.App.root;
//import static com.harambe.App.rootStart;

/**
 * Created by tim on 26.09.2016.
 */
public class StartScreenController implements Initializable, ControlledScreen {

    MasterController myController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
    public void setScreenParent(MasterController screenParent){
        myController = screenParent;
    }

    //onClickEvent switch screen to local game
    @FXML
    private void playLocal(ActionEvent event)/*throws IOException*/ {
        myController.setScreen(App.screen2ID);
       /*
        try {
            root = FXMLLoader.load(getClass().getClassLoader().getResource("scenes/main.fxml"));
            Stage stage = new Stage();
            stage.setTitle
                    ("Harambe Wins!");
            stage.setScene(new Scene(root, 1920, 1080));
            stage.show();

        } catch(Exception e) {
            e.printStackTrace();
        }
        */
    }


}

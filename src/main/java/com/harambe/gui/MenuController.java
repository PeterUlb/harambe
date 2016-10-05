package com.harambe.gui;

import com.harambe.App;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * TODO: insert documentation here
 */
public class MenuController implements Initializable, ControlledScreen {

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

        myController.setScreen(App.MAIN_SCREEN);
    }


}

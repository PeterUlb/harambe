package com.harambe.gui;

import com.harambe.App;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by tim on 26.10.2016.
 */
public class StartMenuController implements Initializable, ControlledScreen {

    private MasterController myController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setScreenParent(MasterController screenParent) {
        myController = screenParent;
    }

    @FXML
    private void startGame(MouseEvent event) {
        myController.loadAndSetScreen(App.MENU_SCREEN, App.MENU_SCREEN_FILE, true);
    }
}

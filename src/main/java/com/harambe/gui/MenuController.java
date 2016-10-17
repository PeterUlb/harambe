package com.harambe.gui;

import com.harambe.App;
import com.harambe.game.SessionVars;
import com.harambe.tools.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import javax.swing.*;
import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;


/**
 * TODO: insert documentation here
 */
public class MenuController implements Initializable, ControlledScreen {

    MasterController myController;
    @FXML
    private ImageView btnImgLocal;
    @FXML
    private ImageView btnImgOnline;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setScreenParent(MasterController screenParent) {
        myController = screenParent;
    }

    //onClickEvent switch screen to local game
    @FXML
    private void playLocal(MouseEvent event)/*throws IOException*/ {
        // TODO this isn't the final user interface
        btnImgLocal.setDisable(true);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(App.stage);
        alert.setTitle("Selection");
        alert.setHeaderText("Offline Mode");
        alert.setContentText("Choose your option.");

        ButtonType buttonTypeAI = new ButtonType("Human vs AI");
        ButtonType buttonTypeVersus = new ButtonType("Human vs Human");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeAI, buttonTypeVersus, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();
        alert.close();

        if (result.get() == buttonTypeAI) {
            SessionVars.soloVsAI(true);
        } else if (result.get() == buttonTypeVersus) {
            // do not set any flags for Human vs Human for now
            SessionVars.resetFlags();
        } else {
            btnImgLocal.setDisable(false);
            return;
        }
        btnImgLocal.setDisable(false);
        myController.loadAndSetScreen(App.MAIN_SCREEN, App.MAIN_SCREEN_FILE, true);
    }

    @FXML
    private void openReplay(MouseEvent event) {
        myController.loadAndSetScreen(App.REPLAY_SCREEN, App.REPLAY_SCREEN_FILE, true);
    }

    @FXML
    void playOnline() {
        // TODO this isn't the final user interface
        btnImgOnline.setDisable(true);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(App.stage);
        alert.setTitle("Selection");
        alert.setHeaderText("Online Mode");
        alert.setContentText("Choose your option.");

        ButtonType buttonTypeFile = new ButtonType("File Interface");
        ButtonType buttonTypePusher = new ButtonType("Pusher Interface");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        ButtonType buttonTypeO = new ButtonType("O");
        ButtonType buttonTypeX = new ButtonType("X");

        alert.getButtonTypes().setAll(buttonTypeFile, buttonTypePusher, buttonTypeCancel);

        alert.getDialogPane().lookupButton(buttonTypePusher).setDisable(true); //TODO remove this when pusher is implemented

        Optional<ButtonType> result = alert.showAndWait();
        alert.close();

        if (result.get() == buttonTypeFile) {
            Alert alertXO = new Alert(Alert.AlertType.CONFIRMATION);
            alertXO.initOwner(App.stage);
            alertXO.setTitle("Selection");
            alertXO.setHeaderText("Player symbol");
            alertXO.setContentText("Choose your symbol.");
            alertXO.getButtonTypes().setAll(buttonTypeO, buttonTypeX, buttonTypeCancel);
            Optional<ButtonType> resultXO = alertXO.showAndWait();
            if (resultXO.get() == buttonTypeO) {
                SessionVars.ourSymbol = 'O';
            } else if (resultXO.get() == buttonTypeX) {
                SessionVars.ourSymbol = 'X';
            } else {
                btnImgOnline.setDisable(false);
                return;
            }

            final DirectoryChooser directoryChooser =
                    new DirectoryChooser();
            final File selectedDirectory =
                    directoryChooser.showDialog(App.stage);
            if (selectedDirectory != null) {
                SessionVars.useFileInterface(true, selectedDirectory.getAbsolutePath());
            } else {
                return;
            }


        } else if (result.get() == buttonTypePusher) {

            Alert alertXO = new Alert(Alert.AlertType.CONFIRMATION);
            alertXO.initOwner(App.stage);
            alertXO.setTitle("Selection");
            alertXO.setHeaderText("Player symbol");
            alertXO.setContentText("Choose your symbol.");
            alertXO.getButtonTypes().setAll(buttonTypeO, buttonTypeX, buttonTypeCancel);
            Optional<ButtonType> resultXO = alertXO.showAndWait();
            if (resultXO.get() == buttonTypeO) {
                SessionVars.ourSymbol = 'O';
            } else if (resultXO.get() == buttonTypeX) {
                SessionVars.ourSymbol = 'X';
            } else {
                btnImgOnline.setDisable(false);
                return;
            }

            SessionVars.usePusherInterface(true);
            Logger.event("Pusher not implemented");
            return;

        } else {
            btnImgOnline.setDisable(false);
            return;
        }
        btnImgOnline.setDisable(false);
        myController.loadAndSetScreen(App.MAIN_SCREEN, App.MAIN_SCREEN_FILE, true);
    }


}

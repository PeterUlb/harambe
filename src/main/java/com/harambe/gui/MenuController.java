package com.harambe.gui;

import com.harambe.App;
import com.harambe.game.SessionVars;
import com.harambe.tools.I18N;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;


/**
 * Controller for the Menu Scene
 */
public class MenuController implements Initializable, ControlledScreen {

    private MasterController myController;
    @FXML
    private ImageView btnImgLocal;
    @FXML
    private ImageView btnImgOnline;
    @FXML
    private ImageView btnImgEnglish;
    @FXML
    private ImageView btnImgGerman;
    @FXML
    private ImageView btnImgPerformance;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //set & play menuTheme
        App.themePlayer.playTheme("/audio/menuTheme.mp3");
        if (I18N.currentLang.equals(I18N.ENGLISH)) {
            btnImgEnglish.setStyle("-fx-image: url('/img/uk.png')");
            btnImgEnglish.setDisable(true);
            btnImgLocal.getStyleClass().add("buttonL");
        } else if (I18N.currentLang.equals(I18N.GERMAN)) {
            btnImgGerman.setStyle("-fx-image: url('/img/germany.png')");
            btnImgGerman.setDisable(true);
            btnImgLocal.getStyleClass().add("buttonLGER");
        }

        // check if performance mode is enabled
        if (SessionVars.performanceMode) {
            btnImgPerformance.getStyleClass().set(1, "buttonPerformance");
        } else {
            btnImgPerformance.getStyleClass().set(1, "buttonQuality");
        }
    }

    public void setScreenParent(MasterController screenParent) {
        myController = screenParent;
    }

    /**
     * Button event which switches the scene to the local character selection
     * @param event MouseEvent of the button click
     */
    @FXML
    private void playLocal(MouseEvent event)/*throws IOException*/ {
        SessionVars.resetFlags();
        myController.loadAndSetScreen(App.CHARACTER_SELECTION_SCREEN, App.CHARACTER_SELECTION_SCREEN_FILE, true);
    }

    /**
     * Button event which switches the scene to the replay selection screen
     * @param event MouseEvent of the button click
     */
    @FXML
    private void openReplay(MouseEvent event) {
        myController.loadAndSetScreen(App.REPLAY_SCREEN, App.REPLAY_SCREEN_FILE, true);
    }

    /**
     * Button event which switches the scene to the online character selection
     */
    @FXML
    void playOnline() {
        btnImgOnline.setDisable(true);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(App.stage);
        alert.setTitle(I18N.getString("selection"));
        alert.setHeaderText(I18N.getString("online.mode"));
        alert.setContentText(I18N.getString("choose.your.option"));

        ButtonType buttonTypeFile = new ButtonType(I18N.getString("file.interface"));
        ButtonType buttonTypePusher = new ButtonType(I18N.getString("pusher.interface"));
        ButtonType buttonTypeCancel = new ButtonType(I18N.getString("cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);

        ButtonType buttonTypeO = new ButtonType("O");
        ButtonType buttonTypeX = new ButtonType("X");

        alert.getButtonTypes().setAll(buttonTypeFile, buttonTypePusher, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();
        alert.close();

        if (result.get() == buttonTypeFile) {
            Alert alertXO = new Alert(Alert.AlertType.CONFIRMATION);
            alertXO.initOwner(App.stage);
            alertXO.setTitle(I18N.getString("selection"));
            alertXO.setHeaderText(I18N.getString("player.symbol"));
            alertXO.setContentText(I18N.getString("choose.your.symbol"));
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
                btnImgOnline.setDisable(false);
                return;
            }


        } else if (result.get() == buttonTypePusher) {

            Alert alertXO = new Alert(Alert.AlertType.CONFIRMATION);
            alertXO.initOwner(App.stage);
            alertXO.setTitle(I18N.getString("selection"));
            alertXO.setHeaderText(I18N.getString("player.symbol"));
            alertXO.setContentText(I18N.getString("choose.your.symbol"));
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

        } else {
            btnImgOnline.setDisable(false);
            return;
        }
        btnImgOnline.setDisable(false);
        myController.loadAndSetScreen(App.CHARACTER_SELECTION_SCREEN, App.CHARACTER_SELECTION_SCREEN_FILE, true);
    }

    /**
     * Changes to language to German
     */
    @FXML
    private void changeToGerman() {
        I18N.setLocale("de");
        myController.loadAndSetScreen(App.MENU_SCREEN, App.MENU_SCREEN_FILE, true);
    }

    /**
     * Changes the language to English
     */
    @FXML
    private void changeToEnglish() {
        I18N.setLocale("en");
        myController.loadAndSetScreen(App.MENU_SCREEN, App.MENU_SCREEN_FILE, true);
    }

    /**
     * Activates the performance mode, disabling animations and other fancy stuff to improve speed
     */
    @FXML
    private void performanceMode() {
        //switch performance mode on/ off
        SessionVars.performanceMode = !SessionVars.performanceMode;
        if (SessionVars.performanceMode) {
            btnImgPerformance.getStyleClass().set(1, "buttonPerformance");
        } else {
            btnImgPerformance.getStyleClass().set(1, "buttonQuality");
        }
    }


}

package com.harambe.gui;

import com.harambe.App;
import com.harambe.database.model.GameModel;
import com.harambe.database.model.SetModel;
import com.harambe.game.SessionVars;
import com.harambe.tools.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;


/**
 * TODO: insert documentation here
 */
public class ReplayController implements Initializable, ControlledScreen {

    @FXML
    TableView<GameModel> gameTableView;
    @FXML
    public TableColumn<GameModel, String> GameUUID;
    @FXML
    public TableColumn<GameModel, String> OpponentName;
    @FXML
    public TableColumn<GameModel, Integer> OurScore;
    @FXML
    public TableColumn<GameModel, Integer> OpponentScore;
    @FXML
    public TableColumn<GameModel, String> GameDate;

    @FXML
    TableView<SetModel> setTableView;
    @FXML
    public TableColumn<SetModel, Integer> SetNumber;
    @FXML
    public TableColumn<SetModel, Boolean> WeStarted;
    @FXML
    public TableColumn<SetModel, Boolean> WeWon;

    @FXML
    public Button startReplayBtn;
    @FXML
    public Button statisticsBtn;

    MasterController myController;

    Tooltip tooltip;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tooltip = new Tooltip("Click here for statistics");
        statisticsBtn.setTooltip(tooltip);
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(1800); //TODO change based on final screen change time
                Platform.runLater(() -> {
                    tooltip.show(statisticsBtn, 1420, 340); //TODO, hard coded coordinates suck, but every single method return 0,0 for the position of the button (local, parent, scene, screen etc)
                });
                Thread.sleep(10000);
                Platform.runLater(tooltip::hide);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        ArrayList<GameModel> gameModels = null;
        try {
            gameModels = GameModel.getGames(App.db);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        GameUUID.setCellValueFactory(new PropertyValueFactory<GameModel, String>("gameUUID"));
        OpponentName.setCellValueFactory(new PropertyValueFactory<GameModel, String>("opponentPlayer"));
        OurScore.setCellValueFactory(new PropertyValueFactory<GameModel, Integer>("ourPoints"));
        OpponentScore.setCellValueFactory(new PropertyValueFactory<GameModel, Integer>("opponentPoints"));
        GameDate.setCellValueFactory(new PropertyValueFactory<GameModel, String>("timestamp"));
        gameTableView.getItems().setAll(gameModels);
        gameTableView.getSortOrder().add(GameDate);

        setTableView.setPlaceholder(new Label("Select game..."));
    }
    public void setScreenParent(MasterController screenParent){
        myController = screenParent;
    }

    @FXML
    private void backToMainMenu() {
        tooltip.hide();
        myController.loadAndSetScreen(App.MENU_SCREEN, App.MENU_SCREEN_FILE, false);
    }

    @FXML
    private void displaySets() {
        startReplayBtn.setDisable(true);
        GameModel gameModel = gameTableView.getSelectionModel().getSelectedItem();
        if (gameModel != null) {
            ArrayList<SetModel> setModels = null;
            try {
                setModels = SetModel.getSets(App.db, gameModel.getGameUUID());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            SetNumber.setCellValueFactory(new PropertyValueFactory<SetModel, Integer>("setNumber"));
            WeStarted.setCellValueFactory(new PropertyValueFactory<SetModel, Boolean>("weStarted"));
            WeWon.setCellValueFactory(new PropertyValueFactory<SetModel, Boolean>("weWon"));
            setTableView.getItems().setAll(setModels);
            setTableView.getSortOrder().add(SetNumber);
        }
    }

    @FXML
    private void startReplay() {
        tooltip.hide();
        GameModel g = gameTableView.getSelectionModel().getSelectedItem();
        SetModel s = setTableView.getSelectionModel().getSelectedItem();
        if (g != null && s != null) {
            SessionVars.setupReplay(g.getGameUUID(), s.getSetNumber(), s.isWeStarted(), g.getOurPlayer(), g.getOpponentPlayer());
            Logger.event("Replay started");
            myController.loadAndSetScreen(App.MAIN_SCREEN, App.MAIN_SCREEN_FILE, true);
        }
    }

    @FXML
    private void enableButton() {
        GameModel g = gameTableView.getSelectionModel().getSelectedItem();
        SetModel s = setTableView.getSelectionModel().getSelectedItem();
        if (g != null && s != null) {
            startReplayBtn.setDisable(false);
        }
    }

    @FXML
    private void openStatistics() {
        tooltip.hide();
        myController.loadAndSetScreen(App.STATISTICS_SCREEN, App.STATISTICS_SCREEN_FILE, true);
    }



}

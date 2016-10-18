package com.harambe.gui;

import com.harambe.App;
import com.harambe.database.model.GameModel;
import com.harambe.database.model.SetModel;
import com.harambe.game.SessionVars;
import com.harambe.tools.I18N;
import com.harambe.tools.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
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
    private TableColumn<GameModel, String> GameUUID;
    @FXML
    private TableColumn<GameModel, String> OpponentName;
    @FXML
    private TableColumn<GameModel, Integer> OurScore;
    @FXML
    private TableColumn<GameModel, Integer> OpponentScore;
    @FXML
    private TableColumn<GameModel, String> GameDate;

    @FXML
    TableView<SetModel> setTableView;
    @FXML
    private TableColumn<SetModel, Integer> SetNumber;
    @FXML
    private TableColumn<SetModel, String> WeStarted;
    @FXML
    private TableColumn<SetModel, String> WeWon;

    @FXML
    private Button startReplayBtn;
    @FXML
    private Button statisticsBtn;
    @FXML
    private Button backBtn;
    @FXML
    private Label pastGamesLabel;

    private MasterController myController;

    private Tooltip tooltip;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pastGamesLabel.setText(I18N.getString("past.games"));
        backBtn.setText(I18N.getString("back"));
        startReplayBtn.setText(I18N.getString("start.replay"));
        tooltip = new Tooltip(I18N.getString("click.here.for.statistics"));
        statisticsBtn.setTooltip(tooltip);
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(800); //TODO change based on final screen change time
                Platform.runLater(() -> {
                    Bounds bounds = statisticsBtn.localToScene(statisticsBtn.getBoundsInLocal());
                    tooltip.show(statisticsBtn, bounds.getMaxX(), bounds.getMinY() + bounds.getHeight() / 2);
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
        GameUUID.setText(I18N.getString("game.id"));
        OpponentName.setCellValueFactory(new PropertyValueFactory<GameModel, String>("opponentPlayer"));
        OpponentName.setText(I18N.getString("opponent"));
        OurScore.setCellValueFactory(new PropertyValueFactory<GameModel, Integer>("ourPoints"));
        OurScore.setText(I18N.getString("our.score.short"));
        OpponentScore.setCellValueFactory(new PropertyValueFactory<GameModel, Integer>("opponentPoints"));
        OpponentScore.setText(I18N.getString("opponent.score.short"));
        GameDate.setCellValueFactory(new PropertyValueFactory<GameModel, String>("timestamp"));
        GameDate.setText(I18N.getString("date"));
        gameTableView.getItems().setAll(gameModels);
        gameTableView.getSortOrder().add(GameDate);

        setTableView.setPlaceholder(new Label(I18N.getString("select.game")));
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
            SetNumber.setText(I18N.getString("set"));
            WeStarted.setCellValueFactory(new PropertyValueFactory<SetModel, String>("weStarted"));
            WeStarted.setText(I18N.getString("we.started"));
            WeWon.setCellValueFactory(new PropertyValueFactory<SetModel, String>("weWon"));
            WeWon.setText(I18N.getString("we.won"));
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

package com.harambe.gui;

import com.harambe.App;
import com.harambe.database.model.GameModel;
import com.harambe.database.model.SetModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
    public TableColumn<GameModel, Integer> SetNumber;
    @FXML
    public TableColumn<GameModel, Boolean> WeStarted;
    @FXML
    public TableColumn<GameModel, Boolean> WeWon;

    @FXML
    public Button startReplayBtn;

    MasterController myController;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
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

        setTableView.setPlaceholder(new Label("Select game..."));
    }
    public void setScreenParent(MasterController screenParent){
        myController = screenParent;
    }

    @FXML
    private void backToMainMenu() {
        myController.setScreen(App.MENU_SCREEN);
    }

    @FXML
    private void displaySets() {
        startReplayBtn.setDisable(true);
        String gameUUID = gameTableView.getSelectionModel().getSelectedItem().getGameUUID();
        if (gameUUID != null) {
            ArrayList<SetModel> setModels = null;
            try {
                setModels = SetModel.getSets(App.db, gameUUID);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            SetNumber.setCellValueFactory(new PropertyValueFactory<GameModel, Integer>("setNumber"));
            WeStarted.setCellValueFactory(new PropertyValueFactory<GameModel, Boolean>("weStarted"));
            WeWon.setCellValueFactory(new PropertyValueFactory<GameModel, Boolean>("weWon"));
            setTableView.getItems().setAll(setModels);
        }
    }

    @FXML
    private void startReplay() {
        GameModel g = gameTableView.getSelectionModel().getSelectedItem();
        SetModel s = setTableView.getSelectionModel().getSelectedItem();
        if (g != null && s != null) {
            System.out.println(g.getGameUUID() + " | " + s.getSetNumber());
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



}

package com.harambe.gui;

import com.harambe.App;
import com.harambe.database.model.GameModel;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;


/**
 * TODO: insert documentation here
 */
public class ReplayController implements Initializable, ControlledScreen {

    @FXML
    TableView<GameModel> tableView;
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
        tableView.getItems().setAll(gameModels);
    }
    public void setScreenParent(MasterController screenParent){
        myController = screenParent;
    }

    @FXML
    private void backToMainMenu() {
        myController.setScreen(App.MENU_SCREEN);
    }



}

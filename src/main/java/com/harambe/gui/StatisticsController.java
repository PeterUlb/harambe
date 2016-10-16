package com.harambe.gui;

import com.harambe.App;
import com.harambe.database.model.GameModel;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;


/**
 * TODO: insert documentation here
 */
public class StatisticsController implements Initializable, ControlledScreen {

    MasterController myController;

    @FXML
    private PieChart gamesPieChart;
    @FXML
    private PieChart setsPieChart;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        int wonGames = 0;
        int lostGames = 0;
        int wonSets = 0;
        int lostSets = 0;
        ArrayList<GameModel> games = null;
        try {
             games = GameModel.getGames(App.db);
            for (GameModel gM :
                    games) {
                if (gM.isWeWon()) {
                    wonGames++;
                } else {
                    lostGames++;
                }
                wonSets += gM.getOurPoints();
                lostSets += gM.getOpponentPoints();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ObservableList<PieChart.Data> gamesData =
                FXCollections.observableArrayList(
                        new PieChart.Data("Won", wonGames),
                        new PieChart.Data("Lost", lostGames)
                );

        gamesData.forEach(data ->
                data.nameProperty().bind(
                        Bindings.concat(
                                data.getName(), " ", (int) data.pieValueProperty().get(), " Game(s)"
                        )
                )
        );

        gamesPieChart.setLabelsVisible(false);
        gamesPieChart.setData(gamesData);

        ObservableList<PieChart.Data> setsData =
                FXCollections.observableArrayList(
                        new PieChart.Data("Won", wonSets),
                        new PieChart.Data("Lost", lostSets)
                );

        setsData.forEach(data ->
                data.nameProperty().bind(
                        Bindings.concat(
                                data.getName(), " ", (int) data.pieValueProperty().get(), " Set(s)"
                        )
                )
        );

        setsPieChart.setLabelsVisible(false);
        setsPieChart.setData(setsData);
    }
    public void setScreenParent(MasterController screenParent){
        myController = screenParent;
    }

    @FXML
    private void backToReplayScreen() {
        myController.loadAndSetScreen(App.REPLAY_SCREEN, App.REPLAY_SCREEN_FILE, false);
    }





}

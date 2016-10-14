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
    private PieChart pieChart;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        int won = 0;
        int lost = 0;
        ArrayList<GameModel> games = null;
        try {
             games = GameModel.getGames(App.db);
            for (GameModel gM :
                    games) {
                if (gM.isWeWon()) {
                    won++;
                } else {
                    lost++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList(
                        new PieChart.Data("Won", won),
                        new PieChart.Data("Lost", lost)
                );

        pieChartData.forEach(data ->
                data.nameProperty().bind(
                        Bindings.concat(
                                data.getName(), " ", (int) data.pieValueProperty().get(), " Game(s)"
                        )
                )
        );

        pieChart.setLabelsVisible(false);
        pieChart.setData(pieChartData);
    }
    public void setScreenParent(MasterController screenParent){
        myController = screenParent;
    }

    @FXML
    private void backToReplayScreen() {
        myController.setScreen(App.REPLAY_SCREEN);
    }





}

package com.harambe.gui;

import com.harambe.App;
import com.harambe.database.model.GameModel;
import com.harambe.database.model.SetModel;
import com.harambe.database.model.TurnModel;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
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
    @FXML
    private AreaChart<Number, Number> turnsPerSetChart;
    @FXML
    private NumberAxis turnsPerSetChartxAxis;
    @FXML
    private NumberAxis turnsPerSetChartyAxis;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        int wonGames = 0;
        int lostGames = 0;
        int wonSets = 0;
        int lostSets = 0;
        int drawSets = 0;
        ArrayList<GameModel> games = null;
        ArrayList<Integer> turnNumbers = new ArrayList<>();
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

                for (SetModel set :
                        SetModel.getSets(App.db, gM.getGameUUID())) {
                    if (set.getWeWon().equals("draw")) {
                        drawSets++;
                    }
                    int turnsInSet = 0;
                    for (TurnModel turn :
                            TurnModel.getTurns(App.db, gM.getGameUUID(), set.getSetNumber())) {
                        turnsInSet++;
                    }
                    turnNumbers.add(turnsInSet);
                }
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
                        new PieChart.Data("Lost", lostSets),
                        new PieChart.Data("Draw: ", drawSets)
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

        turnsPerSetChart.setLegendVisible(false);

        turnsPerSetChartxAxis.setLowerBound(1);
        turnsPerSetChartxAxis.setUpperBound(wonSets + lostSets + drawSets);
        turnsPerSetChartxAxis.setTickUnit(1);
        turnsPerSetChartxAxis.setAutoRanging(false);
        turnsPerSetChartxAxis.setMinorTickVisible(false);
        turnsPerSetChartxAxis.setLabel("Set Number");

        turnsPerSetChartyAxis.setLowerBound(0);
        turnsPerSetChartyAxis.setUpperBound(Collections.max(turnNumbers)); // max turns played in all sets
        turnsPerSetChartyAxis.setTickUnit(1);
        turnsPerSetChartyAxis.setAutoRanging(false);
        turnsPerSetChartyAxis.setMinorTickVisible(false);
        turnsPerSetChartyAxis.setLabel("Turns");

        XYChart.Series<Number, Number> turnSeries = new XYChart.Series<>();
        for (int i = 0; i < turnNumbers.size(); i++) {
            turnSeries.getData().add(new XYChart.Data<>(i + 1, turnNumbers.get(i)));
        }
        turnsPerSetChart.getData().add(turnSeries);
    }
    public void setScreenParent(MasterController screenParent){
        myController = screenParent;
    }

    @FXML
    private void backToReplayScreen() {
        myController.loadAndSetScreen(App.REPLAY_SCREEN, App.REPLAY_SCREEN_FILE, false);
    }





}

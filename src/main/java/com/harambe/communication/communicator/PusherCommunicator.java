package com.harambe.communication.communicator;

import com.harambe.App;
import com.harambe.communication.ServerCommunication;
import com.harambe.database.model.SetModel;
import com.harambe.game.SessionVars;
import com.harambe.gui.MainController;
import com.harambe.pusherconnection.PusherConnector;
import javafx.application.Platform;

import java.sql.SQLException;

public class PusherCommunicator implements ServerCommunication {

    private PusherConnector pusher;
    private long repeatTime = 100;

    public PusherCommunicator(){
        pusher = new PusherConnector();
        Thread thread = new Thread(pusher);
        thread.start();
    }

    @Override
    public void passTurnToServer(int column) throws Exception {
        // TODO
        pusher.sendTurn(column);
    }

    @Override
    public int getTurnFromServer() throws Exception {
        /*
        Done:
            - normally: return enemy column number
            - return '-1' if it is up to us to start the game
            - return '-2' when the set has ended, in this case:
                * Find out who won from the server
                * Call MainController.p?.incrementScore(); ON THE RIGHT PLAYER
                * Call MainController.redrawScore();
                * Set SessionVars.weWonSet
                * Set the boolean: MainController.setDone = true; when the set is over
                * Create a SetModel and setModel.persistInDatabase(App.db);
                *  if (MainController.p1.getScore() >= 2 || MainController.p2.getScore() >= 2) {
                Platform.runLater(() -> {
                    MainController.endGame();
                });
            }

            - short: do everything the FileCommunicator does, gl hf
         */
        String message = pusher.getMessage();
        while (message == null) {
            message = pusher.getMessage();
            Thread.sleep(repeatTime);
        }
        //System.out.println("Message found!" + message);

        String[] splitMessage = message.split(" # ");

        /*
        [0] : message start part
        [1] : Game Status (Satz spielen/Spiel beendet)
        [2] : last turn
        [3] : Winner
         */

        //Check for Winner
        if (!splitMessage[3].contains("offen")){
            //System.out.println("Winner detected!");
            if (!splitMessage[3].contains("X")){
                if(MainController.p1.getSymbol() == 'X') {
                    MainController.p1.incrementScore();
                }else{
                    MainController.p2.incrementScore();
                }
                SessionVars.weWonSet = MainController.ourPlayer.getSymbol() == 'X';
            }else{
                if(MainController.p1.getSymbol() == 'O') {
                    MainController.p1.incrementScore();
                }else{
                    MainController.p2.incrementScore();
                }
                SessionVars.weWonSet = MainController.ourPlayer.getSymbol() == 'O';
            }
            MainController.redrawScore();
            SetModel setModel = new SetModel(SessionVars.currentGameUUID.toString(), SessionVars.setNumber, SessionVars.weStartSet, SessionVars.weWonSet);
            try {
                setModel.persistInDatabase(App.db);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            SessionVars.weWonSet = null;

            if (MainController.p1.getScore() >= 2 || MainController.p2.getScore() >= 2) {
                Platform.runLater(() -> {
                    MainController.endGame();
                });
            }

            MainController.setDone = true;
            return -2;
        }

        //System.out.println("Get Turn return: " + splitMessage[2]);
        return Integer.valueOf(splitMessage[2]);
    }
}


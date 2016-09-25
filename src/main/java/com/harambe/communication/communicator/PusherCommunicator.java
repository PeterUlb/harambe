package com.harambe.communication.communicator;

import com.harambe.communication.ServerCommunication;

public class PusherCommunicator implements ServerCommunication {
    @Override
    public void passTurnToServer(int column) throws Exception {
        // TODO
    }

    @Override
    public int getTurnFromServer() throws Exception {
        /*
        TODO:
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
        return 0;
    }
}

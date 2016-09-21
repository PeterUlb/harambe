package com.harambe.communication;

/**
 * This interface serves as a contract for the UI. Each communication channel has to implement a method to
 * pass the column number of our turn to the server (file or websocket) and a method to return the opponent's turn or
 * -1 if we start the game
 * Created by Peter on 21.09.2016.
 */
public interface ServerCommunication {
    /**
     * Writes the calculated turn to the Server (number in file or via pusher)
     * @param column [0-6]
     * @throws Exception
     */
    public void passTurnToServer(int column) throws Exception;

    /**
     * Gets the opponent's turn from the server
     * @return column [0-6] or -1 if we start!!!
     * @throws Exception
     */
    public int getTurnFromServer() throws Exception;
}

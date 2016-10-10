package com.harambe.communication;

/**
 * This interface serves as a contract for the UI. Each communication channel has to implement a method to
 * pass the column number of our turn to the server (file or websocket) and a method to return the opponent's turn or
 * -1 if we start the game
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
     * This method is also required to increment the player score!!!!!!!!!...
     * @return column [0-6], -2 if set ended or -1 if we start!!!
     * @throws Exception
     */
    public int getTurnFromServer() throws Exception;
}

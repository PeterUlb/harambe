package com.harambe.communication.communicator;

import com.harambe.App;
import com.harambe.communication.ServerCommunication;
import com.harambe.database.model.SetModel;
import com.harambe.game.SessionVars;
import com.harambe.gui.MainController;
import javafx.application.Platform;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Class for communication via files with the server (read xml, write single digit .txt)
 */
public class FileCommunicator implements ServerCommunication {
    private String filePath;
    private String agentFile;
    private String serverFile;
    private long repeatTime = 100; // interval in ms to check for new files

    /**
     *  Class for server communication via communicator interface
     * @param filePath e.g. C:\Users\Username\Desktop\server\
     * @param weArePlayerO determines the filename
     */
    public FileCommunicator(String filePath, boolean weArePlayerO) {
        this.filePath = filePath;
        if(weArePlayerO) {
            this.agentFile = File.separator + "spielero2server.txt";
            this.serverFile = File.separator + "server2spielero.xml";
        } else {
            this.agentFile = File.separator + "spielerx2server.txt";
            this.serverFile = File.separator + "server2spielerx.xml";
        }
    }

    /**
     * Writes the column number to the communicator
     * @param column [0-6]
     * @throws IOException
     */
    public void passTurnToServer(int column) throws IOException {
        FileWriter fileWriter = new FileWriter(filePath + agentFile);
        fileWriter.write(String.valueOf(column));
        fileWriter.close();
//        System.out.println("Wrote to server " + column);
    }

    /**
     * Reads the enemy turn from the server communicator
     * @return enemy turn, -2 if set ended or -1 if we start
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public int getTurnFromServer() throws IOException, ParserConfigurationException, SAXException {
        File inputFile = new File(filePath + serverFile);
        while(!inputFile.exists()) {
            try {
                Thread.sleep(repeatTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        DocumentBuilderFactory dbFactory
                = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(inputFile);
        doc.getDocumentElement().normalize();

        Element content = doc.getDocumentElement();
        NodeList nL = content.getChildNodes();

        inputFile.delete(); // delete so we can check for new files
        if(nL.item(1).getTextContent().equalsIgnoreCase("false") ) {
            //indicate that set has ended, and not that we have to start (server sends -1 in both cases :/
            if (nL.item(7).getTextContent().equals("Spieler O")) {
                // Player O wins
                if (MainController.p1.getSymbol() == 'O') {
                    MainController.p1.incrementScore();
                } else if (MainController.p2.getSymbol() == 'O') {
                    MainController.p2.incrementScore();
                }
                SessionVars.weWonSet = MainController.ourPlayer.getSymbol() == 'O';
            } else if (nL.item(7).getTextContent().equals("Spieler X")) {
                if (MainController.p1.getSymbol() == 'X') {
                    MainController.p1.incrementScore();
                } else if (MainController.p2.getSymbol() == 'X') {
                    MainController.p2.incrementScore();
                }
                SessionVars.weWonSet = MainController.ourPlayer.getSymbol() == 'X';
            }
            SetModel setModel = new SetModel(SessionVars.currentGameUUID.toString(), SessionVars.setNumber, SessionVars.weStartSet, SessionVars.weWonSet);
            try {
                setModel.persistInDatabase(App.db);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            SessionVars.weWonSet = null;
            MainController.redrawScore();
            if (MainController.p1.getScore() >= 2 || MainController.p2.getScore() >= 2) {
                Platform.runLater(() -> {
                    MainController.endGame();
                });
            }

            if( Integer.valueOf(nL.item(5).getTextContent()) == -1) {
                // we won, now wait for the next file
                MainController.setDone = true;
                return -2; // -2 means set ended, not that we start (-1)
            } else {
                // we lost, pass the winning move
                Integer.parseInt(nL.item(5).getTextContent()); // = xml gegnerzug tag
            }

        }
        //        System.out.println("got from server " + nL.item(5).getTextContent());
        return Integer.parseInt(nL.item(5).getTextContent()); // = xml gegnerzug tag
    }

}

package com.harambe.communication.file;

import com.harambe.communication.ServerCommunication;
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

/**
 * Class for communication via files with the server (read xml, write single digit .txt)
 * Created by Peter on 21.09.2016.
 */
public class FileCommunicator implements ServerCommunication {
    private String filePath;
    private String agentFile;
    private String serverFile;
    private long repeatTime = 100; // interval in ms to check for new files

    /**
     *  Class for server communication via file interface
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
     * Writes the column number to the file
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
     * Reads the enemy turn from the server file
     * @return enemy turn or -1 if we start
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

//        System.out.println("got from server " + nL.item(5).getTextContent());
        return Integer.parseInt(nL.item(5).getTextContent()); // = xml gegnerzug tag
    }

}

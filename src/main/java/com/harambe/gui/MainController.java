package com.harambe.gui;

import com.harambe.game.Board;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

import static com.harambe.App.root;


public class MainController implements Initializable {

    //fxml views
    @FXML
    private ImageView p1ImgView;
    @FXML
    private ImageView p2ImgView;
    @FXML
    private Text player1Name;
    @FXML
    private Text player2Name;
    @FXML
    private ImageView p1ChipView;
    @FXML
    private ImageView p2ChipView;


    //other variables
    private Board board;
    private int[] freeSpace;
    private Player p1;
    private Player p2;
    private Player activePlayer;
    private char activeSymbol;


    @Override // is called when FXMLLoader loads main.fxml
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {

        board = new Board();
        //get chip placement columns
        freeSpace = board.getFirstAvailableRow();


        p1 = new Player(false, "Player1", "harambe");
        p2 = new Player(false, "Player2", "poacher_1");
        if (Math.round(Math.random())==1) {
            activePlayer = p1;
            activeSymbol = 'X';
        } else {
            activePlayer = p2;
            activeSymbol = 'O';
        }

        System.out.println(activePlayer.getName() + " begins");

        initPlayers(p1, p2);



    }

    private void initPlayers(Player p1, Player p2) {
        //load image in ImageViewContainer for player 1
        Image p1Img = new Image(p1.getImgLocation());
        p1ImgView.setImage(p1Img);

        //load image in ImageViewContainer for player 2
        Image p2Img = new Image(p2.getImgLocation());
        p2ImgView.setImage(p2Img);
        //mirror Image for player 2
        p2ImgView.setScaleX(-1);


        //init Names
        player1Name.setText(p1.getName());
        player2Name.setText(p2.getName());

        //init Player1 Chips
        Chip c1 = new Chip(p1.getChip());
        Image p1Chip = new Image(c1.getImg());
        p1ChipView.setImage(p1Chip);
        p1ChipView.setFitWidth(64);
        p1ChipView.setPreserveRatio(true);
        p1ChipView.setSmooth(true);
        p1ChipView.setCache(true);

        //init Player2 Chips
        Chip c2 = new Chip(p2.getChip());
        Image p2Chip = new Image(c2.getImg());
        p2ChipView.setImage(p2Chip);
        p2ChipView.setFitWidth(58);
        p2ChipView.setPreserveRatio(true);
        p2ChipView.setSmooth(true);
        p2ChipView.setCache(true);
    }

    //onClickEvent
    @FXML
    private void dropChip(ActionEvent event)
    {
        Button btn = (Button) event.getSource();

        //get column
        int column = Integer.parseInt(btn.getId().substring(1));

        //get xPos
        double x = btn.getTranslateX();

        //spawn chip
        Chip chip = new Chip(activePlayer.getChip());
        Image chipImg = new Image(chip.getImg());
        ImageView imgView = new ImageView(chipImg);


        //move chip to clickLocation
        switch (freeSpace[column]) {
            case 5:
                imgView.setTranslateY(410);
                break;
            case 4:
                imgView.setTranslateY(260);
                break;
            case 3:
                imgView.setTranslateY(110);
                break;
            case 2:
                imgView.setTranslateY(-40);
                break;
            case 1:
                imgView.setTranslateY(-190);
                break;
            case 0:
                imgView.setTranslateY(-340);
                break;
            }
        imgView.setTranslateX(x);

        //drop chip in board array and disable row if full
        try {
            board.put(column, activeSymbol);
        }
        catch (Exception e) {
            System.out.println("column Full");
            btn.setVisible(false);
        }


        //check for fullBoard
        if (board.isFull(column)) {
            btn.setVisible(false);
        }

        //paint chip and move the layer to background
        root.getChildren().add(imgView);
        imgView.toBack();

        //end round
        switchPlayer();
    }


    private void switchPlayer() {
        if (activePlayer==p1) {
            activePlayer = p2;
            activeSymbol = 'O';
        }
        else {
            activePlayer = p1;
            activeSymbol = 'X';
        }

    }
}

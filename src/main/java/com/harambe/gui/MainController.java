package com.harambe.gui;

import com.harambe.game.Board;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
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
    private ArrayList<ImageView> chipArray;
    private ArrayList<Button> buttonArray;
    private ArrayList<ImageView> winCircleArray;
    private int[][] winLocation;



    @Override // is called when FXMLLoader loads main.fxml
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {

        board = new Board();
        chipArray = new ArrayList<>();
        buttonArray = new ArrayList<>();
        winLocation = null;
        //get chip placement columns
        freeSpace = board.getFirstAvailableRow();


        p1 = new Player(false, "Player1", "harambe", Board.PLAYER1);
        p2 = new Player(false, "Player2", "poacher_2", Board.PLAYER2);
        if (Math.round(Math.random())==1) {
            activePlayer = p1;
        } else {
            activePlayer = p2;
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
        p2ChipView.setFitWidth(64);
        p2ChipView.setPreserveRatio(true);
        p2ChipView.setSmooth(true);
        p2ChipView.setCache(true);
    }

    //onClickEvent
    @FXML
    private void dropChip(ActionEvent event)
    {
        Button btn = (Button) event.getSource();
        buttonArray.add(btn);

        //get column
        int column = Integer.parseInt(btn.getId().substring(1));

        //get xPos
        double x = btn.getTranslateX();

        //spawn chip
        Chip chip = new Chip(activePlayer.getChip());
        Image chipImg = new Image(chip.getImg());
        ImageView imgView = new ImageView(chipImg);

        //store chip img
        chipArray.add(imgView);


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
            board.put(column, activePlayer.getSymbol());
        }
        catch (Exception e) {
            System.out.println("column full");
            btn.setVisible(false);
        }


        //check for fullBoard
        if (board.isFull(column)) {
            btn.setVisible(false);
        }

        //paint chip and move the layer to background
        root.getChildren().add(imgView);
        imgView.toBack();

        checkForWin();

        //end round
        switchPlayer();
    }



    private void checkForWin() {

        if ((winLocation = board.getWinForUI(activePlayer.getSymbol())) != null) {
            System.out.println(p1.getName() + " wins");

            //setting variables for win position
            int x0 = -450;
            int step = 150;
            int y0 = -340;

            Image winCircleImg = new Image("/img/winCircle.png");

            int rowPos = 0;
            int columnPos = 0;
            winCircleArray = new ArrayList<>();

            for ( int zeile = 0; zeile < winLocation.length; zeile++ )
            {

                for ( int spalte=0, i=0; spalte < winLocation[zeile].length; i=-i+1, spalte++ ) {
                    if (i==0) {
                        rowPos = (y0+(winLocation[zeile][spalte]*step));
                    }
                    else {
                        columnPos = (x0+(winLocation[zeile][spalte]*step));
                    }
                }
                ImageView winCircle = new ImageView(winCircleImg);
                winCircle.setTranslateY(rowPos);
                winCircle.setTranslateX(columnPos);

                winCircleArray.add(winCircle);
                root.getChildren().add(winCircle);

            }
            endTurn();

        }

    }

    private void switchPlayer() {
        if (activePlayer==p1) {
            activePlayer = p2;
        }
        else {
            activePlayer = p1;
        }
    }


    private void endTurn() {

        //acknowledge player of his victory
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText("Player " + activePlayer.getName() + " wins the turn");

        alert.showAndWait();

        try {
            //reinitialize the board
            board.reset();
            for (ImageView chip: chipArray) {
                root.getChildren().remove(chip);
            }
            chipArray = new ArrayList<>();

            for (ImageView winCircle: winCircleArray) {
                root.getChildren().remove(winCircle);
            }

            for (Button column: buttonArray) {
                column.setVisible(true);
            }



        }
        catch (Exception e) {
            e.printStackTrace();
        }


    }


}

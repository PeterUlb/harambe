package com.harambe.gui;

import com.harambe.App;
import com.harambe.database.model.SetModel;
import com.harambe.database.model.TurnModel;
import com.harambe.game.Board;
import com.harambe.game.SessionVars;
import javafx.animation.TranslateTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import static com.harambe.App.root;

/**
 * Controller of the main game. Contains mostly the chip dropping and logic behind the visual representation of the board.
 */
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
    @FXML
    private Text player1Score;
    @FXML
    private Text player2Score;
    @FXML
    private StackPane field;
    @FXML
    private StackPane bg;
    @FXML
    private Text time;



    //other variables
    private Board board;
    private int[] freeSpace;
    private Player p1;
    private Player p2;
    private Player activePlayer;
    private Image winCircleImg;
    private ArrayList<ImageView> chipArray;
    private ArrayList<Button> buttonArray;
    private ArrayList<ImageView> winCircleArray;
    private int[][] winLocation;


    /**
     * initialization method. Is being called when FXMLLoader loads main.fxml.
     * @param fxmlFileLocation location of the fxml file.
     */
    @Override //
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {

        board = new Board();
        Stage stage = new Stage("coast_2");
        bg.setStyle("-fx-background-image: url('" + stage.getImg() + "'); ");


        timerStart();

        chipArray = new ArrayList<>();
        buttonArray = new ArrayList<>();
        winLocation = null;
        //get chip placement columns
        freeSpace = board.getFirstAvailableRow();


        p1 = new Player(false, "Player1", "harambe", Board.PLAYER1);
        p2 = new Player(false, "Player2", "poacher_2", Board.PLAYER2);

        SessionVars.initializeNewGame(p2.getName());

        if (Math.round(Math.random())==1) {
            activePlayer = p1;
            SessionVars.initializeNewSet(true);

        } else {
            activePlayer = p2;
            SessionVars.initializeNewSet(false);
        }


        winCircleImg = new Image("/img/winCircle.png");


        System.out.println(activePlayer.getName() + " begins");

        initPlayers(p1, p2);

    }

    /**
     * starts the timer of the game
     */
    private void timerStart() {
        Thread thread = new Thread(() -> {
            StringProperty yolo = new SimpleStringProperty();
            time.textProperty().bind(yolo);
            try {
                long start = System.nanoTime();
                while(true) {
                    yolo.set(String.format("%02d:%02d",
                            TimeUnit.NANOSECONDS.toMinutes(System.nanoTime()-start),
                            TimeUnit.NANOSECONDS.toSeconds(System.nanoTime()-start) - TimeUnit.MINUTES.toSeconds(TimeUnit.NANOSECONDS.toMinutes( System.nanoTime()-start))));
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.setDaemon(true);
        thread.start();
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

    /**
     * onClick event for the buttons laying over the different columns.
     * contains most of the UI-logic.
     * @param event onclickEvent of the button to get the different column Positions to drop the chips.
     */
    @FXML
    private void dropChip(ActionEvent event) {
        Button btn = (Button) event.getSource();

        final URL resource = getClass().getResource(activePlayer.getDropSound());
        final Media drop = new Media(resource.toString());
        MediaPlayer player = new MediaPlayer(drop);
        player.play();

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

        moveChip(imgView, column, x);

        //drop chip in board array and disable row if full
        try {
            board.put(column, activePlayer.getSymbol());
        }
        catch (Exception e) {
            System.out.println("column full");
            //add button from full line to buttonarray
            buttonArray.add(btn);
            btn.setVisible(false);
        }


        //check for fullBoard
        if (board.isFull(column)) {
            //add button from full line to buttonarray
            buttonArray.add(btn);
            btn.setVisible(false);
        }

        //paint chip and move the layer to background
        field.getChildren().add(imgView);
        imgView.toBack();

        persistDrop(column);

        checkForWin();

        //end round
        switchPlayer();
    }

    private void persistDrop(int column) {
        TurnModel turnModel = null;
        if (activePlayer== p1) {
            turnModel = new TurnModel(SessionVars.currentGameUUID.toString(), SessionVars.setNumber, SessionVars.turnNumber, false, column);
        } else {
            turnModel = new TurnModel(SessionVars.currentGameUUID.toString(), SessionVars.setNumber, SessionVars.turnNumber, true, column);
        }

        try {
            turnModel.persistInDatabase(App.db);
            SessionVars.turnNumber++;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * moves chip image to the board with an animation/ transition.
     * @param imgView image of the chip
     * @param column selected column to drop the chip
     * @param columnPos x position of the column
     */
    private void moveChip(ImageView imgView, int column, double columnPos) {
        //chip drop transition/ animation
        TranslateTransition trans = new TranslateTransition();
        trans.setNode(imgView);
        trans.setDuration(new Duration(100));

        //move chip to clickLocation
        double startPos = -410f;
        imgView.setTranslateY(startPos);
        switch (freeSpace[column]) {
            case 5:
                trans.setByY(410f-startPos);
                break;
            case 4:
                trans.setByY(260f-startPos);
                break;
            case 3:
                trans.setByY(110f-startPos);
                break;
            case 2:
                trans.setByY(-40f-startPos);
                break;
            case 1:
                trans.setByY(-190f-startPos);
                break;
            case 0:
                trans.setByY(-340f-startPos);
                break;
        }
        imgView.setTranslateX(columnPos);
        trans.play();
    }

    /**
     * checks for a potential winner. Ends the game and shows how he/she won.
     */
    private void checkForWin() {

        if ((winLocation = board.getWinForUI(activePlayer.getSymbol())) != null) {
            System.out.println(p1.getName() + " wins");

            //increment score and change score
            activePlayer.incrementScore();
            SetModel setModel;
            if (activePlayer==p1) {
                player1Score.setText(String.valueOf(activePlayer.getScore()));
                SessionVars.points++;
                setModel = new SetModel(SessionVars.currentGameUUID.toString(), SessionVars.setNumber, SessionVars.weStartSet, true);
            }
            else {
                player2Score.setText(String.valueOf(activePlayer.getScore()));
                setModel = new SetModel(SessionVars.currentGameUUID.toString(), SessionVars.setNumber, SessionVars.weStartSet, false);
            }

            try {
                setModel.persistInDatabase(App.db);
            } catch (SQLException e) {
                e.printStackTrace();
            }


            //setting variables for win position
            int x0 = -450;
            int step = 150;
            int y0 = -340;


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
            endSet();

        }

    }

    /**
     * sets the current player (player 1/ player 2) as active player
     */
    private void switchPlayer() {
        if (activePlayer==p1) {
            activePlayer = p2;
        }
        else {
            activePlayer = p1;
        }
    }

    /**
     * is called when a set is over. reinitializes the board and the viusual representation of it.
     */
    private void endSet() {

        //acknowledge player of his victory
        //TODO: Change this to something that looks better
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText("Player " + activePlayer.getName() + " wins the turn");

        alert.showAndWait();

        try {
            //reinitialize the board
            board.reset();
            for (ImageView chip: chipArray) {
                field.getChildren().remove(chip);
            }
            chipArray = new ArrayList<>();

            for (ImageView winCircle: winCircleArray) {
                root.getChildren().remove(winCircle);
            }

            for (Button column: buttonArray) {
                column.setVisible(true);
            }

            SessionVars.initializeNewSet(!SessionVars.weStartSet);

        }
        catch (Exception e) {
            e.printStackTrace();
        }


    }


}

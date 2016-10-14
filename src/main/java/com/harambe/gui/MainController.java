package com.harambe.gui;

import com.harambe.App;
import com.harambe.algorithm.MiniMax;
import com.harambe.communication.ServerCommunication;
import com.harambe.communication.communicator.FileCommunicator;
import com.harambe.database.model.GameModel;
import com.harambe.database.model.SetModel;
import com.harambe.database.model.TurnModel;
import com.harambe.game.Board;
import com.harambe.game.SessionVars;
import com.harambe.tools.Logger;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.CacheHint;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static com.harambe.App.db;

/**
 * Controller of the main game. Contains mostly the chip dropping and logic behind the visual representation of the board.
 */
public class MainController implements Initializable, ControlledScreen {

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
    private static Text _player1Score;
    @FXML
    private static Text _player2Score;
    @FXML
    private StackPane field;
    @FXML
    private StackPane bg;
    @FXML
    private Text time;
    @FXML
    private ImageView asset1;
    @FXML
    private ImageView asset2;
    @FXML
    private ImageView bgAnim;

    @FXML
    private Button b0;
    @FXML
    private Button b1;
    @FXML
    private Button b2;
    @FXML
    private Button b3;
    @FXML
    private Button b4;
    @FXML
    private Button b5;
    @FXML
    private Button b6;

    private ImageView previewImg;

    //other variables
    private Board board;
    private int[] freeSpace;
    public static Player p1;
    public static Player p2;
    private static Player activePlayer;
    public static Player ourPlayer; // reference whether we are p1 or p2
    public static Player opponentPlayer;
    private Image winCircleImg;
    private ArrayList<ImageView> chipArray;
    private ArrayList<ImageView> winCircleArray;
    private int[][] winLocation;
    private MiniMax miniMax;

    public static boolean setDone = false; // marks a set as done for the server-comm thread
    private static boolean gameDone = false; // marks a game as done for the server-comm thread

    MasterController myController;

    public void setScreenParent(MasterController screenParent){
        myController = screenParent;
    }

    /**
     * initialization method. Is being called when FXMLLoader loads main.fxml.
     * @param fxmlFileLocation location of the fxml file.
     */
    @Override //
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {

        board = new Board();

        Stage stage = new Stage("coast_2");
        bg.setStyle("-fx-background-image: url('" + stage.getImg() + "'); ");

        //init extra images
        Image asset1Img = new Image(stage.getRandomAssetImg());
        asset1.setImage(asset1Img);
        asset1.setScaleX(-1);

        Image asset2Img = new Image(stage.getRandomAssetImg());
        asset2.setImage(asset2Img);



        playBgAnimation(stage);
        timerStart();

        chipArray = new ArrayList<>();
        winLocation = null;
        //get chip placement columns
        freeSpace = board.getFirstAvailableRow();

        _player1Score = player1Score;
        _player2Score = player2Score;


        if(SessionVars.usePusherInterface || SessionVars.useFileInterface) {
            // we play "online"
            if (SessionVars.ourSymbol == 'X') {
                // we are 'X', so right side on the UI
                p2 = new Player(false, System.getProperty("user.name"), "harambe", Board.PLAYER1);
                p1 = new Player(false, "Player2", "poacher_2", Board.PLAYER2);
                player2Name.setStyle("-fx-fill: green");
                player1Name.setStyle("-fx-fill: red");
                ourPlayer = p2; // keep track who we are :)
                opponentPlayer = p1;
                miniMax = new MiniMax(ourPlayer.getSymbol(), SessionVars.timeoutThresholdInMillis);
                Logger.debug("Minimax instantiate for " + ourPlayer.getSymbol());
                if (SessionVars.useFileInterface) {
                    App.sC = new FileCommunicator(SessionVars.fileInterfacePath, false);
                } else if (SessionVars.usePusherInterface) {
                    // TODO when done instantiate here
//                App.sC = new PusherCommunicator();
                }
                SessionVars.initializeNewGame(p2.getName(), p1.getName());
            } else {
                // we are 'O', so left side on the UI
                p1 = new Player(false, System.getProperty("user.name"), "harambe", Board.PLAYER2);
                p2 = new Player(false, "Player2", "poacher_2", Board.PLAYER1);
                player1Name.setStyle("-fx-fill: green");
                player2Name.setStyle("-fx-fill: red");
                ourPlayer = p1; // keep track who we are :)
                opponentPlayer = p2;
                miniMax = new MiniMax(ourPlayer.getSymbol(), SessionVars.timeoutThresholdInMillis);
                Logger.debug("Minimax instantiate for " + ourPlayer.getSymbol());
                if (SessionVars.useFileInterface) {
                    App.sC = new FileCommunicator(SessionVars.fileInterfacePath, true);
                } else if (SessionVars.usePusherInterface) {
                    // TODO when done instantiate here
//                App.sC = new PusherCommunicator();
                }
                SessionVars.initializeNewGame(p1.getName(), p2.getName());
            }
        } else {
            // we play offline
            p1 = new Player(false, System.getProperty("user.name"), "harambe", Board.PLAYER1);
            p2 = new Player(false, "Player2", "poacher_2", Board.PLAYER2);
            SessionVars.initializeNewGame(p1.getName(), p2.getName());
            if (Math.round(Math.random())==1) {
                activePlayer = p1;
                SessionVars.initializeNewSet(true);

            } else {
                activePlayer = p2;
                SessionVars.initializeNewSet(false);
            }
            ourPlayer = p1;
            opponentPlayer = p2;
        }


        winCircleImg = new Image(getClass().getClassLoader().getResourceAsStream(("img/wincircle.png")));


        initPlayers(p1, p2);

        if(SessionVars.usePusherInterface || SessionVars.useFileInterface) {
            // disable user input
            disableAllButtons(true);
            // we do not play offline, so run the server communication thread
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!gameDone) {
                        try {
                            playSet(App.sC);
                            setDone = false;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.setDaemon(true);
            thread.start();
        } else if (SessionVars.soloVsAI) {
            miniMax = new MiniMax(opponentPlayer.getSymbol(), SessionVars.timeoutThresholdInMillis);
            if(activePlayer != ourPlayer) {
                // AI starts, so first turn is AI
                // initialize MiniMax in offline mode
                Logger.debug("Minimax instantiate for " + opponentPlayer.getSymbol());
                long start = System.nanoTime();
                fireButton(miniMax.getBestMove(board));
                Logger.debug("Took: " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime()-start) + " ms");
            }
        }

    }

    /**
     * plays bg animation
     * @param stage
     */
    private void playBgAnimation(Stage stage) {
        Image bgAnimImg = new Image(getClass().getClassLoader().getResourceAsStream((stage.getBgAnimImg())));

        bgAnim.setImage(bgAnimImg);
        bgAnim.setCache(true);
        bgAnim.setCacheHint(CacheHint.SPEED);
        bgAnim.setPreserveRatio(true);
        TranslateTransition trans = new TranslateTransition();
        trans.setNode(bgAnim);
        trans.setDuration(new Duration(TimeUnit.SECONDS.toMillis(20)));
        trans.setByX(4000);
        trans.setByY(-500);
        trans.setOnFinished(event -> {
            bgAnim.setScaleX(bgAnim.getScaleX() * -1);
            bgAnim.setFitHeight(ThreadLocalRandom.current().nextDouble(10, 300));
            if (trans.getByX() > 0) {
                // now to the left
                trans.setByX(trans.getByX() * -1 - 1000);
            } else {
                trans.setByX(4000);
            }
            trans.setByY(trans.getByY() * -1);
            trans.play();
        });
        trans.play();
    }


    /**
     * Plays the set AI driven
     * @param sC a way to reach the server
     * @throws Exception
     */
    private void playSet(ServerCommunication sC) throws Exception {
        boolean flag = false; // marks the first run of the while loop (for setting start player)
        if (ourPlayer == p1) { // assume that the enemy starts as default, until proven otherwise below
            activePlayer = p2;
        } else {
            activePlayer = p1;
        }
            while(!setDone) {
                int col = sC.getTurnFromServer();
                long start = System.nanoTime();
                if(col == -2) {
                    // indicates that set has ended, and not that we have to start (server sends -1 in both cases :/
                    // so just get the next turn
                    continue;
                }
                if(col == -1) {
                    // we start
                    flag = true;
                    activePlayer = ourPlayer;
                    SessionVars.initializeNewSet(true);
                    int column = 3; // first drop in the middle :)
                    final FutureTask<Boolean> query = new FutureTask<>(() -> {
                        dropForUs(sC, column);
                        Logger.debug("Took: " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime()-start) + " ms");
                        return true;
                    });
                    Platform.runLater(query); // drop logic can only be done in the UI Thread
                    query.get(); // wait for the logic top happen to avoid random magic
                    continue;
                }
                if(!flag) {
                    // first while run and we do not start, so the enemy does
                    SessionVars.initializeNewSet(false);
                    flag = true;
                }
                final FutureTask<Boolean> query = new FutureTask<>(() -> {
                    dropForEnemy(col);
                    return true;
                });
                Platform.runLater(query); // drop logic can only be done in the UI Thread
                query.get(); // wait for the logic top happen to avoid random magic

                int column = miniMax.getBestMove(board);
                final FutureTask<Boolean> query2 = new FutureTask<>(() -> {
                    dropForUs(App.sC, column);
                    Logger.debug("Took: " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime()-start) + " ms");
                    return true;
                });
                Platform.runLater(query2); // drop logic can only be done in the UI Thread
                query2.get(); // wait for the logic top happen to avoid random magic
        }
    }

    /**
     * used for "Online games"
     * @param column column where the enemy drops his chip (gotten from server)
     */
    private void dropForEnemy(int column) {
        if(!setDone) {
            fireDisabledButton(column);
        }
    }

    /**
     * used for "Online games"
     * @param sC a way to communicate with the server
     */
    private void dropForUs(ServerCommunication sC, int column) {
        if(!setDone) {
            try {
                sC.passTurnToServer(column);
            } catch (Exception e) {
               e.printStackTrace();
            }
            fireDisabledButton(column);
        }
    }

    /**
     * If no input from the user is required (online games ai vs ai), we can 'push' button that way (since we disabled them)
     * @param column where we want to drop a chip
     */
    private void fireDisabledButton(int column) {
        switch (column) {
            case 0:
                b0.setDisable(false);
                b0.fire();
                b0.setDisable(true);
                break;
            case 1:
                b1.setDisable(false);
                b1.fire();
                b1.setDisable(true);
                break;
            case 2:
                b2.setDisable(false);
                b2.fire();
                b2.setDisable(true);
                break;
            case 3:
                b3.setDisable(false);
                b3.fire();
                b3.setDisable(true);
                break;
            case 4:
                b4.setDisable(false);
                b4.fire();
                b4.setDisable(true);
                break;
            case 5:
                b5.setDisable(false);
                b5.fire();
                b5.setDisable(true);
                break;
            case 6:
                b6.setDisable(false);
                b6.fire();
                b6.setDisable(true);
                break;
        }
    }

    /**
     * For offline AI vs Human or Human vs Human games, we have no disabled buttons, so use this
     * @param column where we want to drop a chip
     */
    private void fireButton(int column) {
        switch (column) {
            case 0:
                b0.fire();
                break;
            case 1:
                b1.fire();
                break;
            case 2:
                b2.fire();
                break;
            case 3:
                b3.fire();
                break;
            case 4:
                b4.fire();
                break;
            case 5:
                b5.fire();
                break;
            case 6:
                b6.fire();
                break;
        }
    }

    /**
     * starts the timer of the game
     */
    private void timerStart() {
        Thread thread = new Thread(() -> {
            StringProperty timerString = new SimpleStringProperty();
            time.textProperty().bind(timerString);
            try {
                long start = System.nanoTime();
                while(true) {
                    timerString.set(String.format("%02d:%02d",
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

    /**
     * initializes player information (e.g. names, images, chips, etc)
     * @param p1
     * @param p2
     */
    private void initPlayers(Player p1, Player p2) {
        //load image in ImageViewContainer for player 1
        Image p1Img = new Image(getClass().getClassLoader().getResourceAsStream((p1.getImgLocation())));
        p1ImgView.setImage(p1Img);

        //load image in ImageViewContainer for player 2
        Image p2Img = new Image(getClass().getClassLoader().getResourceAsStream((p2.getImgLocation())));
        p2ImgView.setImage(p2Img);
        //mirror Image for player 2
        p2ImgView.setScaleX(-1);


        //init Names
        player1Name.setText(p1.getName() + " (" + p1.getSymbol() + ")");
        player2Name.setText(p2.getName() + " (" + p2.getSymbol() + ")");

        //init Player1 Chips
        Chip c1 = new Chip(p1.getChip());
        Image p1Chip = new Image(getClass().getClassLoader().getResourceAsStream((c1.getImg())));
        p1ChipView.setImage(p1Chip);
        p1ChipView.setFitWidth(64);
        p1ChipView.setPreserveRatio(true);
        p1ChipView.setSmooth(true);
        p1ChipView.setCache(true);

        //init Player2 Chips
        Chip c2 = new Chip(p2.getChip());
        Image p2Chip = new Image(getClass().getClassLoader().getResourceAsStream((c2.getImg())));
        p2ChipView.setImage(p2Chip);
        p2ChipView.setFitWidth(64);
        p2ChipView.setPreserveRatio(true);
        p2ChipView.setSmooth(true);
        p2ChipView.setCache(true);
    }

    @FXML
    private void previewChip(MouseEvent event) {
        Button btn = (Button) event.getSource();

        //spawn preview chip
        Chip chip = new Chip(activePlayer.getChip());
        Image chipImg = new Image(getClass().getClassLoader().getResourceAsStream((chip.getImg())));
        previewImg = new ImageView(chipImg);
        previewImg.setId("previewChip");
        previewImg.setStyle("-fx-opacity: .5");
        previewImg.setTranslateY(-500);
        previewImg.setTranslateX(btn.getTranslateX());
        bg.getChildren().add(previewImg);
    }

    @FXML
    private void erasePreviewChip(MouseEvent event) {
        bg.getChildren().remove(previewImg);
    }

    /**
     * onClick event for the buttons laying over the different columns.
     * contains most of the UI-logic.
     * @param event onclickEvent of the button to get the different column Positions to drop the chips.
     */
    @FXML
    private void dropChip(ActionEvent event) {
        //delete previewImg
        if(previewImg != null) {
            bg.getChildren().remove(previewImg);
        }

        Button btn = (Button) event.getSource();

        activePlayer.playDropSound();

        //get column
        int column = Integer.parseInt(btn.getId().substring(1));

        //get xPos
        double x = btn.getTranslateX();

        //spawn chip
        Chip chip = new Chip(activePlayer.getChip());
        Image chipImg = new Image(getClass().getClassLoader().getResourceAsStream((chip.getImg())));
        ImageView imgView = new ImageView(chipImg);

        //store chip img
        chipArray.add(imgView);

        moveChip(imgView, column, x);

        //drop chip in board array and disable row if full
        try {
            board.put(column, activePlayer.getSymbol());
        }
        catch (Exception e) {
            System.err.println("column full");
            btn.setDisable(true);
        }


        //check for fullBoard
        if (board.isFull(column)) {
            btn.setDisable(true);
        }

        //paint chip and move the layer to background
        field.getChildren().add(imgView);
        imgView.toBack();

        persistDrop(column);

        checkForWin();

        //end round
        switchPlayer();

        if(SessionVars.soloVsAI && activePlayer != ourPlayer) {
            // user is playing against AI, so his turn is followed by an AI turn
            disableAllButtons(true);
            long start = System.nanoTime();
            Thread thread = new Thread(() -> {
                int column1 = miniMax.getBestMove(board);
                Platform.runLater(() -> {
                    fireDisabledButton(column1);
                    Logger.debug("Took: " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime()-start) + " ms");
                    disableAllButtons(false);
                });
            });
            thread.start();
        }
    }


    /**
     * makes all buttons/ columns unclickable
     * @param disabled true = disabled/ false = enabled
     */
    private void disableAllButtons(boolean disabled) {
        b0.setDisable(disabled);b1.setDisable(disabled);b2.setDisable(disabled);b3.setDisable(disabled);b4.setDisable(disabled);b5.setDisable(disabled);b6.setDisable(disabled);
    }

    /**
     * persists drop in database
     * @param column
     */
    private void persistDrop(int column) {
        TurnModel turnModel = null;
        if (activePlayer == ourPlayer) {
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
     * checks for a potential winner. Ends the set and shows how he/she won.
     * Decides to end the set or the game.
     */
    private void checkForWin() {

        if ((winLocation = board.getWinForUI(activePlayer.getSymbol())) != null) {
            Logger.event(activePlayer.getName() + " wins");

            //increment score and change score
            if(!SessionVars.useFileInterface && !SessionVars.usePusherInterface) {
                // for online games the server decides who wins (e.g. on draws)
                // for online games the score redraw is handled in the Communicator
                activePlayer.incrementScore();
                player1Score.setText(String.valueOf(p1.getScore()));
                player2Score.setText(String.valueOf(p2.getScore()));


                SetModel setModel;

                if (activePlayer == ourPlayer) {
                    setModel = new SetModel(SessionVars.currentGameUUID.toString(), SessionVars.setNumber, SessionVars.weStartSet, true);
                    winAnim(p1ImgView);
                } else {
                    setModel = new SetModel(SessionVars.currentGameUUID.toString(), SessionVars.setNumber, SessionVars.weStartSet, false);
                    winAnim(p2ImgView);
                }

                try {
                    setModel.persistInDatabase(App.db);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }



            //setting variables for win position
            int x0 = -450;
            int step = 150;
            int y0 = -340;


            int rowPos = 0;
            int columnPos = 0;
            winCircleArray = new ArrayList<>();

            for (int[] aWinLocation : winLocation) {
                for (int counter = 0, i = 0; counter < aWinLocation.length; i = -i + 1, counter++) {
                    if (i == 0) {
                        rowPos = (y0 + (aWinLocation[counter] * step));
                    } else {
                        columnPos = (x0 + (aWinLocation[counter] * step));
                    }
                }
                ImageView winCircle = new ImageView(winCircleImg);
                winCircle.setTranslateY(rowPos);
                winCircle.setTranslateX(columnPos);

                winCircleArray.add(winCircle);
                bg.getChildren().add(winCircle);

            }

            //wait for circles to be drawn
            Thread thread = new Thread(() -> {
                try {
                    bg.setCursor(Cursor.WAIT);
                    Thread.sleep(2000);
                    bg.setCursor(Cursor.DEFAULT);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(() -> cleanBoardImages());
            });
            thread.start();

            if (p1.getScore() >= 2 || p2.getScore() >= 2 && (!SessionVars.useFileInterface && !SessionVars.usePusherInterface)) {
                endGame();
            } else {
                endSet();
            }

            //endGame or endSet for offline games


        } else if (board.isTerminalState()) {
            //should be a draw

            //increment score and change score
            if(!SessionVars.useFileInterface && !SessionVars.usePusherInterface) {
                // for online games the score redraw is handled in the Communicator
                player1Score.setText(String.valueOf(p1.getScore()));
                player2Score.setText(String.valueOf(p2.getScore()));

                SetModel setModel;
                // a draw isnt a win
                setModel = new SetModel(SessionVars.currentGameUUID.toString(), SessionVars.setNumber, SessionVars.weStartSet, false);
                try {
                    setModel.persistInDatabase(App.db);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                if (p1.getScore() >= 2 || p2.getScore() >= 2 && (!SessionVars.useFileInterface && !SessionVars.usePusherInterface)) {
                    endGame();
                } else {
                    endSet();
                }

                // TODO do something nicer here
                Logger.event("a draw");
            }

            Platform.runLater(() -> cleanBoardImages());
        }

    }

    // TODO maybe rename
    private void cleanBoardImages() {
        for (ImageView chip: chipArray) {
            field.getChildren().remove(chip);
        }


        if(winCircleArray != null) {
            for (ImageView winCircle : winCircleArray) {
                bg.getChildren().remove(winCircle);
            }
        }

        chipArray = new ArrayList<>();

        // end set disables all the button, if needed we enable them here again
        if(!(SessionVars.usePusherInterface || SessionVars.useFileInterface || (activePlayer != ourPlayer && SessionVars.soloVsAI))) {
            disableAllButtons(false);
        }
    }


    /**
     * transition to let the playerImage jump up and down
     * @param imgView
     */
    private void winAnim(ImageView imgView) {
        activePlayer.playWinSound();

        TranslateTransition trans = new TranslateTransition();
        trans.setNode(imgView);
        trans.setDuration(new Duration(100));
        trans.setByY(-100);
        trans.setCycleCount(4);
        trans.setAutoReverse(true);
        trans.play();
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
     * is called when a set is over. Reinitializes the board and the visual representation of it.
     */
    private void endSet() {
        setDone = true;

        try {
            //reinitialize the board
            board.reset();

            disableAllButtons(true);

            if(!SessionVars.useFileInterface && !SessionVars.usePusherInterface) {
                SessionVars.initializeNewSet(!SessionVars.weStartSet); // in offline game we have to initialize a new set here
                // online games do it in the playSet method, since there is decided who starts
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * is called when a game is over. Closes the scene.
     */
    public static void endGame() {
        gameDone = true;
        //acknowledge player of his victory
        boolean weWon = false;
        if (ourPlayer.getScore() >= 2) {
            weWon = true;
        }

        GameModel gameModel = new GameModel(SessionVars.currentGameUUID.toString(), SessionVars.ourPlayerName, SessionVars.opponentPlayerName, ourPlayer.getScore(), opponentPlayer.getScore(), weWon);
        try {
            gameModel.persistInDatabase(db);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //TODO: Change this to something that looks better
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText("Player " + activePlayer.getName() + " wins the game");

        alert.showAndWait();

        Platform.exit();
    }

    public static void redrawScore() {
        _player1Score.setText(String.valueOf(p1.getScore()));
        _player2Score.setText(String.valueOf(p2.getScore()));
    }
}
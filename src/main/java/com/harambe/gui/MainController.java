package com.harambe.gui;

import com.harambe.App;
import com.harambe.algorithm.MiniMax;
import com.harambe.communication.ServerCommunication;
import com.harambe.communication.communicator.FileCommunicator;
import com.harambe.communication.communicator.PusherCommunicator;
import com.harambe.database.model.CharacterModel;
import com.harambe.database.model.GameModel;
import com.harambe.database.model.SetModel;
import com.harambe.database.model.TurnModel;
import com.harambe.game.Board;
import com.harambe.game.SessionVars;
import com.harambe.game.ThreadManager;
import com.harambe.tools.I18N;
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
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.net.URL;
import java.sql.SQLException;
import java.text.MessageFormat;
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
    private ImageView backBtn;

    @FXML
    private ToolBar replayToolbar;
    @FXML
    private Button replayTurnBack;
    @FXML
    private Button replayTurnForward;
    @FXML
    private Button replayStart;
    @FXML
    private Button replayStop;


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
    public Player p1;
    public Player p2;
    private Player activePlayer;
    public Player ourPlayer; // reference whether we are p1 or p2
    public Player opponentPlayer;
    private Image winCircleImg;
    private ArrayList<ImageView> chipArray;
    private ArrayList<ImageView> winCircleArray;
    private int[][] winLocation;
    private MiniMax miniMax;
    private Stage stage;

    //replay control stuff
    private boolean replayRunning;

    public boolean setDone = false; // marks a set as done for the server-comm thread
    boolean gameDone = false; // marks a game as done for the server-comm thread

    MasterController myController;

    static String p1Character = Character.characters[0];
    static String p2Character = Character.characters[1];

    public void setScreenParent(MasterController screenParent){
        myController = screenParent;
    }

    /**
     * initialization method. Is being called when FXMLLoader loads main.fxml.
     * @param fxmlFileLocation location of the fxml file.
     */
    @Override //
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        if (I18N.currentLang.equals(I18N.GERMAN)) {
            backBtn.getStyleClass().add("zurueckBtn");
        } else {
            backBtn.getStyleClass().add("backBtn");
        }
        Logger.debug("Max: " + SessionVars.timeoutThresholdInMillis);
        setDone = false; // reset the setDone flag when the screen is loaded a second time
        gameDone = false; // same here
        board = new Board();

        stage = new Stage();
        bg.setStyle("-fx-background-image: url('" + stage.getImg() + "'); ");

        //Performance settings
        if (!SessionVars.performanceMode) {
            //take static music player and play mainTheme
            App.themePlayer.playTheme(stage.getAudioLocation());

            //init extra images
            Image asset1Img = new Image(stage.getRandomAssetImg());
            asset1.setImage(asset1Img);
            asset1.setScaleX(-1);

            Image asset2Img = new Image(stage.getRandomAssetImg());
            asset2.setImage(asset2Img);


            playBgAnimation(stage);
            timerStart();
        } else {
            //disable chip preview images
            b0.setOnMouseEntered(null);
            b1.setOnMouseEntered(null);
            b2.setOnMouseEntered(null);
            b3.setOnMouseEntered(null);
            b4.setOnMouseEntered(null);
            b5.setOnMouseEntered(null);
            b6.setOnMouseEntered(null);

            //disable timer (so it doesn't show 0:00 all the time)
            time.setDisable(true);
        }

        chipArray = new ArrayList<>();
        winLocation = null;
        //get chip placement columns
        freeSpace = board.getFirstAvailableRow();
        winCircleImg = new Image(getClass().getResourceAsStream("/img/wincircle.png"));

        if (SessionVars.getUsePusherInterface() || SessionVars.getUseFileInterface()) {
            initOnlineGame();
        } else if (SessionVars.getReplayMode()) {
            initReplayGame();
        } else {
            initOfflineGame();
        }

        Logger.event("Create game " + SessionVars.currentGameUUID);
        initPlayerVisuals(p1, p2);

        if (SessionVars.getUsePusherInterface() || SessionVars.getUseFileInterface()) {
            playOnlineGame();
        } else if (SessionVars.getSoloVsAI()) {
            playVsAIGame();
        } else if (SessionVars.getReplayMode()) {
            playReplayGame();
        }
    }

    private void initOfflineGame() {
        // we play offline
        p1 = new Player(false, SessionVars.ourPlayerName, p1Character, Board.PLAYER1);
        if (SessionVars.getSoloVsAI()) {
            p2 = new Player(true, SessionVars.opponentPlayerName, p2Character, Board.PLAYER2);
        } else {
            p2 = new Player(false, SessionVars.opponentPlayerName, p2Character, Board.PLAYER2);
        }

        SessionVars.initializeNewGame(p1.getName(), p2.getName());
        if (Math.round(Math.random()) == 1) {
            activePlayer = p1;
            SessionVars.initializeNewSet(true);

        } else {
            activePlayer = p2;
            SessionVars.initializeNewSet(false);
        }
        ourPlayer = p1;
        opponentPlayer = p2;

        CharacterModel characterModel = new CharacterModel(SessionVars.currentGameUUID, p1Character, p2Character);
        try {
            characterModel.persistInDatabase(App.db);
        } catch (SQLException e) {
            Logger.error("Could not persist characterModel");
        }
    }

    private void initReplayGame() {
        // should be a replay
        replayToolbar.setVisible(true);
        Logger.debug("ReplayID: " + SessionVars.currentGameUUID);
        Logger.debug("ReplaySet: " + SessionVars.setNumber);
        try {
            CharacterModel characterModel = CharacterModel.getCharacter(App.db, SessionVars.currentGameUUID);
            p1Character = characterModel.getOurCharacter();
            p2Character = characterModel.getOpponentCharacter();
        } catch (SQLException e) {
            Logger.debug("Couldn't load characters for game " + SessionVars.currentGameUUID);
        }
        p1 = new Player(false, SessionVars.ourPlayerName, p1Character, Board.PLAYER1);
        p2 = new Player(false, SessionVars.opponentPlayerName, p2Character, Board.PLAYER2);
        if (SessionVars.weStartSet) {
            activePlayer = p1;
        } else {
            activePlayer = p2;
        }

        replayStop.setOnAction(e -> {
            replayRunning = false;
            replayStart.setDisable(false);
            replayStop.setDisable(true);
        });
        replayStart.setOnAction(e -> {
            replayRunning = true;
            replayStart.setDisable(true);
            replayStop.setDisable(false);
        });
        replayRunning = true;
        replayStart.setDisable(true);

        // TODO implement or just remove them
        replayTurnForward.setDisable(true);
        replayTurnBack.setDisable(true);
    }

    private void initOnlineGame() {
        // we play "online"
        if (SessionVars.ourSymbol == 'X') {
            // we are 'X', so right side on the UI
            p2 = new Player(false, SessionVars.ourPlayerName, p1Character, Board.PLAYER2);
            p1 = new Player(false, SessionVars.opponentPlayerName, p2Character, Board.PLAYER1);
            player2Name.getStyleClass().add("playerNameGreen");
            player1Name.getStyleClass().add("playerNameRed");
            ourPlayer = p2; // keep track who we are :)
            opponentPlayer = p1;
            miniMax = new MiniMax(ourPlayer.getSymbol(), SessionVars.timeoutThresholdInMillis);
            Logger.debug("Minimax instantiated for " + ourPlayer.getSymbol());
            if (SessionVars.getUseFileInterface()) {
                App.sC = new FileCommunicator(SessionVars.getFileInterfacePath(), false, this);
            } else if (SessionVars.getUsePusherInterface()) {
                App.sC = new PusherCommunicator(this);
            }
            SessionVars.initializeNewGame(p2.getName(), p1.getName());
        } else {
            // we are 'O', so left side on the UI
            p1 = new Player(false, SessionVars.ourPlayerName, p1Character, Board.PLAYER1);
            p2 = new Player(false, SessionVars.opponentPlayerName, p2Character, Board.PLAYER2);
            player1Name.getStyleClass().add("playerNameGreen");
            player2Name.getStyleClass().add("playerNameRed");
            ourPlayer = p1; // keep track who we are :)
            opponentPlayer = p2;
            miniMax = new MiniMax(ourPlayer.getSymbol(), SessionVars.timeoutThresholdInMillis);
            Logger.debug("Minimax instantiated for " + ourPlayer.getSymbol());
            if (SessionVars.getUseFileInterface()) {
                App.sC = new FileCommunicator(SessionVars.getFileInterfacePath(), true, this);
            } else if (SessionVars.getUsePusherInterface()) {
                App.sC = new PusherCommunicator(this);
            }
            SessionVars.initializeNewGame(p1.getName(), p2.getName());
        }
        CharacterModel characterModel = new CharacterModel(SessionVars.currentGameUUID, p1Character, p2Character);
        try {
            characterModel.persistInDatabase(App.db);
        } catch (SQLException e) {
            Logger.error("Could not persist characterModel");
        }
    }

    private void playReplayGame() {
        // disable user input
        disableAllButtons(true);
        Thread thread = new Thread(() -> {
            ArrayList<TurnModel> turns = null;
            try {
                turns = TurnModel.getTurns(App.db, SessionVars.currentGameUUID, SessionVars.setNumber);
                disableAllButtons(true);
                Thread.sleep(1000);

                for (int turnNumber = 0; turnNumber < turns.size(); ) {
                    if (replayRunning) {
                        final int col = turns.get(turnNumber).getColumn();
                        Platform.runLater(() -> fireDisabledButton(col));
                        turnNumber++;
                        Thread.sleep(1000);
                    } else {
                        Thread.sleep(1000);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        ThreadManager.threads.add(thread);
        thread.setDaemon(true);
        thread.start();
    }

    private void playVsAIGame() {
        miniMax = new MiniMax(opponentPlayer.getSymbol(), SessionVars.timeoutThresholdInMillis);
        if (activePlayer != ourPlayer) {
            // AI starts, so first turn is AI
            // initialize MiniMax in offline mode
            Logger.debug("Minimax instantiated for " + opponentPlayer.getSymbol());
            long start = System.nanoTime();
            fireButton(miniMax.getBestMove(board));
            Logger.debug("Took: " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start) + " ms");
        }
    }

    private void playOnlineGame() {
        // disable user input
        disableAllButtons(true);
        // we do not play offline, so run the server communication thread
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!gameDone) {
                    try {
                        playOnlineSet(App.sC);
                        setDone = false;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        ThreadManager.threads.add(thread);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Plays the repeating background animation from left to right and right to left
     * @param stage
     */
    private void playBgAnimation(Stage stage) {
        Image bgAnimImg = new Image(getClass().getResourceAsStream(stage.getBgAnimImg()));

        bgAnim.setImage(bgAnimImg);
        bgAnim.setCache(true);
        bgAnim.setCacheHint(CacheHint.SPEED);
        bgAnim.setPreserveRatio(true);
        TranslateTransition trans = new TranslateTransition();
        trans.setNode(bgAnim);
        trans.setDuration(new Duration(TimeUnit.SECONDS.toMillis(20)));
        trans.setByX(3500);
        trans.setByY(-500);
        trans.setOnFinished(event -> {
            bgAnim.setScaleX(bgAnim.getScaleX() * -1);
            bgAnim.setFitHeight(ThreadLocalRandom.current().nextDouble(10, 300));
            if (trans.getByX() > 0) {
                // now to the left
                trans.setByX(trans.getByX() * -1);
            } else {
                trans.setByX(trans.getByX() * -1);
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
    private void playOnlineSet(ServerCommunication sC) throws Exception {
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
        ThreadManager.threads.add(thread);
        thread.start();
    }

    /**
     * initializes player visuals (e.g.images, chips, etc)
     * @param p1 Left side Player
     * @param p2 right side Player
     */
    private void initPlayerVisuals(Player p1, Player p2) {

        //load image in ImageViewContainer for player 1
        Image p1Img = new Image(getClass().getResourceAsStream(p1.getImgLocation()));
        p1ImgView.setImage(p1Img);

        //load image in ImageViewContainer for player 2
        Image p2Img = new Image(getClass().getResourceAsStream(p2.getImgLocation()));
        p2ImgView.setImage(p2Img);

        //init Names
        player1Name.setText(p1.getName());
        player2Name.setText(p2.getName());

        //adaptive font size
        changePlayerFontSize(player1Name);
        changePlayerFontSize(player2Name);

        //init Player1 Chips
        Image p1Chip = new Image(getClass().getResourceAsStream(p1.getChipImgLocation()));
        p1ChipView.setImage(p1Chip);
        p1ChipView.setFitWidth(64);
        p1ChipView.setPreserveRatio(true);
        p1ChipView.setSmooth(true);
        p1ChipView.setCache(true);

        //init Player2 Chips
        Image p2Chip = new Image(getClass().getResourceAsStream(p2.getChipImgLocation()));
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
        Image chipImg = new Image(getClass().getResourceAsStream(activePlayer.getChipImgLocation()));
        previewImg = new ImageView(chipImg);
        previewImg.setId("previewChip");
        previewImg.setStyle("-fx-opacity: .5");
        previewImg.setTranslateY(-500);
        previewImg.setTranslateX(btn.getTranslateX());
        previewImg.setPreserveRatio(true);
        previewImg.setFitWidth(140);
        previewImg.setFitHeight(150);
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


        //get column
        int column = Integer.parseInt(btn.getId().substring(1));

        //get xPos
        double x = btn.getTranslateX();

        //spawn chip
        Image chipImg = new Image(getClass().getResourceAsStream(activePlayer.getChipImgLocation()));
        ImageView imgView = new ImageView(chipImg);
        imgView.setPreserveRatio(true);
        imgView.setFitWidth(140);
        imgView.setFitHeight(150);

        //store chip img
        chipArray.add(imgView);

        if(!SessionVars.performanceMode) {
            moveChipAnim(imgView, column, x);
            activePlayer.playDropSound();
        } else {
            moveChip(imgView, column, x);
        }

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

        if(SessionVars.getSoloVsAI() && activePlayer != ourPlayer && !setDone) {
            // user is playing against AI, so his turn is followed by an AI turn
            dropForAI();
        }
    }

    private void dropForAI() {
        disableAllButtons(true);
        long start = System.nanoTime();
        Thread thread = new Thread(() -> {
            int column1 = miniMax.getBestMove(board);
            Platform.runLater(() -> {
                fireDisabledButton(column1);
                Logger.debug("Took: " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime()-start) + " ms");
                if(!setDone) {
                    // only renable buttons if the new set has started
                    disableAllButtons(false);
                }
            });
        });
        thread.start();
    }


    /**
     * makes all buttons/ columns unclickable
     * @param disabled true = disabled/ false = enabled
     */
    private void disableAllButtons(boolean disabled) {
        if (disabled) {
            b0.setDisable(true);
            b1.setDisable(true);
            b2.setDisable(true);
            b3.setDisable(true);
            b4.setDisable(true);
            b5.setDisable(true);
            b6.setDisable(true);
        } else {
            if (freeSpace[0] >= 0 ) {
                b0.setDisable(false);
            }
            if (freeSpace[1] >= 0 ) {
                b1.setDisable(false);
            }
            if (freeSpace[2] >= 0 ) {
                b2.setDisable(false);
            }
            if (freeSpace[3] >= 0 ) {
                b3.setDisable(false);
            }
            if (freeSpace[4] >= 0 ) {
                b4.setDisable(false);
            }
            if (freeSpace[5] >= 0 ) {
                b5.setDisable(false);
            }
            if (freeSpace[6] >= 0 ) {
                b6.setDisable(false);
            }
        }
    }

    /**
     * persists drop in database
     * @param column Column in which the chip was dropped
     */
    private void persistDrop(int column) {
        TurnModel turnModel = null;
        if (activePlayer == ourPlayer) {
            turnModel = new TurnModel(SessionVars.currentGameUUID, SessionVars.setNumber, SessionVars.turnNumber, false, column);
        } else {
            turnModel = new TurnModel(SessionVars.currentGameUUID, SessionVars.setNumber, SessionVars.turnNumber, true, column);
        }

        try {
            if(!SessionVars.getReplayMode()) {
                turnModel.persistInDatabase(App.db);
            }
            SessionVars.turnNumber++;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * moves chip image to the board w/ an animation/ transition.
     * @param imgView image of the chip
     * @param column selected column to drop the chip
     * @param columnPos x position of the column
     */
    private void moveChipAnim(ImageView imgView, int column, double columnPos) {
        //chip drop transition/ animation
        TranslateTransition trans = new TranslateTransition();
        trans.setNode(imgView);
        if (SessionVars.timeoutThresholdInMillis >= 100) {
            trans.setDuration(new Duration(100));
        } else {
            trans.setDuration(new Duration(SessionVars.timeoutThresholdInMillis));
        }

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
     * moves chip image to the board w/o an animation/ transition.
     * @param imgView image of the chip
     * @param column selected column to drop the chip
     * @param columnPos x position of the column
     */
    private void moveChip(ImageView imgView, int column, double columnPos) {
        //move chip to clickLocation
        int startPos = -410;
        imgView.setTranslateY(startPos);
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
        imgView.setTranslateX(columnPos);
    }

    /**
     * checks for a potential winner. Ends the set and shows how he/she won.
     * Decides to end the set or the game.
     */
    private void checkForWin() {

        if ((winLocation = board.getWinForUI(activePlayer.getSymbol())) != null) {
            Logger.event(activePlayer.getName() + " wins");

            //increment score and change score
            if(!SessionVars.getUseFileInterface() && !SessionVars.getUsePusherInterface()) {
                // for online games the server decides who wins (e.g. on draws)
                // for online games the score redraw is handled in the Communicator
                activePlayer.incrementScore();
                player1Score.setText(String.valueOf(p1.getScore()));
                player2Score.setText(String.valueOf(p2.getScore()));


                SetModel setModel;

                if (activePlayer == ourPlayer) {
                    setModel = new SetModel(SessionVars.currentGameUUID, SessionVars.setNumber, SessionVars.weStartSet, true);
                    winAnim(p1ImgView);
                } else {
                    setModel = new SetModel(SessionVars.currentGameUUID, SessionVars.setNumber, SessionVars.weStartSet, false);
                    winAnim(p2ImgView);
                }

                try {
                    if(!SessionVars.getReplayMode()) {
                        setModel.persistInDatabase(App.db);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                // trigger online win animation
                if (activePlayer == p1) {
                    winAnim(p1ImgView);
                } else {
                    winAnim(p2ImgView);
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
                Platform.runLater(this::cleanBoardImages);
            });
            thread.start();

            endGameReplayOrSet();

            //endGame or endSet for offline games


        } else if (board.isTerminalState()) {
            //should be a draw

            //increment score and change score
            if(!SessionVars.getUseFileInterface() && !SessionVars.getUsePusherInterface()) {
                // for online games the score redraw is handled in the Communicator
                player1Score.setText(String.valueOf(p1.getScore()));
                player2Score.setText(String.valueOf(p2.getScore()));

                SetModel setModel;
                // a draw is a null for our boolean value
                setModel = new SetModel(SessionVars.currentGameUUID, SessionVars.setNumber, SessionVars.weStartSet, null);
                try {
                    if(!SessionVars.getReplayMode()) {
                        setModel.persistInDatabase(App.db);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                endGameReplayOrSet();

                Logger.event("a draw");
            }

            Platform.runLater(this::cleanBoardImages);
        } else {
            // no win, continue playing
            // online: startPlayer after win is decided by the server
            // offline: startPlayer after win is the one who didn't start last time (= endSet methdd)
            switchPlayer();
        }

    }

    private void endGameReplayOrSet() {
        if (p1.getScore() >= 2 || p2.getScore() >= 2 && (!SessionVars.getUseFileInterface() && !SessionVars.getUsePusherInterface())) {
            endGame();
        } else if (SessionVars.getReplayMode()) {
            Thread thread = new Thread(() -> {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ignored) {
                }
                myController.loadScreen(App.MENU_SCREEN, App.MENU_SCREEN_FILE); //to start the main music again
                myController.loadAndSetScreen(App.REPLAY_SCREEN, App.REPLAY_SCREEN_FILE, false);
            });
            thread.start();
        } else {
            endSet();
        }
    }

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
        if(!(SessionVars.getUsePusherInterface() || SessionVars.getUseFileInterface() || (activePlayer != ourPlayer && SessionVars.getSoloVsAI()) || SessionVars.getReplayMode())) {
            disableAllButtons(false);
        }

        if(SessionVars.getSoloVsAI()) {
            // if ai starts, put next drop for AI
            if (activePlayer == opponentPlayer) {
                dropForAI();
            }
            // needed to verify that new moves can be done by the player
            setDone = false;
        }

        if (gameDone) {
            myController.loadAndSetScreen(App.MENU_SCREEN, App.MENU_SCREEN_FILE, true);
        }
    }


    /**
     * transition to let the playerImage jump up and down
     * @param imgView The imageview of the winning player (on which the animation is performed)
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

            if(!SessionVars.getUseFileInterface() && !SessionVars.getUsePusherInterface()) {
                // Set end means: the next set is started by the player who didnt start the last set
                // online: done in Communicator
                if (SessionVars.weStartSet) {
                    activePlayer = opponentPlayer;
                } else {
                    activePlayer = ourPlayer;
                }
                SessionVars.weStartSet = !SessionVars.weStartSet;
                SessionVars.initializeNewSet(SessionVars.weStartSet); // in offline game we have to initialize a new set here
                // online games do it in the playOnlineSet method, since there is decided who starts
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * is called when a game is over. Closes the scene.
     */
    public void endGame() {
        if (gameDone) {
            // this method is called twice with file interface (1st: enemies winning move, second: notifiny of win) (at least I think so)
            // thats why we avoid the second call (so no PK violations)
            return;
        }
        gameDone = true;
        //acknowledge player of his victory
        boolean weWon = false;
        if (ourPlayer.getScore() >= 2) {
            weWon = true;
        }

        GameModel gameModel = new GameModel(SessionVars.currentGameUUID, SessionVars.ourPlayerName, SessionVars.opponentPlayerName, ourPlayer.getScore(), opponentPlayer.getScore(), weWon);
        try {
            if(!SessionVars.getReplayMode()) {
                gameModel.persistInDatabase(db);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //TODO: Change this to something that looks better
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(App.stage);
        alert.setTitle(I18N.getString("information.dialog"));
        alert.setHeaderText(null);
        // do not trust active player at this point, after the last drop, active player is already set to the next one online
        if (p1.getScore() >= 2) {
            alert.setContentText(MessageFormat.format(I18N.getString("player.?.wins.the.game"), p1.getName()));
        } else if (p2.getScore() >= 2) {
            alert.setContentText(MessageFormat.format(I18N.getString("player.?.wins.the.game"), p2.getName()));
        }

        alert.show();
    }

    public void redrawScore() {
        player1Score.setText(String.valueOf(p1.getScore()));
        player2Score.setText(String.valueOf(p2.getScore()));
    }

    private void changePlayerFontSize(Text playerNameText) {
        int length = playerNameText.getText().length();
        if (length > 9 && length <= 10) {
            playerNameText.setStyle("-fx-font-size: 44");
        } else if (length > 10 && length <= 13) {
            playerNameText.setStyle("-fx-font-size: 37");
        } else if (length > 13 && length <= 15) {
            playerNameText.setStyle("-fx-font-size: 30");
        } else if (length > 15){
            playerNameText.setStyle("-fx-font-size: 23");
        }
    }
    /**
     * Switches back to the main menu
     */
    @FXML
    private void back() {
        myController.loadAndSetScreen(App.MENU_SCREEN, App.MENU_SCREEN_FILE, true);
    }

}
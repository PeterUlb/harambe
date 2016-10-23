package com.harambe.gui;

import com.harambe.App;
import com.harambe.game.SessionVars;
import com.harambe.tools.I18N;
import com.harambe.tools.Logger;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;


/**
 * Controller for the character selection screen
 */
public class CharacterSelectionController implements Initializable, ControlledScreen {


    @FXML
    private GridPane grid;
    @FXML
    private ImageView player1;
    @FXML
    private ImageView player2;
    @FXML
    private Button player1Remove;
    @FXML
    private Button player2Remove;
    @FXML
    private ImageView playBtn;
    @FXML
    private ImageView backBtn;
    @FXML
    private Text player1Text;
    @FXML
    private Text player2Text;
    @FXML
    private ImageView player1EditImg;
    @FXML
    private Text player1Name;
    @FXML
    private TextField player1NameEdit;
    @FXML
    private ImageView player2EditImg;
    @FXML
    private Text player2Name;
    @FXML
    private TextField player2NameEdit;
    @FXML
    private Spinner<Integer> turnTime;
    @FXML
    private Text turnTimeLabel;
    @FXML
    private ImageView aiHumanImg;


    private MasterController myController;
    private String player1Character;
    private String player2Character;
    private ArrayList<Rectangle> squareArray;
    private int columns;
    private int sliderHeight = 30;
    private String p1NameTemp;
    private String p2NameTemp;
    private Node p1ImgTemp;
    private Node p2ImgTemp;
    private boolean soloVsAI;
    private ColorAdjust blackout = new ColorAdjust();
    private ArrayList<MediaPlayer> antiGarbageCollection;

    /**
     * initialization method of the view. Initializes variables and sets the initial settings
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        antiGarbageCollection = new ArrayList<>();
        //initialize array
        squareArray = new ArrayList<>();

        playBtn.setDisable(true);

        //set effect for blacking out the player images
        blackout.setBrightness(-1.0);

        if (I18N.currentLang.equals(I18N.GERMAN)) {
            backBtn.getStyleClass().add("zurueckBtn");
            playBtn.getStyleClass().add("startBtn");
        } else {
            backBtn.getStyleClass().add("backBtn");
            playBtn.getStyleClass().add("playBtn");
        }

        //setup images (image size & number of playercolumns)
        final int characterSize = 150;
        columns = 9;
        createCharacterList(characterSize, columns);

        //init player default images & buttons
        if (!SessionVars.getUsePusherInterface() && !SessionVars.getUseFileInterface()) {
            player1Text.setText(I18N.getString("select.player.1"));
            player2Text.setText(I18N.getString("select.player.2"));
        } else {
            player1Text.setText(I18N.getString("select.our.player"));
            player2Text.setText(I18N.getString("select.opponent.player"));
            // 350 not 400 since text is longer
            player2Text.setTranslateX(350);
        }

        SpinnerValueFactory.IntegerSpinnerValueFactory x = (SpinnerValueFactory.IntegerSpinnerValueFactory) turnTime.getValueFactory();
        final int lowerBound = x.getMin();
        final int upperBound = x.getMax();
        turnTime.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() != KeyCode.LEFT && event.getCode() != KeyCode.RIGHT){
                turnTime.getEditor().setStyle("-fx-text-fill: black");
                String turnTimeText = turnTime.getEditor().getText();
                if (turnTimeText.length() > 0) {
                    try {
                        // workaround since the spinner value needs an enter input after manual value setting. So we get the editor value...
                        long turnTimeValue = Long.parseLong(turnTimeText);
                        if (turnTimeValue < 0) {
                            turnTime.getEditor().setStyle("-fx-text-fill: red");
                            throw new NumberFormatException();
                        }
                        if (turnTimeValue < lowerBound) {
                            turnTime.getEditor().setText(String.valueOf(x.getMin()));
                            turnTime.getEditor().setStyle("-fx-text-fill: red");
                            turnTime.getEditor().positionCaret(turnTime.getEditor().getText().length());
                        } else if (turnTimeValue > upperBound) {
                            turnTime.getEditor().setText(String.valueOf(x.getMax()));
                            turnTime.getEditor().setStyle("-fx-text-fill: red");
                            turnTime.getEditor().positionCaret(turnTime.getEditor().getText().length());
                        }
                    } catch (NumberFormatException e) {
                        turnTime.getEditor().setText(turnTimeText.replaceAll("[^\\d]", "")); // replace all non-numeric charracters
                        if (turnTime.getEditor().getText().length() == 0) {
                            turnTime.getEditor().setText("2000");
                            turnTime.getEditor().setStyle("-fx-text-fill: red");
                        }
                        turnTime.getEditor().positionCaret(turnTime.getEditor().getText().length());
                    }
                }
            }
        });

        turnTimeLabel.setText(I18N.getString("turn.time.colon"));
    }


    /**
     * plays the select sound of the selected character
     * @param character selected character
     */
    private void playSelectSound(String character) {
        URL resource = getClass().getResource("/characters/" + character + "/select.mp3");
        Media select = new Media(resource.toString());
        MediaPlayer player = new MediaPlayer(select);
        antiGarbageCollection.add(player);
        player.setOnEndOfMedia(() -> {
            player.dispose();
            antiGarbageCollection.remove(player);
        });
        player.play();
    }

    /**
     * dynamically creates a list of characters depending on Character Class
     * @param characterSize size of the character images
     * @param columns number of characters per row
     */
    private void createCharacterList(int characterSize, int columns) {
        Image characterBg = new Image(getClass().getResourceAsStream("/img/gradient_orange.png"));

        //create the list of characters dynamically
        for (int i = 0, column = 0, row = -1; i < Character.characters.length; i++, column++) {

            //increase row, if column is full
            if (i % columns == 0) {
                row++;
                column = 0;
            }

            //character background image
            ImageView characterBgImg= new ImageView(characterBg);
            characterBgImg.setFitHeight(characterSize - characterSize / 13);
            characterBgImg.setFitWidth(characterSize);
            characterBgImg.setSmooth(true);
            characterBgImg.setCache(true);


            //character image
            Image characterSrc = new Image(getClass().getResourceAsStream("/characters/" + Character.characters[i] + "/avatar.png"));
            ImageView characterImg= new ImageView(characterSrc);
            characterImg.setPreserveRatio(true);
            characterImg.setFitWidth(characterSize);
            characterImg.setFitHeight(characterSize - characterSize / 10);
            characterImg.getStyleClass().set(0, "charImage"); //set styleClass to get a unique identifier
            characterImg.setSmooth(true);
            characterImg.setCache(true);
            GridPane.setHalignment(characterImg, HPos.CENTER);
            GridPane.setValignment(characterImg, VPos.BOTTOM);
            characterImg.setTranslateY(-1);


            //selection square
            Rectangle square = new Rectangle(characterSize-1, characterSize - characterSize / 10, Color.TRANSPARENT);
            square.setStroke(Color.DARKRED);
            square.setStrokeWidth(characterSize / 20);
            square.setVisible(false);
            GridPane.setHalignment(square, HPos.CENTER);
            GridPane.setValignment(square, VPos.BOTTOM);
            square.getStyleClass().add("select");
            squareArray.add(square);


            //eventhandlers for every player
            square.addEventHandler(MouseEvent.MOUSE_CLICKED, clickHandler);
            characterImg.addEventHandler(MouseEvent.MOUSE_ENTERED, mouseOverHandler);
            characterBgImg.addEventHandler(MouseEvent.MOUSE_ENTERED, mouseOverHandler);
            square.addEventHandler(MouseEvent.MOUSE_EXITED, mouseOutHandler);

            //character name label
            Text t = new Text(Character.getLocalizedCharacterName(i));
            t.getStyleClass().add("characterText");
            
            int length = t.getText().length();
            if (length <= 10) {
                t.setStyle("-fx-font-size: 30");
            } else if (length > 10 && length <= 13) {
                t.setStyle("-fx-font-size: 25");
            } else if (length > 13 && length <= 15) {
                t.setStyle("-fx-font-size: 20");
            } else if (length > 15){
                t.setText(Character.getLocalizedCharacterName(i).substring(0, 13) + "...");
                t.setStyle("-fx-font-size: 20");
            }
            
            GridPane.setHalignment(t, HPos.CENTER);
            t.setTranslateY(characterSize / 2);

            //add all elements to the grid
            grid.add(characterBgImg, column, row);
            grid.add(characterImg, column, row);
            grid.add(square, column, row);
            grid.add(t, column, row);
        }
    }

    /**
     * event handler for the mouseover action of the selection square (visible)
     */
    private EventHandler<MouseEvent> mouseOverHandler = event -> {
        Node source = (Node)event.getSource();
        int colIndex = GridPane.getColumnIndex(source);
        int rowIndex = GridPane.getRowIndex(source);

        squareArray.get(colIndex+rowIndex*columns).setVisible(true);

        event.consume();
    };

    /**
     * event handler for the mouseout action of the selection square (invisible)
     */
    private EventHandler<MouseEvent> mouseOutHandler = event -> {
        Node source = (Node)event.getSource();
        int colIndex = GridPane.getColumnIndex(source);
        int rowIndex = GridPane.getRowIndex(source);

        squareArray.get(colIndex+rowIndex*columns).setVisible(false);

        event.consume();
    };


    /**
     * event handler for clicking on a character image to load it as a selected player
     */
    private EventHandler<MouseEvent> clickHandler = event -> {
        Node source = (Node)event.getSource() ;
        int colIndex = GridPane.getColumnIndex(source);
        int rowIndex = GridPane.getRowIndex(source);
        String selectedCharacter = Character.characters[colIndex+rowIndex*columns];

        event.consume();

        Image pImg = new Image(getClass().getResourceAsStream("/characters/" + selectedCharacter + "/avatar.png"));


        if (player1Text.isVisible() && !selectedCharacter.equals(player2Character)) {
            player1.setImage(pImg);
            player1Remove.setVisible(true);
            player1Remove.setDisable(false);
            player1Character = selectedCharacter;
            playSelectSound(selectedCharacter);
            player1Text.setVisible(false);

            //black out image
            for (Node node : grid.getChildren()) {
                if(GridPane.getRowIndex(node) == rowIndex && GridPane.getColumnIndex(node) == colIndex && node.getStyleClass().get(0).equals("charImage")) {
                    node.setEffect(blackout);
                    p1ImgTemp = node;
                    break;
                }
            }

            //edit Name
            player1EditImg.setVisible(true);
            player1Name.setVisible(true);
            player1Name.setText(Character.getLocalizedCharacterName(colIndex+rowIndex*columns));
            changeSelectionFontSize(player1Name);

            //save player name and player img for later usage
            p1NameTemp = player1Name.getText();
        } else {
            if (player2Text.isVisible() && !selectedCharacter.equals(player1Character)) {
                player2.setImage(pImg);
                player2Remove.setVisible(true);
                player2Remove.setDisable(false);
                player2Character = selectedCharacter;
                playSelectSound(selectedCharacter);
                player2Text.setVisible(false);

                //black out image
                for (Node node : grid.getChildren()) {
                    if(GridPane.getRowIndex(node) == rowIndex && GridPane.getColumnIndex(node) == colIndex && node.getStyleClass().get(0).equals("charImage")) {
                        node.setEffect(blackout);
                        p2ImgTemp = node;
                        break;
                    }
                }

                //edit Name
                player2EditImg.setVisible(true);
                player2Name.setVisible(true);
                player2Name.setText(Character.getLocalizedCharacterName(colIndex+rowIndex*columns));
                changeSelectionFontSize(player2Name);


                //enable ai switch
                aiHumanImg.setVisible(true);

                //save player name to string
                p2NameTemp = player2Name.getText();
            }
        }

        event.consume();
        //enable/ disable play button
        checkDisablePlayBtn();
    };

    /**
     * removes the player from the imgview so another one can take his place
     * @param event mouse click event of the X-imgview (close button)
     */
    @FXML
    private void deletePlayer(ActionEvent event) {
        Button source = (Button)event.getSource();

        //is it player 1 or 2 that should be deleted?
        if (source.getTranslateX() < 0) {
            //it is player 1
            player1Text.setVisible(true);
            player1.setImage(null);
            player1Character=null;

            //hide name edit
            player1EditImg.getStyleClass().set(1, "editImg");
            player1EditImg.setVisible(false);
            player1NameEdit.setVisible(false);
            player1Name.setVisible(false);

            //"unblack" player
            p1ImgTemp.setEffect(null);
        } else {
            //it is player 2
            player2Text.setVisible(true);
            player2.setImage(null);
            player2Character=null;

            //hide name edit
            player2EditImg.getStyleClass().set(1, "editImg");
            player2EditImg.setVisible(false);
            player2NameEdit.setVisible(false);
            player2Name.setVisible(false);

            //"unblack" player
            p2ImgTemp.setEffect(null);

            //move label/ edit/ editImg back to its place
            if (aiHumanImg.getStyleClass().get(1).equals("ai")) {
                player2NameEdit.setTranslateY(player2NameEdit.getTranslateY() + sliderHeight);
                player2Name.setTranslateY(player2Name.getTranslateY() + sliderHeight);
                player2EditImg.setTranslateY(player2EditImg.getTranslateY() + sliderHeight);
            }

            //hide turntime spinner + label, deactivate button
            aiHumanImg.setVisible(false);
            aiHumanImg.getStyleClass().set(1, "human");
            turnTime.setVisible(false);
            turnTimeLabel.setVisible(false);
        }
        source.setVisible(false);
        source.setDisable(true);
        event.consume();

        //enable/ disable play button
        checkDisablePlayBtn();
    }

    /**
     * enables/ disables the edit functionality of the playername
     * @param event mouse click event of the imgviews
     */
    @FXML
    private void editName(MouseEvent event) {
        Node source = (Node)event.getSource();

        if (source.getStyleClass().get(1).equals("editImg")) {
            //started editing
            source.getStyleClass().set(1, "editImgCheck");
            //check for player1/ player2, change visibility from editText->visible ,Text->invisible and copy over the values from Text->editText

            if (source.getId().equals(player1EditImg.getId())) {
                player1NameEdit.setVisible(true);
                player1Name.setVisible(false);
                player1NameEdit.setText(player1Name.getText());
            } else {
                player2NameEdit.setVisible(true);
                player2Name.setVisible(false);
                player2NameEdit.setText(player2Name.getText());
            }
        } else {
            //finished editing
            source.getStyleClass().set(1, "editImg");

            //check for player1/ player2, change visibility from editText->invisible ,Text->visible and copy over the values from editText->Text
            if (source.getId().equals(player1EditImg.getId())) {
                player1NameEdit.setVisible(false);
                player1Name.setVisible(true);
                player1Name.setText(player1NameEdit.getText());
            } else {
                player2NameEdit.setVisible(false);
                player2Name.setVisible(true);
                player2Name.setText(player2NameEdit.getText());
            }
        }

        event.consume();
    }

    /**
     * checks whether 2 characters are selected and enables/ disables the play button
     */
    private void checkDisablePlayBtn() {
        if (player1Character!=null && player2Character!=null) {
            playBtn.setDisable(false);
        } else {
            playBtn.setDisable(true);
        }
    }

    @FXML
    private void aiSwitch(MouseEvent event) {
        Node source = (Node)event.getSource();

        if (source.getStyleClass().get(1).equals("ai")) {
            //switch to human icon
            source.getStyleClass().set(1, "human");

            //set turntime spinner invisible
            turnTime.setVisible(false);
            turnTimeLabel.setVisible(false);

            //move p2NameLabel down
            player2NameEdit.setTranslateY(player2NameEdit.getTranslateY() + sliderHeight);
            player2Name.setTranslateY(player2Name.getTranslateY() + sliderHeight);
            player2EditImg.setTranslateY(player2EditImg.getTranslateY() + sliderHeight);

            //change name to add "[AI]"
            player2Name.setText(p2NameTemp);

            changeSelectionFontSize(player2Name);

        } else {
            //switch to ai icon
            source.getStyleClass().set(1, "ai");

            //set turntime spinner visible
            turnTime.setVisible(true);
            turnTimeLabel.setVisible(true);

            //move p2NameLabel up
            player2NameEdit.setTranslateY(player2NameEdit.getTranslateY() - sliderHeight);
            player2Name.setTranslateY(player2Name.getTranslateY() - sliderHeight);
            player2EditImg.setTranslateY(player2EditImg.getTranslateY() - sliderHeight);

            //change name to add "[AI]"
            player2Name.setText("[" + I18N.getString("ai") + "]" + p2NameTemp);

            changeSelectionFontSize(player2Name);

            //set vs ai
            soloVsAI = true;
        }

        event.consume();
    }

    private void changeSelectionFontSize(Text playername) {
        int length = playername.getText().length();
        if (length <= 9) {
            playername.setStyle("-fx-font-size: 50");
        } else if (length > 9 && length <= 10) {
            playername.setStyle("-fx-font-size: 44");
        } else if (length > 10 && length <= 13) {
            playername.setStyle("-fx-font-size: 37");
        } else if (length > 13 && length <= 15) {
            playername.setStyle("-fx-font-size: 30");
        } else if (length > 15){
            playername.setStyle("-fx-font-size: 23");
        }
    }


    public void setScreenParent(MasterController screenParent){
        myController = screenParent;
    }

    /**
     * sets variables for maincontroller and loads the fxml
     */
    @FXML
    private void play(MouseEvent event)/*throws IOException*/ {

        if (player1Character!=null && player2Character!=null) {

            //set player names & their corresponding characters
            SessionVars.ourPlayerName = player1Name.getText();
            MainController.p1Character = player1Character;
            SessionVars.opponentPlayerName = player2Name.getText();
            MainController.p2Character = player2Character;

            //set playmode
            if (soloVsAI) {
                SessionVars.soloVsAI(true);
            }
            //set turn time
            try {
                // workaround since the spinner value needs an enter input after manual value setting. So we get the editor value...
                long turnTimeValue = Long.parseLong(turnTime.getEditor().getText());
                SessionVars.timeoutThresholdInMillis = turnTimeValue;
            } catch (NumberFormatException e) {
                // this can happen when the user input is nothing, every other false possibility (too high, too low, non-numeric)
                // is handled by the key event handler attached to the spinner
                SessionVars.timeoutThresholdInMillis = 2000; // set default
            }

            myController.loadAndSetScreen(App.MAIN_SCREEN, App.MAIN_SCREEN_FILE, true);
        }

        event.consume();
    }

    @FXML
    private void back(MouseEvent event)/*throws IOException*/ {
        myController.loadAndSetScreen(App.MENU_SCREEN, App.MENU_SCREEN_FILE, false);

        event.consume();
    }



}
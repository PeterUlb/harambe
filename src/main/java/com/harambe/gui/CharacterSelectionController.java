package com.harambe.gui;

import com.harambe.App;
import com.harambe.game.SessionVars;
import com.harambe.tools.I18N;
import javafx.beans.property.Property;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;


/**
 * Controller for the character selection screen
 */
public class CharacterSelectionController implements Initializable, ControlledScreen {


    @FXML
    private StackPane bg;
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


    private MasterController myController;
    private String player1Character;
    private String player2Character;
    private ArrayList<Rectangle> squareArray;
    private int columns;


    /**
     * initialization method of the view. Initializes variables and sets the initial settings
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //initialize array
        squareArray = new ArrayList<>();

        playBtn.setDisable(true);

        if (I18N.currentLang.equals(I18N.GERMAN)) {
            backBtn.getStyleClass().add("zurueckBtn");
            playBtn.getStyleClass().add("startBtn");
        } else {
            backBtn.getStyleClass().add("backBtn");
            playBtn.getStyleClass().add("playBtn");
        }

        //setup images (image size & number of playercolumns)
        final int characterSize = 150;
        columns = 10;
        createCharacterList(characterSize, columns);

        //init player default images & buttons
        player1Text.setText(I18N.getString("select.player.1"));
        player2Text.setText(I18N.getString("select.player.2"));
    }


    /**
     * plays the select sound of the selected character
     * @param character selected character
     */
    private void playSelectSound(String character) {
        URL resource = getClass().getResource("/characters/" + character.toLowerCase() + "/select.mp3");
        Media select = new Media(resource.toString());
        MediaPlayer player = new MediaPlayer(select);
        player.play();
    }

    /**
     * dynamically creates a list of characters depending on Character Class
     * @param characterSize size of the character images
     * @param columns number of characters per row
     */
    private void createCharacterList(int characterSize, int columns) {
        Image characterBg = new Image(getClass().getClassLoader().getResourceAsStream("img/gradient_orange.png"));

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
            Image characterSrc = new Image(getClass().getClassLoader().getResourceAsStream("characters/" + Character.characters[i].toLowerCase() + "/avatar.png"));
            ImageView characterImg= new ImageView(characterSrc);
            characterImg.setPreserveRatio(true);
            characterImg.setFitWidth(characterSize);
            characterImg.setFitHeight(characterSize - characterSize / 10);
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


            Text t = new Text(Character.getLocalizedCharacterName(i));
            t.getStyleClass().add("characterText");
            GridPane.setHalignment(t, HPos.CENTER);
            t.setTranslateY(characterSize / 2);

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
    };

    /**
     * event handler for the mouseout action of the selection square (invisible)
     */
    private EventHandler<MouseEvent> mouseOutHandler = event -> {
        Node source = (Node)event.getSource();
        int colIndex = GridPane.getColumnIndex(source);
        int rowIndex = GridPane.getRowIndex(source);

        squareArray.get(colIndex+rowIndex*columns).setVisible(false);
    };


    /**
     * event handler for clicking on a character image
     */
    private EventHandler<MouseEvent> clickHandler = event -> {
        Node source = (Node)event.getSource() ;
        int colIndex = GridPane.getColumnIndex(source);
        int rowIndex = GridPane.getRowIndex(source);
        String selectedCharacter = Character.characters[colIndex+rowIndex*columns];

        event.consume();

        Image pImg = new Image(getClass().getClassLoader().getResourceAsStream("characters/" + selectedCharacter.toLowerCase() + "/avatar.png"));


        if (player1Text.isVisible() && !Objects.equals(selectedCharacter, player2Character)) {
            player1.setImage(pImg);
            player1Remove.setVisible(true);
            player1Remove.setDisable(false);
            player1Character = selectedCharacter;
            playSelectSound(selectedCharacter);
            player1Text.setVisible(false);

            //edit Name
            player1EditImg.setVisible(true);
            player1Name.setVisible(true);
            player1Name.setText(Character.getLocalizedCharacterName(colIndex+rowIndex*columns));
        } else {
            if (player2Text.isVisible() && !Objects.equals(selectedCharacter, player1Character)) {
                player2.setImage(pImg);
                player2Remove.setVisible(true);
                player2Remove.setDisable(false);
                player2Character = selectedCharacter;
                playSelectSound(selectedCharacter);
                player2Text.setVisible(false);

                //edit Name
                player2EditImg.setVisible(true);
                player2Name.setVisible(true);
                player2Name.setText(Character.getLocalizedCharacterName(colIndex+rowIndex*columns));
            }
        }

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

        if (source.getTranslateX() < 0) {
            player1Text.setVisible(true);
            player1.setImage(null);
            player1Character=null;

            //hide name edit
            player1EditImg.setVisible(false);
            player1EditImg.getStyleClass().set(1, "editImg");
            player1NameEdit.setVisible(false);
            player1Name.setVisible(false);

        } else {
            player2Text.setVisible(true);
            player2.setImage(null);
            player2Character=null;

            //hide name edit
            player2EditImg.setVisible(false);
            player2EditImg.getStyleClass().set(1, "editImg");
            player2NameEdit.setVisible(false);
            player2Name.setVisible(false);
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
            if (source.getTranslateX() < 0) {
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
            if (source.getTranslateX() < 0) {
                player1NameEdit.setVisible(false);
                player1Name.setVisible(true);
                player1Name.setText(player1NameEdit.getText());
            } else {
                player2NameEdit.setVisible(false);
                player2Name.setVisible(true);
                player2Name.setText(player2NameEdit.getText());
            }
        }
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


    public void setScreenParent(MasterController screenParent){
        myController = screenParent;
    }

    /**
     * sets variables for maincontroller and loads the fxml
     */
    @FXML
    private void play(MouseEvent event)/*throws IOException*/ {

        if (player1Character!=null && player2Character!=null) {

            SessionVars.ourPlayerName = player1Name.getText();
            MainController.p1Character = player1Character;
            SessionVars.opponentPlayerName = player2Name.getText();
            MainController.p2Character = player2Character;

            myController.loadAndSetScreen(App.MAIN_SCREEN, App.MAIN_SCREEN_FILE, true);
        }
    }

    @FXML
    private void back(MouseEvent event)/*throws IOException*/ {
        myController.loadAndSetScreen(App.MENU_SCREEN, App.MENU_SCREEN_FILE, false);

    }



}
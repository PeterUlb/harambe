package com.harambe.gui;

import com.harambe.App;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
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

import javax.xml.stream.events.Characters;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;


/**
 * TODO: insert documentation here
 */
public class characterSelectionController implements Initializable, ControlledScreen {


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
    private Button playBtn;

    private MasterController myController;
    private Image p1ImgDefault = new Image(("img/select_1.png"));
    private Image p2ImgDefault = new Image(("img/select_2.png"));
    private String player1Character;
    private String player2Character;
    private MediaPlayer themePlayer;
    private ArrayList<Rectangle> squareArray;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //initialize array
        squareArray = new ArrayList<>();

        playBtn.setDisable(true);

        //set & play menuTheme
        final URL resource = getClass().getResource("/audio/menuTheme.mp3");
        final Media theme = new Media(resource.toString());
        themePlayer = new MediaPlayer(theme);
        themePlayer.setVolume(0.1);
        themePlayer.play();

        //setup windows
        final int characterSize = 200;
        try {
            createCharacterList(characterSize);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //init player default images & buttons
        player1.setImage(p1ImgDefault);
        player2.setImage(p2ImgDefault);
    }



    private void playSelectSound(String character) {
        URL resource = getClass().getResource("/audio/"+character.toLowerCase()+"_select.mp3");
        Media select = new Media(resource.toString());
        MediaPlayer player = new MediaPlayer(select);
        player.play();
    }

    /**
     * dynamically creates a list of characters depending on Character Class
     */
    private void createCharacterList(int characterSize) throws Exception{
        Image characterBg = new Image("img/gradient_orange.png");

        //create the list of characters dynamically
        for (int i = 0; i < Character.characters.length; i++) {

            //character background image
            ImageView characterBgImg= new ImageView(characterBg);
            characterBgImg.setFitHeight(characterSize -15);
            characterBgImg.setFitWidth(characterSize);
            characterBgImg.setSmooth(true);
            characterBgImg.setCache(true);

            //character image
            Image characterSrc = new Image("img/"+Character.characters[i].toLowerCase()+".png");
            ImageView characterImg= new ImageView(characterSrc);
            characterImg.setPreserveRatio(true);
            characterImg.setFitWidth(characterSize);
            characterImg.setFitHeight(characterSize -20);
            characterImg.setSmooth(true);
            characterImg.setCache(true);
            GridPane.setHalignment(characterImg, HPos.CENTER);
            GridPane.setValignment(characterImg, VPos.BOTTOM);
            characterImg.setTranslateY(-1);


            //selection square
            Rectangle square = new Rectangle(200, 180, Color.TRANSPARENT);
            square.setStroke(Color.DARKRED);
            square.setStrokeWidth(10);
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


            Text t = new Text(Character.characters[i]);
            t.getStyleClass().add("characterText");
            GridPane.setHalignment(t, HPos.CENTER);
            t.setTranslateY(100);

            grid.add(characterBgImg, i, 0);
            grid.add(characterImg, i, 0);
            grid.add(square, i, 0);
            grid.add(t, i, 0);
        }
    }


    private EventHandler<MouseEvent> mouseOverHandler = event -> {
        Node source = (Node)event.getSource();
        int colIndex = GridPane.getColumnIndex(source);

        squareArray.get(colIndex).setVisible(true);
    };

    private EventHandler<MouseEvent> mouseOutHandler = event -> {
        Node source = (Node)event.getSource();
        int colIndex = GridPane.getColumnIndex(source);

        squareArray.get(colIndex).setVisible(false);
    };



        // Define an event handler for clicking on Character Image
    private EventHandler<MouseEvent> clickHandler = event -> {
        Node source = (Node)event.getSource() ;
        int colIndex = GridPane.getColumnIndex(source);
        int rowIndex = GridPane.getRowIndex(source);
        String selectedCharacter = Character.characters[colIndex];

        event.consume();

        Image pImg = new Image(("img/"+selectedCharacter.toLowerCase()+".png"));

        if (player1.getImage() == p1ImgDefault && !Objects.equals(selectedCharacter, player2Character)) {
            player1.setImage(pImg);
            player1Remove.setVisible(true);
            player1Remove.setDisable(false);
            player1Character = selectedCharacter;
            playSelectSound(selectedCharacter);
        } else {
            if (player2.getImage() == p2ImgDefault && !Objects.equals(selectedCharacter, player1Character)) {
                player2.setImage(pImg);
                player2Remove.setVisible(true);
                player2Remove.setDisable(false);
                player2Character = selectedCharacter;
                playSelectSound(selectedCharacter);
            }
        }

        //enable/ disable play button
        checkDisablePlayBtn();
    };

    @FXML
    private void deletePlayer(ActionEvent event) {
        Button source = (Button)event.getSource();
        if (source.getTranslateX() > 0) {
            player2.setImage(p2ImgDefault);
            player2Character=null;
        } else {
            player1.setImage(p1ImgDefault);
            player1Character=null;
        }
        source.setVisible(false);
        source.setDisable(true);
        event.consume();

        //enable/ disable play button
        checkDisablePlayBtn();
    }

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
    private void play(ActionEvent event)/*throws IOException*/ {

        if (player1Character!=null && player2Character!=null) {

            MainController.p1Name = "Player 1";
            MainController.p1Character = player1Character;
            MainController.p2Name = "Player 2";
            MainController.p2Character = player2Character;
            themePlayer.stop();

            myController.loadScreen(App.MAIN_SCREEN, App.MAIN_SCREEN_FILE);
            myController.setScreen(App.MAIN_SCREEN);
        }
    }

    @FXML
    private void back(ActionEvent event)/*throws IOException*/ {
        themePlayer.stop();

        myController.setScreen(App.MENU_SCREEN);
    }



}
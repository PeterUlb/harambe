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
import javafx.scene.text.Text;

import javax.xml.stream.events.Characters;
import java.net.URL;
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

    private MasterController myController;
    private Image p1ImgDefault = new Image(("img/select_1.png"));
    private Image p2ImgDefault = new Image(("img/select_2.png"));
    private String player1Character;
    private String player2Character;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        final int characterSize = 200;
        Image characterBg = new Image("img/gradient_orange.png");

        //init player default images & buttons
        player1.setImage(p1ImgDefault);
        player2.setImage(p2ImgDefault);


        //create the list of characters dynamically
        for (int i = 0; i < Character.characters.length; i++) {

            //character background image
            ImageView characterBgImg= new ImageView(characterBg);
            characterBgImg.setFitHeight(characterSize -15);
            characterBgImg.setFitWidth(characterSize);
            characterBgImg.setSmooth(true);
            characterBgImg.setCache(true);
            characterBgImg.getStyleClass().add("characterImg");

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
            characterImg.getStyleClass().add("characterImg");


            characterImg.addEventHandler(MouseEvent.MOUSE_CLICKED, clickHandler);
            characterBgImg.addEventHandler(MouseEvent.MOUSE_CLICKED, clickHandler);


            Text t = new Text(Character.characters[i]);
            t.getStyleClass().add("characterText");
            GridPane.setHalignment(t, HPos.CENTER);
            t.setTranslateY(100);

            grid.add(characterBgImg, i, 0);
            grid.add(characterImg, i, 0);
            grid.add(t, i, 0);
        }

    }

    // Define an event handler for clicking on Character Image
    private EventHandler<MouseEvent> clickHandler = event -> {
        Node source = (Node)event.getSource() ;
        Integer colIndex = GridPane.getColumnIndex(source);
        Integer rowIndex = GridPane.getRowIndex(source);
        event.consume();

        Image pImg = new Image(("img/"+Character.characters[colIndex].toLowerCase()+".png"));

        if (player1.getImage() == p1ImgDefault) {
            player1.setImage(pImg);
            player1Remove.setVisible(true);
            player1Remove.setDisable(false);
            player1Character = Character.characters[colIndex];
        } else {
            if (player2.getImage() == p2ImgDefault) {
                player2.setImage(pImg);
                player2Remove.setVisible(true);
                player2Remove.setDisable(false);
                player2Character = Character.characters[colIndex];
            } else {
                //TODO: display proper message
                System.out.println("second player already selected");
            }
        }

    };

    @FXML
    private void deletePlayer(ActionEvent event) {
        Button source = (Button)event.getSource();
        if (source.getTranslateX() > 0) {
            player2.setImage(p2ImgDefault);
        } else {
            player1.setImage(p1ImgDefault);
        }
        source.setVisible(false);
        source.setDisable(true);
        event.consume();
    }


    public void setScreenParent(MasterController screenParent){
        myController = screenParent;
    }

    //onClickEvent switch screen to game
    @FXML
    private void play(ActionEvent event)/*throws IOException*/ {
        MainController.p1Name = "Player 1";
        MainController.p1Character = player1Character;
        MainController.p2Name = "Player 2";
        MainController.p2Character = player2Character;

        myController.loadScreen(App.MAIN_SCREEN, App.MAIN_SCREEN_FILE);
        myController.setScreen(App.MAIN_SCREEN);
    }




}

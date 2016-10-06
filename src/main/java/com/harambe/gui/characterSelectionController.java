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
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

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

    MasterController myController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        final int characterSize = 200;
        Image characterBg = new Image("img/gradient_orange.png");

        // Define an event handler for clicking on Character Image
        EventHandler clickHandler = new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                System.out.println("Clicked on " + event.getSource());
                //TODO handle Picked Character
                event.consume();
            }
        };
        // Define an event handler for entering Character Image with Mouse
        EventHandler enterHandler = new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                Node source = (Node)event.getSource() ;
                Integer colIndex = GridPane.getColumnIndex(source);
                Integer rowIndex = GridPane.getRowIndex(source);
                System.out.println("Entered Character at: " + colIndex +" , " + rowIndex);
                event.consume();
            }
        };
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
            grid.setHalignment(characterImg, HPos.CENTER);
            grid.setValignment(characterImg, VPos.BOTTOM);
            characterImg.setTranslateY(-1);
            characterImg.addEventHandler(MouseEvent.MOUSE_CLICKED, clickHandler);
            characterImg.addEventHandler(MouseEvent.MOUSE_ENTERED, enterHandler);


            Text t = new Text(Character.characters[i]);
            t.getStyleClass().add("characterText");
            grid.setHalignment(t, HPos.CENTER);
            t.setTranslateY(100);

            grid.add(characterBgImg, i, 0);
            grid.add(characterImg, i, 0);
            grid.add(t, i, 0);
        }

    }

    public void setScreenParent(MasterController screenParent){
        myController = screenParent;
    }

    //onClickEvent switch screen to game
    @FXML
    private void play(ActionEvent event)/*throws IOException*/ {
        myController.setScreen(App.MAIN_SCREEN);
    }




}

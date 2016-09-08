package com.harambe.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;


public class MainController implements Initializable {


    @FXML
    private ImageView p1ImgView;
    @FXML
    private ImageView p2ImgView;


    @Override // is called when FXMLLoader loads main.fxml
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {

        Player p1 = new Player(false, "Player1", "harambe");
        Player p2 = new Player(false, "Player2", "poacher_1");

        initPlayers(p1, p2);


    }

    public void initPlayers(Player p1, Player p2) {
        //load image in ImageViewContainer for player 1
        Image p1Img = new Image(p1.getImgLocation());
        p1ImgView.setImage(p1Img);

        //load image in ImageViewContainer for player 2
        Image p2Img = new Image(p2.getImgLocation());
        p2ImgView.setImage(p2Img);
        //mirror Image
        p2ImgView.setScaleX(-1);
    }
}

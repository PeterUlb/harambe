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
    @FXML
    private ImageView g1;
    @FXML
    private ImageView g2;


    @Override // is called when FXMLLoader loads main.fxml
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {

        Player p1 = new Player(false, "Player1", "harambe");
        Player p2 = new Player(false, "Player2", "poacher_1");

        initPlayers(p1, p2);

        //set chip test
        Chip c1 = new Chip(p1.getChip());
        Image chipImg1 = new Image(c1.getImg());
        g1.setImage(chipImg1);

        //set chip test
        Chip c2 = new Chip(p2.getChip());
        Image chipImg2 = new Image(c2.getImg());
        g2.setImage(chipImg2);


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
    }
}

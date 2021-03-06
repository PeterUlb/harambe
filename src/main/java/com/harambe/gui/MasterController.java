package com.harambe.gui;

import com.harambe.game.ThreadManager;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.HashMap;

/**
 * This class manages the adding, loading and changing of scenes.
 */
public class MasterController extends StackPane {

    //Holds the screens to be displayed

    private HashMap<String, Node> screens = new HashMap<>();

    public MasterController() {
        super();
    }

    /**
     * Adds the screen to the collection
     * @param name name of the scene
     * @param screen the specified Node representing the screen to be added
     */
    private void addScreen(String name, Node screen) {
        screens.put(name, screen);
    }

    public Node getScreen(String name) {
        return screens.get(name);
    }

    /**
     *
     * @param name Name of the scene defined in App
     * @param resource Resource File of the scene defined in App
     * @param forceReload if true: the fxml+controller is forced to reload (useful for e.g. the main game)
     */
    public void loadAndSetScreen(String name, String resource, boolean forceReload) {
        try {
            ThreadManager.reset();
            // remove screen from the loaded screens, forcing a reload (useful for MainGame)
            if (forceReload) {
                screens.remove(name);
            }
            if(!setScreen(name)) {
                loadScreen(name, resource);
                setScreen(name);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * ONLY USE IF PRELOADING IS NECESSARY, ELSE USE 'loadAndSetScreen'
     * Loads the fxml file, add the screen to the screens collection and finally injects the screenPane to the controller.
     * @param name Name of the scene defined in App
     * @param resource Resource File of the scene defined in App
     * @return boolean indicating success or failure
     */
    public boolean loadScreen(String name, String resource) {
        try {
            FXMLLoader myLoader = new FXMLLoader(getClass().getResource(resource));
            Parent loadScreen = myLoader.load();
            ControlledScreen myScreenController = myLoader.getController();
            myScreenController.setScreenParent(this);
            addScreen(name, loadScreen);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Loads the scene
     * @param name Name of the scene defined in App
     * @return boolean indicating the success
     */
    private boolean setScreen(final String name) {
        if (screens.get(name) != null) {   //screen loaded
            final DoubleProperty opacity = opacityProperty();

            if (!getChildren().isEmpty()) {    //if there is more than one screen
                Timeline fade = new Timeline(
                        new KeyFrame(Duration.ZERO, new KeyValue(opacity, 1.0)),
                        new KeyFrame(new Duration(100), t -> {
                            getChildren().remove(0);                    //remove the displayed screen
                            getChildren().add(0, screens.get(name));     //add the screen
                            Timeline fadeIn = new Timeline(
                                    new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)),
                                    new KeyFrame(new Duration(80), new KeyValue(opacity, 1.0)));
                            fadeIn.play();
                        }, new KeyValue(opacity, 0.0)));
                fade.play();

            } else {
                setOpacity(0.0);
                getChildren().add(screens.get(name));       //no one else been displayed, then just show
                Timeline fadeIn = new Timeline(
                        new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)),
                        new KeyFrame(new Duration(250), new KeyValue(opacity, 1.0)));
                fadeIn.play();
            }
            return true;
        } else {
            return false;
        }
    }
}

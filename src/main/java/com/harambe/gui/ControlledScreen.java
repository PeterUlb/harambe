package com.harambe.gui;

/**
 * This interface serves as a contract for every screen that allows it being controlled by a MasterController.
 * This allows the UI to switch between scenes
 */
public interface ControlledScreen {
    /**
     * Sets the master screen controller
     * @param screenPage the master controller which manages the screen changes
     */
    public void setScreenParent(MasterController screenPage);
}

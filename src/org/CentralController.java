package org;

import Database.DatabaseInterface;
import UIControllers.CentralUIController;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Created by Tom on 4/2/2017.
 */
public class CentralController {

  private FileController fController;
  static Session currSession = new Session();
  private CentralUIController uiController;

  public CentralController(){
  }

  public void startUI (Stage primaryStage, DatabaseInterface dbe) throws Exception {
    primaryStage.setMinHeight(790);
    primaryStage.setMinWidth(1300);
    primaryStage.setTitle("Faulkner Hospital Kiosk");
    primaryStage.getIcons().add(new Image("/icons/kioskicon.png"));
    this.uiController = new CentralUIController();
    uiController.setSession(currSession, dbe);
    uiController.restartUI(primaryStage);
  }

  public static Session getCurrSession(){
    return currSession;
  }

  public static void resetSession(){
    currSession = new Session();
  }
}

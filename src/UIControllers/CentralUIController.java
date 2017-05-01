package UIControllers;

import CredentialManager.CredentialManager;
import Database.DatabaseInterface;
import Definitions.Physician;
import FileController.DefaultKioskNotInMemoryException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import FileController.SettingsIO;
import org.Dictionary;
import org.ListPoints;
import org.Point;
import org.Session;


public class CentralUIController {
  // TODO Add method to create/update header images on every page, could mean making another class
  /* Type of map to show when mapView is displayed
    1 for interactive map
    2 for directory map
    3 for admin map
   */
  protected static int mapViewFlag = 0;
  protected static Session currSession;
  protected static CredentialManager credentialManager = CredentialManager.getInstance();
  protected static Boolean adminPermissions = false;
  protected static Boolean isLoggedIn = false;
  protected static String currUsername;
  protected static HashMap<String, String> currentUser = new HashMap<>();
  protected static Dictionary dictionary;
  private static Timeline timeOut = null;
  /* resolution */
  protected static double x_res = 1300;
  protected static double y_res = 750;
  /* banner and background */
  protected static Image banner = new Image ("/icons/banner.png");
  protected static Image background = new Image ("/icons/background_shapte.png");
  protected static Image logo = new Image ("/icons/BWFH_logo_rgb.jpg");
  protected static ImageView bannerView = new ImageView();
  protected static ImageView backgroundView = new ImageView();
  protected static ImageView logoView = new ImageView();
  /* database object */
  protected static DatabaseInterface database;
  /* global points */
  protected static Point searchingPoint;
  protected static Point kioskLocation;

  /**@author Haofan Zhang
   * set the session and database controller of central ui controller
   * @param session the session to be set
   * @param dbInterface the database controller to be set
   */
  public void setSession (Session session, DatabaseInterface dbInterface) {
    this.currSession = session;
    this.credentialManager = credentialManager;
    this.dictionary = session.dictionary;
    this.database = dbInterface;
    try {
      database.load();
    } catch (Exception e) {
      System.out.println("Failed to load from database");
    }
  }

  /**@author Haofan Zhang
   * Set the stage to the initial scene (main menu)
   * @parameter primaryStage: The main stage of the application
   */
  public void restartUI(Stage primaryStage) throws Exception {
    applySettings(primaryStage);
    Parent root = FXMLLoader.load(getClass().getResource("/MainMenu.fxml"));
    primaryStage.setScene(new Scene(root, x_res, y_res));
    primaryStage.show();
  }

  /**@author Haofan Zhang
   * @parameter primaryStage: The main stage of the application
   * @parameter fxmlpath: the file path of the fxml file to be loaded
   * Set the stage to a scene by an fxml file
   */
  public void loadScene (Stage primaryStage, String fxmlpath) throws Exception {
    if (timeOut != null) {
      stopTimeOut();
    }
    Parent root = FXMLLoader.load(getClass().getResource(fxmlpath));
    Scene currScene = primaryStage.getScene();
    currScene.setRoot(root);
    if (!fxmlpath.equals("/MainMenu.fxml")) {
      addTimeOut(currScene);
    }
  }

  ////////////////////////
  //// apply settings ////
  ////////////////////////

  /**@author Haofan Zhang
   * apply settings to the ui from settings IO
   * @param primaryStage the Stage to apply settings on
   */
  public void applySettings (Stage primaryStage) {
    SettingsIO settings = new SettingsIO();
    if (settings.getScreenPreference() == 1) {
      primaryStage.setFullScreen(false);
      primaryStage.setMaximized(false);
      primaryStage.setWidth(1300);
      primaryStage.setHeight(750);
    } else if (settings.getScreenPreference() == 2) {
      primaryStage.setMaximized(false);
      primaryStage.setFullScreen(true);
    } else if (settings.getScreenPreference() == 3) {
      primaryStage.setFullScreen(false);
      primaryStage.setMaximized(true);
    }
    try {
      kioskLocation = settings.getDefaultKiosk(new ListPoints(database.getNamedPoints()));
    } catch (DefaultKioskNotInMemoryException e) {
      kioskLocation = null;
    }
    currSession.setAlgorithm(settings.getAlgorithm());
  }

  ////////////////////////
  //// sort functions ////
  ////////////////////////

  /**@author Haofan Zhang
   * sort a list of physicians by last name, if last name is the same, sort by first name
   * @param docs the list of physicians to be sorted
   */
  public void sortDocs (List<Physician> docs) {
    Collections.sort(docs, new Comparator<Physician>() {
      @Override
      public int compare(Physician doc1, Physician doc2) {
        int cmpLast = doc1.getLastName().compareToIgnoreCase(doc2.getLastName());
        if (cmpLast != 0) {
          return cmpLast;
        } else {
          return doc1.getFirstName().compareToIgnoreCase(doc2.getFirstName());
        }
      }
    });
  }

  /**@author Haofan Zhang
   * sort a list of points by name
   * @param rooms the list of points to be sorted
   */
  public void sortRooms (List<Point> rooms) {
    Collections.sort(rooms, new Comparator<Point>() {
      @Override
      public int compare(Point room1, Point room2) {
        return room1.getName().compareTo(room2.getName());
      }
    });
  }

  ////////////////////////
  //// banner and logo ///
  ////////////////////////

  /**@author Haofan Zhang
   * add a resolution listener for banner, background and logo
   * @param anchorPane the anchor pane to listen on
   */
  public void addResolutionListener (AnchorPane anchorPane) {
    anchorPane.widthProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) -> {
      x_res = (double) newSceneWidth;
      bannerView.setFitWidth(x_res);
      backgroundView.setFitWidth(x_res);
      logoView.setLayoutX(x_res/2 - logoView.getFitWidth()/2);
      customListenerX();
    });
    anchorPane.heightProperty().addListener((observableValue, oldSceneHeight, newSceneHeight) -> {
      y_res = (double) newSceneHeight;
      backgroundView.setLayoutY(y_res/2.5);
      customListenerY();
    });
  }

  /**@author Haofan Zhang
   * the resolution scale function for each ui controller to override
   * set layoutX/width of individual ui element here
   */
  public void customListenerX () {}

  /**@author Haofan Zhang
   * the resolution scale function for each ui controller to override
   * set layoutY/height of individual ui element here
   */
  public void customListenerY () {}

  /**@author Haofan Zhang
   * add banner background and logo to an anchor pane
   * @param anchorPane the anchor pane to add on
   */
  public void setBackground (AnchorPane anchorPane) {
    bannerView.setImage(banner);
    backgroundView.setImage(background);
    backgroundView.setPreserveRatio(true);
    logoView.setImage(logo);
    logoView.setFitWidth(380);
    logoView.setLayoutY(12);
    logoView.setPreserveRatio(true);
    anchorPane.getChildren().add(bannerView);
    anchorPane.getChildren().add(backgroundView);
    anchorPane.getChildren().add(logoView);
    logoView.toBack();
    bannerView.toBack();
    backgroundView.toBack();
  }

  //////////////////////////
  /// time out functions ///
  //////////////////////////

  /**@author Haofan Zhang
   * add time out implementation to a scene
   * @param scene the scene to add on
   */
  private void addTimeOut (Scene scene) {
    SettingsIO settings = new SettingsIO();
    if (settings.getTimeout() != 0) {
      setTimeOut(settings.getTimeout(), (Stage) scene.getWindow());
      scene.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
        resetTimeOut(settings.getTimeout(), (Stage) scene.getWindow());
      });
      scene.addEventFilter(MouseEvent.MOUSE_MOVED, event -> {
        resetTimeOut(settings.getTimeout(), (Stage) scene.getWindow());
      });
      scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
        resetTimeOut(settings.getTimeout(), (Stage) scene.getWindow());
      });
    }
  }

  /**@author Haofan Zhang
   * initialize the time out of a stage
   * @param time the time out length in seconds
   * @param primaryStage the primary stage to apply the time out
   */
  public void setTimeOut (int time, Stage primaryStage) {
    timeOut = makeKeyFrame(time, primaryStage);
    timeOut.play();
  }

  /**@author Haofan Zhang
   * reset the timer of time out of a stage
   * @param time the new time out length in seconds
   * @param primaryStage the primary stage to reset the time out
   */
  private void resetTimeOut (int time, Stage primaryStage) {
    timeOut.stop();
    setTimeOut(time, primaryStage);
  }

  /**
   * stop the time out
   */
  public void stopTimeOut () {
    timeOut.stop();
  }

  /**@author Haofan Zhang
   * make a timeline schedule for the time out
   * @param time the time out length in seconds
   * @param primaryStage the primary stage to show the main menu after time out
   * @return the newly created timeline
   */
  private Timeline makeKeyFrame (int time, Stage primaryStage) {
    KeyFrame KF = new KeyFrame(javafx.util.Duration.seconds(time), event-> {
      System.out.println("Session timed out");
      try {
        loadScene(primaryStage, "/MainMenu.fxml");
      } catch (Exception e) {
        System.out.println("Cannot load main menu from keyframe");
        e.printStackTrace();
      }
    });
    Timeline timeout = new Timeline(KF);
    timeout.setCycleCount(1);
    return timeout;
  }
}

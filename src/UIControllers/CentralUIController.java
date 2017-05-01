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


  public void setSession (Session session, DatabaseInterface dbInterface) {
    this.currSession = session;
    this.credentialManager = credentialManager;
    this.dictionary = session.dictionary;
    this.database = dbInterface;
    try {
      database.load();
    } catch (Exception e) {
    }
  }

  /**
   * Set the stage to the initial scene (main menu)
   * @parameter primaryStage: The main stage of the application
   */
  public void restartUI(Stage primaryStage) throws Exception {
    applySettings(primaryStage);
    loadScene(primaryStage, "/MainMenu.fxml");
    primaryStage.show();
  }

  /**
   * @parameter primaryStage: The main stage of the application
   * @parameter fxmlpath: the file path of the fxml file to be loaded
   * Set the stage to a scene by an fxml file
   */
  public void loadScene (Stage primaryStage, String fxmlpath) throws Exception {
    if (timeOut != null) {
      stopTimeOut();
    }
    Parent root = FXMLLoader.load(getClass().getResource(fxmlpath));
    Scene newScene = new Scene(root, x_res, y_res);
    if (!fxmlpath.equals("/MainMenu.fxml")) {
      addTimeOut(newScene);
    }
    primaryStage.setScene(newScene);
  }

  ////////////////////////
  //// apply settings ////
  ////////////////////////

  public void applySettings (Stage primaryStage) {
    SettingsIO settings = new SettingsIO();
    if (settings.getScreenPreference() == 1) {
      x_res = 1300;
      y_res = 750;
    } else if (settings.getScreenPreference() == 2) {
      primaryStage.setFullScreen(true);
    } else if (settings.getScreenPreference() == 3) {
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

  public void sortDocs (List<Physician> docs) {
    Collections.sort(docs, new Comparator<Physician>() {
      @Override
      public int compare(Physician doc1, Physician doc2) {
        int cmpLast = doc1.getLastName().compareToIgnoreCase(doc2.getLastName());
        if (cmpLast == 0) {
          return cmpLast;
        } else {
          return doc1.getFirstName().compareToIgnoreCase(doc2.getFirstName());
        }
      }
    });
  }

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
      bannerView.setFitHeight(120*y_res/750);
      backgroundView.setLayoutY(y_res/2.5);
      logoView.setFitHeight(60*y_res/750);
      logoView.setFitWidth(350*y_res/750);
      logoView.setLayoutX(x_res/2 - logoView.getFitWidth()/2);
      logoView.setLayoutY(14*y_res/750);
      customListenerY();
    });
  }

  public void customListenerX () {

  }

  public void customListenerY () {

  }

  public void setBackground (AnchorPane anchorPane) {
    bannerView.setImage(banner);
    backgroundView.setImage(background);
    backgroundView.setPreserveRatio(true);
    logoView.setImage(logo);
    logoView.setFitHeight(60);
    logoView.setFitWidth(350);
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

  private void setTimeOut (int time, Stage primaryStage) {
    timeOut = makeKeyFrame(time, primaryStage);
    timeOut.play();
  }

  private void resetTimeOut (int time, Stage primaryStage) {
    timeOut.stop();
    setTimeOut(time, primaryStage);
  }

  private void stopTimeOut () {
    timeOut.stop();
  }

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

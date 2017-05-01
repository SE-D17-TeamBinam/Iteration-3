package UIControllers;

import FileController.DefaultKioskNotInMemoryException;
import FileController.SettingsIO;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.Astar;
import org.BFS;
import org.DFS;
import org.Language;
import org.ListPoints;
import org.Point;

/**
 * Created by Haofan Zhang on 4/27/2017.
 */
public class SettingsMenuController extends CentralUIController implements Initializable {
  private ArrayList<Point> rooms;
  SettingsIO settings = new SettingsIO();

  @FXML
  private Label SettingsResolution;
  @FXML
  private Label SettingsLocation;
  @FXML
  private Label SettingsTimeout;
  @FXML
  private Label SettingsAlgorithm;
  @FXML
  private Label TimeoutTip;
  @FXML
  private Label TimeoutError;
  @FXML
  private Pane timeoutPane;
  @FXML
  private AnchorPane anchorPane;
  @FXML
  private Button SettingsLogoff;
  @FXML
  private RadioButton defaultResolution;
  @FXML
  private RadioButton fullscreenResolution;
  @FXML
  private RadioButton fullwindowResolution;
  @FXML
  private RadioButton bfsAlgorithm;
  @FXML
  private RadioButton dfsAlgorithm;
  @FXML
  private RadioButton astarAlgorithm;
  @FXML
  private ChoiceBox locationsKiosk;
  @FXML
  private TextField timeTimeout;

  @Override
  public void customListenerX () {
    SettingsLogoff.setLayoutX(x_res - SettingsLogoff.getPrefWidth() - 12);
  }

  @Override
  public void customListenerY () {
    double resLY = 353 * y_res/750;
    SettingsResolution.setLayoutY(resLY);
    defaultResolution.setLayoutY(resLY);
    fullscreenResolution.setLayoutY(resLY);
    fullwindowResolution.setLayoutY(resLY);
    double algLY = 553 * y_res/750;
    SettingsAlgorithm.setLayoutY(algLY);
    bfsAlgorithm.setLayoutY(algLY);
    dfsAlgorithm.setLayoutY(algLY);
    astarAlgorithm.setLayoutY(algLY);
    double locLY = 247 * y_res/750;
    SettingsLocation.setLayoutY(locLY);
    locationsKiosk.setLayoutY(locLY);
    double toutLY = 447 * y_res/740;
    SettingsTimeout.setLayoutY(toutLY);
    timeoutPane.setLayoutY(toutLY);
    TimeoutTip.setLayoutY(toutLY);
    TimeoutError.setLayoutY(toutLY + 20);
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    if (currSession.getLanguage() == Language.SPANISH) {
      SettingsLogoff.setPrefWidth(200);
    } else if (currSession.getLanguage() == Language.PORTUGESE) {
      SettingsLogoff.setPrefWidth(240);
    } else {
      SettingsLogoff.setPrefWidth(150);
    }
    addResolutionListener(anchorPane);
    setBackground(anchorPane);

    /* initialize radio buttons */
    rooms = database.getNamedPoints();
    sortRooms(rooms);

    /* radio button group policies */
    final ToggleGroup resolution = new ToggleGroup();
    defaultResolution.setToggleGroup(resolution);
    fullscreenResolution.setToggleGroup(resolution);
    fullwindowResolution.setToggleGroup(resolution);
    final ToggleGroup algorithm = new ToggleGroup();
    bfsAlgorithm.setToggleGroup(algorithm);
    dfsAlgorithm.setToggleGroup(algorithm);
    astarAlgorithm.setToggleGroup(algorithm);

    /* apply existing settings on radio buttons */
    if (settings.getScreenPreference() == 1){
      defaultResolution.setSelected(true);
    } else if (settings.getScreenPreference() == 2) {
      fullscreenResolution.setSelected(true);
    } else if (settings.getScreenPreference() == 3) {
      fullwindowResolution.setSelected(true);
    }
    if (settings.getAlgorithm().getClass() == DFS.class) {
      dfsAlgorithm.setSelected(true);
    } else if (settings.getAlgorithm().getClass() == BFS.class) {
      bfsAlgorithm.setSelected(true);
    } else if (settings.getAlgorithm().getClass() == Astar.class) {
      astarAlgorithm.setSelected(true);
    }

    /* initialize kiosk location */
    locationsKiosk.setItems(FXCollections.observableList(rooms));
    try {
      locationsKiosk.getSelectionModel().select(settings.getDefaultKiosk(new ListPoints(rooms)));
    } catch (DefaultKioskNotInMemoryException e) {
      System.out.println("Default kiosk location is not set");
    }

    /* initialize timeout */
    timeTimeout.setText(Integer.toString(settings.getTimeout()));

    /* kiosk location listener */
    locationsKiosk.getSelectionModel().selectedIndexProperty().addListener((ov, old_value, new_value) -> {
        if ((Integer) new_value >= 0) {
          settings.updateSetting("startingKiosk", Integer
              .toString(((Point) locationsKiosk.getItems().get((Integer) new_value)).getId()));
          try {
            System.out.println(
                "changed kiosk location to " + settings.getDefaultKiosk(new ListPoints(rooms))
                    .toString());
          } catch (DefaultKioskNotInMemoryException e) {

          }
        }
    });

    /* timeout field listener */
    timeTimeout.textProperty().addListener((observable, oldValue, newValue) -> {
      try {
        Integer newTimeOut = Integer.parseInt(newValue);
        if (newTimeOut < 10 && newTimeOut != 0) {
          System.out.println("minimum timeout length is 10s");
          TimeoutError.setVisible(true);
        } else if (newTimeOut == 0) {
          System.out.println("timeout disabled");
          settings.updateSetting("timeoutLength", newValue);
          TimeoutError.setVisible(false);
        } else {
          System.out.println("set timeout length to " + newValue);
          settings.updateSetting("timeoutLength", newValue);
          TimeoutError.setVisible(false);
        }
      } catch (NumberFormatException e) {
        System.out.println("please enter a valid number");
        TimeoutError.setVisible(true);
      }
    });

    /* radio button listeners */
    defaultResolution.setOnAction(event -> {
      settings.updateSetting("screenSize", "1");
      System.out.println("changed resolution to default");
    });
    fullscreenResolution.setOnAction(event -> {
      settings.updateSetting("screenSize", "2");
      System.out.println("changed resolution to fullscreen");
    });
    fullwindowResolution.setOnAction(event -> {
      settings.updateSetting("screenSize", "3");
      System.out.println("changed resolution to fullwindow");
    });
    bfsAlgorithm.setOnAction(event -> {
      settings.updateSetting("algorithm", "bfs");
      System.out.println("changed algorithm to bfs");
    });
    dfsAlgorithm.setOnAction(event -> {
      settings.updateSetting("algorithm", "dfs");
      System.out.println("changed algorithm to dfs");
    });
    astarAlgorithm.setOnAction(event -> {
      settings.updateSetting("algorithm", "astar");
      System.out.println("changed algorithm to astar");
    });

  }

  /**
   * try to increase timeout length by 1
   */
  public void increaseTimeout () {
    timeTimeout.setText(Integer.toString(Integer.parseInt(timeTimeout.getText()) + 1));
  }

  /**
   * try to decrease timeout length by 1
   */
  public void decreaseTimeout() {
    timeTimeout.setText(Integer.toString(Integer.parseInt(timeTimeout.getText()) - 1));
  }

  /**
   * set the scene back to admin menu
   */
  public void back () {
    Stage primaryStage = (Stage) anchorPane.getScene().getWindow();
    applySettings(primaryStage);
    try {
      loadScene(primaryStage, "/AdminMenu.fxml");
    } catch (Exception e) {
      System.out.println("Cannot load admin login menu");
      e.printStackTrace();
    }
  }

  /**
   * set the scene back to main menu
   */
  public void logoff () {
    Stage primaryStage = (Stage) anchorPane.getScene().getWindow();
    applySettings(primaryStage);
    try {
      loadScene(primaryStage, "/MainMenu.fxml");
    } catch (Exception e) {
      System.out.println("Cannot load main menu");
      e.printStackTrace();
    }
  }

}

package UIControllers;

import Definitions.Physician;
import FileController.DefaultKioskNotInMemoryException;
import FileController.SettingsIO;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.Language;
import org.ListPoints;
import org.Point;

/**
 * Created by Leon Zhang on 4/27/2017.
 */
public class SettingsMenuController extends CentralUIController implements Initializable {
  private ArrayList<Point> points;
  SettingsIO settings = new SettingsIO();

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
  public void initialize(URL location, ResourceBundle resources) {
    addResolutionListener(anchorPane);
    setBackground(anchorPane);
    points = database.getPoints();
    final ToggleGroup resolution = new ToggleGroup();
    defaultResolution.setToggleGroup(resolution);
    fullscreenResolution.setToggleGroup(resolution);
    fullwindowResolution.setToggleGroup(resolution);
    final ToggleGroup algorithm = new ToggleGroup();
    bfsAlgorithm.setToggleGroup(algorithm);
    dfsAlgorithm.setToggleGroup(algorithm);
    astarAlgorithm.setToggleGroup(algorithm);
    ArrayList<Integer> pids = new ArrayList<>();
    for (Point point : points) {
      pids.add(point.getId());
    }
    locationsKiosk.setItems(FXCollections.observableList(pids));
    try {
      locationsKiosk.getSelectionModel().select(settings.getDefaultKiosk(new ListPoints(points)).getId());
      locationsKiosk.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
        public void changed(ObservableValue ov, Number old_value, Number new_value) {
          for (Point point : points) {
            if ((Integer) locationsKiosk.getItems().get((Integer)new_value) == (point.getId())) {

            }
          }
        }
      });
    } catch (DefaultKioskNotInMemoryException e) {
      System.out.println("Default kiosk location is not set");
    }
    timeTimeout.setText(Integer.toString(settings.getTimeout()));
    defaultResolution.setOnAction(event -> {
      settings.updateSetting("resolution", "default");
      System.out.println("changed resolution to default");
    });
    fullscreenResolution.setOnAction(event -> {
      settings.updateSetting("resolution", "fullscreen");
      System.out.println("changed resolution to fullscreen");
    });
    fullwindowResolution.setOnAction(event -> {
      settings.updateSetting("resolution", "fullwindow");
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

  public void back () {
    Stage primaryStage = (Stage) anchorPane.getScene().getWindow();
    try {
      loadScene(primaryStage, "/AdminMenu.fxml");
    } catch (Exception e) {
      System.out.println("Cannot load admin login menu");
      e.printStackTrace();
    }
  }

  public void logoff () {
    Stage primaryStage = (Stage) anchorPane.getScene().getWindow();
    try {
      loadScene(primaryStage, "/MainMenu.fxml");
    } catch (Exception e) {
      System.out.println("Cannot load main menu");
      e.printStackTrace();
    }
  }

  public void selectResolution () {
  }
  public void selectLocation () {

  }
  public void selectAlgorithm () {

  }
  public void setTimeout () {

  }
}

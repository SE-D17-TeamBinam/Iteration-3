package UIControllers;

import Definitions.Physician;
import FileController.SettingsIO;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
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
import org.Point;

/**
 * Created by Leon Zhang on 4/27/2017.
 */
public class SettingsMenuController extends CentralUIController implements Initializable {
  private ArrayList<Point> rooms;

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
    final ToggleGroup resolution = new ToggleGroup();
    defaultResolution.setToggleGroup(resolution);
    fullscreenResolution.setToggleGroup(resolution);
    fullwindowResolution.setToggleGroup(resolution);
    final ToggleGroup algorithm = new ToggleGroup();
    bfsAlgorithm.setToggleGroup(algorithm);
    dfsAlgorithm.setToggleGroup(algorithm);
    astarAlgorithm.setToggleGroup(algorithm);
    rooms = database.getNamedPoints();
    locationsKiosk.setItems(FXCollections.observableList(rooms));
    //SettingsIO settings = new SettingsIO();
    //timeTimeout.setText(Integer.toString(settings.getTimeout()));
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

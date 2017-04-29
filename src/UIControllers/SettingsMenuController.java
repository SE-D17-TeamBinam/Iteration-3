package UIControllers;

import FileController.DefaultKioskNotInMemoryException;
import FileController.SettingsIO;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.Astar;
import org.BFS;
import org.DFS;
import org.ListPoints;
import org.Point;

/**
 * Created by Leon Zhang on 4/27/2017.
 */
public class SettingsMenuController extends CentralUIController implements Initializable {
  private ArrayList<Point> rooms;
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

  public void customListenerX () {
    SettingsLogoff.setLayoutX(x_res - SettingsLogoff.getPrefWidth() - 12);
  }

  public void customListenerY () {

  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    addResolutionListener(anchorPane);
    setBackground(anchorPane);

    /* initialize radio buttons */
    rooms = database.getNamedPoints();
    sortRooms(rooms);

    final ToggleGroup resolution = new ToggleGroup();
    defaultResolution.setToggleGroup(resolution);
    fullscreenResolution.setToggleGroup(resolution);
    fullwindowResolution.setToggleGroup(resolution);
    final ToggleGroup algorithm = new ToggleGroup();
    bfsAlgorithm.setToggleGroup(algorithm);
    dfsAlgorithm.setToggleGroup(algorithm);
    astarAlgorithm.setToggleGroup(algorithm);

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
      locationsKiosk.getSelectionModel().select(settings.getDefaultKiosk(new ListPoints(rooms)).getId());
    } catch (DefaultKioskNotInMemoryException e) {
      System.out.println("Default kiosk location is not set");
    }

    /* initialize timeout */
    timeTimeout.setText(Integer.toString(settings.getTimeout()));

    /* kiosk location listener */
    locationsKiosk.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
      public void changed(ObservableValue ov, Number old_value, Number new_value) {
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
      }
    });

    timeTimeout.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue,
          String newValue) {
        try {
          if (Integer.parseInt(newValue) < 0) {
            timeTimeout.setText(Integer.toString(0));
          } else {
            System.out.println("changed timeout length to " + newValue);
            settings.updateSetting("timeoutLength", newValue);
          }
        } catch (NumberFormatException e) {
          System.out.println("Please enter something legit");
          timeTimeout.setText(oldValue);
        }
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

  public void increaseTimeout () {
    timeTimeout.setText(Integer.toString(Integer.parseInt(timeTimeout.getText()) + 1));
  }

  public void decreaseTimeout() {
    timeTimeout.setText(Integer.toString(Integer.parseInt(timeTimeout.getText()) - 1));
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

}

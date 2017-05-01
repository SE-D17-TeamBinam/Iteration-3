package UIControllers;

import FileController.SettingsIO;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.Language;

/**
 * Created by Praneeth Appikatla on 4/17/2017.
 */
public class AdminMenuController extends CentralUIController implements Initializable {

  @FXML
  private AnchorPane anchorPane;
  @FXML
  private Pane AdminMenu;
  @FXML
  private ImageView EditButton;
  @FXML
  private ImageView MapButton;
  @FXML
  private ImageView SettingsButton;
  @FXML
  private Label MapLabel;
  @FXML
  private Label DirectEditLabel;
  @FXML
  private Label SettingsLabel;
  @FXML
  private Button LogOffButton;

  @Override
  public void customListenerX() {
    LogOffButton.setLayoutX(x_res - LogOffButton.getPrefWidth() - 12);
    MapButton.setLayoutX(x_res/5 - MapButton.getFitWidth()/2);
    EditButton.setLayoutX(x_res/2 - EditButton.getFitWidth()/2);
    SettingsButton.setLayoutX(4*x_res/5 - SettingsButton.getFitWidth()/2);
    MapLabel.setLayoutX(x_res/5 - MapLabel.getPrefWidth()/2);
    DirectEditLabel.setLayoutX(x_res/2 - DirectEditLabel.getPrefWidth()/2);
    SettingsLabel.setLayoutX(4*x_res/5 - SettingsLabel.getPrefWidth()/2);
  }

  @Override
  public void customListenerY() {
    MapButton.setLayoutY(6*y_res / 11 - MapButton.getFitHeight()/2);
    EditButton.setLayoutY(6*y_res / 11 - EditButton.getFitHeight()/2);
    SettingsButton.setLayoutY(6*y_res/11 -SettingsButton.getFitHeight()/2);
    DirectEditLabel.setLayoutY(6*y_res / 11 + EditButton.getFitHeight()/2 + 20);
    MapLabel.setLayoutY(6*y_res / 11 + MapButton.getFitHeight()/2 + 20);
    SettingsLabel.setLayoutY(6*y_res/11 + SettingsButton.getFitHeight()/2 + 20);
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    if (currSession.getLanguage() == Language.SPANISH) {
      LogOffButton.setPrefWidth(200);
    } else if (currSession.getLanguage() == Language.PORTUGESE) {
      LogOffButton.setPrefWidth(240);
    } else {
      LogOffButton.setPrefWidth(150);
    }
    addResolutionListener(anchorPane);
    setBackground(anchorPane);
  }

  /**
   * set the scene to admin map
   */
  @FXML
  public void editMap() {
    mapViewFlag = 3;
    Stage primaryStage = (Stage) AdminMenu.getScene().getWindow();
    try {
      loadScene(primaryStage, "/MapScene.fxml");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * set the scene to directory editor
   */
  @FXML
  public void editDirectory() {
    Stage primaryStage = (Stage) AdminMenu.getScene().getWindow();
    try {
      loadScene(primaryStage, "/DirectEdit.fxml");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * set the scene to settings menu
   */
  @FXML
  public void editSettings () {
    Stage primaryStage = (Stage) AdminMenu.getScene().getWindow();
    try {
      loadScene(primaryStage, "/SettingsMenu.fxml");
    } catch (Exception e) {
      System.out.println("Cannot load main menu");
      e.printStackTrace();
    }
  }

  /**
   * set the scene back to admin login page
   */
  @FXML
  public void back() {
    Stage primaryStage = (Stage) AdminMenu.getScene().getWindow();
    try {
      loadScene(primaryStage, "/AdminLogin.fxml");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * set the scene to main menu
   */
  public void logoff() {
    Stage primaryStage = (Stage) AdminMenu.getScene().getWindow();
    try {
      loadScene(primaryStage, "/MainMenu.fxml");
    } catch (Exception e) {
      System.out.println("Cannot load main menu");
      e.printStackTrace();
    }
  }

}



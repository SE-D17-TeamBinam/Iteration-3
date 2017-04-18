package UIControllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Created by Praneeth Appikatla on 4/17/2017.
 */
public class AdminMenuController extends CentralUIController implements Initializable {

  @FXML
  private AnchorPane anchorPane;
  @FXML
  private Pane AdminMenu;
  @FXML
  private Button EditButton;
  @FXML
  private Button MapButton;
  @FXML
  private Label MapLabel;
  @FXML
  private Label DirectEditLabel;
  @FXML
  private Button AdminBack;


  @Override
  public void initialize(URL location, ResourceBundle resources) {
    addResolutionListener(anchorPane);
    setBackground(anchorPane);
  }

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

  @FXML
  public void editDirectory(){
    Stage primaryStage = (Stage) AdminMenu.getScene().getWindow();
    try {
      loadScene(primaryStage, "/DirectEdit.fxml");
    } catch (Exception e){
      e.printStackTrace();
    }
  }

  @FXML
  public void back(){
    Stage primaryStage = (Stage) AdminMenu.getScene().getWindow();
    try {
      loadScene(primaryStage, "/AdminLogin.fxml");
    } catch (Exception e){
      e.printStackTrace();
    }
  }

  public void logoff () {
    Stage primaryStage = (Stage) AdminMenu.getScene().getWindow();
    try {
      loadScene(primaryStage, "/MainMenu  .fxml");
    } catch (Exception e) {
      System.out.println("Cannot load main menu");
      e.printStackTrace();
    }
  }

}



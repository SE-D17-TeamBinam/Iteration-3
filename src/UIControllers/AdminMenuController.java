package UIControllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
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
  private Label LogOffButton;
  @FXML
  private Label LoginLabel;
  @FXML
  private Label CreateAccountButton;

  @Override
  public void customListenerX () {
    LogOffButton.setLayoutX(x_res - LogOffButton.getPrefWidth() - 5);
    MapButton.setLayoutX(5*(x_res/7) - 175);
    EditButton.setLayoutX(2*(x_res/7) - 175);
    DirectEditLabel.setLayoutX(2*(x_res/7) - 105);
    MapLabel.setLayoutX(5*(x_res/7) - 95);
  }

  @Override
  public void customListenerY () {
    MapButton.setLayoutY(6*(y_res/11) - 160);
    EditButton.setLayoutY(6*(y_res/11) - 160);
    DirectEditLabel.setLayoutY(6*(y_res/11) + 120);
    MapLabel.setLayoutY(6*(y_res/11) + 120);

  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    addResolutionListener(anchorPane);
    setBackground(anchorPane);
    LoginLabel.setText(LoginLabel.getText() + currUser);
    initializeCreationButton();
  }

  public void initializeCreationButton() {
    if(!adminPermissions) {
      CreateAccountButton.setVisible(false);
    }
  }

  @FXML
  public void editMap() {
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
      loadScene(primaryStage, "/MainMenu.fxml");
    } catch (Exception e) {
      System.out.println("Cannot load main menu");
      e.printStackTrace();
    }
  }

  public void createAccount() {
    Stage primaryStage = (Stage) AdminMenu.getScene().getWindow();
    try {
      loadScene(primaryStage, "/SignupMenu.fxml");
    } catch (Exception e) {
      System.out.println("Cannot load signup menu");
      e.printStackTrace();
    }
  }

  public void verifyCredentials() {
    Dialog verification = new Dialog();
  }

}



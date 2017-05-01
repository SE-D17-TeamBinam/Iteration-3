package UIControllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Created by Haofan Zhang on 2017/4/1.
 */


public class AdminLoginController extends CentralUIController implements Initializable {

  /* define all ui elements */
  @FXML
  private Pane AdminLogin;
  @FXML
  private TextField AdminNameField;
  @FXML
  private PasswordField AdminPassField;

  @FXML
  private Button AdminBack;
  @FXML
  private Label AdminNameLabel;
  @FXML
  private Label AdminPassLabel;
  @FXML
  private Button AdminLoginButton;
  @FXML
  private Label LoginError;
  @FXML
  private AnchorPane anchorPane;

  @Override
  public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
    addResolutionListener(anchorPane);
    setBackground(anchorPane);
  }

  @Override
  public void customListenerX () {
    AdminNameLabel.setLayoutX(x_res/2 - 540);
    AdminPassLabel.setLayoutX(x_res/2 - 540);
    AdminLoginButton.setLayoutX(x_res/2 - AdminLoginButton.getPrefWidth()/2);
    AdminNameField.setLayoutX(x_res/2 - 20);
    AdminPassField.setLayoutX(x_res/2 - 20);
    LoginError.setLayoutX(x_res/2 - LoginError.getPrefWidth()/2);
  }
  @Override
  public void customListenerY () {
    AdminNameLabel.setLayoutY(4*y_res/11);
    AdminPassLabel.setLayoutY(6*y_res/11);
    AdminLoginButton.setLayoutY(8*y_res/11);
    AdminNameField.setLayoutY(4*y_res/11);
    AdminPassField.setLayoutY(6*y_res/11);
    LoginError.setLayoutY(7*y_res/11 + 10);
  }

  /**@author Haofan Zhang
   * try to login when enter is pressed
   * @param event the Key event containing pressed key code
   */
  @FXML
  private void tryLogin(KeyEvent event) throws IOException {
    if(event.getCode().toString().equals("ENTER")){
      login();
    }
  }

  /**
   * checks login credential. If pass, log into directory editor; if fail, show an error message
   * TODO: throw an exception in the future.
   */
  public void login () throws IOException {
    adminPermissions = false;
    Stage primaryStage = (Stage) AdminLogin.getScene().getWindow();
    String enteredName = AdminNameField.getText();
    String enteredPass = AdminPassField.getText();

    if (credentialManager.login(enteredName, enteredPass)){
      LoginError.setVisible(false);
      currUsername = enteredName;
      currentUser.put(enteredName, enteredPass);
      if (credentialManager.userIsAdmin(enteredName)){
        mapViewFlag = 3;
        adminPermissions = true;
        isLoggedIn = true;
      }
      else {
        mapViewFlag = 2;
        isLoggedIn = true;
      }

      try {
        loadScene(primaryStage, "/AdminMenu.fxml");
      } catch (Exception e) {
        System.out.println("Cannot load Admin Menu");
        e.printStackTrace();
      }
    } else {
      LoginError.setVisible(true);
    }
  }

  /**@author Haofan Zhang
   * go back to the main menu
   */
  public void back () {
    Stage primaryStage = (Stage) AdminLogin.getScene().getWindow();
    try {
      loadScene(primaryStage, "/MainMenu.fxml");
    } catch (Exception e) {
      System.out.println("Cannot load main menu");
      e.printStackTrace();
    }
  }

}
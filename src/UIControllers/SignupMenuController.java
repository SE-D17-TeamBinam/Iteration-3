package UIControllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

/**
 * Created by Praneeth Appikatla on 4/25/2017.
 */
public class SignupMenuController extends CentralUIController implements Initializable {

  /* define all UI elements */
  @FXML
  private AnchorPane anchorPane;
  @FXML
  private Pane SignupMenu;
  @FXML
  private Label SignupTypeLabel;
  @FXML
  private Label SignupNameLabel;
  @FXML
  private Label SignupPassLabel;
  @FXML
  private PasswordField SignupPassField;
  @FXML
  private TextField SignupNameField;
  @FXML
  private ComboBox SignupBox;
  @FXML
  private Label SignupBack;
  @FXML
  private Label SignupButton;
  @FXML
  private Label UsernameError;
  @FXML
  private Label PassError;
  @FXML
  private Label SignupError;
  @FXML
  private Label UsernameExistsError;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    addResolutionListener(anchorPane);
    setBackground(anchorPane);
    intializeChoiceBox();
    SignupError.setVisible(false);
    UsernameExistsError.setVisible(false);
  }

  public void intializeChoiceBox(){
    SignupBox.getItems().addAll("STAFF", "ADMIN");
    SignupBox.getSelectionModel().select("STAFF");
  }

  @FXML
  private void trySignup(KeyEvent e){
    if(e.getCode().toString().equals("ENTER")){
      signup();
    }
  }

  public void signup() {
    String pass = SignupPassField.getText();
    String username = SignupNameField.getText();
    if (pass.equals("") || username.equals("")){
      SignupError.setVisible(true);
      UsernameError.setTextFill(Paint.valueOf("red"));
      PassError.setTextFill(Paint.valueOf("red"));
    }
    else if (credentialManager.signup(username, pass)){
      UsernameExistsError.setVisible(true);
    }
    else {
      Stage primaryStage = (Stage) SignupMenu.getScene().getWindow();
      try {
        loadScene(primaryStage, "/AdminLogin.fxml");
      } catch (Exception e) {
        System.out.println("Cannot load employee login");
        e.printStackTrace();
      }
    }
  }

  /**
   * go back to the main menu
   */
  public void back () {
    Stage primaryStage = (Stage) SignupMenu.getScene().getWindow();
    try {
      loadScene(primaryStage, "/AdminLogin.fxml");
    } catch (Exception e) {
      System.out.println("Cannot load main menu");
      e.printStackTrace();
    }
  }
}

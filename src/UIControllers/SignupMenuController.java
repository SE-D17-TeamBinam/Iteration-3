package UIControllers;

import CredentialManager.UserType;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
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
  private Label UsernameRequired;
  @FXML
  private Label PassRequired;
  @FXML
  private Label SignupError;
  @FXML
  private Label UsernameExistsError;
  @FXML
  private ProgressBar pwdStrength;
  @FXML
  private Label progressLabel;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    addResolutionListener(anchorPane);
    setBackground(anchorPane);
    intializeChoiceBox();
    SignupError.setVisible(false);
    UsernameExistsError.setVisible(false);
    intializeProgressBar();
  }

  @Override
  public void customListenerX () {
    SignupTypeLabel.setLayoutX(x_res/2 - 540);
    SignupNameLabel.setLayoutX(x_res/2 - 540);
    SignupPassLabel.setLayoutX(x_res/2 - 540);
    SignupButton.setLayoutX(x_res/2 - SignupButton.getPrefWidth()/2);
    SignupNameField.setLayoutX(x_res/2 - 20);
    SignupPassField.setLayoutX(x_res/2 - 20);
    SignupBox.setLayoutX(x_res/2 - 20);
    UsernameExistsError.setLayoutX(x_res/2 - 20);
    SignupError.setLayoutX(x_res/2 - 125);
    UsernameRequired.setLayoutX(x_res/2 + 160);
    PassRequired.setLayoutX(x_res/2 + 160);
    pwdStrength.setLayoutX(x_res/2 + pwdStrength.getPrefWidth()*1.5);
    progressLabel.setLayoutX(x_res/2 + pwdStrength.getPrefWidth()*1.5 + pwdStrength.getPrefWidth()/2 - 15);
  }
  @Override
  public void customListenerY () {
    SignupTypeLabel.setLayoutY(3*y_res/11 - 10);
    SignupNameLabel.setLayoutY(4.5*y_res/11 - 5);
    SignupPassLabel.setLayoutY(6*y_res/11);
    SignupButton.setLayoutY(8*y_res/11);
    SignupBox.setLayoutY(3*y_res/11);
    SignupNameField.setLayoutY(4.5*y_res/11);
    SignupPassField.setLayoutY(6*y_res/11);
    UsernameExistsError.setLayoutY(4.5*y_res/11 + 40);
    SignupError.setLayoutY(2*y_res/11);
    UsernameRequired.setLayoutY(4.5*y_res/11 - 22);
    PassRequired.setLayoutY(6*y_res/11 - 22);
    pwdStrength.setLayoutY(6*y_res/11 + pwdStrength.getHeight()/2 +5);
    progressLabel.setLayoutY(6*y_res/11 + pwdStrength.getHeight()/2 - 20);
  }

  public void intializeChoiceBox(){
    SignupBox.getItems().addAll(UserType.STAFF, UserType.ADMIN);
    SignupBox.getSelectionModel().select(UserType.STAFF);
  }

  @FXML
  private void trySignup(KeyEvent e) throws IOException {
    if(e.getCode().toString().equals("ENTER")){
      signup();
    }
  }

  public void intializeProgressBar() {
      pwdStrength.setProgress(0.02F);
      progressLabel.setVisible(false);
      pwdStrength.setVisible(false);
      pwdStrength.setTooltip(new Tooltip("• Use 6 to 64 characters.\n• Besides letters, include at least a number or symbol\n (!@$%^*-_+=).\n• Password is case sensitive."));
    }

  public void updateProgressBar() {
    if (!SignupPassField.getText().equals("")) {
      pwdStrength.setVisible(true);
      progressLabel.setVisible(true);
    }
    else {
      pwdStrength.setVisible(false);
      progressLabel.setVisible(false);
    }
    String s = SignupPassField.getText();
    double d = calculatePassStrength(s);
    pwdStrength.setProgress(d);
      if (d < 0.1) {
        pwdStrength.setStyle("-fx-accent: red");
        progressLabel.setText("Weak");
      }
      else if (d >= 0.1 && d < 0.3){
        pwdStrength.setStyle("-fx-accent: orange");
        progressLabel.setText("Weak");
      }
      else if (d >= 0.3 && d < 0.6){
        pwdStrength.setStyle("-fx-accent: yellow");
        progressLabel.setText("Fair");
      }
      else if (d >= 0.6) {
        pwdStrength.setStyle("-fx-accent: green");
        progressLabel.setText("Strong");
      }
    }


  private Double calculatePassStrength(String pass) {
    double strength = 0;
    if (pass.length()< 1){
      strength = 0;
    }
    else {
      strength += pass.length() * 0.07;
    }
    return strength;

  }

  /**
   * sign up account from text in text fields and choice box selection
   */
  public void signup() throws IOException {
    String pass = SignupPassField.getText();
    String username = SignupNameField.getText();
    UserType type = (UserType) SignupBox.getSelectionModel().getSelectedItem();

    if (pass.equals("") || username.equals("")){
      SignupError.setVisible(true);
      UsernameRequired.setTextFill(Paint.valueOf("red"));
      PassRequired.setTextFill(Paint.valueOf("red"));
    }
    else if (!credentialManager.signup(username, pass, type)){
      UsernameExistsError.setVisible(true);
    }
    else {
      Stage primaryStage = (Stage) SignupMenu.getScene().getWindow();
      try {
        loadScene(primaryStage, "/AdminMenu.fxml");
      } catch (Exception e) {
        System.out.println("Cannot load employee login");
        e.printStackTrace();
      }
    }
  }

  /**
   * go back to admin login
   */
  public void back () {
    Stage primaryStage = (Stage) SignupMenu.getScene().getWindow();
    try {
      loadScene(primaryStage, "/AdminMenu.fxml");
    } catch (Exception e) {
      System.out.println("Cannot load main menu");
      e.printStackTrace();
    }
  }
}

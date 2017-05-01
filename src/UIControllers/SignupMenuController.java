package UIControllers;

import CredentialManager.UserType;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
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
  private Button SignupBack;
  @FXML
  private Button SignupButton;
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
  private Label ProgressLabel;

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
    ProgressLabel.setLayoutX(x_res/2 + pwdStrength.getPrefWidth()*1.5 + pwdStrength.getPrefWidth()/2 - 15);
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
    ProgressLabel.setLayoutY(6*y_res/11 + pwdStrength.getHeight()/2 - 20);
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
      ProgressLabel.setVisible(false);
      pwdStrength.setVisible(false);
  }

  public void updateProgressBar() {
    if (!SignupPassField.getText().equals("")) {
      pwdStrength.setVisible(true);
      ProgressLabel.setVisible(true);
    }
    else {
      pwdStrength.setVisible(false);
      ProgressLabel.setVisible(false);
    }
    String s = SignupPassField.getText();
    double d = calculatePassStrength(s);
    pwdStrength.setProgress(d);
      if (d < 0.1) {
        pwdStrength.setStyle("-fx-accent: red");
        ProgressLabel.setText("Weak");
      }
      else if (d >= 0.1 && d < 0.3){
        pwdStrength.setStyle("-fx-accent: orange");
        ProgressLabel.setText("Weak");
      }
      else if (d >= 0.3 && d < 0.6){
        pwdStrength.setStyle("-fx-accent: yellow");
        ProgressLabel.setText("Fair");
      }
      else if (d >= 0.6) {
        pwdStrength.setStyle("-fx-accent: green");
        ProgressLabel.setText("Strong");
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

      if (pass.equals("") || username.equals("")) { // gives indicators if the required fields are empty
        SignupError.setVisible(true);
        UsernameRequired.setTextFill(Paint.valueOf("red"));
        PassRequired.setTextFill(Paint.valueOf("red"));
      }
      else if (credentialManager.containsUser(username, pass)) { // if the user already exists show an error
          UsernameExistsError.setVisible(true);
      }

      else if (verifyCreation()) { // validates creation based on credentials entered
          credentialManager.signup(username, pass, type);
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

  public void back() {
    Stage primaryStage = (Stage) SignupMenu.getScene().getWindow();
    try {
      loadScene(primaryStage, "/AdminMenu.fxml");
    } catch (Exception e) {
      System.out.println("Cannot load main menu");
      e.printStackTrace();
    }
  }


  public boolean verifyCreation() {
    boolean getFlag;
    Stage primaryStage = (Stage) SignupMenu.getScene().getWindow();
    Stage dialog = new Stage();
    dialog.setMinHeight(300);
    dialog.setMaxHeight(300);
    VBox vbox = new VBox(20);
    dialog.setResizable(true);
    dialog.initModality(Modality.APPLICATION_MODAL);
    dialog.initOwner(primaryStage);

    Button okButton = new Button("OK");
    Button cancelButton = new Button("Cancel");

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(40, 0, 10, 10));

    TextField userName = new TextField();
    PasswordField userPass = new PasswordField();
    Label uLabel = new Label("Username:");
    Label pLabel = new Label("Password:");

    grid.add(uLabel, 0, 0);
    grid.add(pLabel, 0, 1);
    grid.add(userName, 1, 0);
    grid.add(userPass, 1, 1);

    TilePane tileButtons = new TilePane();
    cancelButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    okButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    tileButtons.setHgap(10);
    tileButtons.setVgap(8.0);
    tileButtons.setPadding(new Insets(20, 10, 20, 20));
    tileButtons.getChildren().addAll(okButton, cancelButton);

    vbox.getChildren().addAll(grid, tileButtons);

    Scene dialogScene = new Scene(vbox, 300, 300);
    dialog.setScene(dialogScene);

    BooleanProperty res = new SimpleBooleanProperty();

    okButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent event) {
        res.set(false);
        String username = userName.getText();
        String pass = userPass.getText();
        Dialog error = new Alert(AlertType.ERROR);
        error.setContentText("The username or password you have entered is incorrect.");
        error.setHeaderText("Verification failed");
        if (username.equals(currUsername) && pass.equals(currentUser.get(currUsername))) {
          res.set(true);
          dialog.close();
        } else {
          res.set(false);
          error.showAndWait();
        }
        if (username.equals("") || pass.equals("")) {
          res.set(false);
          error.showAndWait();
        }

      }
    });

    userName.setOnKeyPressed(new EventHandler<KeyEvent>() {
      @Override
      public void handle(KeyEvent event) {
        if (event.getCode().toString().equals("ENTER")){
          res.set(false);
          String username = userName.getText();
          String pass = userPass.getText();
          Dialog error = new Alert(AlertType.ERROR);
          error.setContentText("The username or password you have entered is incorrect.");
          error.setHeaderText("Verification failed");
          if (username.equals(currUsername) && pass.equals(currentUser.get(currUsername))) {
            res.set(true);
            dialog.close();
          } else {
            res.set(false);
            error.showAndWait();
          }
          if (username.equals("") || pass.equals("")) {
            res.set(false);
            error.showAndWait();
          }
        }
      }
    });

    userPass.setOnKeyPressed(new EventHandler<KeyEvent>() {
      @Override
      public void handle(KeyEvent event) {
        if (event.getCode().toString().equals("ENTER")){
          res.set(false);
          String username = userName.getText();
          String pass = userPass.getText();
          Dialog error = new Alert(AlertType.ERROR);
          error.setContentText("The username or password you have entered is incorrect.");
          error.setHeaderText("Verification failed");
          if (username.equals(currUsername) && pass.equals(currentUser.get(currUsername))) {
            res.set(true);
            dialog.close();
          } else {
            res.set(false);
            error.showAndWait();
          }
          if (username.equals("") || pass.equals("")) {
            res.set(false);
            error.showAndWait();
          }
        }
      }
    });


    cancelButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent event) {
        dialog.close();
        res.set(false);
      }
    });

    dialog.showAndWait();

      getFlag = res.getValue();
      return getFlag;
    }


  }







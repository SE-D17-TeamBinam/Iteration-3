package UIControllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.CentralController;
import org.Language;
import org.Session;


public class MainMenuController extends CentralUIController implements Initializable {
  // define all ui elements
  @FXML
  private Pane MainMenu;
  @FXML
  private ChoiceBox langBox;
  @FXML
  private AnchorPane anchorPane;
  @FXML
  private ImageView MainKey;
  @FXML
  private ImageView InfoButton;

  @FXML
  private ImageView MapButton;
  @FXML
  private ImageView SearchButton;
  @FXML
  private Label SearchLabel;
  @FXML
  private Label MapLabel;

  @Override
  public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
    chooseLang();
    addResolutionListener(anchorPane);
    setBackground(anchorPane);
    CentralController.resetSession();
    applyLanguageConfig();
    isLoggedIn = false;
  }

  /**
   * apply language configuration by set text from dictionary
   */
  public void applyLanguageConfig(){
    SearchLabel.setText(dictionary.getString("Search", currSession.getLanguage()));
    MapLabel.setText(dictionary.getString("Map", currSession.getLanguage()));
  }

  @Override
  public void customListenerX () {
    MapLabel.setLayoutX(5*(x_res/7) - MapLabel.getPrefWidth()/2);
    MapButton.setLayoutX(5*(x_res/7) - MapButton.getFitWidth()/2);
    SearchButton.setLayoutX(2*(x_res/7) - SearchButton.getFitWidth()/2);
    MainKey.setLayoutX(x_res - MainKey.getFitWidth() - 10);
    InfoButton.setLayoutX(15*x_res/16);
    SearchLabel.setLayoutX(2*(x_res/7) - SearchLabel.getPrefWidth()/2);
  }

  @Override
  public void customListenerY () {
    MainKey.setLayoutY(y_res - MainKey.getFitHeight() - 10);
    MapButton.setLayoutY(6*(y_res/11) - MapButton.getFitHeight()/2);
    MapLabel.setLayoutY(6*(y_res/11) + 100);
    SearchButton.setLayoutY(6*(y_res/11) - SearchButton.getFitHeight()/2);
    SearchLabel.setLayoutY(6*(y_res/11) + 100);
    langBox.setLayoutY(y_res - 50);
    MainKey.setLayoutY(y_res - 50);
    InfoButton.setLayoutY(150*y_res/750);
  }

  /**@author Haofan Zhang
   * set the scene to user map
   */
  public void gotoMap () {
    Stage primaryStage = (Stage) MainMenu.getScene().getWindow();
    try {
      mapViewFlag = 1;
      loadScene(primaryStage, "/MapScene.fxml");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**@author Haofan Zhang
   * set the scene to search menu
   */
  public void gotoSearch () {
    Stage primaryStage = (Stage) MainMenu.getScene().getWindow();
    try {
      loadScene(primaryStage, "/SearchMenu.fxml");
    } catch (Exception e) {
    }
  }

  /**@author Haofan Zhang
   * set the scene to admin login menu
   */
  public void gotoAdmin () throws IOException {
    // for now sign up admin when loaded so people can get to admin view
    //credentialManager.signup("admin", "admin", UserType.ADMIN);
    // sign up staff when loaded for testing purposes
    Stage primaryStage = (Stage) MainMenu.getScene().getWindow();
    try {
      loadScene(primaryStage, "/AdminLogin.fxml");
    } catch (Exception e) { e.printStackTrace();
    }
  }

  /**@author Haofan Zhang
   * set the scene to about page
   */
  public void gotoAbout() {
    Stage primaryStage = (Stage) MainMenu.getScene().getWindow();
    try {
      loadScene(primaryStage, "/AboutPage.fxml");
    } catch (Exception e) {
    }
  }

  /**
   * select and apply current language
   */
  public void chooseLang() {
    langBox.getItems().add("ENGLISH");
    langBox.getItems().add("SPANISH");
    langBox.getItems().add("PORTUGUESE");

    langBox.getSelectionModel().select(Language.ENGLISH.toString());
    currSession.setLanguage(Language.ENGLISH);
    langBox.getSelectionModel().selectedIndexProperty().addListener(
        new ChangeListener<Number>() {
          public void changed(ObservableValue ov, Number old_value, Number new_value) {
            // Change the language that's being displayed when the input changes\
            if ("ENGLISH".equals((String) langBox.getItems().get((int) new_value))) {
              currSession.setLanguage(Language.ENGLISH);
            } else if ("SPANISH".equals((String) langBox.getItems().get((int) new_value))) {
              currSession.setLanguage(Language.SPANISH);
            } else if ("PORTUGUESE".equals((String) langBox.getItems().get((int) new_value))) {
              currSession.setLanguage(Language.PORTUGESE);
            }
            SearchLabel.setText(dictionary.getString("Search", currSession.getLanguage()));
            MapLabel.setText(dictionary.getString("Map", currSession.getLanguage()));
            System.out.println(currSession.getLanguage());
          }
        });
  }

}






package UIControllers;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.CentralController;
import org.Language;
import org.Session;

/**
 * Created by Leon Zhang on 2017/4/1.
 */

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
  private Button MapButton;
  @FXML
  private Button SearchButton;
  @FXML
  private Label SearchLabel;
  @FXML
  private Label MapLabel;

  public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
    Session session = new Session();
    chooseLang();
    addResolutionListener(anchorPane);
    setBackground(anchorPane);
    CentralController.resetSession();
    SearchLabel.setText(dictionary.getString("Search", currSession.getLanguage()));
    MapLabel.setText(dictionary.getString("Map", currSession.getLanguage()));
  }

  @Override
  public void customListenerX () {
    MapLabel.setLayoutX(5*(x_res/7) - 95);
    MapButton.setLayoutX(5*(x_res/7) - 175);
    SearchButton.setLayoutX(2*(x_res/7) - 175);
    MainKey.setLayoutX(x_res - MainKey.getFitWidth());
    InfoButton.setLayoutX(x_res - InfoButton.getFitWidth());
    SearchLabel.setLayoutX(2*(x_res/7) - 95);
  }

  @Override
  public void customListenerY () {
    MainKey.setLayoutY(y_res - MainKey.getFitHeight());
    MapButton.setLayoutY(6*(y_res/11) - 160);
    MapLabel.setLayoutY(6*(y_res/11) + 100);
    SearchButton.setLayoutY(6*(y_res/11) - 160);
    SearchLabel.setLayoutY(6*(y_res/11) + 100);
    langBox.setLayoutY(y_res - 50);
  }

  public void gotoMap () {
    Stage primaryStage = (Stage) MainMenu.getScene().getWindow();
    try {
      mapViewFlag = 2;
      loadScene(primaryStage, "/MapScene.fxml");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void gotoSearch () {
    Stage primaryStage = (Stage) MainMenu.getScene().getWindow();
    try {
      loadScene(primaryStage, "/SearchMenu.fxml");
    } catch (Exception e) {
    }
  }

  public void gotoAdmin () {
    Stage primaryStage = (Stage) MainMenu.getScene().getWindow();
    try {
      loadScene(primaryStage, "/AdminLogin.fxml");
    } catch (Exception e) {
    }
  }

  public void gotoAbout() {
    Stage primaryStage = (Stage) MainMenu.getScene().getWindow();
    try {
      loadScene(primaryStage, "/AboutPage.fxml");
    } catch (Exception e) {
    }
  }

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

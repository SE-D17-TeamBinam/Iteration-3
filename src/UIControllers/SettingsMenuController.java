package UIControllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Created by Leon Zhang on 4/27/2017.
 */
public class SettingsMenuController extends CentralUIController implements Initializable {
  @FXML
  private AnchorPane anchorPane;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    addResolutionListener(anchorPane);
    setBackground(anchorPane);
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

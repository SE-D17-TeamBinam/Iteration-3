package UIControllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Created by Praneeth Appikatla on 4/17/2017.
 */
public class AboutPageController extends CentralUIController implements Initializable{
  // define all UI elements
  @FXML
  private Pane AboutPage;
  @FXML
  private AnchorPane anchorPane;
  @FXML
  private TextArea TeamInfo;
  @FXML
  private Label MainLabel;
  @FXML
  private Button AboutDone;
  @FXML
  private Button AboutBack;

  @Override
  public void initialize(URL fxmlFileLocation, ResourceBundle resources){
    AboutDone.setVisible(false);
    addResolutionListener(anchorPane);
    setBackground(anchorPane);
  }

  public void back () {
  Stage primaryStage = (Stage) AboutPage.getScene().getWindow();
    try {
      loadScene(primaryStage, "/MainMenu.fxml");
    } catch (Exception e) {
      System.out.println("Cannot load main menu");
      e.printStackTrace();
    }
  }

}

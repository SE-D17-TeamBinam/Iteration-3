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
  private AnchorPane anchorPane;
  @FXML
  private Pane AboutPage;
  @FXML
  private TextArea TeamInfo;
  @FXML
  private Label MainLabel;
  @FXML
  private Button AboutBack;
  @FXML
  private Label EndLabel;


  @Override
  public void customListenerX(){
    AboutPage.setLayoutX((x_res - AboutPage.getPrefWidth())/2);
  }

  @Override
  public void customListenerY(){
    AboutPage.setLayoutY((y_res - AboutPage.getPrefHeight())/2);
  }

  @Override
  public void initialize(URL fxmlFileLocation, ResourceBundle resources){
    addResolutionListener(anchorPane);
    setBackground(anchorPane);

    /* apply language configs */
    AboutBack.setText(dictionary.getString("Back", currSession.getLanguage()));
    backgroundView.toBack();
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

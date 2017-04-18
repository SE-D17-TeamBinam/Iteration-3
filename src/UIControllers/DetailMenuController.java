package UIControllers;
import Definitions.Physician;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.Point;

/**
 * Created by Leon Zhang on 2017/4/1.
 */

public class DetailMenuController extends CentralUIController implements Initializable {
  /* 0 is physician, 1 is room */
  //private int mode = 0;
  private Point currentPoint = null;
  private Physician currentPhysician = null;

  // define all ui elements
  @FXML
  private AnchorPane anchorPane;
  @FXML
  private ScrollPane DocPane;
  @FXML
  private ScrollPane RoomPane;
  @FXML
  private Pane DetailDoc;
  @FXML
  private Pane DetailRoom;
  @FXML
  private VBox DocLocations;
  @FXML
  private VBox RoomHPs;
  @FXML
  private Label DocFirstNameField;
  @FXML
  private Label DocLastNameField;
  @FXML
  private Label DocTitleField;
  @FXML
  private Label RoomNameField;
  @FXML
  private Label RoomFloorField;

  @Override
  public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
    /* apply language configs */
    /*
    DetailTitle.setText(dictionary.getString("Room Details", currSession.getLanguage()));
    DetailRoomName.setText(dictionary.getString("Room Name", currSession.getLanguage()));
    DetailRoomFloor.setText(dictionary.getString("Floor", currSession.getLanguage()));
    DetailRoomHCPs.setText(dictionary.getString("Healthcare Providers", currSession.getLanguage()));
    */
    addResolutionListener(anchorPane);
    setBackground(anchorPane);
    if (currentPoint == null && currentPhysician != null) {
      DetailDoc.setVisible(true);
      DetailRoom.setVisible(false);
      DocFirstNameField.setText(currentPhysician.getFirstName());
      DocLastNameField.setText(currentPhysician.getLastName());
      DocTitleField.setText(currentPhysician.getTitle());
      for (int i = 0; i < currentPhysician.getLocations().size(); i++){
        Label loc = new Label();
        loc.setPrefHeight(50);
        loc.setFont(Font.font("Times New Roman", 30));
        loc.setText(currentPhysician.getLocations().get(i).getName());
        loc.setStyle("-fx-background-color: transparent");
        DocLocations.setPrefHeight(DocLocations.getPrefHeight() + 50);
        DocLocations.getChildren().add(loc);
      }
    } else if (currentPhysician == null && currentPoint != null) {
      DetailDoc.setVisible(false);
      DetailRoom.setVisible(true);
      RoomNameField.setText(currentPoint.getName());
      RoomFloorField.setText(Integer.toString(currentPoint.getFloor()));
      ArrayList<Physician> docs = database.getPhysicians();
      for (Physician doc : docs){
        for (Point room : doc.getLocations()){
          if (room.getId() == currentPoint.getId()){
            System.out.println(doc.getFirstName());
            Label loc = new Label();
            loc.setPrefHeight(50);
            loc.setFont(Font.font("Times New Roman", 30));
            loc.setText(doc.getFirstName() + " " + doc.getLastName());
            loc.setStyle("-fx-background-color: transparent");
            RoomHPs.setPrefHeight(RoomHPs.getPrefHeight() + 50);
            RoomHPs.getChildren().add(loc);
            break;
          }
        }
      }
    } else {
      DetailDoc.setVisible(false);
      DetailRoom.setVisible(false);
    }
  }

  DetailMenuController (Physician doc, Point room) {
    currentPhysician = doc;
    currentPoint = room;
  }

  public void quit () {
    Stage primaryStage = (Stage) anchorPane.getScene().getWindow();
    try {
      restartUI(primaryStage);
    } catch (Exception e) {
      System.out.println("Cannot load main menu");
      e.printStackTrace();
    }
  }

  public void back () {
    Stage primaryStage = (Stage) anchorPane.getScene().getWindow();
    try {
      loadScene(primaryStage, "/SearchMenu.fxml");
    } catch (Exception e) {
      System.out.println("Cannot load search menu");
      e.printStackTrace();
    }
  }

  public void gotoMap () {
    Stage primaryStage = (Stage) anchorPane.getScene().getWindow();
    try {
      loadScene(primaryStage, "/MapScene.fxml");
    } catch (Exception e) {
      System.out.println("Cannot load map view");
      e.printStackTrace();
    }
  }

}


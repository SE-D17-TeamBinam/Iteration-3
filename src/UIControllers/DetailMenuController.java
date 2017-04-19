package UIControllers;
import Definitions.Physician;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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
      for (Point room : currentPhysician.getLocations()){
        String txt = room.getName();
        Pane locPane = new Pane();

        Label ILabel = new Label();
        ILabel.setPrefHeight(50);
        ILabel.setMinWidth(50);
        ILabel.setFont(Font.font("Times New Roman", 30));
        ILabel.setText(txt);
        //Platform.runLater(() -> {ILabel.setPrefWidth(ILabel.getWidth() + 15);});


        Label Goto = new Label();
        Goto.setPrefHeight(46);
        Goto.setPrefWidth(46);
        Goto.setLayoutY(2);
        Goto.setStyle("-fx-background-color: #3255bc; -fx-text-fill: white;");
        Goto.setAlignment(Pos.CENTER);
        Goto.setFont(Font.font("Times New Roman", 24));
        Goto.setText("Go");
        Goto.setVisible(false);
        Platform.runLater(() -> {Goto.setLayoutX(ILabel.getWidth() + 10);});

        ILabel.setStyle("-fx-background-color: transparent");
        ILabel.setOnMouseEntered(e -> {
          ILabel.setStyle("-fx-background-color: f7f7f7");
        });
        ILabel.setOnMouseExited(e -> {
          ILabel.setStyle("-fx-background-color: transparent");
        });
        locPane.setOnMouseEntered(e -> {
          Goto.setVisible(true);
        });
        locPane.setOnMouseExited(e -> {
          Goto.setVisible(false);
        });
        Goto.setOnMouseClicked(e -> {
          gotoMap(room);
        });

        locPane.getChildren().add(ILabel);
        locPane.getChildren().add(Goto);
        DocLocations.setPrefHeight(DocLocations.getPrefHeight() + 50);
        DocLocations.getChildren().add(locPane);
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
            String txt = doc.getFirstName() + " " + doc.getLastName();
            RoomHPs.setPrefHeight(RoomHPs.getPrefHeight() + 50);
            Label ILabel = new Label();
            ILabel.setPrefHeight(50);
            ILabel.setFont(Font.font("Times New Roman", 30));
            ILabel.setText(txt);
            RoomHPs.getChildren().add(ILabel);
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


  public Label makeGoto (double LayoutX) {
    Label Goto = new Label("Go");
    Goto.setPrefWidth(50);
    Goto.setPrefHeight(50);
    Goto.setLayoutX(LayoutX);
    return Goto;
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

  public void gotoMap (Point room) {
    Stage primaryStage = (Stage) anchorPane.getScene().getWindow();
    try {
      searchingPoint = room;
      loadScene(primaryStage, "/MapScene.fxml");
    } catch (Exception e) {
      System.out.println("Cannot load main menu");
      e.printStackTrace();
    }
  }
}


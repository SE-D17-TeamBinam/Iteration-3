package UIControllers;
import Definitions.Physician;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.Point;

/**
 * Created by Haofan Zhang on 2017/4/1.
 */

public class DetailMenuController extends CentralUIController implements Initializable {
  /* 0 is physician, 1 is room */
  private int mode;
  private Point currentPoint;
  private Physician currentPhysician;

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
  private Button DetailDone;
  @FXML
  private Label DocInfoLabel;
  @FXML
  private Label RoomDetailLabel;
  @FXML
  private Label DocFirstNameField;
  @FXML
  private Label DocLastNameField;
  @FXML
  private Label DocTitleField;
  @FXML
  private Label DocFirstNameLabel;
  @FXML
  private Label DocLastNameLabel;
  @FXML
  private Label DocTitleLabel;
  @FXML
  private Label DocLocationsLabel;
  @FXML
  private Label RoomNameField;
  @FXML
  private Label RoomFloorField;
  @FXML
  private Label RoomNameLabel;
  @FXML
  private Label RoomHPLabel;
  @FXML
  private Label RoomFloorLabel;

  @Override
  public void customListenerX () {
    DetailDone.setLayoutX(x_res - DetailDone.getPrefWidth() - 12);
    DetailRoom.setPrefWidth(x_res);
    DetailDoc.setPrefWidth(x_res);
    double LX = x_res * 0.5 - RoomNameLabel.getPrefWidth() - 100;
    RoomNameLabel.setLayoutX(LX);
    RoomFloorLabel.setLayoutX(LX);
    RoomHPLabel.setLayoutX(LX);
    double FX = x_res * 0.5 + 50;
    RoomPane.setLayoutX(FX);
    RoomFloorField.setLayoutX(FX);
    RoomNameField.setLayoutX(FX);

    DocFirstNameLabel.setLayoutX(LX);
    DocLastNameLabel.setLayoutX(LX);
    DocTitleLabel.setLayoutX(LX);
    DocLocationsLabel.setLayoutX(LX);

    DocPane.setLayoutX(FX);
    DocFirstNameField.setLayoutX(FX);
    DocLastNameField.setLayoutX(FX);
    DocTitleField.setLayoutX(FX);

    DocInfoLabel.setLayoutX(x_res/2 - DocInfoLabel.getPrefWidth()/2);
    RoomDetailLabel.setLayoutX(x_res/2 - RoomDetailLabel.getPrefWidth()/2);
  }

  @Override
  public void customListenerY () {
    DocPane.setPrefHeight(y_res*9/10 - DocPane.getLayoutY() - 167*(y_res/750 - 1));
    RoomPane.setPrefHeight(y_res*9/10 - RoomPane.getLayoutY() - 167*(y_res/750 - 1));
    DetailRoom.setLayoutY(167*(y_res/750 - 1));
    DetailDoc.setLayoutY(167*(y_res/750 - 1));
  }

  @Override
  public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
    /* apply language configs */
    addResolutionListener(anchorPane);
    setBackground(anchorPane);
    if (mode == 0) {
      DetailDoc.setVisible(true);
      DetailRoom.setVisible(false);
      DocFirstNameField.setText(currentPhysician.getFirstName());
      DocLastNameField.setText(currentPhysician.getLastName());
      DocTitleField.setText(currentPhysician.getTitle());
      for (Point room : currentPhysician.getLocations()){
        String txt = "  " + room.getName() + "  ";
        Pane locPane = new Pane();

        Label ILabel = new Label();
        ILabel.setPrefHeight(50);
        ILabel.setMinWidth(50);
        ILabel.setFont(Font.font("Times New Roman", 30));
        ILabel.setText(txt);

        Button Goto = new Button();
        Goto.setPrefHeight(46);
        Goto.setPrefWidth(50);
        Goto.setLayoutY(2);
        Goto.setAlignment(Pos.CENTER);
        Goto.setFont(Font.font("Times New Roman", 24));
        Goto.setText("Go");
        Goto.setVisible(false);

        ILabel.setStyle("-fx-background-color: transparent");
        locPane.setOnMouseEntered(e -> {
          Goto.setLayoutX(ILabel.getWidth() + 10);
          Goto.setVisible(true);
          ILabel.setStyle("-fx-background-color: white");
        });
        locPane.setOnMouseExited(e -> {
          Goto.setVisible(false);
          ILabel.setStyle("-fx-background-color: transparent");
        });
        Goto.setOnMouseClicked(e -> {
          gotoMap(room);
        });

        locPane.getChildren().add(ILabel);
        locPane.getChildren().add(Goto);
        DocLocations.setPrefHeight(DocLocations.getPrefHeight() + 50);
        DocLocations.getChildren().add(locPane);
      }
    } else if (mode == 1) {
      DetailDoc.setVisible(false);
      DetailRoom.setVisible(true);
      RoomNameField.setText(currentPoint.getName());
      RoomFloorField.setText(Integer.toString(currentPoint.getFloor()));
      ArrayList<Physician> docs = database.getPhysicians();
      sortDocs(docs);
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

  /**
   * constructor for detail menu controller
   * @param room the room to display, if null, display physician
   * @param doc the physician to display, if null, display room
   * room and doc will not be both null
   */
  DetailMenuController (Point room, Physician doc) {
    if (room == null) {
      this.mode = 0;
    } else if (doc == null) {
      this.mode = 1;
    }
    currentPoint = room;
    currentPhysician = doc;
  }

  /**
   * set the scene back to main menu
   */
  public void quit () {
    Stage primaryStage = (Stage) anchorPane.getScene().getWindow();
    try {
      loadScene(primaryStage, "/MainMenu.fxml");
    } catch (Exception e) {
      System.out.println("Cannot load main menu");
      e.printStackTrace();
    }
  }

  /**
   * set the scene back to search menu
   */
  public void back () {
    Stage primaryStage = (Stage) anchorPane.getScene().getWindow();
    try {
      loadScene(primaryStage, "/SearchMenu.fxml");
    } catch (Exception e) {
      System.out.println("Cannot load search menu");
      e.printStackTrace();
    }
  }

  /**
   * set the scene to user map and display room selected
   * @param room the room to be displayed
   */
  public void gotoMap (Point room) {
    mapViewFlag = 1;
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


package UIControllers;

import Definitions.Physician;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.Point;

/**
 * Created by Leon Zhang on 2017/4/1.
 */
public class SearchMenuController extends CentralUIController implements Initializable {
  private ArrayList<Physician> docs;
  private ArrayList<Point> rooms;
  private String searchString = "";
  private ObservableList<Physician> HCOL = FXCollections.observableArrayList();
  private ObservableList<Point> RMOL = FXCollections.observableArrayList();
  private int searchMode = 0;

  // define all ui elements
  @FXML
  private TableView<Physician> PhysicianDirectory;
  @FXML
  private TableView<Point> RoomDirectory;
  @FXML
  private TableColumn firstName;
  @FXML
  private TableColumn lastName;
  @FXML
  private TableColumn title;
  @FXML
  private TableColumn locations;
  @FXML
  private TableColumn roomNumber;
  @FXML
  private TableColumn roomPhysicians;
  @FXML
  private TextField SearchField;
  @FXML
  private ImageView SearchClear;

  @FXML
  private Label SearchMap;
  @FXML
  private Label SearchInfo;
  @FXML
  private Label SearchBack;
  @FXML
  private Label SearchDoc;
  @FXML
  private Label SearchRoom;

  @FXML
  private AnchorPane anchorPane;

  public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
    addResolutionListener(anchorPane);
    setBackground(anchorPane);
    PhysicianDirectory.setStyle("-fx-font-size: 16px;");
    RoomDirectory.setStyle("-fx-font-size: 16px;");
    /* apply language configs */
    SearchMap.setText(dictionary.getString("Show on Map", currSession.getLanguage()));
    SearchInfo.setText(dictionary.getString("View Details", currSession.getLanguage()));
    SearchBack.setText(dictionary.getString("Back", currSession.getLanguage()));
    SearchField.setPromptText(dictionary.getString("Search Directory", currSession.getLanguage()));

    firstName.setCellValueFactory(
        new PropertyValueFactory<Physician, String>("firstName")
    );
    lastName.setCellValueFactory(
        new PropertyValueFactory<Physician, String>("lastName")
    );
    title.setCellValueFactory(
        new PropertyValueFactory<Physician, String>("title")
    );
    locations.setCellValueFactory(new Callback<CellDataFeatures<Physician, String>, ObservableValue<String>>() {
      public ObservableValue<String> call(CellDataFeatures<Physician, String> p) {
        String locations = "";
        for (int i = 0; i < p.getValue().getLocations().size(); i++) {
          locations += p.getValue().getLocations().get(i).getName();
          locations += "\n";
        }
        return new ReadOnlyStringWrapper(locations);
      }
    });
    roomNumber.setCellValueFactory(
        new PropertyValueFactory<Point, String>("name")
    );
    roomPhysicians.setCellValueFactory(new Callback<CellDataFeatures<Point, String>, ObservableValue<String>>() {
      public ObservableValue<String> call(CellDataFeatures<Point, String> p) {
        String physicians = "";
        for (Physician doc : docs) {
          for (Point room : doc.getLocations()) {
            if (room.getId() == p.getValue().getId()){
              physicians = physicians + doc.getFirstName() + " " + doc.getLastName() + "\n";
              break;
            }
          }
        }
        return new ReadOnlyStringWrapper(physicians);
      }
    });
    SearchField.textProperty().addListener((observable, oldValue, newValue) -> {
      searchString = newValue.toString();
      if (searchMode == 0){
        updatePhysicians(docs);
      } else if (searchMode == 1){
        updateRooms(rooms);
      }
    });

    docs = database.getPhysicians();
    rooms = database.getNamedPoints();
    updatePhysicians(docs);
  }

  @Override
  public void customListenerX () {
    double DirX = x_res/2 - PhysicianDirectory.getPrefWidth()/2;
    PhysicianDirectory.setLayoutX(DirX);
    RoomDirectory.setLayoutX(DirX);
    SearchField.setLayoutX(DirX);
    SearchDoc.setLayoutX(DirX);
    SearchClear.setLayoutX(x_res/2 + 294);
    SearchRoom.setLayoutX(x_res/2 - 247);
    double ButtonX = DirX + PhysicianDirectory.getPrefWidth() - SearchInfo.getPrefWidth();
    SearchInfo.setLayoutX(ButtonX);
    SearchMap.setLayoutX(ButtonX - SearchMap.getPrefWidth() - 20*(x_res)/750);
  }
  @Override
  public void customListenerY () {
    SearchRoom.setLayoutY(y_res * 0.2166666666666667 - SearchRoom.getPrefHeight()/2);
    SearchDoc.setLayoutY(SearchRoom.getLayoutY());
    SearchField.setLayoutY(SearchDoc.getLayoutY() + 42 * (y_res)/750);
    SearchClear.setLayoutY(SearchField.getLayoutY() + SearchField.getPrefHeight()/2 - SearchClear.getFitHeight()/2);
    PhysicianDirectory.setLayoutY(SearchField.getLayoutY() + 62 * (y_res)/750);
    RoomDirectory.setLayoutY(PhysicianDirectory.getLayoutY());
    PhysicianDirectory.setPrefHeight(y_res * 11/13 - PhysicianDirectory.getLayoutY());
    RoomDirectory.setPrefHeight(PhysicianDirectory.getPrefHeight());
    SearchInfo.setLayoutY(PhysicianDirectory.getLayoutY() + PhysicianDirectory.getPrefHeight() + 15 * (y_res)/750);
    SearchMap.setLayoutY(SearchInfo.getLayoutY());
  }

  public void toggleDoc () {
    searchMode = 0;
    SearchDoc.setStyle("-fx-background-color: grey; -fx-text-fill: white;");
    SearchRoom.setStyle("-fx-background-color: f7f7f7; -fx-text-fill: black;");
    updatePhysicians(docs);
    SearchMap.setVisible(false);
    PhysicianDirectory.setVisible(true);
    RoomDirectory.setVisible(false);
  }
  public void toggleRoom () {
    searchMode = 1;
    SearchRoom.setStyle("-fx-background-color: grey; -fx-text-fill: white;");
    SearchDoc.setStyle("-fx-background-color: f7f7f7; -fx-text-fill: black;");
    updateRooms(rooms);
    SearchMap.setVisible(true);
    PhysicianDirectory.setVisible(false);
    RoomDirectory.setVisible(true);
  }

  public void updatePhysicians (List<Physician> HCs){
    HCOL.clear();

    if (searchString != "") {
      for (Physician doc : HCs) {
        if ((doc.getFirstName() + " " + doc.getLastName()).contains(searchString)) {
          HCOL.add(doc);
        }
      }
    } else {
      HCOL.addAll(HCs);
    }

    HCOL.addAll(database.fuzzySearchPhysicians(searchString));
    PhysicianDirectory.setItems(HCOL);
  }

  public void updateRooms (List<Point> Rooms){
    RMOL.clear();

    if (searchString != "") {
      for (Point room : Rooms) {
        if (room.getName().contains(searchString)) {
          RMOL.add(room);
        }
      }
      for (Physician doc : docs) {
        if ((doc.getFirstName() + " " + doc.getLastName()).contains(searchString)) {
          for (Point room : doc.getLocations()) {
            if (!RMOL.contains(room)) {
              RMOL.add(room);
            }
          }
        }
      }
    } else {
      RMOL.addAll(Rooms);
    }
    //RMOL.addAll(database.fuzzySearchPoints(searchString));
    RoomDirectory.setItems(RMOL);
  }

  public void viewInfo () {
    Stage primaryStage = (Stage) anchorPane.getScene().getWindow();
    if (searchMode == 0 && PhysicianDirectory.getSelectionModel().getSelectedItem() != null) {
      try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/DetailMenu.fxml"));
        DetailMenuController DC = new DetailMenuController(
            PhysicianDirectory.getSelectionModel().getSelectedItem(), null);
        loader.setController(DC);
        Pane mainPane = (Pane) loader.load();
        primaryStage.setScene(new Scene(mainPane, x_res, y_res));
        primaryStage.show();
      } catch (Exception e) {
        System.out.println("Cannot load main menu");
        e.printStackTrace();
      }
    } else if (searchMode == 1 && RoomDirectory.getSelectionModel().getSelectedItem() != null){
      try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/DetailMenu.fxml"));
        DetailMenuController DC = new DetailMenuController(
            null, RoomDirectory.getSelectionModel().getSelectedItem());
        loader.setController(DC);
        Pane mainPane = (Pane) loader.load();
        primaryStage.setScene(new Scene(mainPane, x_res, y_res));
        primaryStage.show();
      } catch (Exception e) {
        System.out.println("Cannot load main menu");
        e.printStackTrace();
      }
    }
  }

  public void back () {
    Stage primaryStage = (Stage) anchorPane.getScene().getWindow();
    try {
      loadScene(primaryStage, "/MainMenu.fxml");
    } catch (Exception e) {
      System.out.println("Cannot load main menu");
      e.printStackTrace();
    }
  }

  public void clear () {
    SearchField.setText("");
  }

  public void viewMap () {
    if (searchMode == 1 && RoomDirectory.getSelectionModel().getSelectedItem() != null) {
      Stage primaryStage = (Stage) anchorPane.getScene().getWindow();
      try {
        searchingPoint = RoomDirectory.getSelectionModel().getSelectedItem();
        loadScene(primaryStage, "/MapScene.fxml");
      } catch (Exception e) {
        System.out.println("Cannot load main menu");
        e.printStackTrace();
      }
    }
  }
}
package UIControllers;

import Definitions.Physician;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
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
  private boolean isST = false;
  private boolean isBS = false;
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
  private Button SearchMap;
  @FXML
  private Button SearchInfo;
  @FXML
  private Button SearchBack;
  @FXML
  private Label SearchDoc;
  @FXML
  private Label SearchRoom;

  @FXML
  private AnchorPane anchorPane;

  public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
    addResolutionListener(anchorPane);
    setBackground(anchorPane);
    docs = database.getPhysicians();
    rooms = database.getNamedPoints();
    sortDocs(docs);
    sortRooms(rooms);

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
        String physician = "";
        for (Physician doc : docs) {
          for (Point room : doc.getLocations()) {
            if (room.getName().equals(p.getValue().getName())){
              physician = physician + doc.getFirstName() + " " + doc.getLastName() + "\n";
              break;
            }
          }
        }
        return new ReadOnlyStringWrapper(physician);
      }
    });

    SearchField.setOnKeyPressed(new EventHandler<KeyEvent>() {
      @Override
      public void handle(KeyEvent event) {
          if (SearchField.isFocused()) {
            switch (event.getCode()) {
              case BACK_SPACE:
                if (!SearchField.getSelectedText().equals("")) {
                  isST = true;
                  SearchField.deleteText(SearchField.getSelection());
                }
                if (!SearchField.getText().equals("")) {
                  isBS = true;
                }
                break;
              case ENTER:
                SearchField.deselect();
                searchString = SearchField.getText();
                updatePhysicians(docs);
                updateRooms(rooms);
                break;
            }
          }
        }

    });

    SearchField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (!isST && !isBS) {
        if (searchMode == 0) {
          searchString = SearchField.getText();
          autoCompleteDoc();
          updatePhysicians(docs);
        } else if (searchMode == 1) {
          searchString = SearchField.getText();
          autoCompleteRoom();
          updateRooms(rooms);
        }
      } else if (isST && isBS) {
        isST = false;
        isBS = false;
      } else if (isBS) {
        searchString = SearchField.getText();
        updatePhysicians(docs);
        updateRooms(rooms);
        isBS = false;
      } else {
        isST = false;
      }
    });
    toggleDoc();
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
    SearchRoom.setStyle("-fx-background-color: white; -fx-text-fill: black;");
    updatePhysicians(docs);
    SearchMap.setVisible(false);
    PhysicianDirectory.setVisible(true);
    RoomDirectory.setVisible(false);
  }
  public void toggleRoom () {
    searchMode = 1;
    SearchRoom.setStyle("-fx-background-color: grey; -fx-text-fill: white;");
    SearchDoc.setStyle("-fx-background-color: white; -fx-text-fill: black;");
    updateRooms(rooms);
    SearchMap.setVisible(true);
    PhysicianDirectory.setVisible(false);
    RoomDirectory.setVisible(true);
  }


  public void updatePhysicians (List<Physician> HCs){
    HCOL.clear();
    if (searchString.equals("")) {
      HCOL.addAll(HCs);
    } else {
      HCOL.addAll(database.fuzzySearchPhysicians(searchString));
    }
    PhysicianDirectory.setItems(HCOL);
  }

  public void updateRooms (List<Point> Rooms){
    RMOL.clear();
    if (searchString.equals("")) {
      RMOL.addAll(Rooms);
    } else {
      RMOL.addAll(database.fuzzySearchPoints(searchString));
    }
    RoomDirectory.setItems(RMOL);
  }

  public void autoCompleteDoc () {
    if (!searchString.equals("")) {
      for (Physician doc : docs) {
        String docName = doc.getFirstName() + " " + doc.getLastName();
        if (docName.length() >= searchString.length()
            && docName.substring(0, searchString.length()).equalsIgnoreCase(searchString)) {
          final String newSearchString = doc.getFirstName() + " " + doc.getLastName();
          isST = true;
          SearchField.setText(newSearchString);
          Platform.runLater(() -> {
            if (SearchField.isFocused()) {
              SearchField.selectRange(searchString.length(), newSearchString.length());
            }
          });
          break;
        }
      }
    }
  }

  public void autoCompleteRoom () {
    if (!searchString.equals("")) {
      for (Point room : rooms) {
        String roomName = room.getName();
        if (roomName.length() >= searchString.length()
            && roomName.substring(0, searchString.length()).equalsIgnoreCase(searchString)) {
          final String newSearchString = room.getName();
          isST = true;
          SearchField.setText(newSearchString);
          Platform.runLater(() -> {
            if (SearchField.isFocused()) {
              SearchField.selectRange(searchString.length(), newSearchString.length());
            }
          });
          break;
        }
      }
    }
  }

  public void viewInfo () {
    Stage primaryStage = (Stage) anchorPane.getScene().getWindow();
    Physician selectedDoc = PhysicianDirectory.getSelectionModel().getSelectedItem();
    Point selectedRoom = RoomDirectory.getSelectionModel().getSelectedItem();
    if (searchMode == 0 && selectedDoc != null) {
      DMCBuilder(null, selectedDoc);
    } else if (searchMode == 1 && selectedRoom != null){
      DMCBuilder(selectedRoom, null);
    }
  }

  private void DMCBuilder (Point room, Physician doc) {
    Stage primaryStage = (Stage) anchorPane.getScene().getWindow();
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/DetailMenu.fxml"));
      DetailMenuController DC = new DetailMenuController(room, doc);
      loader.setController(DC);
      Pane mainPane = (Pane) loader.load();
      primaryStage.setScene(new Scene(mainPane, x_res, y_res));
      primaryStage.show();
    } catch (Exception e) {
      System.out.println("Cannot load main menu");
      e.printStackTrace();
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
    updatePhysicians(docs);
    updateRooms(rooms);
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
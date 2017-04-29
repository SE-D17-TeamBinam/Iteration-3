package UIControllers;

import Database.DatabaseController;
import Definitions.Coordinate;
import Definitions.Physician;
import Networking.Carrier;
import Networking.Emailer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.Astar;
import org.BFS;
import org.CentralController;
import org.DFS;
import org.ElevatorPoint;
import org.FindDirections;
import org.ListPoints;
import org.PathfindingStrategy;
import org.Point;
import org.StairPoint;

public class MapViewController extends CentralUIController implements Initializable {

  // define all ui elements
  @FXML
  private ImageView mapImage;
  @FXML
  private Pane mapViewPane;

  @FXML
  private ChoiceBox floorChoiceBox;
  @FXML
  private AnchorPane anchorPane;
  @FXML
  private Rectangle leftBar;

  @FXML
  private Button backButton;
  @FXML
  private Button AdminLogOff;
  @FXML
  private Pane adminPane;
  @FXML
  private Rectangle adminPaneRectangle;

  // Admin Pane fields
  @FXML
  private TextField xCoordField;
  @FXML
  private TextField yCoordField;
  @FXML
  private TextField floorField;
  @FXML
  private TextField nameField;
  @FXML
  private TextField idField;


  // User Pane fields
  @FXML
  private Text startLabel;
  @FXML
  private Text endLabel;
  @FXML
  private Button goButton;
  @FXML
  private Label selectedNameLabel;
  @FXML
  private Button newButton;
  @FXML
  private Button deleteButton;

  @FXML
  private Button saveButton;
  @FXML
  private Label mainFloorLabel;

  // The pane for point type selection
  @FXML
  private Pane typeSelectionPane;

  @FXML
  private Rectangle typeSelectionPaneRectangle;
  @FXML
  private RadioButton normalButton;
  @FXML
  private RadioButton elevatorButton;
  @FXML
  private RadioButton stairButton;

  private final ToggleGroup typeSelect = new ToggleGroup();

  // The pane for text directions
  @FXML
  private RadioButton emailButton;
  @FXML
  private RadioButton textButton;
  @FXML
  private TextField detailEntry;
  @FXML
  private ChoiceBox carrierBox;

  private final ToggleGroup directionSelect = new ToggleGroup();

  // The pane with the + and - on it
  @FXML
  private Pane zoomPane;


  @FXML
  private Pane helpPane;

  @FXML
  private Button beginConnectionButton;
  @FXML
  private Button deselectButton;
  @FXML
  private Button connectButton;

  @FXML
  private ChoiceBox startNodeBox;
  @FXML
  private ChoiceBox endNodeBox;

  @FXML
  private ImageView helpButton;

  @FXML
  private Text searchLabel;

  @FXML
  private ChoiceBox pathFindingChoiceBox;

  @FXML
  private VBox searchPaneVBox;

  @FXML
  private VBox choice1;
  @FXML
  private VBox choice2;
  @FXML
  private VBox choice3;

  @FXML
  private Label floorSearchLabel;

  @FXML
  private Label searchFieldLabel;

  @FXML
  private Label hospitalSearchLabel;

  @FXML
  private Label physicianSearchLabel;

  @FXML
  private Button searchGoButton;

  @FXML
  private Label searchTabLabel;

  @FXML
  private Pane progressPane;

  @FXML
  private ProgressBar progressBar;

  @FXML
  private TextField searchTextField;

  @FXML
  private ListView resultsList;

  @FXML
  private Pane textDirectionsPane;

  @FXML
  private Label textDirectionsTabLabel;

  @FXML
  private ImageView textDirectionsPaneTabImageView;

  @FXML
  private Pane userPane;

  @FXML
  private ImageView userPaneTabImageView;

  @FXML
  private Rectangle userPaneRectangle;

  @FXML
  private Rectangle textDirectionsTabRectangle;

  @FXML
  private Rectangle textDirectionsPaneRectangle;

  private double textDirectionsPaneTargetX;
  private int textDirectionsPaneVisible = 0;

  @FXML
  private Text textDirectionsLabel;

  @FXML
  private Text sendToMeLabel;

  @FXML
  private Pane emailPane;

  @FXML
  private ListView textDirectionsListView;

  private int searchType;

  private String searchString = "";
  private ArrayList<Point> results = new ArrayList<>();
  // allPoints

  private HashMap<String, Point> searchPoints = new HashMap<>();


  private int userPaneVisible = 1;

  private void initializeSearchChoices() {
    choose1();
  }

  private Point floorConnectFocus = null;

  private final double ZOOM_PANE_OFFSET_HORIZONTAL = 20;
  private final double ZOOM_PANE_OFFSET_VERTICAL = 20;

  // Controls how much the zoom changes at a time.
  private final double ZOOM_COEF = 0.05;
  private final int ZOOM_MIN = -20;
  private final int ZOOM_MAX = 20;
  private int current_zoom = 0;
  private double current_zoom_scale = 1;

  // Map Image positioning boundaries
  // the furthest to the right that the left side of the map image may move
  private double map_x_min = 150;
  // the furthest to the left that the right side of the map image may move
  private double map_x_max = 1300;
  // the furthest down that the top of the map image may move
  private double map_y_min = 125;
  // the furthest up that the bottom of the map image may move
  private double map_y_max = 750;

  // Difference in cursor location from map image location
  private double difX = 0;
  private double difY = 0;
  // Where the mouse drag was initiated
  private double mapPressedX;
  private double mapPressedY;

  ////////////////////////
  // Administrator Data //
  ////////////////////////

  // ArrayList of Points to maintain in memory
  private ArrayList<Point> floorPoints = new ArrayList<>();
  // ArrayList of Edges to help track for drawing
  private ArrayList<Connection> connections = new ArrayList<>();

  private ArrayList<Point> clipBoard = new ArrayList<>();

  // The currently selected point
  private Point pointFocus = null;

  // TODO this should be a ListPoints
  private ArrayList<Point> allPoints = new ArrayList<>();

  private ArrayList<Point> secondaryPointFoci = new ArrayList<>();

  private double userPaneTargetX = 0;

  private boolean saving = false;


  // For drawing the points
  private final double POINT_STROKE_WIDTH = 4;
  private final double POINT_RADIUS_MAX = 25;
  private final double POINT_RADIUS_MIN = 5;
  private double point_radius = 15;
  private final Color STAIR_POINT_COLOR = new Color(0, 1, 0, 1);
  private final Color ELEVATOR_POINT_COLOR = new Color(1, 0, 1, 1);
  private final Color POINT_COLOR = new Color(1, 0, 0, 1);
  private final Color POINT_STROKE = new Color(0, 0, 0, 1);
  private double PATHFINDING_LINE_MULT = 3;
  private final Color PATH_COLOR = new Color(1, 0, 0, 1);

  // For drawing connections between points
  private final double LINE_FILL = 8;
  private final Color LINE_COLOR = new Color(0, 0, 0, 1);

  private final Color PRIMARY_POINT_FOCUS_COLOR = new Color(1, 1, 0, 1);
  private final Color SECONDARY_POINT_FOCUS_COLOR = new Color(0, 0, 1, 1);
  private final Color SELECTION_RECTANGLE_FILL = new Color(0, 0.7, 1, 0.5);

  private double selectionRectangleX = 0;
  private double selectionRectangleY = 0;
  private Rectangle selectionRectangle = new Rectangle();

  // Tracks whether or not the mouse has been dragged since being pressed down
  private boolean mouseDragged = false;

  // The circles and lines that are currently drawn
  private HashMap<Point, Circle> circles = new HashMap<>();
  private HashMap<Connection, Line> lines = new HashMap<>();

  // Proxies the images for each floor
  private HashMap<Integer, Image> floorImages = new HashMap<>();

  private int maxID = 0;

  private class Connection {

    private Point start;
    private Point end;

    public String toString() {
      return start.toString() + "-" + end.toString();
    }

    public Connection(Point start, Point end) {
      this.start = start;
      this.end = end;
    }

    public Point getStart() {
      return start;
    }

    public Point getEnd() {
      return end;
    }

    @Override
    public boolean equals(Object o) {
      if (o.getClass() != this.getClass()) {
        return false;
      } else {
        Connection c = (Connection) o;
        boolean b = (
            (c.getStart().hashCode() == this.getStart().hashCode() && c.getEnd().hashCode() == this
                .getEnd().hashCode()) ||
                (c.getEnd().hashCode() == this.getStart().hashCode()
                    && c.getStart().hashCode() == this.getEnd().hashCode()));
        return b;
      }
    }

    // TODO - If it turns out that any hash codes are repeating, this needs to change
    @Override
    public int hashCode() {
      // I crie
      // Chances are this won't result in repeats.
      return start.hashCode() + end.hashCode();
    }
  }

  //-----/////////////-----//
  //-----// Methods //-----//
  //-----/////////////-----//

  ////////////////////
  // Initialization //
  ////////////////////


  @FXML
  public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
    initializeSearch();
    if (mapViewFlag == 1) {
      AdminLogOff.setVisible(false);
      helpButton.setVisible(false);
      initializeLanguageConfigs();
    } else {
      userPane.setVisible(false);
      textDirectionsPane.setVisible(false);
      selectedNameLabel.setVisible(false);
    }
    helpPane.setVisible(false);
    typeSelection();
    getMap();
    selectionRectangle.setStroke(Color.BLACK);
    selectionRectangle.setFill(SELECTION_RECTANGLE_FILL);
    mapViewPane.getChildren().add(selectionRectangle);
    initializeScene();
    initializeFloorChoiceBox();
    initializeMapImage();
    initializeGlobalTimer();
    initializeSearchChoices();
    setDirectionsOptions();
    repositionResultsList();
    initializePathFindingBox();
    findMaxID();
  }

  private void initializePathFindingBox() {
    ArrayList<PathfindingStrategy> strats = new ArrayList<>();
    PathfindingStrategy as = new Astar();
    strats.add(as);
    strats.add(new DFS());
    strats.add(new BFS());
    pathFindingChoiceBox.getItems().setAll(strats);
    pathFindingChoiceBox.getSelectionModel().selectedIndexProperty().addListener(
        new ChangeListener<Number>() {
          public void changed(ObservableValue ov, Number old_value, Number new_value) {
            CentralController.getCurrSession().setAlgorithm(
                (PathfindingStrategy) pathFindingChoiceBox.getItems().get((int) new_value));
          }
        });
  }

  // Used to track the first tick of the global timer
  boolean first = true;

  private void initializeGlobalTimer() {

    Timeline globalTimer = new Timeline(
        new KeyFrame(Duration.millis(1), new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {
            if(first){
              first = false;
              updateUserPane();
              if (searchingPoint != null) {
                int ind = allPoints.indexOf(searchingPoint);
                if(ind > -1) {
                  Point myPoint = allPoints.get(ind);
                  searchingPoint = null;
                  floorChoiceBox.setValue(myPoint.getFloor());
                  setPointFocus(myPoint);
                  setEnd(myPoint);
                }
              }
            }
            globalTimerActions();

          }
        }));
    globalTimer.setCycleCount(Timeline.INDEFINITE);
    globalTimer.play();
  }

  private void globalTimerActions() {
    updateProgressBar();
    repositionResultsList();
    animateUserPane();
    animateTextDirectionsPane();
    emailPane.setLayoutY(textDirectionsPaneRectangle.getHeight() - emailPane.getHeight() - 5);
    textDirectionsListView.setPrefHeight(emailPane.getLayoutY() - textDirectionsListView.getLayoutY() - 5);
  }

  private void repositionResultsList() {
    resultsList.setPrefHeight(
        userPaneRectangle.getHeight() - searchPaneVBox.getLayoutY() - resultsList.getLayoutY()
            - searchGoButton.getPrefHeight() - 50);
  }

  private void animateTextDirectionsPane() {
    double x = textDirectionsPane.getLayoutX();
    if (x < textDirectionsPaneTargetX) {
      textDirectionsPane.setLayoutX(x + 1);
      map_x_max = x + 1 + textDirectionsPaneTabImageView.getFitWidth();
      fixMapDisplayLocation();
      fixZoomPanePos();
    } else if (x > textDirectionsPaneTargetX) {
      textDirectionsPane.setLayoutX(x - 1);
      map_x_max = x - 1 + textDirectionsPaneTabImageView.getFitWidth();
      fixZoomPanePos();
    }
  }

  private void animateUserPane() {
    double x = userPane.getLayoutX();
    double amt;
    if (x < userPaneTargetX) {
      amt = x + 1;
    } else if (x > userPaneTargetX) {
      amt = x - 1;
    }else{
      amt = x;
    }
    userPane.setLayoutX(amt);
    map_x_max = amt + userPaneTabImageView.getFitWidth();
    fixZoomPanePos();
  }

  private void updateProgressBar() {
    if (saving) {
      progressBar.setProgress(((DatabaseController) database).progressBarPercentage);
      if (((DatabaseController) database).progressBarPercentage < 1) {
      } else {
        //TODO
        //TODO
        //TODO MAKE SURE RACE CONDITION IS FIXED
        //TODO
        //TODO
        saving = false;
        progressPane.setVisible(false);
        saveButton.setDisable(false);
      }
    }
  }

  private void initializeLanguageConfigs() {
    /* apply language configs */
    sendToMeLabel.setText(dictionary.getString("Send to Me", currSession.getLanguage()));
    searchGoButton.setText(dictionary.getString("Go", currSession.getLanguage()));
    floorSearchLabel.setText(dictionary.getString("Floor", currSession.getLanguage()));
    hospitalSearchLabel.setText(dictionary.getString("Hospital", currSession.getLanguage()));
    physicianSearchLabel.setText(dictionary.getString("Physicians", currSession.getLanguage()));
    searchLabel.setText(dictionary.getString("Search", currSession.getLanguage()) + ":");
    searchTabLabel.setText(dictionary.getString("Search", currSession.getLanguage()));
    textDirectionsTabLabel.setText(dictionary.getString("Text Directions", currSession.getLanguage()));
    textDirectionsLabel.setText(dictionary.getString("Text Directions", currSession.getLanguage()) + ":");
    startLabel.setText(dictionary.getString("Start", currSession.getLanguage()));
    endLabel.setText(dictionary.getString("End", currSession.getLanguage()));
    goButton.setText(dictionary.getString("Go", currSession.getLanguage()));
    backButton.setText(dictionary.getString("Back", currSession.getLanguage()));
    selectedNameLabel.setText(dictionary.getString("Name", currSession.getLanguage()) + ":");
    newButton.setText(dictionary.getString("New", currSession.getLanguage()));
    deleteButton.setText(dictionary.getString("Delete", currSession.getLanguage()));
    saveButton.setText(dictionary.getString("Save Map", currSession.getLanguage()));
    mainFloorLabel.setText(dictionary.getString("Floor", currSession.getLanguage()));

  }

  private boolean pathfinding = false;

  private void clearMapDisplay(){
    setPointFocus(null);
    clearSecondaryPointFoci();
    mapViewPane.getChildren().clear();
    mapViewPane.getChildren().add(mapImage);
    mapViewPane.getChildren().add(selectionRectangle);
    circles.clear();
    lines.clear();
    connections.clear();
  }

  private void switchFloors(int floor) {
    currentFloor = floor;
    clearMapDisplay();
    ListPoints lp = new ListPoints(allPoints);
    floorPoints = lp.getFloor(floor).getPoints();
    if(pathfinding){
      displayPoints(pathPoints);
    }else {
      displayPoints(floorPoints);
    }

    // Setup the Point choice boxes on the left

    Point start = (Point) startNodeBox.getValue();
    Point end = (Point) endNodeBox.getValue();

    // Clear them first
    startNodeBox.getItems().clear();
    endNodeBox.getItems().clear();

    if(start != null && start.getFloor() != currentFloor) {
      startNodeBox.getItems().add(start);
      endNodeBox.getItems().add(start);
    }
    if(end != null && end.getFloor() != currentFloor) {
      startNodeBox.getItems().add(end);
      endNodeBox.getItems().add(end);
    }


    ArrayList<Point> selectablePoints = new ArrayList<Point>();
    // Now add the points on the current floor
    for (Point p : floorPoints) {
      if (p.getName() == null || p.getName().equals("") || p.getName().equals("null") || p.getName()
          .equals("ELEVATOR")) {
      }else{
        selectablePoints.add(p);
      }
    }
    startNodeBox.getItems().addAll(selectablePoints);
    endNodeBox.getItems().addAll(selectablePoints);

    startNodeBox.setValue(start);
    endNodeBox.setValue(end);

    refreshListView();
  }

  private void typeSelection() {
    normalButton.setToggleGroup(typeSelect);
    normalButton.setSelected(true);
    stairButton.setToggleGroup(typeSelect);
    elevatorButton.setToggleGroup(typeSelect);
    normalButton.setUserData("Normal");
    stairButton.setUserData("Stair");
    elevatorButton.setUserData("Elevator");
  }

  // Add listeners for resizing the screen
  private void initializeScene() {
    addResolutionListener(anchorPane);
    setBackground(anchorPane);
    leftBar.toBack();
    mapViewPane.toBack();
    backgroundView.toBack();
    if (mapViewFlag != 3) {
      adminPane.setVisible(false);
      typeSelectionPane.setVisible(false);
    } else {
      fixZoomPanePos();
    }
  }

  @Override
  public void customListenerX() {
    map_x_max = x_res - adminPaneRectangle.getWidth() * (adminPane.isVisible() ? 1 : 0);
    adminPane.setLayoutX(x_res - adminPaneRectangle.getWidth());
    typeSelectionPane.setLayoutX(adminPane.getLayoutX());
    AdminLogOff.setLayoutX(x_res - AdminLogOff.getPrefWidth() - 5);
    fixMapDisplayLocation();
    updateUserPane();
    updateTextDirectionsPane();
  }

  @FXML
  private HBox pointNameHBox;

  @Override
  public void customListenerY() {
    leftBar.setHeight(y_res - banner.getHeight());
    leftBar.setY(banner.getHeight());
    adminPane.setLayoutY(leftBar.getY());
    map_y_max = y_res;
    adminPaneRectangle.setHeight((y_res - banner.getHeight()) / 2);
    typeSelectionPane.setLayoutY(adminPane.getLayoutY() + adminPaneRectangle.getHeight());
    typeSelectionPaneRectangle.setHeight(adminPaneRectangle.getHeight());
    fixMapDisplayLocation();
    updateUserPane();
    updateTextDirectionsPane();
    helpButton.setLayoutY(y_res - 60);
    helpPane.setLayoutY(y_res - 540);
    repositionResultsList();
    pointNameHBox.setLayoutY(y_res - pointNameHBox.getPrefHeight() - 2);
  }


  private void displayPoints(ArrayList<Point> points){
    for (int i = 0; i < points.size(); i++) {
      Point p = points.get(i);
      // TODO
      // Replace the first 'true' with point.shouldOnlyBeSeenByStaff
      // Replace second 'true' with !point.shouldOnlyBeSeenByStaff
      if((true && mapViewFlag >= 2) || (true && !p.getBlocked() && mapViewFlag == 1)) {
        if (p.getFloor() == currentFloor && (!pathfinding || (pathfinding && pathPoints.contains(p)))) {
          addVisualNodesForPoint(p, points);
        }
      }
    }
  }

  private int currentFloor = 1;

  // Add values to the floor selector, add a listener, and set its default value
  private void initializeFloorChoiceBox() {
    // Add options to change floors
    allFloors.add(7);
    allFloors.add(6);
    allFloors.add(5);
    allFloors.add(4);
    allFloors.add(3);
    allFloors.add(2);
    allFloors.add(1);
    floorChoiceBox.getItems().addAll(allFloors);
    // Add a ChangeListener to the floorChoiceBox
    floorChoiceBox.getSelectionModel().selectedIndexProperty().addListener(
        new ChangeListener<Number>() {
          public void changed(ObservableValue ov, Number old_value, Number new_value) {
            // Change the image that's being displayed when the input changes
            currentFloor = (int) floorChoiceBox.getItems().get((int) new_value);
            Image new_img;
            Image floorImg = floorImages.get(currentFloor);
            if (floorImages.get(currentFloor) == null) {
              new_img = new Image(
                  "/floor_plans/" + currentFloor
                      + "floor.png");
              floorImages.put(currentFloor, new_img);
            } else {
              new_img = floorImg;
            }
            mapImage.setImage(new_img);
            switchFloors(currentFloor);
          }
        });
    floorChoiceBox.setValue(1);
  }


  /**
   * Sets the defaults for the map image upon loading this scene
   */
  private void initializeMapImage() {
    mapImage.setFitHeight(mapImage.getImage().getHeight());
    mapImage.setFitWidth(mapImage.getImage().getWidth());
    for (int i = 0; i < 20; i++) {
      zoomOut();
    }
    moveMapImage(-302, -130);
  }

  /**
   * Generates Circles, Lines, and Connections for a given point
   * Circles are mapped into circles with the associated Point as the key
   * Connections are stored into connections
   * Lines are mapped into lines with the associated Connection as the key
   * Also adds listeners to Circles
   *
   * @param p the Point to generate visual JavaFX.Node's for
   */
  private void addVisualNodesForPoint(Point p, ArrayList<Point> showingPoints) {
    // For every neighbor, turn it into a connection if it doesn't exist
    // Also checks to make sure that each neighbor is contained by floorPoints
    for (int j = 0; j < p.getNeighbors().size(); j++) {
      if (showingPoints.contains(p.getNeighbors().get(j)) && p.getNeighbors().get(j).getFloor() == currentFloor) {
        Connection c = new Connection(p, p.getNeighbors().get(j));
        if (mapViewFlag == 3 || pathfinding) {
          addVisualConnection(c);
          if (pathfinding) {
            lines.get(c).setStrokeWidth(
                LINE_FILL * current_zoom_scale * PATHFINDING_LINE_MULT * (point_radius
                    / POINT_RADIUS_MAX));
            lines.get(c).setStroke(PATH_COLOR);
          }
        }
      }

    }
    // Now, for every point, create a Circle
    Coordinate coord = pixelToCoordinate(new Coordinate(p.getXCoord(), p.getYCoord()));
    Circle c = new Circle(coord.getX(), coord.getY(), point_radius * current_zoom_scale);
    c.setStroke(POINT_STROKE);
    c.setStrokeWidth(POINT_STROKE_WIDTH * current_zoom_scale);

    if (p.isStair()) {
      c.setFill(STAIR_POINT_COLOR);
    } else if (p.isElevator()) {
      c.setFill(ELEVATOR_POINT_COLOR);
    } else {
      if ((p.getName() == null || p.getName().equals("") || p.getName().equals("null"))
          && mapViewFlag == 3) {
        c.setFill(Color.GRAY);
      } else {
        c.setFill(POINT_COLOR);
      }
    }
    if (mapViewFlag == 3 || !(p.getName() == null || p.getName().equals("") || p.getName()
        .equals("null"))) {
      if (circles.get(p) == null) {
        circles.put(p, c);
        mapViewPane.getChildren().add(c);
        addCircleListeners(c, p);
      }
    }
  }

  /**
   * Creates the Line associated with a given Connection
   * Adds the Line into lines, mapped with the Connection as a key
   *
   * @param c the Connection to add a Line for
   */
  private void addVisualConnection(Connection c) {
    if (!connections.contains(c)) {
      connections.add(c);
      Line l = new Line();
      l.setStroke(LINE_COLOR);
      lines.put(c, l);
      addLineListeners(l, c);
      updateLineForConnection(c);
      // ensures that lines will always be drawn behind points
      mapViewPane.getChildren().add(1, l);
    }else{
    }
  }

  /**
   * Updates the locations for all visual Lines and Points to account for the zoom scale
   */
  private void updateVisualNodes() {
    for (Point p1 : circles.keySet()) {
      updateCircleForPoint(p1);
    }
    for (Connection c1 : lines.keySet()) {
      updateLineForConnection(c1);
    }
  }

  /**
   * Updates the Lines for a given Point to account for zoom
   *
   * @param p the Point whose Lines should be updated
   */
  private void updateLinesForPoint(Point p) {
    ArrayList<Point> neighbors = p.getNeighbors();
    for (int i = 0; i < neighbors.size(); i++) {
      Connection c = new Connection(p, neighbors.get(i));
      updateLineForConnection(c);
    }
  }

  /**
   * Updates the visual Line for a given Connection to account for zoom
   *
   * @param c the Connection whose Line should be updated
   */
  private void updateLineForConnection(Connection c) {
    Line l = lines.get(c);
    if (l != null) {
      Point start = c.getStart();
      Point end = c.getEnd();
      Coordinate startCoord = pixelToCoordinate(
          new Coordinate(start.getXCoord(), start.getYCoord()));
      Coordinate endCoord = pixelToCoordinate(new Coordinate(end.getXCoord(), end.getYCoord()));
      l.setStartX(startCoord.getX());
      l.setStartY(startCoord.getY());
      l.setEndX(endCoord.getX());
      l.setEndY(endCoord.getY());
      l.setStrokeWidth(
          LINE_FILL * current_zoom_scale * (pathfinding ? PATHFINDING_LINE_MULT * (point_radius
              / POINT_RADIUS_MAX) : 1));
    } else {
      // This could be when dragging nodes that are connected to other floors
    }
  }

  /**
   * Updates the visual Circle for a given Point to account for zoom
   *
   * @param p the Point whose Circle should be updated
   */
  private void updateCircleForPoint(Point p) {
    Coordinate coord = pixelToCoordinate(new Coordinate(p.getXCoord(), p.getYCoord()));
    Circle c = circles.get(p);
    c.setCenterX(coord.getX());
    c.setCenterY(coord.getY());
    c.setRadius(point_radius * current_zoom_scale);
    c.setStrokeWidth(POINT_STROKE_WIDTH * current_zoom_scale);
  }

  /**
   * Removes the visual Line associated with a given Connection
   * Removes the Connection from connections
   *
   * @param c the Connection to remove
   */
  private void removeVisualConnection(Connection c) {
    mapViewPane.getChildren().remove(lines.get(c));
    connections.remove(c);
    lines.remove(c);
  }

  private void addCircleListeners(Circle c, Point p) {
    c.setCursor(Cursor.HAND);
    c.setOnMouseClicked(e -> circleMouseClicked(e, p, c));
    c.setOnMouseDragged(e -> circleMouseDragged(e, p, c));
    c.setOnMousePressed(e -> circleMousePressed(e, p, c));
    c.setOnMouseReleased(e -> circleMouseReleased(e, p, c));
    c.setOnMouseEntered(e -> circleMouseEntered(e, p, c));
    c.setOnScroll(e -> circleMouseScrolled(e, p, c));
  }

  private void addLineListeners(Line l, Connection c) {
    l.setOnMouseDragged(e -> lineMouseDragged(e, c, l));
    l.setOnMouseClicked(e -> lineMouseClicked(e, c, l));
    l.setOnMousePressed(e -> lineMousePressed(e, c, l));
  }


  private void addPointToSecondarySelection(Point p) {
    if (secondaryPointFoci.contains(p)) {

    } else {
      if (p.equals(pointFocus)) {
        setPointFocus(null);
      }
      secondaryPointFoci.add(p);
      circles.get(p).setStroke(SECONDARY_POINT_FOCUS_COLOR);
    }
  }

  private void removePointFromSecondarySelection(Point p) {
    if (!secondaryPointFoci.contains(p)) {

    } else {
      secondaryPointFoci.remove(p);
      circles.get(p).setStroke(POINT_STROKE);
    }
  }

  private void togglePointToSecondarySelection(Point p) {
    if (p.equals(pointFocus)) {
      setPointFocus(null);
    }
    if (!secondaryPointFoci.contains(p)) {
      secondaryPointFoci.add(p);
      circles.get(p).setStroke(SECONDARY_POINT_FOCUS_COLOR);
    } else {
      secondaryPointFoci.remove(p);
      circles.get(p).setStroke(POINT_STROKE);
    }
  }

  private void clearSecondaryPointFoci() {
    // need to clone because foreach doesn't like modification while looping
    for (Point p : (ArrayList<Point>) secondaryPointFoci.clone()) {
      circles.get(p).setStroke(POINT_STROKE);
      secondaryPointFoci.remove(p);
    }
  }


  private void setFloorConnectFocus(Point newFocus) {
    // newFocus will be pointFocus initially.
    if (newFocus != null && newFocus.isElevator()) { // TODO should include stairs in the future
      setPointFocus(null);
      floorConnectFocus = newFocus;
      connectButton.setDisable(false);
      deselectButton.setDisable(false);
      beginConnectionButton.setDisable(true);
    } else {
      connectButton.setDisable(true);
      deselectButton.setDisable(true);
      beginConnectionButton.setDisable(false);
    }
  }


  private void setPointFocus(Point newFocus) {
    if (secondaryPointFoci.contains(newFocus)) {
      togglePointToSecondarySelection(newFocus);
    }
    if (pointFocus != null) {
      Circle circ = circles.get(pointFocus);
      if (circ != null) {
        circ.setStroke(POINT_STROKE);
      }
    }
    pointFocus = newFocus;

    String xText = "";
    String yText = "";
    String floorText = "";
    String nameText = "";
    String idText = "";
    if (newFocus != null && circles.get(newFocus) != null) {
      System.out.println(newFocus.getId());
      circles.get(newFocus).setStroke(PRIMARY_POINT_FOCUS_COLOR);
      xText = "" + pointFocus.getXCoord();
      yText = "" + pointFocus.getYCoord();
      floorText = "" + pointFocus.getFloor();
      nameText = pointFocus.getName();
      idText = "" + pointFocus.getId();
    } else {
      mapViewPane.requestFocus();
    }
    if (mapViewFlag == 3) {
      xCoordField.setText("" + xText);
      yCoordField.setText("" + yText);
      floorField.setText("" + floorText);
      nameField.setText(nameText);
      idField.setText(idText);
      if (nameText == "" && newFocus != null) {
        nameField.requestFocus();
      }
    }
    selectedNameLabel
        .setText(dictionary.getString("Name", currSession.getLanguage()) + ": " + nameText);
  }


  private void movePoint(Point p, Coordinate c) {
    if (c.getX() > mapImage.getImage().getWidth()) {
      c.setX(mapImage.getImage().getWidth());
    } else if (c.getX() < 0) {
      c.setX(0);
    }
    if (c.getY() > mapImage.getImage().getHeight()) {
      c.setY(mapImage.getImage().getHeight());
    } else if (c.getY() < 0) {
      c.setY(0);
    }
    p.setXCoord(c.getX());
    p.setYCoord(c.getY());
    updateCircleForPoint(p);
    updateLinesForPoint(p);
  }


  // Fixes the location of the zoom buttons and label, vertically and horizontally
  private void fixZoomPanePos() {
    setZoomPaneY(y_res - zoomPane.getPrefHeight() - ZOOM_PANE_OFFSET_VERTICAL);

    double txt = (textDirectionsPane.getLayoutX() + textDirectionsPaneTabImageView.getFitWidth());
    double usr = (userPane.getLayoutX() + userPaneTabImageView.getFitWidth());

    setZoomPaneX(
        (textDirectionsPane.isVisible() ? (txt < usr ? txt : usr) : x_res)
            - zoomPane.getPrefWidth() - ZOOM_PANE_OFFSET_HORIZONTAL
            - adminPaneRectangle.getWidth() * (adminPane.isVisible() ? 1 : 0));


  }

  // Change the zoom pane's horizontal location
  private void setZoomPaneX(double newX) {
    zoomPane.setLayoutX(newX);
  }

  // Change the zoom pane's vertical location
  private void setZoomPaneY(double newY) {
    zoomPane.setLayoutY(newY);
  }

  // Stress Test for displaying points.
  private void addRandomNodes(int count) {
    Point point = null;
    for (int i = 0; i < count; i++) {
      double xCoord = Math.random() * 5000;
      double yCoord = Math.random() * 2500;
      Point newPoint = new Point(xCoord, yCoord, 1);
      newPoint.setName("");
      if (point == null) {
      } else {
        newPoint.connectTo(point);
      }
      point = newPoint;
      allPoints.add(newPoint);
    }
  }


  private void getMap() {
    allPoints = database.getPoints();
//    for (int i = 0; i < allPoints.size(); i++) {
//      System.out.println("id : " + allPoints.get(i).getId());
//      for (int k = 0; k < allPoints.get(i).getNeighbors().size(); k++) {
//        System.out.println("neighbor id : " + allPoints.get(i).getNeighbors().get(k).getId());
//      }
//    }
  }

  private void updateSelected() {
    if (pointFocus != null) {
      pointFocus
          .setXCoord(xCoordField.getLength() == 0 ? 0 : Double.parseDouble(xCoordField.getText()));
      pointFocus
          .setYCoord(yCoordField.getLength() == 0 ? 0 : Double.parseDouble(yCoordField.getText()));
      pointFocus.setFloor(floorField.getText() == "" ? 0 : Integer.parseInt(floorField.getText()));
      pointFocus.setName(nameField.getLength() == 0 ? "" : nameField.getText());
      movePoint(pointFocus, new Coordinate(pointFocus.getXCoord(), pointFocus.getYCoord()));
    }
  }

  ///////////////////////////
  // Scene Control Methods //
  ///////////////////////////

  // Changes the location of the map, such that it is within the maintained bounds
  private void moveMapImage(double x, double y) {
    mapViewPane.setLayoutX(x);
    mapViewPane.setLayoutY(y);
    fixMapDisplayLocation();
  }

  private void fixMapDisplayLocation() {
    // Make sure that the top of the map is above the minimum
    boolean isAbove = mapViewPane.getLayoutY() < map_y_min;
    // Make sure that the bottom of the map is below the maximum
    boolean isBelow = (mapViewPane.getLayoutY() + mapImage.getFitHeight()) > map_y_max;
    // Make sure that the left of the map is to the left of the minimum
    boolean isLeft = (mapViewPane.getLayoutX()) < map_x_min;
    // Make sure that the right of the map is to the right of the maximum
    mapImage.getImage().getWidth();
    mapImage.getFitWidth();
    boolean isRight = (mapViewPane.getLayoutX() + mapImage.getFitWidth()) > map_x_max;
    // Make the assertions, move the map
    if (isAbove && isBelow) {
    } else if (!isAbove && isBelow) {
      mapViewPane.setLayoutY(map_y_min);
    } else if (isAbove && !isBelow) {
      mapViewPane.setLayoutY(map_y_max - mapImage.getFitHeight());
    } else {
      // The map is too small, not sure what to do.
    }
    if (isLeft && isRight) {
    } else if (!isLeft && isRight) {
      mapViewPane.setLayoutX(map_x_min);
    } else if (isLeft && !isRight) {
      mapViewPane.setLayoutX(map_x_max - mapImage.getFitWidth());
    } else {
      // The map is too small, not sure what to do.
    }
  }

  // Takes a point in the scene and returns the pixel on the map that corresponds
  private Coordinate coordinateToPixel(Coordinate p) {
    double xRelToMapOrigin = p.getX();
    double yRelToMapOrigin = p.getY();
    return new Coordinate(xRelToMapOrigin / current_zoom_scale,
        yRelToMapOrigin / current_zoom_scale);

  }

  // Takes the pixel on the map image and returns the position in the scene where it corresponds to.
  // Useful for drawing
  private Coordinate pixelToCoordinate(Coordinate p) {
    double actualX = p.getX() * current_zoom_scale;
    double actualY = p.getY() * current_zoom_scale;
    return new Coordinate(actualX, actualY);
  }

  // Either zooms in or out
  // if delta is true, the zoom increases
  // if delta is false, the zoom decreases
  // Also distributes spacing around the image, 50% of the difference in size on each side
  private void changeZoom(boolean delta) {
    updateMapScale(delta);
    fixMapDisplayLocation();
    updateVisualNodes();
  }

  private void updateMapScale(boolean delta) {
    // Find the old dimensions, will be used for making the zooming look better
    double oldWidth = mapImage.getFitWidth();
    double oldHeight = mapImage.getFitHeight();

    // How to change zoom in one line
    current_zoom = (delta ?
        (current_zoom >= ZOOM_MAX ? ZOOM_MAX :
            current_zoom + 1 + ((int) (0 * (current_zoom_scale *= (1 + ZOOM_COEF))))) : // Zoom in
        (current_zoom <= ZOOM_MIN ? ZOOM_MIN :
            current_zoom - 1 + (int) (0 * (current_zoom_scale /= (1 + ZOOM_COEF))))); // Zoom out
    mapImage.setFitWidth(current_zoom_scale * mapImage.getImage().getWidth());
    mapImage.setFitHeight(current_zoom_scale * mapImage.getImage().getHeight());

    // Find the new dimensions
    double newWidth = mapImage.getFitWidth();
    double newHeight = mapImage.getFitHeight();
    // Move the image so that the zoom appears to be in the center of the image
    moveMapImage(mapViewPane.getLayoutX() - (newWidth - oldWidth) / 2,
        mapViewPane.getLayoutY() - (newHeight - oldHeight) / 2);
  }

  //-----///////////////-----//
  //-----// Listeners //-----//
  //-----///////////////-----//

  ///////////////////////
  // Control Listeners //
  ///////////////////////

  @FXML
  private void clearSearchField(){
    searchTextField.clear();
  }

  @FXML
  private void beginConnectionButtonClicked() {
    setFloorConnectFocus(pointFocus);
  }

  @FXML
  private void connectButtonClicked() {
    if (pointFocus != null) {
      if (pointFocus.isElevator()) { // TODO should include stairs in the future
        if (pointFocus.getFloor() != floorConnectFocus.getFloor()) {
          pointFocus.connectTo(floorConnectFocus);
          System.out.println("Connected Elevator on floor " + floorConnectFocus.getFloor()
              + " to Elevator on floor " + pointFocus.getFloor());
          setFloorConnectFocus(null);
        } else {
          System.out.println("Can't use this button to connect nodes on the same floor");
        }
      } else {
        System.out.println("You may only connect this Elevator to another Elevator.");
      }
    }
  }

  @FXML
  private void deselectButtonClicked() {
    setFloorConnectFocus(null);
  }


  private void sortFloorChoiceBox(){
    floorChoiceBox.getItems().sort(new Comparator() {
      @Override
      public int compare(Object o1, Object o2) {
        if(o1.getClass() == Integer.class && o2.getClass() == Integer.class){
          int o11 = (int) o1;
          int o22 = (int) o2;
          if(o11 < o22){
            return 1;
          }else if(o11 == o22){
            return 0;
          }else{
            return -1;
          }
        }else{
          return 0;
        }
      }
    });
  }

  @FXML
  private void clearButtonClicked() {
    startNodeBox.getSelectionModel().clearSelection();
    endNodeBox.getSelectionModel().clearSelection();
    pathfinding = false;
    switchFloors(currentFloor);
    saveButton.setDisable(false);
    goButton.setDisable(false);
    directions = "";
    textDirectionsListView.getItems().clear();
    clearMapDisplay();
    displayPoints(floorPoints);
    startNodeBox.setDisable(false);
    endNodeBox.setDisable(false);
    for(int i : allFloors){
      if(!floorChoiceBox.getItems().contains(i)){
        floorChoiceBox.getItems().add(i);
      }
    }
    sortFloorChoiceBox();
  }

  @FXML
  private void setEndButtonClicked() {
    setEnd(getSelectedPointInSearch());
  }

  private void setEnd(Point newEnd){
    if(newEnd != null){
      endNodeBox.getSelectionModel().clearSelection();
      if(newEnd.getFloor() == currentFloor){
        endNodeBox.setValue(newEnd);
      }else{
        endNodeBox.getItems().add(newEnd);
        startNodeBox.getItems().add(newEnd);
        endNodeBox.setValue(newEnd);
      }
    }
  }

  private void setStart(Point newStart){
    if(newStart != null){
      startNodeBox.getSelectionModel().clearSelection();
      if(newStart.getFloor() == currentFloor){
        startNodeBox.setValue(newStart);
      }else{
        startNodeBox.getItems().add(newStart);
        endNodeBox.getItems().add(newStart);
        startNodeBox.setValue(newStart);
      }
    }
  }

  @FXML
  private void setStartButtonClicked() {
    setStart(getSelectedPointInSearch());
  }

  @FXML
  private void choose1() {
    searchType = 1;
    choice1.setStyle("-fx-background-color: gray");
    choice2.setStyle("-fx-background-color: lightgray");
    choice3.setStyle("-fx-background-color: lightgray");
    refreshListView();
  }

  @FXML
  private void choose2() {
    // Searching Points
    searchType = 2;
    choice1.setStyle("-fx-background-color: lightgray");
    choice2.setStyle("-fx-background-color: gray");
    choice3.setStyle("-fx-background-color: lightgray");
    refreshListView();
  }

  @FXML
  private void choose3() {
    // Searching Physicians
    searchType = 3;
    choice1.setStyle("-fx-background-color: lightgray");
    choice2.setStyle("-fx-background-color: lightgray");
    choice3.setStyle("-fx-background-color: gray");
    refreshListView();
  }

  @FXML
  private void toggleUserPane() {
    userPaneVisible = ~userPaneVisible & 0x1; // toggles 1 or 0
    if(userPaneVisible == 1 && textDirectionsPaneVisible == 1){
      toggleTextDirectionsPane();
    }
    userPaneTabImageView.setImage(new Image("/icons/tab" + userPaneVisible + ".png"));
    userPaneTargetX =
        x_res - userPane.getWidth() * userPaneVisible
            - (~userPaneVisible & 0x1) * userPaneTabImageView
            .getFitWidth();
  }

  @FXML
  private void toggleTextDirectionsPane() {
    textDirectionsPaneVisible = ~textDirectionsPaneVisible & 0x1; // toggles 1 or 0
    if(userPaneVisible == 1 && textDirectionsPaneVisible == 1){
      toggleUserPane();
    }
    textDirectionsPaneTabImageView
        .setImage(new Image("/icons/tab" + textDirectionsPaneVisible + ".png"));
    textDirectionsPaneTargetX =
        x_res - textDirectionsPane.getWidth() * textDirectionsPaneVisible
            - (~textDirectionsPaneVisible & 0x1) * textDirectionsPaneTabImageView
            .getFitWidth();
  }

  private void updateTextDirectionsPane() {
    textDirectionsPaneTargetX =
        x_res - textDirectionsPane.getWidth() * textDirectionsPaneVisible
            - (~textDirectionsPaneVisible & 0x1) * textDirectionsPaneTabImageView
            .getFitWidth();
    textDirectionsPaneRectangle.setHeight(y_res - textDirectionsPane.getLayoutY());
    textDirectionsPane
        .setLayoutY(bannerView.getImage().getHeight() + textDirectionsTabRectangle.getHeight());
    textDirectionsPane.setLayoutX(textDirectionsPaneTargetX);
    map_x_max = textDirectionsPaneTargetX + textDirectionsPaneTabImageView.getFitWidth();
    fixZoomPanePos();
    emailPane.setLayoutY(textDirectionsPaneRectangle.getHeight() - emailPane.getHeight() - 5);


  }

  private void updateUserPane() {
    userPaneTargetX =
        x_res - userPane.getWidth() * userPaneVisible
            - (~userPaneVisible & 0x1) * userPaneTabImageView
            .getFitWidth();
    userPaneRectangle.setHeight(y_res - bannerView.getImage().getHeight());
    userPane.setLayoutY(bannerView.getImage().getHeight() - 1);
    userPane.setLayoutX(userPaneTargetX);
    map_x_max = userPaneTargetX + userPaneTabImageView.getFitWidth();
    fixZoomPanePos();
  }

  @FXML
  private void increaseFloorButtonClicked() {
    if (currentFloor >= (int) floorChoiceBox.getItems().get(0)) { // TODO Shouldn't hard code this
      floorChoiceBox.setValue(currentFloor);
    } else {
      floorChoiceBox.setValue(currentFloor + 1);
    }
  }

  @FXML
  private void decreaseFloorButtonClicked() {
    if (currentFloor <= 1) { // TODO shouldn't hard code this - could go higher
      floorChoiceBox.setValue(1);
    } else {
      floorChoiceBox.setValue(currentFloor - 1);
    }
  }

  private ArrayList<Point> pathPoints = new ArrayList<>();
  private ArrayList<Integer> allFloors = new ArrayList<>();
  private HashSet<Integer> showingFloors = new HashSet<>();

  @FXML
  private void drawPathButtonClicked() {
    Point start = (Point) startNodeBox.getSelectionModel().getSelectedItem();
    Point end = (Point) endNodeBox.getSelectionModel().getSelectedItem();
    // If points are selected, then begin finding a path
    if (start != null && end != null) {
      // try to get a path first, so that if it gets an exception it doesn't look bad
      ListPoints lp = new ListPoints(allPoints);
      pathPoints = lp.executeStrategy(start, end);
      // Set the pathfinding flag to true
      pathfinding = true;
      // Disable stuff that shouldn't be pressed while showing path
      startNodeBox.setDisable(true);
      endNodeBox.setDisable(true);
      saveButton.setDisable(true);
      goButton.setDisable(true);
      // Update the floors that appear in the floor selector
      // Unfortunate, but this can't be done efficiently
      showingFloors = new HashSet<Integer>();
      for(Point p : pathPoints){
        showingFloors.add(p.getFloor());
      }
      for(int i = 0; i < floorChoiceBox.getItems().size(); i++){
        Object o = floorChoiceBox.getItems().get(i);
        if(!showingFloors.contains(o)){
          floorChoiceBox.getItems().remove(o);
          i--;
        }
      }
      floorChoiceBox.setValue(start.getFloor());
      // Display the path on the map
      clearMapDisplay();
      displayPoints(pathPoints);
      // Get Text Directions
      displayTextDirections(pathPoints);
      if(textDirectionsPaneVisible == 0){
        toggleTextDirectionsPane();
      }
    }
  }

  private void displayTextDirections(ArrayList<Point> path){
    directions = "";
    FindDirections td = new FindDirections();
    ArrayList<String> directions = td.getTextDirections(path);
    for(int i = 0; i < directions.size(); i++) {
      String s = directions.get(i);
      if (i < directions.size() - 1){
        this.directions += s + ", ";
      }else{
        this.directions += s + ".";
      }
      // Now add the string and associated icon to an hbox, then add the hbox to the list
      VBox vbox = new VBox();
      HBox item = new HBox();
      item.setAlignment(Pos.CENTER);
      Image iconImg = directionToImage(s);
      ImageView iconView = new ImageView(iconImg);
      iconView.setFitWidth(40);
      iconView.setFitHeight(40);
      String maxString = "" + directions.size() + ". ";
      String label = (i + 1) + ". ";
      for(int k = 0; k < maxString.length() - label.length(); k++){
        label = "  " + label;
      }
      item.setMaxWidth(200);
      item.getChildren().add(new Label(label));
      item.getChildren().add(iconView);
      Text step = new Text(s);
      step.setWrappingWidth(115);
      Separator sep1 = new Separator();
      sep1.setVisible(false);
      item.getChildren().add(sep1);
      item.getChildren().add(step);

      Separator sep2 = new Separator();
      sep2.setVisible(false);
      vbox.getChildren().add(sep2);
      vbox.getChildren().add(item);

      Separator sep3 = new Separator();
      sep3.setVisible(false);
      vbox.getChildren().add(sep3);


      textDirectionsListView.getItems().add(vbox);

    }
  }

  private Image directionToImage(String directions){
    Image out = new Image("/icons/straight.png");
    if(directions.contains("left")){
      out = new Image("/icons/left.png");
    }else if(directions.contains("right")){
      out = new Image("/icons/right.png");

    }else if(directions.contains("straight")){
      out = new Image("/icons/straight.png");

    }else if(directions.contains("destination")){
      out = new Image("/icons/destination.png");
    }else if(directions.contains("around")){
      out = new Image("/icons/turn-around.png");
    }
    return out;
  }

  @FXML
  private void zoomIn() {
    changeZoom(true);
  }

  @FXML
  private void zoomOut() {
    changeZoom(false);
  }

  @FXML
  private void yCoordFieldKeyTyped(KeyEvent e) {
    if (!Character.isDigit(e.getCharacter().charAt(0))) {
      e.consume(); // throws out the KeyEvent before it can reach the text field
    }
  }

  @FXML
  private void xCoordFieldKeyTyped(KeyEvent e) {
    if (!Character.isDigit(e.getCharacter().charAt(0))) {
      e.consume(); // throws out the KeyEvent before it can reach the text field
    }
  }

  @FXML
  private void coordFieldReleased(KeyEvent e) {
    if(pointFocus != null){
      updateSelected();
    }
  }

  @FXML
  private void nameFieldKeyTyped(KeyEvent e) {
    if (pointFocus != null) {
      updateSelected();
      if (nameField.getText() != null && nameField.getText().length() > 0) {
        circles.get(pointFocus).setFill(POINT_COLOR);
      } else {
        circles.get(pointFocus).setFill(Color.GRAY);
      }
    }
  }

  @FXML
  private void deleteButtonClicked(MouseEvent e) {
    deletePoints(e.isControlDown());
  }

  private void deletePoints(boolean ctrl) {
    if (!ctrl) {
      if (pointFocus == null) {
        return;
      }
      ArrayList<Point> neighbors = (ArrayList<Point>) pointFocus.getNeighbors().clone();
      for (Point p : neighbors) {
        Connection c = new Connection(pointFocus, p);
        p.getNeighbors().remove(pointFocus); // TODO should be replaced by a method
        pointFocus.getNeighbors().remove(p);
        removeVisualConnection(c);
      }
      mapViewPane.getChildren().remove(circles.get(pointFocus));
      floorPoints.remove(pointFocus);
      allPoints.remove(pointFocus);
      circles.remove(pointFocus);
      setPointFocus(null);
    } else {
      for (Point secondaryFocus : (ArrayList<Point>) secondaryPointFoci.clone()) {
        ArrayList<Point> neighbors = (ArrayList<Point>) secondaryFocus.getNeighbors().clone();
        for (Point p : neighbors) {
          Connection c = new Connection(secondaryFocus, p);
          p.getNeighbors().remove(secondaryFocus); // TODO should be replaced by a method
          secondaryFocus.getNeighbors().remove(p);
          removeVisualConnection(c);
        }
        removePointFromSecondarySelection(secondaryFocus);
        mapViewPane.getChildren().remove(circles.get(secondaryFocus));
        floorPoints.remove(secondaryFocus);
        allPoints.remove(secondaryFocus);
        circles.remove(secondaryFocus);
      }
    }
  }

  // Navigates back to the main menu
  @FXML
  private void backButtonClicked() {
    Stage primaryStage = (Stage) floorChoiceBox.getScene().getWindow();
    try {
      if (mapViewFlag == 3) {
        loadScene(primaryStage, "/AdminMenu.fxml");
      } else {
        loadScene(primaryStage, "/MainMenu.fxml");
      }
    } catch (Exception e) {
      System.out.println("Cannot load main menu");
      e.printStackTrace();
    }
  }

  @FXML
  private void newButtonClicked() {
    double x = xCoordField.getLength() == 0 ? 0 : Double.parseDouble(xCoordField.getText());
    double y = yCoordField.getLength() == 0 ? 0 : Double.parseDouble(yCoordField.getText());
    int floor = currentFloor;
    String name = nameField.getLength() == 0 ? "" : nameField.getText();
    Point newPoint = new Point(x, y, floor);
    newPoint.setName(name);
    allPoints.add(newPoint);
    floorPoints.add(newPoint);
    ArrayList<Point> a = new ArrayList<>();
    a.add(newPoint);
    displayPoints(a);
  }



  private void initializeSearch() {
    searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
      searchString = newValue.toString();
      refreshListView();
    });
  }


  private void refreshListView() {
    searchPoints.clear();
    String searching = dictionary
        .getString(searchType == 1 ? "Floor" : (searchType == 2 ? "Hospital" : "Physicians"),
            currSession.getLanguage());
    searchFieldLabel
        .setText(dictionary.getString("Search", currSession.getLanguage()) + " " + searching);
    results.clear();
    ArrayList<Point> resultPoints = new ArrayList<Point>();
    switch (searchType) {
      case 1:
        // Searching Current Floor's Points
        resultPoints = searchFloorPoints(searchString);
        break;
      case 2:
        // Searching all Points
        resultPoints = searchAllPoints(searchString);
        break;
      case 3:
        // Searching Physicians' Points
        resultPoints = searchPhysicians(searchString);
        break;
      default:
        break;
    }

    resultsList.getItems().setAll(pointsToStrings(resultPoints));

  }

  @FXML
  private void searchGoButtonClicked() {
    Point p = getSelectedPointInSearch();
    if(p != null) {
      floorChoiceBox.setValue(p.getFloor());
      setPointFocus(p);
    }
  }


  private Point getSelectedPointInSearch(){
    Point selected = searchPoints.get(resultsList.getSelectionModel().getSelectedItem());
    int ind = allPoints.indexOf(selected);
    if (ind != -1) {
      Point actual = allPoints.get(ind);
      if (actual != null) {
        return actual;
      }
    }
    return null;
  }


  private ArrayList<String> pointsToStrings(ArrayList<Point> points) {
    ArrayList<String> out = new ArrayList<String>();
    for (Point p : points) {
      int floor = p.getFloor() % 10;
      String post = (floor == 1 ? "st" : (floor == 2 ? "nd" : (floor == 3 ? "rd" : "th")));
      String thisName = (p.getName() + " " + p.getFloor() + post + " " + dictionary
          .getString("Floor", currSession.getLanguage()));
      out.add(thisName);
      searchPoints.put(thisName, p);
    }
    return out;
  }

  private ArrayList<Point> searchPointList(String search, ArrayList<Point> points) {
    ArrayList<Point> out = new ArrayList<Point>();
    for (Point p : points) {
      if (p.getName() != null && !p.getName().equals("null") && !p.getName().equals("") && !p
          .getName().equals("ELEVATOR") && p.getName().contains(search)) {
        out.add(p);
      }
    }
    return out;
  }

  private ArrayList<Point> filterPointList(ArrayList<Point> points) {
    ArrayList<Point> out = new ArrayList<Point>();
    for (Point p : points) {
      if (p.getName() != null && !p.getName().equals("null") && !p.getName().equals("") && !p
          .getName().equals("ELEVATOR")) {
        out.add(p);
      }
    }
    return out;
  }

  private ArrayList<Point> searchAllPoints(String search) {
    return filterPointList(database.fuzzySearchPoints(search));
  }

  private ArrayList<Point> searchFloorPoints(String search) {
    return (new ListPoints(filterPointList(database.fuzzySearchPoints(search))))
        .getFloor(currentFloor).getPoints();
  }

  private ArrayList<Point> searchPhysicians(String search) {
    ArrayList<Point> out = new ArrayList<Point>();
    ArrayList<Physician> docs = database.fuzzySearchPhysicians(search);
    for (Physician p : docs) {
      if (p.getFirstName().contains(search) || p.getLastName().contains(search) || p.getTitle()
          .contains(search)) {
        out.addAll(p.getLocations());
      }
    }
    return out;
  }


  private void findMaxID(){
    if(allPoints.size() > 0) {
      maxID = allPoints.get(allPoints.size() - 1).getId();
    }
  }

  private void defragmentIDs(){
    for(int i = 0; i < allPoints.size(); i++){
      allPoints.get(i).setID(i+1);
    }
  }

  @FXML
  private void saveMapButtonClicked() {
    saveButton.setDisable(true);
    progressPane.setVisible(true);
    saving = true;
    // Only assigns IDs to points that have not been assigned IDs
    // Checks if the newly assigned IDs exceed the value of Point.ID_MAX
    // If it exceeds that value, then it defragments the unique IDs, essentially reassigning ID
    // values based on index in the allPoints ArrayList
    for (Point p : allPoints) {
      if(p.getId() == 0){
        int newID = ++maxID;
        if(newID < 0 || newID > Point.ID_MAX) {
          defragmentIDs();
        }else{
          p.setID(maxID);
        }
      }
    }
    database.setPoints(allPoints);
  }

  ///////////////////
  // Map Listeners //
  ///////////////////

  // "scrolled" means the scroll wheel. This method controls zooming with the scroll wheel.
  @FXML
  private void mapMouseScrolled(ScrollEvent e) { // TODO
    changeZoom(e.getDeltaY() > 0);
    // Then update the tracking for cursor location vs image location
    // Prevents odd behavior when dragging and scrolling simultaneously
    if (mapViewPane.isPressed()) { // only if it's pressed to increase efficiency
      mapPressedX = e.getSceneX();
      mapPressedY = e.getSceneY();
      difX = mapPressedX - mapViewPane.getLayoutX();
      difY = mapPressedY - mapViewPane.getLayoutY();
    }
  }

  @FXML
  private void mapMousePressed(MouseEvent e) {

    mapViewPane.requestFocus();
    String buttonUsed = e.getButton().name();
    mouseDragged = false;
    if (buttonUsed.equals("SECONDARY")) {
      if (mapViewFlag == 3) {
      }
    } else {
      if (mapViewFlag == 3) {
        if (e.isControlDown()) {
          selectionRectangleX = e.getX();
          selectionRectangleY = e.getY();
          selectionRectangle.setX(e.getX());
          selectionRectangle.setY(e.getY());
        }
      }
      mapPressedX = e.getSceneX();
      mapPressedY = e.getSceneY();
      difX = mapPressedX - mapViewPane.getLayoutX();
      difY = mapPressedY - mapViewPane.getLayoutY();
    }
  }

  @FXML
  private void mapMouseDragged(MouseEvent e) {
    String buttonUsed = e.getButton().name();
    mouseDragged = true;
    if (buttonUsed.equals("SECONDARY")) {

    } else {
      // If control is down, draw a rectangle from the starting point to the current cursor location
      if (e.isControlDown()) {
        if (mapViewFlag == 3) {
          selectionRectangle.setVisible(true);
          double width = e.getX() - selectionRectangleX;
          double height = e.getY() - selectionRectangleY;
          selectionRectangle.setX(
              width > 0 ? selectionRectangleX : selectionRectangleX - (width *= -1));
          selectionRectangle.setY(height > 0 ? selectionRectangleY
              : selectionRectangleY - (height *= -1));
          selectionRectangle.setWidth(width);
          selectionRectangle.setHeight(height);
        }
      } else {
        selectionRectangle.setVisible(false);
        mapImage.setCursor(Cursor.CLOSED_HAND);
        double newX = e.getSceneX() - difX;
        double newY = e.getSceneY() - difY;
        moveMapImage(newX, newY);
      }
    }
  }

  @FXML
  private void mapMouseReleased(MouseEvent e) {
    String buttonUsed = e.getButton().name();
    if (mapViewFlag == 3) {
      if (selectionRectangle.isVisible()) { // if it's visible, then select any nodes in its area
        double v1 = selectionRectangle.getY();
        double v2 = selectionRectangle.getY() + selectionRectangle.getHeight();
        double h1 = selectionRectangle.getX();
        double h2 = selectionRectangle.getX() + selectionRectangle.getWidth();
        double top = Double.min(v1, v2) / current_zoom_scale;
        double bot = Double.max(v1, v2) / current_zoom_scale;
        double right = Double.max(h1, h2) / current_zoom_scale;
        double left = Double.min(h1, h2) / current_zoom_scale;
        for (Point p : floorPoints) {
          double x = p.getXCoord();
          double y = p.getYCoord();
          if (x > left && x < right && y > top && y < bot) {
            addPointToSecondarySelection(p);
          }
        }
      }
    }
    selectionRectangle.setVisible(false);
    if (buttonUsed.equals("SECONDARY")) {
      if (mapViewFlag == 3) {

      }
    }
    mapImage.setCursor(Cursor.DEFAULT);
  }


  @FXML
  private void mapMouseClicked(MouseEvent e) {
    if (!mouseDragged) {
      mapMouseClickNoDrag(e);
    }
  }

  private void mapMouseClickNoDrag(MouseEvent e) {
    String buttonUsed = e.getButton().name();
    if (buttonUsed.equals("SECONDARY")) {
      mapMouseRightClick(e);
    } else {
      mapMouseLeftClick(e);
    }
  }

  private void mapMouseRightClick(MouseEvent e) {
    if (mapViewFlag == 3) {
      if (e.isShiftDown()) {
        if (!pointFocus.getNeighbors().containsAll(secondaryPointFoci)) {
          for (Point p : secondaryPointFoci) {
            p.connectTo(pointFocus);
            addVisualConnection(new Connection(p, pointFocus));

          }
        } else {
          for (Point p : secondaryPointFoci) {
            p.severFrom(pointFocus);
            removeVisualConnection(new Connection(p, pointFocus));
          }
        }
      }else{
        displayContextMenu(adminMapMenu, (Point) circles.keySet().toArray()[0], e.getScreenX(), e.getScreenY());
      }
    }
  }

  private void mapMouseLeftClick(MouseEvent e) {
    setPointFocus(null);
    if (mapViewFlag == 3) {
      if (e.isShiftDown()) {
        String s = typeSelect.getSelectedToggle().getUserData().toString();
        Coordinate c = coordinateToPixel(new Coordinate(e.getX(), e.getY()));
        Point p;
        if (s.equals("Stair")) {
          p = new StairPoint((int) c.getX(), (int) c.getY(), "STAIR", 0, new ArrayList<Point>(),
              currentFloor);
        } else if (s.equals("Elevator")) {
          p = new ElevatorPoint((int) c.getX(), (int) c.getY(), "ELEVATOR", 0,
              new ArrayList<Point>(),
              currentFloor);
        } else {
          p = new Point(c.getX(), c.getY(), currentFloor);
        }
        floorPoints.add(p);
        allPoints.add(p);
        addVisualNodesForPoint(p, floorPoints);
        setPointFocus(p);
      }
      if (e.isControlDown()) {
        clearSecondaryPointFoci();
      }
    }
  }

  @FXML
  private void mapKeyPressed(KeyEvent e) {
    if (mapViewFlag == 3) {
      if (e.getCode().toString().equals("DELETE")) {
        deletePoints(e.isControlDown());
      }
      if (e.isControlDown()) {
        if (e.getCode().toString().equals("C")) {
          copy();
        }
        if (e.getCode().toString().equals("X")) {
        }
        if (e.getCode().toString().equals("V")) {
          paste();
        }
        if (e.getCode().toString().equals("S")) {
          saveMapButtonClicked();
        }
      }
    }
  }

  private void copy(){
    // Cloned once here because the points could be changed after being copied, which is bad
    ListPoints lp = new ListPoints(secondaryPointFoci);
    clipBoard = lp.deepClone().getPoints();

    mapViewPane.setCursor(Cursor.DEFAULT);
  }

  private void paste(){
    if (clipBoard.isEmpty()) {
    } else {
      mapViewPane.setCursor(Cursor.WAIT);
      floorPoints.addAll(clipBoard);
      allPoints.addAll(clipBoard);
      clearSecondaryPointFoci();
      displayPoints(clipBoard);
      for (Point p : clipBoard) {
        p.setFloor(currentFloor);
        addPointToSecondarySelection(p);
      }

      // Cloned again, after being pasted, because they could be pasted more than once
      ListPoints lp = new ListPoints(clipBoard);
      clipBoard = lp.deepClone().getPoints();
      mapViewPane.setCursor(Cursor.DEFAULT);
    }
  }

  //////////////////////
  // Circle Listeners //
  //////////////////////

  private void circleMouseScrolled(ScrollEvent e, Point p, Circle c) {
    if (e.isControlDown()) {
      // TODO change size of points
      boolean delta = e.getDeltaY() > 0;
      if (!delta) {
        if (point_radius >= POINT_RADIUS_MAX) {
          point_radius = POINT_RADIUS_MAX;
        } else {
          point_radius += 1;
        }
      } else {
        if (point_radius <= POINT_RADIUS_MIN) {
          point_radius = POINT_RADIUS_MIN;
        } else {
          point_radius -= 1;
        }
      }
      updateVisualNodes();
    } else {
      mapMouseScrolled(e);
    }
  }

  private void circleMouseEntered(MouseEvent e, Point p, Circle c) {
  }

  private void circleMousePressed(MouseEvent e, Point p, Circle c) {
    String button = e.getButton().toString();
    if (button.equals("PRIMARY")) {
      if (mapViewFlag == 3) {
        c.setCursor(Cursor.CLOSED_HAND);
      }
    }
    mouseDragged = false;
  }

  private void circleMouseClicked(MouseEvent e, Point p, Circle c) {
    if (!mouseDragged) { // if it was dragged, then it's not a click
      circleMouseClickNoDrag(e, p, c);
    }
  }

  private void circleMouseClickNoDrag(MouseEvent e, Point p, Circle c) {
    String button = e.getButton().toString();
    if (button.equals("PRIMARY")) {
      circleMouseLeftClick(e, p, c);
    } else if (button.equals("SECONDARY")) {
      circleMouseRightClick(e, p, c);
    }
  }

  private void circleMouseLeftClick(MouseEvent e, Point p, Circle c) {
    if (mapViewFlag == 3) {
      if (e.isControlDown()) {
        togglePointToSecondarySelection(p);
      } else {
        setPointFocus(p);
      }
    } else {
      if (e.isControlDown()) {

      } else {
        setPointFocus(p);
      }
    }
  }

  private void circleMouseRightClick(MouseEvent e, Point p, Circle c) {
    if (e.isShiftDown()) {
      if (mapViewFlag == 3) {
        adminCircleMouseRightClick(e, p, c);
      } else {
      }
    } else {
      if (mapViewFlag == 3) {
        displayContextMenu(adminPointMenu, p, e.getScreenX(), e.getScreenY());
      } else {
        displayContextMenu(userMapMenu, p, e.getScreenX(), e.getScreenY());
      }
    }
  }

  private void adminCircleMouseRightClick(MouseEvent e, Point p, Circle c) {
    // Ensure that it does not get connected to itself or a repeat connection
    if (!p.equals(pointFocus) && pointFocus != null) {
      if (p.getNeighbors().contains(pointFocus)) {
        p.severFrom(pointFocus);
        removeVisualConnection(new Connection(p, pointFocus));
      } else {
        p.connectTo(pointFocus);
        addVisualConnection(new Connection(p, pointFocus));
      }
    }
  }

  private void circleMouseDragged(MouseEvent e, Point p, Circle c) {
    mouseDragged = true;
    String button = e.getButton().toString();
    if (button.equals("PRIMARY")) {
      if (mapViewFlag == 3) {
        // control + drag on a circle means dragging all selected circles
        if (e.isControlDown()) {
          if (p.equals(pointFocus)) {
            setPointFocus(null);
          }
          if (!secondaryPointFoci.contains(p)) {
            secondaryPointFoci.add(p);
            circles.get(p).setStroke(SECONDARY_POINT_FOCUS_COLOR);
          }
          Coordinate c1 = coordinateToPixel(new Coordinate(e.getX(), e.getY()));
          Coordinate c2 = new Coordinate(p.getXCoord(), p.getYCoord());
          for (Point p2 : secondaryPointFoci) {
            p2.setXCoord(p2.getXCoord() + c1.getX() - c2.getX());
            p2.setYCoord(p2.getYCoord() + c1.getY() - c2.getY());
            updateCircleForPoint(p2);
            updateLinesForPoint(p2);
          }

        } else {
          Coordinate c1 = coordinateToPixel(new Coordinate(e.getX(), e.getY()));
          movePoint(p, c1);
        }

      }
    } else if (button.equals("SECONDARY")) {

    }
  }

  private void circleMouseReleased(MouseEvent e, Point p, Circle c) {
    c.setCursor(Cursor.HAND);
  }

  @FXML
  private void toggleHelp() {
    helpPane.setVisible(!helpPane.isVisible());
  }


  public void logoff() {
    Stage primaryStage = (Stage) mapViewPane.getScene().getWindow();
    try {
      loadScene(primaryStage, "/MainMenu.fxml");
    } catch (Exception e) {
      System.out.println("Cannot load main menu");
      e.printStackTrace();
    }
  }

  ////////////////////
  // Line Listeners //
  ////////////////////

  private void lineMouseClicked(MouseEvent e, Connection c1, Line l) {
    if (mapViewFlag == 3) {
      if (e.isShiftDown()) {
        if (!mouseDragged) {
          // Get rid of the old connection
          removeVisualConnection(c1);
          c1.getStart().severFrom(c1.getEnd());
          // Create a point in between that is connected to both points
          String s = typeSelect.getSelectedToggle().getUserData().toString();
          Coordinate c = coordinateToPixel(new Coordinate(e.getX(), e.getY()));
          Point p;
          if (s.equals("Stair")) {
            p = new StairPoint((int) c.getX(), (int) c.getY(), "STAIR", 0, new ArrayList<Point>(),
                currentFloor);
          } else if (s.equals("Elevator")) {
            p = new ElevatorPoint((int) c.getX(), (int) c.getY(), "ELEVATOR", 0,
                new ArrayList<Point>(),
                currentFloor);
          } else {
            p = new Point(c.getX(), c.getY(), currentFloor);
          }
          allPoints.add(p);
          floorPoints.add(p);
          p.connectTo(c1.getStart());
          p.connectTo(c1.getEnd());
          addVisualNodesForPoint(p, floorPoints);
        }
      }
    }
  }

  private void lineMouseDragged(MouseEvent e, Connection c, Line l) {
    mouseDragged = true;
  }

  private void lineMousePressed(MouseEvent e, Connection c, Line l) {
    mouseDragged = false;
  }

  private void setDirectionsOptions() {
    textButton.setToggleGroup(directionSelect);
    emailButton.setToggleGroup(directionSelect);
    textButton.setSelected(true);
    emailButton.setUserData("email");
    textButton.setUserData("text");
    carrierBox.getItems().add(Carrier.att);
    carrierBox.getItems().add(Carrier.tmobile);
    carrierBox.getItems().add(Carrier.sprint);
    carrierBox.getItems().add(Carrier.verizon);

    directionSelect.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
      @Override
      public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue,
          Toggle newValue) {
        if (directionSelect.getSelectedToggle().getUserData().equals(emailButton.getUserData())) {
          carrierBox.setDisable(true);
        } else {
          carrierBox.setDisable(false);
        }
      }
    });
  }

  private String directions = "";

  @FXML
  private void sendDirections() {
    Emailer e = new Emailer();
    if (directionSelect.getSelectedToggle().getUserData().toString().equals("email")) {
      if(!detailEntry.getText().equals("") && !directions.equals("")) {
        e.email(detailEntry.getText(), directions);
      }
    } else {
      Carrier carrier = (Carrier) carrierBox.getSelectionModel().getSelectedItem();
      if(carrier != null && !directions.equals("") && !detailEntry.getText().equals("")) {
        e.text(detailEntry.getText(), carrier,
            directions);
      }
    }
  }

  //Testing Context Menus

  /** User Context Menu for the map:
   * This menu is displayed when the user right clicks on a point while viewing the navigation
   * map. It has two MenuItem options, startingLocation and destination, which allow the user
   * to set the starting location and destination for navigation if they choose to.
   */
  MenuItem startingLocation = new MenuItem("Set as Current Location");
  MenuItem destination = new MenuItem("Set as Destination");
  ContextMenu userMapMenu = new ContextMenu(startingLocation, destination);

  /** Admin Context Menu for the map:
   *
   */
  MenuItem deletePoint = new MenuItem("Delete");
  MenuItem copyPoint = new MenuItem("Copy");
  MenuItem deleteAllPoints = new MenuItem("Delete All");
  ContextMenu adminPointMenu = new ContextMenu(copyPoint, deletePoint, deleteAllPoints);

  //Admin Map Menu
  MenuItem pastePoint = new MenuItem("Paste");
  ContextMenu adminMapMenu = new ContextMenu(pastePoint);


  /**
   * Sets the actions of startingLocation and destination MenuItems to set the starting and ending
   * locations respectively.
   * @param point: The point that has been called by the ContextMenu.
   */
  public void handlePoint(Point point){
    startingLocation.setOnAction(new EventHandler<ActionEvent>() {
      @Override public void handle(ActionEvent e) {
        setStart(point); //Sets the starting location as the point if MenuItem is startingLocation
      }
    });
    destination.setOnAction(new EventHandler<ActionEvent>() {
      @Override public void handle(ActionEvent e) {
        setEnd(point); //Sets the destination as the point if MenuItem is destination.
      }
    });
    deletePoint.setOnAction(new EventHandler<ActionEvent>() {
      @Override public void handle(ActionEvent e) {
        setPointFocus(point);
        deletePoints(false); //Removes the one point selected.
      }
    });
    deleteAllPoints.setOnAction(new EventHandler<ActionEvent>() {
      @Override public void handle(ActionEvent e) {
        addPointToSecondarySelection(point);
        deletePoints(true); //Removes all points selected.
      }
    });
    copyPoint.setOnAction(new EventHandler<ActionEvent>() {
      @Override public void handle(ActionEvent e) {
        addPointToSecondarySelection(point);
        copy(); //Copies a single point.
      }
    });
    pastePoint.setOnAction(new EventHandler<ActionEvent>() {
      @Override public void handle(ActionEvent e) {
        paste(); //Pastes all points in the queue.
      }
    });
  }

  /**
   * Shows a new context menu at a location when a point is clicked. Passes the point to the context
   * menu event handling function handlePoint.
   * @param contextMenu: The type of contextMenu to display.
   * @param point: The point that was selected.
   * @param xLocation: The xLocation of the click mouseEvent.
   * @param yLocation: The yLocation of the click mouseEvent.
   */
  public void displayContextMenu(ContextMenu contextMenu, Point point, double xLocation, double yLocation){
    contextMenu.show(circles.get(point).getScene().getWindow(),xLocation,yLocation);
    handlePoint(point);
  }

  //End Context Menu Testing
}
package Database;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.language.Soundex;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import Definitions.*;
import org.ElevatorPoint;
import org.Point;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by evan on 3/25/17.
 * This Object will add, remove and edit our hospital database
 */
public class DatabaseController implements DatabaseInterface {

  private static final int fuzzySearchThreshold = 2;
  private static final int fuzzySearchLimit = 20;
  SaveThread saveThread;
  LoadThread loadThread;
  public double progressBarPercentage = 0;
  ArrayList<Point> localPoints;
  ArrayList<Physician> localPhysicians;

  ArrayList<Point> diffPoints = null;
  ArrayList<Physician> diffPhysicians = null;
  ArrayList<Point> remPoints = null;
  ArrayList<Physician> remPhysicians = null;


  DatabaseDriver dbc = null;

  public DatabaseController(DatabaseDriver _dbc) {
    this.localPhysicians = new ArrayList<Physician>();
    this.localPoints = new ArrayList<Point>();
    this.dbc = _dbc;
    saveThread = new SaveThread(this);
    loadThread = new LoadThread(this);
  }

  ///////////////////////////
  /////// Physician /////////
  ///////////////////////////

  public boolean removePhysician(long pid) {
    dbc.send_Command(
        "delete from physician where PID = " + pid
    );

    //ArrayList<Physician> new_physicians = localPhysicians;
    Physician old_physician = findRealPhysician((int) pid, localPhysicians);
    localPhysicians.remove(old_physician);
    //setPhysicians(new_physicians);

    return true;
  }

  public boolean addPhysician(
      //long PID, String first_name, String last_name, String title,
      //ArrayList<FakePoint> array_points,
      Physician real_ph
  ) {
    //FakePhysician fake_ph = new FakePhysician(real_ph);
    long PID = real_ph.getID();
    String first_name = real_ph.getFirstName().replace(';','_');
    String last_name = real_ph.getLastName().replace(';','_');
    String title = real_ph.getTitle().replace(';','_');
    ArrayList<Point> array_points = real_ph.getLocations();

    dbc.send_Command(
        "insert into physician (pid,first_name, last_name, title) values (" + PID + ",'"
            + first_name + "','"
            + last_name + "','" + title + "')");

    int i;
    for (i = 0; i < array_points.size(); i++) {
      this.addPhysicianLocation(PID, array_points.get(i).getId());
    }
    System.out.println("added or tried to add physician with id : " + PID);

    //ArrayList<Physician> new_physicians = localPhysicians;
    //new_physicians.add(real_ph);
    //setPhysicians(new_physicians);
    //localPhysicians.add(real_ph);
    if (check_physicians(localPhysicians, real_ph)) {
      localPhysicians.add(real_ph);
    }
    return true;
  }

  private boolean check_physicians(ArrayList<Physician> ap, Physician p) {
    long id = p.getID();
    for (int i = 0; i < ap.size(); i++) {
      if (id == ap.get(i).getID()) {
        return false;
      }
    }
    //ap.add(p);
    return true;
  }

  private boolean check_points(ArrayList<Point> ap, Point p) {
    long id = p.getId();
    for (int i = 0; i < ap.size(); i++) {
      if (id == ap.get(i).getId()) {
        return false;
      }
    }
    //ap.add(p);
    return true;
  }

  private boolean check_points(ArrayList<Point> ap, long pid) {
    //long id = p.getId();
    for (int i = 0; i < ap.size(); i++) {
      if (pid == ap.get(i).getId()) {
        return false;
      }
    }
    //ap.add(p);
    return true;
  }



  public boolean editPhysician(
      Physician real_ph
  ) {
    //FakePhysician fake_ph = new FakePhysician(real_ph);
    long PID = real_ph.getID();
    String first_name = real_ph.getFirstName().replace(';','_');
    String last_name = real_ph.getLastName().replace(';','_');
    String title = real_ph.getTitle().replace(';','_');
    ArrayList<Point> array_points = real_ph.getLocations();

    dbc.send_Command(
        "update physician SET first_name = '" + first_name + "', last_name =  '" + last_name
            + "', title  =  '" + title + "' WHERE PID = " + PID
    );

    dbc.send_Command(
        "delete from physician_location WHERE PID_ph = " + PID
    );

    int i;
    for (i = 0; i < array_points.size(); i++) {
      this.addPhysicianLocation(PID, array_points.get(i).getId());
    }

    //ArrayList<Physician> new_physicians = localPhysicians;
    Physician old_physician = findRealPhysician((int) real_ph.getID(), localPhysicians);
    localPhysicians.remove(old_physician);
    localPhysicians.add(real_ph);
    //setPhysicians(new_physicians);

    return true;
  }


  public FakePhysician get_physician(int pid) {
    ResultSet res = dbc.send_Command("select * from physician where pid = " + pid).get(0);
    int c = 0;
    FakePhysician my_p = null;
    c++;
    try {
      while (res.next()) {
        if (c > 1) {
          System.out.println("db error, was not supposed to happen");
          break;
        }
        String first_name = res.getString("FIRST_NAME");
        String last_name = res.getString("LAST_NAME");
        String title = res.getString("TITLE");
        int new_pid = res.getInt("PID");

        my_p = new FakePhysician(first_name, last_name, title, pid, new ArrayList<Integer>());
        //physicians.add(p);

      }

      res.close();

      if (my_p == null) {
        System.out.println("no physician found");
        return null;
      }

      ResultSet res2 = dbc.send_Command("select * from physician_location where pid_ph = " + pid)
          .get(0);

      ArrayList<Integer> my_locs = new ArrayList<Integer>();
      while (res2.next()) {
        int new_pid2 = res2.getInt("PID_po");
        my_locs.add(get_point(new_pid2).getId());

      }
      res2.close();
      my_p.setLocations(my_locs);


    } catch (SQLException e) {
      //e.printStackTrace();
      System.out.println("error getting fake physcians from DB; Query Error: " + e.getMessage());
      Alert alert = new Alert(AlertType.ERROR, "Message. Bad Things Happened! : " + "DB ERROR: error while trying to retrieve a physician"); //can add buttons if you want, or change to different popup types
      alert.showAndWait(); //this puts it in focus
      if (alert.getResult() == ButtonType.YES) {
        //do stuff, if neccesary, else, delete
      }
    }

    return my_p;


  }


  public ArrayList<Physician> getAllPhysicians() throws SQLException {
    ArrayList<FakePhysician> fphysicians = new ArrayList<FakePhysician>();
    ResultSet res = dbc.send_Command("select pid from physician").get(0);
    progressBarPercentage = .6;
    while (res.next()) {
      int pid = res.getInt("PID");

      FakePhysician p = get_physician(pid);
      fphysicians.add(p);
    }
    progressBarPercentage = .7;
    ArrayList<Physician> physicians = new ArrayList<Physician>();
    for (int i = 0; i < fphysicians.size(); i++) {
      physicians.add(fphysicians.get(i).toRealPhysician());
      progressBarPercentage = .7 + .05 * i / fphysicians.size();
    }
    for (int i = 0; i < physicians.size(); i++) {
      ArrayList<Integer> currentLocations = findFakePhysician(physicians.get(i), fphysicians)
          .getLocations();
      ArrayList<Point> locations = new ArrayList<Point>();
      for (int j = 0; j < currentLocations.size(); j++) {
        locations.add(findRealPoint(currentLocations.get(j), localPoints));
      }
      physicians.get(i).setLocations(locations);
      progressBarPercentage = .75 + .25 * (i / physicians.size());
    }
    return physicians;

  }

  private FakePhysician findFakePhysician(Physician p, ArrayList<FakePhysician> fps) {
    for (int i = 0; i < fps.size(); i++) {
      if (p.getID() == fps.get(i).getID()) {
        return fps.get(i);
      }
    }
    return null;
  }

  private Physician findRealPhysician(int p, ArrayList<Physician> pts) {
    for (int i = 0; i < pts.size(); i++) {
      if (p == pts.get(i).getID()) {
        return pts.get(i);
      }
    }
    return null;
  }


  public boolean updatePhysicians(ArrayList<Physician> ap) throws SQLException {
//    dbc.send_Command("truncate table Physician; truncate table Physician_location;");
    dbc.send_Command("DELETE from Physician where 1=1; DELETE from Physician_location where 1=1");
    int i;
    for (i = 0; i < ap.size(); i++) {
      ArrayList<Point> points = ap.get(i).getLocations();
      /*ArrayList<FakePoint> fakePoints = new ArrayList<FakePoint>();
      for (int j = 0; j < points.size(); j++) {
        fakePoints.add(new FakePoint(points.get(j)));
      }*/
      Physician physician = ap.get(i);
      this.addPhysician(physician);
      progressBarPercentage = .5 + .5 * i / (ap.size() - 1);
    }

    return true;
  }

  ///////////////////////////
  /// Location - Physician //
  ///////////////////////////

  public boolean addPhysicianLocation(long pid_ph, int pid_po) {
    dbc.send_Command(
        "insert into Physician_Location (pid_po,pid_ph) values(" + pid_po + "," + pid_ph
            + ");\n");
    return true;
  }

  public boolean removePhysicianLocation(int pid_ph, int pid_po) {
    dbc.send_Command(
        "delete from Physician_Location where pid_ph = " + pid_ph + " and pid_po ='" + pid_po
            + ");\n");
    return true;
  }

  ////////////////////////
  /////////Point/////////
  //////////////////////

  public boolean addPoint(Point realpoint) {
    FakePoint point = new FakePoint(realpoint);
    int cost = point.getCost();
    int x = point.getXCoord();
    int y = point.getYCoord();
    int id = point.getId();
    int floor = point.getFloor();
    String name = point.getName().replace(';','_');
    ArrayList<Integer> neighbors = point.getNeighbors();

    dbc.send_Command(
        "insert into Point (x,y,cost,pid,floor,name) values (" + x + ","
            + y + "," + cost + "," + id + "," + floor + ",'" + name + "'); \n");

    for(int k = 0;k < neighbors.size();k++) {
      this.addNeighbor(point.getId(), neighbors.get(k));
    }

    if(check_points(localPoints,realpoint)){
      localPoints.add(realpoint);
    }


    return true;
  }
  public boolean addPointWithoutNeighbors(Point realpoint) {
    FakePoint point = new FakePoint(realpoint);
    int cost = point.getCost();
    int x = point.getXCoord();
    int y = point.getYCoord();
    int id = point.getId();
    int floor = point.getFloor();
    String name = point.getName().replace(';','_');
    ArrayList<Integer> neighbors = point.getNeighbors();

    dbc.send_Command(
        "insert into Point (x,y,cost,pid,floor,name) values (" + x + ","
            + y + "," + cost + "," + id + "," + floor + ",'" + name + "'); \n");

    if(check_points(localPoints,realpoint)){
      localPoints.add(realpoint);
    }
    return true;
  }

  //PREFERABLY NOT USE FOR SINGLE ADDING, BECAUSE IT CANNNOT ADD THE POIN TO THE LOCAL COPY
  public boolean addPoint(FakePoint point) {
    int cost = point.getCost();
    int x = point.getXCoord();
    int y = point.getYCoord();
    int id = point.getId();
    int floor = point.getFloor();
    String name = point.getName().replace(';','_');
    ArrayList<Integer> neighbors = point.getNeighbors();

    if (name == null) {
      name = "";
    }

    dbc.send_Command(
        "insert into Point (x,y,cost,pid,floor,name) values (" + x + ","
            + y + "," + cost + "," + id + "," + floor + ",'" + name + "'); \n");

    for(int k = 0;k < neighbors.size();k++) {
      this.addNeighbor(point.getId(), neighbors.get(k));
    }

    if(check_points(localPoints,id)){
      //localPoints.add(realpoint);
    }



    return true;
  }




  public boolean editPoint(
      Point real_po
  ) {
    removePoint(real_po.getId());
    addPoint(real_po);

    /*
    //FakePhysician fake_ph = new FakePhysician(real_ph);
    long PID = real_po.getId();
    String name = real_po.getName(); //real_po.getFirstName().replace(';','_');
    //String last_name = real_ph.getLastName().replace(';','_');
    //String title = real_ph.getTitle().replace(';','_');
    int cost = real_po.getCost();
    int xcoord = real_po.getXCoord();
    int ycoord = real_po.getYCoord();
    int floor = real_po.getFloor();
    //real_po.ge
    ArrayList<Point> array_points = real_po.getNeighbors();

    dbc.send_Command(
        "update point SET name = '" + name + "', cost =  " + cost
            + ", x  =  " + xcoord + ", y  =  " + ycoord  + " WHERE PID = " + PID + ")"
    );

    dbc.send_Command(
        "delete from neighbor WHERE PID1 = " + PID + "OR PID2 = " + PID + ")"
    );

    int i;
    for (i = 0; i < array_points.size(); i++) {
      this.addNeighbor((int)PID, array_points.get(i).getId());
      this.addNeighbor(array_points.get(i).getId(),(int)PID);
    }

    //ArrayList<Physician> new_physicians = localPhysicians;
    Point old_point = findRealPoint((int) real_po.getId(), localPoints);
    localPoints.remove(old_point);
    localPoints.add(real_po);
    //setPhysicians(new_physicians);
*/
    return true;
  }



  public boolean removePoint(long pid) {

    dbc.send_Command(
        "delete from Point where pid = " + pid + ";");

    Point old_point = findRealPoint((int) pid, localPoints);
    localPhysicians.remove(old_point);
    return true;
  }


  public boolean update_points(ArrayList<Point> rpal) {
    ArrayList<FakePoint> al = new ArrayList<FakePoint>();
    for (int q = 0; q < rpal.size(); q++) {
      al.add(new FakePoint(rpal.get(q)));
    }
    dbc.send_Command("DELETE from Point where 1=1;DELETE from Neighbor where 1=1;");
    int i;
    for (i = 0; i < al.size(); i++) {
      this.addPoint(al.get(i));
      progressBarPercentage = .25 * i / al.size();
    }
    //int i;

//    int k, l;
//    for (k = 0; k < al.size(); k++) {
//      //this.addPoint(al.get(i));
//      FakePoint point = al.get(k);
//      ArrayList<Integer> neighbor_ids = point.getNeighbors();
//      for (l = 0; l < neighbor_ids.size(); l++) {
//        this.addNeighbor(point.getId(), neighbor_ids.get(l));
//        //this.addNeighboring(pl.get(i).id,point.id);
//        progressBarPercentage = .25 + .25 * l / neighbor_ids.size();
//      }
//    }

    return true;
  }

  public FakePoint get_point(int my_pid) {
    FakePoint my_point = null;
    ResultSet res1 = dbc.send_Command("select * from point where pid = " + my_pid).get(0);
    int c = 0;
    try {
      while (res1.next()) {
        c++;
        if (c > 1) {
          System.out.println("that was not supposed to happen. ");
          break;
        }

        int floor = res1.getInt("floor");
        String name = res1.getString("NAME");
        if (name == null) {
          name = "";
        }
        int pid = res1.getInt("PID");
        int x = res1.getInt("x");
        int y = res1.getInt("y");
        int cost = res1.getInt("cost");

        my_point = new FakePoint(x, y, name, pid, new ArrayList<Integer>(), floor);

        ArrayList<Integer> neighbor_ids = new ArrayList<Integer>();
        ResultSet res4 = dbc.send_Command(
            "select pid1,pid2 from Neighbor where pid1 = " + pid /*+ " OR pid2 = " + pid*/).get(0);
        while (res4.next()) {
          //int pid1 = res4.getInt("Pid1");
          int pid2 = res4.getInt("Pid2");
          //if (pid1 != my_pid) {
          //  neighbor_ids.add(pid1);
          //} else {
          neighbor_ids.add(pid2);
          //}

        }
        res4.close();

        my_point.setNeighbors(neighbor_ids);


      }
    } catch (SQLException e) {
      // e.printStackTrace();
      System.out.println("error getting fake points from DB; Query Error: " + e.getMessage());
      Alert alert = new Alert(AlertType.ERROR, "Message. Bad Things Happened! : " + "DB ERROR: error while trying to retrieve a point"); //can add buttons if you want, or change to different popup types
      alert.showAndWait(); //this puts it in focus
      if (alert.getResult() == ButtonType.YES) {
        //do stuff, if neccesary, else, delete
      }

    }

    return my_point;

  }


  public ArrayList<Point> getAllPoints() throws SQLException {
    ArrayList<FakePoint> fakepoints = new ArrayList<FakePoint>();
    ResultSet res = null;
    try {
      res = dbc.send_Command("select pid from point").get(0);
    } catch (IndexOutOfBoundsException e) {
      System.out.println("No Result Available");
    }

    FakePoint new_point;
    while (res != null && res.next()) {
      int pid = res.getInt("PID");
      new_point = get_point(pid);
      fakepoints.add(new_point);
    }
    res.close();
    //Now convert to real
    ArrayList<Point> ret = new ArrayList<Point>();
    for (int i = 0; i < fakepoints.size(); i++) {
      ret.add(fakepoints.get(i).toRealPoint());
    }
    for (int i = 0; i < ret.size(); i++) {
      if (ret.get(i).getName().equals("ELEVATOR")) {
        Point p = ret.get(i);
        ret.remove(i);
        ret.add(i, toElevatorPoint(p));
      }
    }
    for (int i = 0; i < ret.size(); i++) {
      ArrayList<Integer> currentNeighbors = findFakePoint(ret.get(i), fakepoints).getNeighbors();
      for (int j = 0; j < currentNeighbors.size(); j++) {
        ret.get(i).connectTo(findRealPoint(currentNeighbors.get(j), ret));
      }
      progressBarPercentage = .25 + .25 * i / ret.size();
    }
    return ret;
  }


  private FakePoint findFakePoint(Point p, ArrayList<FakePoint> fps) {
    for (int i = 0; i < fps.size(); i++) {
      if (p.getId() == fps.get(i).getId()) {
        return fps.get(i);
      }
    }
    return null;
  }

  private Point findRealPoint(int p, ArrayList<Point> pts) {
    for (int i = 0; i < pts.size(); i++) {
      if (p == pts.get(i).getId()) {
        return pts.get(i);
      }
    }
    return null;
  }

  //////////////////////
///////Neighbor///////
//////////////////////

  public boolean addNeighbor(int pid1, int pid2) {

    dbc.send_Command(
        "insert into Neighbor (pid1,pid2) values (" + pid1 + "," + pid2 + "); \n");
    return true;
  }

  public boolean removeNeighbor(int pid1, int pid2) {

    dbc.send_Command(
        "delete from Neighbor where pid1 = " + pid1 + " or pid2 = " + pid2 + "); \n");
    return true;
  }

  ///////////////////////
  ////EXTRA DB METHODS///
  ///////////////////////

  private boolean compare_physicians_lists(ArrayList<Physician> l1, ArrayList<Physician> l2) {
    int i, j;
    if (l1.size() != l2.size()) {
      System.out.println("verification failed not same physician size lists");
      return false;
    } else {
      Physician p1, p2;
      for (i = 0; i < l1.size(); i++) {
        p1 = l1.get(i);
        p2 = l2.get(i);
        if (!p1.compareTo(p2)) {
          return false;
        }
      }

    }

    return true;
  }


  private boolean compare_points_lists(ArrayList<Point> l1, ArrayList<Point> l2) {
    int i, j;
    if (l1.size() != l2.size()) {
      System.out.println("verification failed, different points list size");
      return false;
    } else {
      Point p1, p2;
      for (i = 0; i < l1.size(); i++) {
        int k, l;
        p1 = l1.get(i);
        p2 = l2.get(i);
        if (!p1.compareTo(p2)) {
          return false;
        }

      }

    }

    return true;
  }


  private boolean verify_points_update() {

    boolean result;
    try {
      ArrayList<Point> db_points = getAllPoints();
      result = compare_points_lists(db_points, localPoints);
    } catch (SQLException e) {
      System.out.println("Cannot complete verification of points, querry/connection error");
      Alert alert = new Alert(AlertType.ERROR, "Message. Bad Things Happened! : " + "DB ERROR: Cannot complete verification of points, querry/connection error"); //can add buttons if you want, or change to different popup types
      alert.showAndWait(); //this puts it in focus
      if (alert.getResult() == ButtonType.YES) {
        //do stuff, if neccesary, else, delete
      }
      e.printStackTrace();
      return false;
    }

    return result;
  }

  private boolean verify_physicians_update() {
    boolean result;
    try {
      ArrayList<Physician> db_physicians = getAllPhysicians();
      result = compare_physicians_lists(db_physicians, localPhysicians);
    } catch (SQLException e) {
      System.out.println("Cannot complete verification of physicians, query/connection error");
      Alert alert = new Alert(AlertType.ERROR, "Message. Bad Things Happened! : " + "DB ERROR: Cannot complete verification of physicians, querry/connection error"); //can add buttons if you want, or change to different popup types
      alert.showAndWait(); //this puts it in focus
      if (alert.getResult() == ButtonType.YES) {
        //do stuff, if neccesary, else, delete
      }
      e.printStackTrace();
      return false;
    }

    return result;

  }

  private boolean save_and_verify() {
    int c = 0;
    while (c < 3) {
      c++;
      save();
      if (!(verify_physicians_update() && verify_points_update())) {
        System.out
            .println("ERROR: verification failed, retrying to save " + (3 - c) + " more times");
        save();
      } else {
        System.out.println("VERIFICATION SUCCEEDED");
        return true;
      }
    }
    return false;
  }


  @Override
  public void load() throws SQLException {
//    loadThread.start();
    progressBarPercentage = 0;
    localPoints = getAllPoints();
    localPhysicians = getAllPhysicians();
    progressBarPercentage = 1;
  }

  @Override
  public void save() {
    if (remPoints != null){
      for (Point p : remPoints){
        System.out.println("Rem point " + p.getId());
        this.removePoint(p.getId());
      }
      remPoints = null;
    }
    if (diffPoints != null){
      for (Point p : diffPoints){
        System.out.println("Diff point updating " + p.getId());
        this.removePoint(p.getId());
        this.addPointWithoutNeighbors(p);
      }
      for(Point p : diffPoints){
        for (int i = 0; i < p.neighbors.size(); i++){
          System.out.println("Diff neighbor point updating " + p.getId() + " -> "  + p.neighbors.get(i).getId());
          this.addNeighbor(p.getId(), p.neighbors.get(i).getId());
        }
      }

      diffPoints = null;
    }


    if (remPhysicians != null){
      for (Physician p : remPhysicians){
        System.out.println("Rem Physician " + p.getID());
        this.removePhysician(p.getID());
      }
      remPhysicians = null;
    }
    if (diffPhysicians != null){
      for(Physician p : diffPhysicians){
        System.out.println("Diff Physician updating " + p.getID());
        this.removePhysician(p.getID());
        this.addPhysician(p);
      }
      diffPhysicians = null;

    }
  }

  //TODO Filter special nodes like ELEVATOR or STAIRS etc.
  @Override
  public ArrayList<Point> getNamedPoints() {
    while (saveThread.running || loadThread.running) {
      ;
    }
    try {
      load();
    } catch (SQLException e) {
      Alert alert = new Alert(AlertType.ERROR, "Message. Bad Things Happened! : " + "DB ERROR: failed to load in getNamedPoints method: " + e.getMessage()); //can add buttons if you want, or change to different popup types
      alert.showAndWait(); //this puts it in focus
      if (alert.getResult() == ButtonType.YES) {
        //do stuff, if neccesary, else, delete
      }
      e.printStackTrace();
    }
    System.out.println("trying to get Points with names");
    ArrayList<Point> namedPoints = new ArrayList<Point>();
    int i;
    for (i = 0; i < localPoints.size(); i++) {
      if (localPoints.get(i).getName() != null && !localPoints.get(i).getName().equals("null")
          && !localPoints.get(i).getName().equals("") && !(
          localPoints.get(i).getName().replaceAll("\\s", "") == "")) {
        namedPoints.add(localPoints.get(i));
      }
    }

    return namedPoints;
  }

  @Override
  public ArrayList<Point> getPoints() {
    while (saveThread.running || loadThread.running) {
      ;
    }
    try {
      System.out.println("requesting points from DB, trying to load");
      load();
    } catch (SQLException e) {
      //e.printStackTrace();
      System.out.println(
          "Error Getting Data From The Database, failed to load, will return DB local points copy \n Query/Connection Error : "
              + e.getMessage());
      Alert alert = new Alert(AlertType.ERROR, "Message. Bad Things Happened! : " + "DB ERROR:  failed to load, will return DB local points copy \n Query/Connection Error " + e.getMessage()); //can add buttons if you want, or change to different popup types
      alert.showAndWait(); //this puts it in focus
      if (alert.getResult() == ButtonType.YES) {
        //do stuff, if neccesary, else, delete
      }
    }
    return localPoints;
  }

  @Override
  public void setPoints(ArrayList<Point> points) {
    while (saveThread.running || loadThread.running) {
      ;
    }
    progressBarPercentage = 0;
    System.out.println("Setting the DB local points copy");
    diffPoints = new ArrayList<Point>(points);
    System.out.println("" + diffPoints + "  :  " + localPoints);

    for (int i = 0; i < diffPoints.size(); i++){
      if (localPoints.contains(diffPoints.get(i))){
        System.out.println("Removing " + diffPoints.get(i).getId() + " from diff");
        diffPoints.remove(i);
        i--;
      }
    }
    remPoints = new ArrayList<Point>(localPoints);
    for (int i = 0; i < remPoints.size(); i++){
      if (points.contains(remPoints.get(i)) || diffPoints.contains(remPoints.get(i))){
        remPoints.remove(i);
        i--;
      }
    }

    localPoints = (ArrayList<Point>) points.clone();
    //save_and_verify();
//    saveThread.start();
    save();
  }


  @Override
  public ArrayList<Physician> getPhysicians() {
    while (saveThread.running || loadThread.running) {
      ;
    }
    try {
      System.out.println("requesting physicians from DB, trying to load");
      load();
    } catch (SQLException e) {
      System.out.println(
          "Error Getting Data From The Database, failed to load, will return DB local physicians copy \n Query/Connection Error : "
              + e.getMessage());
      Alert alert = new Alert(AlertType.ERROR, "Message. Bad Things Happened! : " + "DB ERROR:  failed to load, will return DB local physicians copy \n Query/Connection Error " + e.getMessage()); //can add buttons if you want, or change to different popup types
      alert.showAndWait(); //this puts it in focus
      if (alert.getResult() == ButtonType.YES) {
        //do stuff, if neccesary, else, delete
      }

      //e.printStackTrace();
    }
    ArrayList<Physician> copyOfPhysicians = new ArrayList<Physician>();
    for (Physician p : localPhysicians)
      copyOfPhysicians.add((Physician) p.clone());

    return copyOfPhysicians;
  }

  @Override
  public void setPhysicians(ArrayList<Physician> physicians) {
    while (saveThread.running || loadThread.running) {
      ;
    }
    progressBarPercentage = 0;
    diffPhysicians = new ArrayList<Physician>(physicians);
    for (int i = 0; i < diffPhysicians.size(); i++){
      if (localPhysicians.contains(diffPhysicians.get(i))){
        diffPhysicians.remove(i);
        i--;
      }
    }
    System.out.println(diffPhysicians);
    remPhysicians = new ArrayList<Physician>(localPhysicians);
    for (int i = 0; i < remPhysicians.size(); i++){
      if (physicians.contains(remPhysicians.get(i)) || diffPhysicians.contains(remPhysicians.get(i))){
        remPhysicians.remove(i);
        i--;
      }
    }

    System.out.println("Setting the DB local physicians copy");
    localPhysicians = (ArrayList<Physician>) physicians.clone();

    //save_and_verify();
//    saveThread.start();
    save();
  }

  ElevatorPoint toElevatorPoint(Point p) {
    ElevatorPoint ep = new ElevatorPoint(p.getXCoord(), p.getYCoord(), p.getName(), p.getId(),
        p.getNeighbors(), p.getFloor());
    return ep;
  }

  public ArrayList<Physician> fuzzySearchPhysicians(String searchTerm) {
    long startTime = System.nanoTime();

    ArrayList<Physician> candidates = new ArrayList<Physician>();
    if(searchTerm.replaceAll("\\s","") == "" || searchTerm == null){
      candidates = localPhysicians;
      return candidates;
    }
    LinkedHashMap<Physician,Integer> my_map = new LinkedHashMap<Physician,Integer>();
//    Soundex soundex = new Soundex();
//    System.out.println("here");
    searchTerm = searchTerm.toLowerCase();
    for (Physician p : localPhysicians) {
//      System.out.println("here");
      String first_name = p.getFirstName().toLowerCase();
      String last_name = p.getLastName().toLowerCase();
      if(StringUtils.containsAny(first_name,searchTerm) ||
            StringUtils.containsAny(last_name,searchTerm)/*||
            StringUtils.containsAny(p.getTitle(),searchTerm)*/){
          //candidates.add(p);
          int fn = StringUtils.getLevenshteinDistance(p.getFirstName(),searchTerm);
          int ln = StringUtils.getLevenshteinDistance(p.getLastName(),searchTerm);
          int t = StringUtils.getLevenshteinDistance(p.getTitle(),searchTerm);
          int value = Math.min(fn,ln);//,t);
          my_map.put(p,value);
//          System.out.println("here, value, id: " + value + " " + p.getID());

        }

    }
      LinkedHashMap sortedMap = sortByValues(my_map);
      //Map<Integer,Physician> sortedMap = new TreeMap<Integer,Physician>(map);
      ArrayList list2 = new ArrayList(sortedMap.entrySet());

      int counter = -1;
      //Set set = sortedMap.entrySet();
      //Iterator iterator = set.iterator();
      //HashMap sortedHashMap = new HashMap();
      for (Iterator it2 = list2.iterator(); it2.hasNext() && counter < fuzzySearchLimit;) {
        counter++;
        Entry my_entry = (Map.Entry) it2.next();
        //sortedHashMap.put(entry.getKey(),entry.getValue());
        candidates.add(counter,(Physician) my_entry.getKey());
//        System.out.println("key, value : " + my_entry.getKey() + " " + my_entry.getValue());
      }



/*      while(iterator.hasNext() && counter < fuzzySearchLimit) {
        counter++;
        Map.Entry my_entry = (Map.Entry)iterator.next();
        //candidates.add((Physician) my_entry.getKey());
        candidates.add(counter,(Physician) my_entry.getKey());
        System.out.println("key, value : " + my_entry.getKey() + " " + my_entry.getValue());
        iterator.remove();
      }*/
//      System.out.println("size : " + candidates.size());


    long endTime = System.nanoTime();
    long duration = endTime - startTime;
    System.out.println("duration physician: " +  duration);

    return candidates;
  }

  private  LinkedHashMap sortByValues(Map map) {

    ArrayList list = new ArrayList(map.entrySet());

    // Define comparator
    Collections.sort(list, new Comparator() {
      public int compare(Object o1, Object o2) {
        return ((Comparable) ((Map.Entry) (o1)).getValue())
            .compareTo(((Map.Entry) (o2)).getValue());
      }
    });

    LinkedHashMap sortedHashMap = new LinkedHashMap();
    for (Iterator it = list.iterator(); it.hasNext();) {
      Map.Entry entry = (Map.Entry) it.next();
      sortedHashMap.put(entry.getKey(),entry.getValue());
//      System.out.println("in comaprator : " + entry.getValue());
    }
    return sortedHashMap;
  }


  public ArrayList<Point> fuzzySearchPoints(String searchTerm) {
    long startTime = System.nanoTime();

    ArrayList<Point> candidates = new ArrayList<Point>();
    LinkedHashMap<Point,Integer> my_map = new LinkedHashMap<Point,Integer>();
    ArrayList<Point> named_points = getLocalNamedPoints();
//    ArrayList<Point> named_points = getNamedPoints(); slow af
//    Soundex soundex = new Soundex();
//    System.out.println("here");

    searchTerm = searchTerm.toLowerCase();
    for (Point p : named_points) {
      boolean worthit = false;
//      System.out.println("here");
      ArrayList<String> names = p.getNames();
      ArrayList<String> lc_names = new ArrayList<String>();
      ArrayList<Integer> distances = new ArrayList<Integer>();
      long startTime2 = System.nanoTime();
      int i;
      for(i = 0;i < names.size();i++){
        lc_names.add(names.get(i).toLowerCase());
        if(StringUtils.containsAny(lc_names.get(i),searchTerm)){
          worthit = true;
          distances.add(StringUtils.getLevenshteinDistance(lc_names.get(i),searchTerm));
        }
      }
      long endTime2 = System.nanoTime();
      long duration2 = endTime2 - startTime2;
      System.out.println("duration point first nested loop, counter: " +  duration2 + " " + i);

      if (worthit) {
        int value = distances.get(0);
        long startTime3 = System.nanoTime();
        int k;
        for(k = 0;i < distances.size();i++){
          if(distances.get(i) < value){
            value = distances.get(i);
          }
        }
        long endTime3 = System.nanoTime();
        long duration3 = endTime3 - startTime3;
        System.out.println("duration point first nested loop, counter: " +  duration3 + " " + i);


        my_map.put(p,value);
        System.out.println("here, value, id: " + value + " " + p.getId());

      }

    }
    LinkedHashMap sortedMap = sortByValues(my_map);
    //Map<Integer,Physician> sortedMap = new TreeMap<Integer,Physician>(map);
    ArrayList list2 = new ArrayList(sortedMap.entrySet());

    int counter = -1;
    //Set set = sortedMap.entrySet();
    //Iterator iterator = set.iterator();
    //HashMap sortedHashMap = new HashMap();
    for (Iterator it2 = list2.iterator(); it2.hasNext() && counter < fuzzySearchLimit;) {
      counter++;
      Entry my_entry = (Map.Entry) it2.next();
      //sortedHashMap.put(entry.getKey(),entry.getValue());
      candidates.add(counter,(Point) my_entry.getKey());
      System.out.println("key, value : " + my_entry.getKey() + " " + my_entry.getValue());
    }

    System.out.println("size : " + candidates.size());

    long endTime = System.nanoTime();
    long duration = endTime - startTime;
    System.out.println("duration point: " +  duration);
    return candidates;
  }

  /**
   * A much faster search for named points. Doesn't contact the database!
   * @author backslash166
   * @return ArrayList\<Point> The list of points which have names
   */
  private ArrayList<Point> getLocalNamedPoints() {
    ArrayList<Point> named_points = new ArrayList<Point>();
    if(localPoints.size()>0) {
      for (Point p : localPoints) {
        if (p.getName() != null) {
          String name = p.getName();
          if (!name.equals("") && !name.equals("null") && !name.equals("ELEVATOR")) {
            //the point is named
            named_points.add(p);
          }
        }
      }
    }
    return named_points;
  }


}
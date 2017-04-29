package Database;

import static jdk.nashorn.internal.objects.NativeMath.min;
import static jdk.nashorn.internal.objects.NativeMath.round;

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
import org.ListPoints;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.language.Soundex;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import Definitions.*;
import org.ElevatorPoint;
import org.Point;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;

/**
 * Created by evan on 3/25/17.
 * This Object will add, remove and edit our hospital database
 */
public class DatabaseController implements DatabaseInterface {

  private static final int fuzzySearchThreshold = 5;
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
    String first_name = real_ph.getFirstName().replace(';', '_');
    String last_name = real_ph.getLastName().replace(';', '_');
    String title = real_ph.getTitle().replace(';', '_');
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
    String first_name = real_ph.getFirstName().replace(';', '_');
    String last_name = real_ph.getLastName().replace(';', '_');
    String title = real_ph.getTitle().replace(';', '_');
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
      Alert alert = new Alert(AlertType.ERROR, "Message. Bad Things Happened! : "
          + "DB ERROR: error while trying to retrieve a physician"); //can add buttons if you want, or change to different popup types
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
    String name = point.getName().replace(';', '_');
    ArrayList<Integer> neighbors = point.getNeighbors();

    dbc.send_Command(
        "insert into Point (x,y,cost,pid,floor,name) values (" + x + ","
            + y + "," + cost + "," + id + "," + floor + ",'" + name + "'); \n");

    for (int k = 0; k < neighbors.size(); k++) {
      this.addNeighbor(point.getId(), neighbors.get(k));
    }

//    for (Point neighbor : realpoint.getNeighbors()) {
//      realpoint.connectTo(neighbor);
//    }
    if (check_points(localPoints, realpoint)) {
      localPoints.add(realpoint);
    }
    return true;
  }

  // These 2 functions are depreciated
//  public boolean addPointWithoutNeighbors(Point realpoint) {
//    FakePoint point = new FakePoint(realpoint);
//    int cost = point.getCost();
//    int x = point.getXCoord();
//    int y = point.getYCoord();
//    int id = point.getId();
//    int floor = point.getFloor();
//    String name = point.getName().replace(';', '_');
//    ArrayList<Integer> neighbors = point.getNeighbors();
//
//    dbc.send_Command(
//        "insert into Point (x,y,cost,pid,floor,name) values (" + x + ","
//            + y + "," + cost + "," + id + "," + floor + ",'" + name + "'); \n");
//
//    localPoints.add(realpoint);
//    return true;
//  }

  //PREFERABLY NOT USE FOR SINGLE ADDING, BECAUSE IT CANNNOT ADD THE POIN TO THE LOCAL COPY
//  public boolean addPoint(FakePoint point) {
//    int cost = point.getCost();
//    int x = point.getXCoord();
//    int y = point.getYCoord();
//    int id = point.getId();
//    int floor = point.getFloor();
//    String name = point.getName().replace(';', '_');
//    ArrayList<Integer> neighbors = point.getNeighbors();
//
//    if (name == null) {
//      name = "";
//    }
//
//    dbc.send_Command(
//        "insert into Point (x,y,cost,pid,floor,name) values (" + x + ","
//            + y + "," + cost + "," + id + "," + floor + ",'" + name + "'); \n");
//
//    for (int k = 0; k < neighbors.size(); k++) {
//      this.addNeighbor(point.getId(), neighbors.get(k));
//    }
//
//    if (check_points(localPoints, id)) {
//      //localPoints.add(realpoint);
//    }
//
//    return true;
//  }


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
    if (old_point != null) {
      ArrayList<Point> neighbors = old_point.getNeighbors();
      for (int i = 0; i < neighbors.size(); i++) {
        if (neighbors.get(i) != null) {
          old_point.severFrom(neighbors.get(i));
          i--;
        }
      }
    }
    localPoints.remove(old_point);
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
      this.addPoint(rpal.get(i));
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
      Alert alert = new Alert(AlertType.ERROR, "Message. Bad Things Happened! : "
          + "DB ERROR: error while trying to retrieve a point"); //can add buttons if you want, or change to different popup types
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
      Point point_to_add = fakepoints.get(i).toRealPoint();
      if (point_to_add.getName().equals("Elevator")) {
        point_to_add = toElevatorPoint(point_to_add);
      }
      ret.add(point_to_add);
    }
    /*for (int i = 0; i < ret.size(); i++) {

    for (int i = 0; i < ret.size(); i++) {
      ArrayList<Integer> currentNeighbors = findFakePoint(ret.get(i), fakepoints).getNeighbors();
      for (int j = 0; j < currentNeighbors.size(); j++) {
        ret.get(i).connectTo(findRealPoint(currentNeighbors.get(j), ret));
      }
      progressBarPercentage = .25 + .2 * i / ret.size();
    }
    for (int i = 0; i < ret.size(); i++) {
      if (ret.get(i).getName().equals("ELEVATOR")) {
        Point p = ret.get(i);
        ret.remove(i);
        ret.add(i, toElevatorPoint(p));
      }
    }*/
    for (int i = 0; i < ret.size(); i++) {
      ArrayList<Integer> currentNeighbors = findFakePoint(ret.get(i), fakepoints).getNeighbors();
      for (int j = 0; j < currentNeighbors.size(); j++) {
        ret.get(i).connectTo(findRealPoint(currentNeighbors.get(j), ret));
      }
      progressBarPercentage = .45 + .05 * i / ret.size();
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
//    for (int i = 0; i < pts.size(); i++) {
//      try {
//        if (p == pts.get(i).getId()) {
//          return pts.get(i);
//        }
//      }
//      catch (NullPointerException e){
//        System.out.println("Null PTR");
//      }
    for (Point point : pts) {
      if (point == null) {
        System.out.println("The thing");
      }
      if (p == point.getId()) {
        return point;
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
      Alert alert = new Alert(AlertType.ERROR, "Message. Bad Things Happened! : "
          + "DB ERROR: Cannot complete verification of points, querry/connection error"); //can add buttons if you want, or change to different popup types
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
      Alert alert = new Alert(AlertType.ERROR, "Message. Bad Things Happened! : "
          + "DB ERROR: Cannot complete verification of physicians, querry/connection error"); //can add buttons if you want, or change to different popup types
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
    if (remPoints != null) {
      for (Point p : remPoints) {
        System.out.println("Rem point " + p.getId());
        this.removePoint(p.getId());
      }
      remPoints = null;
    }
    if (diffPoints != null) {
      for (Point p : diffPoints) {
        System.out.println("Diff point updating " + p.getId());
        this.removePoint(p.getId());
        this.addPoint(p);
      }
      diffPoints = null;
      for (Point p : localPoints) {
        ArrayList<Integer> neighbors = new FakePoint(p).getNeighbors();
        for (int neighbor : neighbors) {
          p.connectTo(findRealPoint(neighbor, localPoints));
        }
      }
    }

    if (remPhysicians != null) {
      for (Physician p : remPhysicians) {
        System.out.println("Rem Physician " + p.getID());
        this.removePhysician(p.getID());
      }
      remPhysicians = null;
    }
    if (diffPhysicians != null) {
      for (Physician p : diffPhysicians) {
        System.out.println("Diff Physician updating " + p.getID());
        this.removePhysician(p.getID());
        this.addPhysician(p);
      }
      diffPhysicians = null;

    }
    // Cleanup
    for (int i = 0; i < localPoints.size(); i++) {
      if (localPoints.get(i) == null) {
        localPoints.remove(i);
        i--;
      }
    }
    for (int i = 0; i < localPhysicians.size(); i++) {
      if (localPhysicians.get(i) == null) {
        localPhysicians.remove(i);
        i--;
      }
    }
  }

  @Override
  public ArrayList<Point> getNamedPoints() {
    while (saveThread.running || loadThread.running) {
      ;
    }
    if (localPoints.size() < 1) {
      try {
        load();
      } catch (SQLException e) {
        Alert alert = new Alert(AlertType.ERROR,
            "Message. Bad Things Happened! : "
                + "DB ERROR: failed to load in getNamedPoints method: "
                + e.getMessage()); //can add buttons if you want, or change to different popup types
        alert.showAndWait(); //this puts it in focus
        if (alert.getResult() == ButtonType.YES) {
          //do stuff, if neccesary, else, delete
        }
        e.printStackTrace();
      }
    }
    System.out.println("trying to get Points with names");
    ArrayList<Point> namedPoints = new ArrayList<Point>();
    int i;
    for (i = 0; i < localPoints.size(); i++) {
      if (localPoints.get(i).getName() != null && !localPoints.get(i).getName().equals("null")
          && !localPoints.get(i).getName().equals("") && !(
          localPoints.get(i).getName().replaceAll("\\s", "") == "")) {
        namedPoints.add((Point) localPoints.get(i).clone());
      }
    }

    return namedPoints;
  }

  @Override
  public ArrayList<Point> getPoints() {
    while (saveThread.running || loadThread.running) {
      ;
    }
    if (localPoints.size() < 1) {
      try {
        System.out.println("requesting points from DB, trying to load");
        load();
      } catch (SQLException e) {
        //e.printStackTrace();
        System.out.println(
            "Error Getting Data From The Database, failed to load, will return DB local points copy \n Query/Connection Error : "
                + e.getMessage());
        Alert alert = new Alert(AlertType.ERROR, "Message. Bad Things Happened! : "
            + "DB ERROR:  failed to load, will return DB local points copy \n Query/Connection Error "
            + e.getMessage()); //can add buttons if you want, or change to different popup types
        alert.showAndWait(); //this puts it in focus
        if (alert.getResult() == ButtonType.YES) {
          //do stuff, if neccesary, else, delete
        }
      }
    }

    ArrayList<Point> ret = FakePoint.deepClone(localPoints);

    for (int i = 0; i < ret.size(); i++) {
      if (ret.get(i).getName() != null && ret.get(i).getName().equals("Elevator")) {
        Point tempPoint = ret.get(i);
        ret.remove(i);
        ret.add(i, toElevatorPoint(tempPoint));
      }
    }
    return ret;
  }

  @Override
  public void setPoints(ArrayList<Point> points) {
    while (saveThread.running || loadThread.running) {
      ;
    }
    for (int i = 0; i < points.size(); i++) {
      if (points.get(i) == null) {
        System.out.println(i + " was null");
        points.remove(i);
        i--;
      }
    }
    progressBarPercentage = 0;
    System.out.println("Setting the DB local points copy");

    diffPoints = new ArrayList<>();
    for (Point p : points) {
      Point localP = findRealPoint(p.getId(), localPoints);
      if (localP == null) {
        diffPoints.add(p);
      } else if (!p.equals(localP)) {
        System.out.println(p.toStringMoreInfo() + " : " + localP.toStringMoreInfo());
        diffPoints.add(
            p); // FIXED This was the part that broke the neighbors because p is a copy, I modified save to reconnect neighbors in the local copy, not the foreign copy
      }
    }

    if (localPoints.size() > points.size()) {
      remPoints = new ArrayList<>();
      for (Point localP : localPoints) {
        Point p = findRealPoint(localP.getId(), points);
        if (p == null) {
          remPoints.add(localP);
        }
      }
    }

    save();
    progressBarPercentage = 1;
  }


  @Override
  public ArrayList<Physician> getPhysicians() {
    while (saveThread.running || loadThread.running) {
      ;
    }
    if (localPhysicians.size() < 1) {
      try {
        System.out.println("requesting physicians from DB, trying to load");
        load();
      } catch (SQLException e) {
        System.out.println(
            "Error Getting Data From The Database, failed to load, will return DB local physicians copy \n Query/Connection Error : "
                + e.getMessage());
        Alert alert = new Alert(AlertType.ERROR, "Message. Bad Things Happened! : "
            + "DB ERROR:  failed to load, will return DB local physicians copy \n Query/Connection Error "
            + e.getMessage()); //can add buttons if you want, or change to different popup types
        alert.showAndWait(); //this puts it in focus
        if (alert.getResult() == ButtonType.YES) {
          //do stuff, if neccesary, else, delete
        }

        //e.printStackTrace();
      }
    }
    ArrayList<Physician> copyOfPhysicians = new ArrayList<Physician>();
    for (Physician p : localPhysicians) {
      copyOfPhysicians.add((Physician) p.clone());
    }

    return copyOfPhysicians;
  }

  @Override
  public void setPhysicians(ArrayList<Physician> physicians) {
    while (saveThread.running || loadThread.running) {
      ;
    }
    progressBarPercentage = 0;
    diffPhysicians = new ArrayList<Physician>(physicians);
    for (int i = 0; i < diffPhysicians.size(); i++) {
      if (localPhysicians.contains(diffPhysicians.get(i))) {
        diffPhysicians.remove(i);
        i--;
      }
    }
    System.out.println(diffPhysicians);
    remPhysicians = new ArrayList<Physician>(localPhysicians);
    for (int i = 0; i < remPhysicians.size(); i++) {
      if (physicians.contains(remPhysicians.get(i)) || diffPhysicians
          .contains(remPhysicians.get(i))) {
        remPhysicians.remove(i);
        i--;
      }
    }

    System.out.println("Setting the DB local physicians copy");
    localPhysicians = (ArrayList<Physician>) physicians.clone();

    //save_and_verify();
//    saveThread.start();
    save();
    progressBarPercentage = 1;
  }

  static ElevatorPoint toElevatorPoint(Point p) {
    ElevatorPoint ep = new ElevatorPoint(p.getXCoord(), p.getYCoord(), p.getNames(), p.getId(),
        p.getNeighbors(), p.getFloor());
    for (int i = 0; i < ep.neighbors.size(); i++) {
      ep.neighbors.get(i).neighbors.remove(p);
      ep.connectTo(ep.neighbors.get(i));
    }
    return ep;
  }


  public static boolean compareNeighbors(ArrayList<Integer> a1, ArrayList<Integer> a2) {
    for (int neighbor : a1) {
      if (!a2.contains(neighbor)) {
        return false;
      }
    }
    for (int neighbor : a2) {
      if (!a1.contains(neighbor)) {
        return false;
      }
    }
    return true;
  }

  public ArrayList<Physician> fuzzySearchPhysicians(String searchTerm) {
    ///long startTime = System.nanoTime();

    System.out.println("STARTING FUZZY SEARCH: " + searchTerm);
    ArrayList<Physician> candidates = new ArrayList<Physician>();
    if (searchTerm.replaceAll("\\s+", "") == "" || searchTerm == null) {
      candidates = localPhysicians;
      return candidates;
    }
    LinkedHashMap<Physician,Double> my_map1 = new LinkedHashMap<Physician,Double>();
    LinkedHashMap<Physician,Double> my_map2 = new LinkedHashMap<Physician,Double>();
    LinkedHashMap<Physician,Double> my_map3 = new LinkedHashMap<Physician,Double>();


    String first_name, last_name,fl,lf;
    int fORl = 10;
    LinkedHashMap<Integer,Double> length_map = make_length_map(100);
    String searchTerm2 = searchTerm.toLowerCase().replaceAll("\\s+","");
    System.out.println("search term @" + searchTerm2 + "@ ");
    double value1,value2;
    boolean include,check;
    int length;
    int length1,length2;
    int fuzzySearchThreshold;
    double FN,LN,FLN,LFN;
    for (Physician p : localPhysicians) {
      include = true;
      check = false;
      length1 = 1000;
      length2 = 1000;
      length = -101;
//      System.out.println("here");
      first_name = p.getFirstName().toLowerCase().replaceAll("\\s+","");
      last_name = p.getLastName().toLowerCase().replaceAll("\\s+","");
      fl = first_name + last_name;
      lf = last_name + first_name;
      fuzzySearchThreshold = fl.length() - 1;
      //System.out.println("first, last, fist-last,last-first : @" + first_name + "@ @" + last_name + "@ @" + fl + "@ @" + lf + "@ ");
      if(StringUtils.containsAny(first_name,searchTerm2) ||
          StringUtils.containsAny(last_name,searchTerm2)/*||
            StringUtils.containsAny(p.getTitle(),searchTerm)*/){
        //candidates.add(p);
        int fn = StringUtils.getLevenshteinDistance(first_name,searchTerm2,fuzzySearchThreshold);
        //System.out.println("fn weight: @" + fn);
        int ln = StringUtils.getLevenshteinDistance(last_name,searchTerm2,fuzzySearchThreshold);
        int fln = StringUtils.getLevenshteinDistance(fl,searchTerm2,fuzzySearchThreshold);
        int lfn = StringUtils.getLevenshteinDistance(lf,searchTerm2,fuzzySearchThreshold);
        //System.out.println("ln weight: @" + ln);
        //int t = StringUtils.getLevenshteinDistance(p.getTitle(),searchTerm);
          /*if(first_name.length() + last_name.length() < searchTerm2.length() || fn == -1){
            fn = 100000;
          }
          if(last_name.length() + first_name.length()< searchTerm2.length() || ln == -1){
            ln = 100000;
          }*/
//          if(fn == 100000 && ln == 100000){
//            include = false;
//          }

        if(fn == -1){
          fn = 10000;
        }
        if(ln == -1){
          ln = 10000;
        }
        if(lfn == -1){
          lfn = 10000;
        }
        if(fln == -1){
          fln = 10000;
        }
        if(fn == 10000 && ln == 10000){
          include = false;
        }

        System.out.println("firs-last fn, ln, fln, lfn: @" + fl + "  " + fn + ", " + ln + ", " + fln + ", " + lfn);

        FN = (double)fn;
        LN = (double)ln;
        FLN = (double)fln;
        LFN = (double)lfn;
        value1 = Math.min(FN,Math.min(LN,Math.min(FLN,LFN)));
        value2 = Math.min(FLN,LFN);


        if(StringUtils.containsIgnoreCase(first_name,searchTerm2)){
          fORl = 0;
          length1 = first_name.length();
          check = true;
        }
        if(StringUtils.containsIgnoreCase(last_name,searchTerm2)){
          fORl = 1;
          length2 = last_name.length();
          check = true;
        }
        if(check){
          if(length1 > length2){
            length = length2;
          }else{
            length = length1;
          }
        }
        if(length != -101){
          value1 = -2 + length_map.get(length);
        }

        /*if(value == 100000.0){
            //System.out.println("in NOT include ");
            include = false;
        }*/


        //System.out.println("first , last, value: @" + first_name + "@ @" + last_name + "@ " + value);

        if(include){
          my_map1.put(p,value1);
          my_map2.put(p,value2);
        }
//          System.out.println("here, value, id: " + value + " " + p.getID());

      }

    }
    LinkedHashMap sortedMap1 = sortByValues(my_map1);
    //Map<Integer,Physician> sortedMap = new TreeMap<Integer,Physician>(map);
    ArrayList list2 = new ArrayList(sortedMap1.entrySet());

    int counter = -1;
    //Set set = sortedMap.entrySet();
    //Iterator iterator = set.iterator();
    //HashMap sortedHashMap = new HashMap();
    for (Iterator it2 = list2.iterator(); it2.hasNext() && counter < fuzzySearchLimit;) {
      counter++;
      Entry my_entry = (Map.Entry) it2.next();
      my_map3.put((Physician)my_entry.getKey(),my_map2.get(my_entry.getKey()));
      //sortedHashMap.put(entry.getKey(),entry.getValue());
      candidates.add(counter,(Physician) my_entry.getKey());
      System.out.println("key1, value1 : " + ((Physician)(my_entry.getKey())).getLastName() + " " + ((Physician)(my_entry.getKey())).getFirstName()  + " " + my_entry.getValue());
    }

    LinkedHashMap sortedMap2 = sortByValues(my_map3);
    ArrayList list3 = new ArrayList(sortedMap2.entrySet());

/*    int counter2 = -1;
    //Set set = sortedMap.entrySet();
    //Iterator iterator = set.iterator();
    //HashMap sortedHashMap = new HashMap();
    for (Iterator it3 = list3.iterator(); it3.hasNext() && counter2 < fuzzySearchLimit;) {
      counter2++;
      Entry my_entry2 = (Map.Entry) it3.next();
      //my_map3.put((Physician)my_entry.getKey(),my_map2.get(my_entry.getKey()));
      //sortedHashMap.put(entry.getKey(),entry.getValue());
      candidates.add(counter2,(Physician) my_entry2.getKey());
      System.out.println("key2, value2 : " + ((Physician)(my_entry2.getKey())).getLastName() + " " + ((Physician)(my_entry2.getKey())).getFirstName()  + " " + my_entry2.getValue());
    }
*/


    return candidates;
  }

  private LinkedHashMap<Integer, Double> make_length_map(int interval){
    LinkedHashMap<Integer,Double> this_map = new LinkedHashMap<Integer,Double>();
    double mini_value;
    for(int i = 0;i < interval;i++){
      mini_value = i * (((double)1)/((double)interval));
      mini_value = Math.round(mini_value*100)/100.0d;
      //System.out.println("mini value: " + mini_value);
      //mini_value = round(mini_value,2);
      this_map.put(i,mini_value);
      //System.out.println("mini interval : " + mini_value);
    }
    return this_map;
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
    for (Iterator it = list.iterator(); it.hasNext(); ) {
      Map.Entry entry = (Map.Entry) it.next();
      sortedHashMap.put(entry.getKey(), entry.getValue());
//      System.out.println("in comaprator : " + entry.getValue());
    }
    return sortedHashMap;
  }


  public ArrayList<Point> fuzzySearchPoints(String searchTerm) {
    //long startTime = System.nanoTime();

    ArrayList<Point> candidates = new ArrayList<Point>();
    LinkedHashMap<Point,Double> my_map = new LinkedHashMap<Point,Double>();

    ArrayList<Point> named_points = getLocalNamedPoints();
//    ArrayList<Point> named_points = getNamedPoints(); slow af
//    Soundex soundex = new Soundex();
//    System.out.println("here");

    searchTerm = searchTerm.toLowerCase();
    LinkedHashMap<Integer,Double> length_map = make_length_map(50);
    int fuzzySearchThreshold2 = 20;
    for (Point p : named_points) {
      boolean worthit = false;
//      System.out.println("here");
      ArrayList<String> names = p.getNames();
      ArrayList<String> lc_names = new ArrayList<String>();
      ArrayList<Double> distances = new ArrayList<Double>();
      long startTime2 = System.nanoTime();
      int i;
      for (i = 0; i < names.size(); i++) {
        lc_names.add(names.get(i).toLowerCase());
        if (StringUtils.containsAny(lc_names.get(i), searchTerm)) {
          worthit = true;
          double value2 = (double)StringUtils.getLevenshteinDistance(lc_names.get(i),searchTerm,fuzzySearchThreshold2);
          if(StringUtils.startsWith(lc_names.get(i),searchTerm)){
            if(lc_names.get(i) != null) {
              System.out.println("this name,length: " + lc_names.get(i) + " " + lc_names.get(i).length());
              value2 = -2 + length_map.get(lc_names.get(i).length());
            }
            //System.out.println("");
            //System.out.println("in fz points,value in map : " + length_map.get(lc_names.get(i).length()));
          }
          if(value2 != -1){
            distances.add(value2);
          }
        }
      }
      //long endTime2 = System.nanoTime();
      //long duration2 = endTime2 - startTime2;
      //System.out.println("duration point first nested loop, counter: " +  duration2 + " " + i);


      if (worthit && distances != null && distances.size() != 0) {
        double value = distances.get(0);
        long startTime3 = System.nanoTime();
        int k;
        for (k = 0; i < distances.size(); i++) {
          if (distances.get(i) < value) {
            value = distances.get(i);
          }
        }
        //long endTime3 = System.nanoTime();
        //long duration3 = endTime3 - startTime3;
        //System.out.println("duration point first nested loop, counter: " +  duration3 + " " + i);


        my_map.put(p, value);
        System.out.println("here, value, id: " + value + " " + p.getId());

      }else{
        my_map.put(p, 10000.0);
      }

    }
    LinkedHashMap sortedMap = sortByValues(my_map);
    //Map<Integer,Physician> sortedMap = new TreeMap<Integer,Physician>(map);
    ArrayList list2 = new ArrayList(sortedMap.entrySet());

    int counter = -1;
    //Set set = sortedMap.entrySet();
    //Iterator iterator = set.iterator();
    //HashMap sortedHashMap = new HashMap();
    for (Iterator it2 = list2.iterator(); it2.hasNext() && counter < fuzzySearchLimit; ) {
      counter++;
      Entry my_entry = (Map.Entry) it2.next();
      //sortedHashMap.put(entry.getKey(),entry.getValue());
      candidates.add(counter, (Point) my_entry.getKey());
      System.out.println("key, value : " + my_entry.getKey() + " " + my_entry.getValue());
    }

    System.out.println("size : " + candidates.size());

    //long endTime = System.nanoTime();
    //long duration = endTime - startTime;
    //System.out.println("duration point: " +  duration);

    return candidates;
  }

  /**
   * A much faster search for named points. Doesn't contact the database!
   *
   * @return ArrayList\<Point> The list of points which have names
   * @author backslash166
   */
  private ArrayList<Point> getLocalNamedPoints() {
    ArrayList<Point> named_points = new ArrayList<Point>();
    if (localPoints.size() > 0) {
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
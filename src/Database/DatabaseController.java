package Database;

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

  private static final int fuzzySearchThreshold = 10;
  private static final int fuzzySearchLimit = 20;
  SaveThread saveThread;
  public double progressBarPercentage = 0;
  ArrayList<Point> localPoints;
  ArrayList<Physician> localPhysicians;


  DatabaseDriver dbc = null;

  public DatabaseController(DatabaseDriver _dbc) {
    this.localPhysicians = new ArrayList<Physician>();
    this.localPoints = new ArrayList<Point>();
    this.dbc = _dbc;
    saveThread = new SaveThread(this);
  }

  ///////////////////////////
  /////// Physician /////////
  ///////////////////////////

  public boolean removePhysician(long pid) {
    dbc.send_Command(
        "delete from physician where PID = " + pid + ")"
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
            + "', title  =  '" + title + "' WHERE PID = " + PID + ")"
    );

    dbc.send_Command(
        "delete from physician_location WHERE PID_ph = " + PID + ")"
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
      System.out.println("error getting fake physcians from DB; Query Erro: " + e.getMessage());
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
        ret.get(i).neighbors.add(findRealPoint(currentNeighbors.get(j), ret));
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
    System.out.println("loading physicians and points from DB to local copies ");
    localPoints = getAllPoints();
    localPhysicians = getAllPhysicians();
    progressBarPercentage = 1;
  }

  @Override
  public void save() {
    System.out.println("trying to transfer local copies of physicians and points to DB");
    for (Physician p : localPhysicians) {
      ArrayList<Point> locations = p.getLocations();
      for (int i = 0; i < locations.size(); i++) {
        if (locations.get(i) == null) {
          locations.remove(i);
        }
      }
      p.setLocations(locations);
    }
    update_points(localPoints);
    System.out.println("transferred local points copy");
    try {
      updatePhysicians(localPhysicians);
      System.out.println("transferred local physicians copy");
    } catch (SQLException e) {
      //e.printStackTrace();
      System.out
          .println("failed to transfer local physicians copy to DB; Error: " + e.getMessage());
    }
    progressBarPercentage = 1;
  }

  @Override
  public ArrayList<Point> getNamedPoints() {
    while (saveThread.running) {
      ;
    }
    try {
      load();
    } catch (SQLException e) {
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
    while (saveThread.running) {
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
    }
    return localPoints;
  }

  @Override
  public void setPoints(ArrayList<Point> points) {
    while (saveThread.running) {
      ;
    }
    System.out.println("Setting the DB local points copy");
    localPoints = points;
    //save_and_verify();
    saveThread.start();
  }

  @Override
  public ArrayList<Physician> getPhysicians() {
    while (saveThread.running) {
      ;
    }
    try {
      System.out.println("requesting physicians from DB, trying to load");
      load();
    } catch (SQLException e) {
      System.out.println(
          "Error Getting Data From The Database, failed to load, will return DB local physicians copy \n Query/Connection Error : "
              + e.getMessage());
      //e.printStackTrace();
    }
    return localPhysicians;
  }

  @Override
  public void setPhysicians(ArrayList<Physician> physicians) {
    while (saveThread.running) {
      ;
    }
    System.out.println("Setting the DB local physicians copy");
    localPhysicians = physicians;
    //save_and_verify();
    saveThread.start();
  }

  ElevatorPoint toElevatorPoint(Point p) {
    ElevatorPoint ep = new ElevatorPoint(p.getXCoord(), p.getYCoord(), p.getName(), p.getId(),
        p.getNeighbors(), p.getFloor());
    return ep;
  }

  public ArrayList<Physician> fuzzySearchPhysicians(String searchTerm) {
    ArrayList<Physician> candidates = new ArrayList<Physician>();
    LinkedHashMap<Physician,Integer> my_map = new LinkedHashMap<Physician,Integer>();
    Soundex soundex = new Soundex();
    System.out.println("here");
    for (Physician p : localPhysicians) {
      System.out.println("here");
      if(StringUtils.containsAny(p.getFirstName(),searchTerm) ||
            StringUtils.containsAny(p.getLastName(),searchTerm)/*||
            StringUtils.containsAny(p.getTitle(),searchTerm)*/){
          //candidates.add(p);
          int fn = StringUtils.getLevenshteinDistance(p.getFirstName(),searchTerm);
          int ln = StringUtils.getLevenshteinDistance(p.getLastName(),searchTerm);
          int t = StringUtils.getLevenshteinDistance(p.getTitle(),searchTerm);
          int value = Math.min(fn,ln);//,t);
          my_map.put(p,value);
          System.out.println("here, value, id: " + value + " " + p.getID());

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
        System.out.println("key, value : " + my_entry.getKey() + " " + my_entry.getValue());
      }


/*      while(iterator.hasNext() && counter < fuzzySearchLimit) {
        counter++;
        Map.Entry my_entry = (Map.Entry)iterator.next();
        //candidates.add((Physician) my_entry.getKey());
        candidates.add(counter,(Physician) my_entry.getKey());
        System.out.println("key, value : " + my_entry.getKey() + " " + my_entry.getValue());
        iterator.remove();
      }*/
      System.out.println("size : " + candidates.size());

    return candidates;
  }

  private  LinkedHashMap sortByValues(Map map) {

    /*
    Set<Entry<Physician, Integer>> set = map.entrySet();
    List<Entry<Physician, Integer>> list = new ArrayList<Entry<Physician, Integer>>(set);
    Collections.sort( list, new Comparator<Map.Entry<Physician, Integer>>()
    {
      public int compare( Map.Entry<Physician, Integer> o1, Map.Entry<Physician, Integer> o2 )
      {
        return (o2.getValue()).compareTo( o1.getValue() );
      }
    } );
    HashMap sortedHashMap = new HashMap();
    for (Iterator it = list.iterator(); it.hasNext();) {
      Map.Entry entry = (Map.Entry) it.next();
      sortedHashMap.put(entry.getKey(),entry.getValue());
    }
*/
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
      System.out.println("in comaprator : " + entry.getValue());
    }
    return sortedHashMap;
  }


  public ArrayList<Point> fuzzySearchPoints(String searchTerm) {
    ArrayList<Point> ret = new ArrayList<Point>();
    Soundex soundex = new Soundex();
    try {
      for (Point p : getNamedPoints()) {
        if (soundex.difference(searchTerm, p.getName()) > fuzzySearchThreshold) {
          ret.add(p);
        }
      }
    } catch (EncoderException e) {
      e.printStackTrace();
      System.out.println("There was a problem encoding one of the strings");
    }
    return ret;
  }


}
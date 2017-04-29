package Database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.ListPoints;
import org.Point;

/**
 * The purpose of this class is to store the values from the database,
 * and to make pathfinding easier.
 */
public class FakePoint {

  int xCoord;    //X coordinate
  int yCoord;    //Y coordinate
  String name;  //Name of the room
  int id;      //Unique Identifier
  int floor;
  public ArrayList<Integer> neighbors = new ArrayList<>();
  //Attributes For A* only below.
  int parent;
  int cost;

  //Constructor
  public FakePoint(double xCoord, double yCoord, String name) {
    this.xCoord = (int) xCoord;
    this.yCoord = (int) yCoord;
    this.name = name;
    this.name.replace(';','_');
  }

  public FakePoint(double xCoord, double yCoord, int floor) {
    this.xCoord = (int) xCoord;
    this.yCoord = (int) yCoord;
    this.floor = floor;
  }

  public FakePoint(int xCoord, int yCoord, String name, int id, ArrayList<Integer> new_neighbors,
      int floor) {
    this.xCoord = xCoord;
    this.yCoord = yCoord;
    this.name = name;
    this.id = id;
    this.parent = -1;
    this.neighbors = new_neighbors;
    this.cost = 0;
    this.floor = floor;
    this.name.replace(';','_');
  }

  public FakePoint(Point equivalent) {
    this.xCoord = equivalent.getXCoord();
    this.yCoord = equivalent.getYCoord();
    this.name = "";
    if (equivalent.getNames() != null) {
      for (String n : equivalent.getNames()) {
        if (n != null && n.length() > 1) {
          this.name += n + "\t";
        }
      }
      if (this.name.length() > 1) {
        this.name = this.name.substring(0, this.name.length() - 1);
      }
    }

    this.id = equivalent.getId();
    if (equivalent.getParent() == null) {
      this.parent = -1;
    } else {
      this.parent = equivalent.getParent().getId();
    }
    this.cost = equivalent.getCost();
    this.floor = equivalent.getFloor();
    for (int i = 0; i < equivalent.neighbors.size(); i++) {
      this.neighbors.add(equivalent.neighbors.get(i).getId());
    }
    this.name.replace(';','_');
  }

  //Methods
  public void connectTo(FakePoint node) {
    node.getNeighbors().add(this.getId());
    this.neighbors.add(node.getId());
  }

  /**
   * Creates a new real point with all the same parameters EXCEPT WITHOUT THE NEIGHBORS, THAT IS ON
   * YOU
   *
   * @return The Real Point Equivalent (w/o Neighbors)
   */
  Point toRealPoint() {
    ArrayList<String> names = new ArrayList<String>(Arrays.asList(this.name.split("\t")));
    Point ret = new Point(this.xCoord, this.yCoord, names, this.id, new ArrayList<Point>(),
        this.floor);
    return ret;
  }


  public ArrayList<Integer> getNeighbors() {
    return this.neighbors;
  }

  public void setNeighbors(ArrayList<Integer> neighbors) {
    this.neighbors = neighbors;
  }

  public void addParent(int padre) {
    this.parent = padre;
  }

  public void setName(String newName) {
    name = newName;
  }

  public String getName() {
    return name;
  }

  public void setFloor(int floor) {
    this.floor = floor;
  }

  public int getFloor() {
    return floor;
  }

  public void setXCoord(double xCoord) {
    this.xCoord = (int) xCoord;
  }

  public int getXCoord() {
    return this.xCoord;
  }

  public int getYCoord() {
    return this.yCoord;
  }

  public int getId() {
    return this.id;
  }

  public int getCost() {
    return this.cost;
  }

  public void setYCoord(double yCoord) {
    this.yCoord = (int) yCoord;
  }



  public static ArrayList<Point> deepClone(ArrayList<Point> points){
    HashMap<Point, Point> newPoints = new HashMap<Point, Point>();
    for(Point p : points){
      newPoints.put(p, new Point(p.getXCoord(), p.getYCoord(), p.getName(), p.getId(), new ArrayList<Point>(), p.getFloor()));
    }
    for(Point p : points){
      Point p2 = newPoints.get(p);
      for(Point pN : p.getNeighbors()){
        Point newNeighbor = newPoints.get(pN);
        if(newNeighbor != null) {
          p2.connectTo(newPoints.get(pN));
        }
      }
    }
    ArrayList<Point> out = new ArrayList<Point>();
    for(Point p : newPoints.values()){
      out.add(p);
    }
    return out;
  }

}

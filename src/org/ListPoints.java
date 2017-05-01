package org;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author  ajanagal and aramirez2
 *
 * @since 1.0
 */

/**
 * This class will store lists of Points to deliver to the controller.
 */
public class ListPoints {
  // Used for generation unique IDS
  private ArrayList<Point> points;  //List of Nodes that relate to each other

  //Constructor
  public ListPoints(ArrayList<Point> points){
    this.points = points;
  }

  //Methods
  private void sort(){}

  //Helpers
  public ArrayList<Point> getPoints(){
    return this.points;
  } //Getter

  public void include(Point n){
    this.points.add(n);  //adds node to ArrayList Nodes
  }

  /**
   * Generates a clone of this ListPoints with no identical references
   * Will be used for copying points so that pasting does not break the program
   * Does not duplicate neighbors that are not selected
   * @return a clone of this ListPoints with no identical references
   */
  public ListPoints deepClone(){
    HashMap<Point, Point> newPoints = new HashMap<Point, Point>();
    for(Point p : points){
      if(p.isElevator()){
        newPoints.put(p, new ElevatorPoint(p.getXCoord(), p.getYCoord(), p.getNames(), p.getId(), new ArrayList<Point>(), p.getFloor()));
      }else if(p.isStair()){
        newPoints.put(p, new StairPoint(p.getXCoord(), p.getYCoord(), p.getNames(), p.getId(), new ArrayList<Point>(), p.getFloor()));
      }else{
        newPoints.put(p, new Point(p.getXCoord(), p.getYCoord(), p.getNames(), p.getId(), new ArrayList<Point>(), p.getFloor()));
      }
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
    return new ListPoints(out);
  }

  /**
   * Creates a HashMap that maps the names of buildings, as Strings, to the list of Points that
   * are contained by each building. This method assumes that every point's last alias is the name
   * of the building in which it resides.
   * @return A HashMap mapping the names of the buildings to an ArrayList of Points in each building
   */
  public HashMap<String, ArrayList<Point>> separateIntoBuildings(){
    HashMap<String, ArrayList<Point>> out = new HashMap<>();
    for(Point p : points){
      String buildingName = p.getNames().get(p.getNames().size() - 1);
      if(out.keySet().contains(buildingName)){
        out.get(buildingName).add(p);
      }else{
        ArrayList<Point> buildingPoints = new ArrayList<>();
        buildingPoints.add(p);
        out.put(buildingName, buildingPoints);
      }
    }
    return out;
  }

  /**
   * Searches through this ListPoints and returns any points on the given floor
   * @param floor the floor to search for
   * @return a ListPoints of the points on the requested floor
   */
  public ListPoints getFloor(int floor, String building){
    ArrayList<Point> out = new ArrayList<Point>();
    for(Point p : this.getPoints()){
      if(p.getFloor() == floor && building.equals(p.getBuilding())){
        out.add(p);
      }
    }
    return new ListPoints(out);
  }


  /**
   * gridCreate creates a grid based on width and height
   *
   * <p>
   *   gridCreate creates a grid based on a size of width and height, all of the adjacent nodes are
   *   connected to each other ( the 4 neighbors).
   * </p>
   * @param width
   * @param height
   * @param floor
   * @return
   */

  public ArrayList<Point> gridCreate(int width, int height, int floor){
    int x = 0;
    int y = 0;
    ArrayList<Point> p = new ArrayList<Point>();

    for(int i = 0; i < width*height; i++) {//creates Points
      x = i % width;
      y = i / width;

      Point c = new Point(x, y, "", i, new ArrayList<Point>(), floor);
      p.add(i, c);
    }

    for (int i = 0; i < width*height; i++){
      Point p1, p2, p3, p4 = null;
      x = i % width;
      y = i / width;

      if(y > 0){
        p1 = p.get(i-width);
        p.get(i).neighbors.add(p1);
      }
      if(x < (width-1)){
        p2 = p.get(i+1);
        p.get(i).neighbors.add(p2);
      }
      if(y < (height-1)){
        p3 = p.get(i+width);
        p.get(i).neighbors.add(p3);
      }
      if(x > 0){
        p4 = p.get(i-1);
        p.get(i).neighbors.add(p4);
      }
    }
    return p;
  }

  public ArrayList<Point> executeStrategy(Point start, Point goal){
    return CentralController.currSession.algorithm.execute(start, goal);
  }

  /**
   * gridCreate creates a grid based on width and height
   *
   * <p>
   *   gridCreate creates a grid based on a size of width and height, all of the adjacent nodes are
   *   connected to each other ( the 4 neighbors).
   * </p>
   * @param width
   * @param length
   * @param height
   * @return
   */
  public ArrayList<ArrayList<Point>> grid3dCreate(int width, int length, int height){
    ArrayList<ArrayList<Point>> p = new ArrayList< ArrayList<Point>>();

    for(int i = 1; i < height+1; i++) {//creates Points

      p.add(gridCreate(width,length,i));
    }
    return p;
  }

  /**
   * Checks if a node is unreachable from the ListPoint object
   *
   * @throws DisconnectedNodeException
   */
  public void verifyNodeConnections(Point p) throws DisconnectedNodeException{
    if(p.getNeighbors().size() == 0){
      throw new DisconnectedNodeException(p);
    }
    try{
      for(Point goal : this.points){
        executeStrategy(p, goal);
      }
    } catch (NoPathException e){
      throw new DisconnectedNodeException(p);
    }
  }

  /**
   * TimedPath will create a string that will display the time estimation of the path in minutes
   * <p>
   *    It takes the path, reverse the order and uses the x,y coordinates, stairs and elevators to
   *    estimate the how long will it take to reach the destination
   * </p>
   * @param path - array list of points in reverse order, taken from the pathfinding algorithm
   * @return String - Describes the amount of the time in a string
   *          if it is under a minute, it will say that otherwise it will round to the nearest minute
   */
  public String TimedPath(ArrayList<Point> path){
    double pixelToFeet = .20;//483/2242; // ft/pixel
    double timeWalkConstant = pixelToFeet/4.54; //sec/pixel for walking
    double timeElev = 1 ; // in sec , assumes average speed is 50 ft/sec - one flight is 10ft
    double timeStair = 10; // sec assumes climbing up or down ten ft is ten seconds

    double totalmin = 0.0;
    double totalsec = 0.0;

    ArrayList<Point> correctedPath = new ArrayList<Point>();
    for (Point end : path){
      correctedPath.add(0,end); //updates it so always places new things in the beginning
    }
    for (int i = 0; i < correctedPath.size(); i++){
      if(correctedPath.size()-1 == i){
        //last point, doesnt do anything
      }
      else if((correctedPath.get(i) instanceof  ElevatorPoint) && //current and next point are both elevators
          (correctedPath.get(i+1) instanceof ElevatorPoint)) {
        totalsec += timeElev; //adds time in elevator
      }else if((correctedPath.get(i) instanceof  StairPoint) && //current and next point are both stairs
          (correctedPath.get(i+1) instanceof StairPoint)) {
        totalsec += timeStair;  //adds time in stairs
      }else{
        double pixel = correctedPath.get(i).TimeDistance(correctedPath.get(i+1)); //gets float distance
        totalsec += timeWalkConstant*pixel;   //times with walk time constant to get time in sec
      }
    }
    totalmin = totalsec / 60; //converts to minutes
    if (totalmin < 1){
      return ("The time estimation to arrive at your Destination will take less than a minute.");
    }
    int total = (int) totalmin;
    if(totalmin - total >= .5){
      total++;
    }
    return "The time estimation to arrive at your Destination will take about " + Integer.toString(total)+ " minutes.";
  }
}

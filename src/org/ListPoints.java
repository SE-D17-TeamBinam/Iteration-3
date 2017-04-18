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
      newPoints.put(p, new Point(p.getXCoord(), p.getYCoord(), p.getName(), generateUniqueID(), new ArrayList<Point>(), p.getFloor()));
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
   * Attempts to generate a unique ID
   * This method should probably be rewritten more reliably, as this could theoretically repeat
   * @return a unique ID as an integer
   */
  public int generateUniqueID(){
    return (int) Math.random()*Integer.MAX_VALUE;
  }

  /**
   * Searches through this ListPoints and returns any points on the given floor
   * @param floor the floor to search for
   * @return a ListPoints of the points on the requested floor
   */
  public ListPoints getFloor(int floor){
    ArrayList<Point> out = new ArrayList<Point>();
    for(Point p : this.getPoints()){
      if(p.getFloor() == floor){
        out.add(p);
      }else{
        for(Point p2 : out){
//          p2.severFrom(p);
        }
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
}

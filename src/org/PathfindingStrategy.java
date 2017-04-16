package org;

import org.NoPathException;
import org.Point;

import java.util.ArrayList;

/**
 * Created by Tom on 4/15/2017.
 */
public abstract class PathfindingStrategy {


  /**
   * Execute the current algorithm in Session
   * @param start: starting Point
   * @param goal: ending Point
   * @return path from start to goal
   * @throws NoPathException
   */
  public abstract ArrayList<Point> execute(Point start, Point goal) throws NoPathException;

  /**
   *  ListPath will create a path from the pathfinding algorithm
   *  <p>
   *    It creates a ListPoints classs which will hold the arraylist of the path from
   *    one direction to another.
   *  </p>
   *
   * @param destination     This is usually the goal from A*
   * @param begin           This is the start of A*
   * @return                A list of points that represent a path from goal to start
   */

  public ArrayList<Point> ListPath(Point destination, Point begin){    //ceate a path list by reading parents
    ArrayList<Point> order = new ArrayList<Point>();


    while(destination != begin){  //while destination is not start,
      // since start should not have a parent
      order.add(destination);
      destination = destination.parent; //set the parent as new destination and try again.
      //Add destination to path list
    }
    order.add(destination);  //add "start" to listpath
    return order;
  }
}

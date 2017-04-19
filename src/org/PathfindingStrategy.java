package org;

import java.util.Collections;
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

  /**
   * Helper function that iterates through Points using parents. Starts
   * at the goal and moves to the starting Point. Reverses this order when
   * complete.
   * @param start: starting Point
   * @param goal: ending Point
   * @return the path from the starting Point to the ending Point
   */
  public ArrayList<Point> findPath(Point start, Point goal){

    // Initializes the path to be returned
    ArrayList<Point> path = new ArrayList<Point>();
    path.add(goal);
    // Sets the current Point to iterate through.
    Point currPoint = goal;

    // Runs though the path by starting at the end and using parents to
    // find the next position up.
    while(currPoint != start){
      path.add(currPoint.parent);
      currPoint = currPoint.getParent();
    }

    // Reverses the path so it moves from start to end instead of end to start
    Collections.reverse(path);
    return path;
  }
}

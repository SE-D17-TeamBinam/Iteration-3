package org;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Created by Brandon on 4/17/2017.
 */
public class BFS extends PathfindingStrategy{

  // Initializes a visited and queue HashSet and LinkedList for execute function
  HashSet<Point> visited = new HashSet<Point>();
  LinkedList<Point> queue = new LinkedList<Point>();

  /**
   * Uses a LinkedList queue and HashSet visited to iterate through points
   * until the goal is found. Sets the parent of each Point so that a clear path
   * can be found by the helper function findPath. If the queue empties and no
   * path is found throws a NoPathException.
   * @param start: starting Point
   * @param goal: ending Point
   * @return the path from the start to the goal
   * @throws NoPathException when a path from start to goal does not exist
   */
  @Override
  public ArrayList<Point> execute(Point start, Point goal) throws NoPathException {

    // Used to denote the current node to enqueue neighbors.
    Point currPoint;

    // Used to track visited nodes.
    visited.add(start);
    // Used to queue through nodes to check and add neighbors.
    queue.add(start);

    while(queue.size() != 0){
      //Sets the current point in the queue by retrieving and removing the list's head.
      currPoint = queue.poll();

      // If a neighbor has not yet been visited add
      // them to the queue and to visited. Additionally,
      // set their parent to the current node.
      for(Point neighbor: currPoint.getNeighbors()){
        if(!visited.contains(neighbor)) {
          neighbor.addParent(currPoint);
          queue.add(neighbor);
          visited.add(neighbor);
        }
      }

      // If the current point is the goal, use the helper function to return the path
      // and clear the queue and visited lists.
      if(currPoint == goal){
        queue.clear();
        visited.clear();
        return findPath(start, goal);
      }
    }
    throw new NoPathException();
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

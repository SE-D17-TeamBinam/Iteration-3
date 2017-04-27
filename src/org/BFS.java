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
    System.out.println("Using BFS");
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
        if(!visited.contains(neighbor) && !neighbor.isBlocked) {
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


  public String toString(){
    return "BFS";
  }

}

package org;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;

/**
 * Created by Tom on 4/15/2017.
 */
public class DFS extends PathfindingStrategy {

  HashSet<Point> visited = new HashSet<>(); // Used to prevent stack overflow
  Point startingLoc; // Used to validate where we are in recursion
  Stack<Point> stack;

  /**
   * uses a stack to find the first path that connects start to goal using a DFS algorithm
   * @implNote this completely ignores any heuristics
   * @param start: starting Point
   * @param goal: ending Point
   * @return the path to get from the original `start` to `goal`
   * @throws NoPathException
   */
  @Override
  public ArrayList<Point> execute(Point start, Point goal) throws NoPathException{
    System.out.println("Using DFS");
    stack = new Stack();
    visited = new HashSet<>();
    Point currPoint;

    // Used to track visited nodes.
    visited.add(start);
    // Used to queue through nodes to check and add neighbors.
    stack.push(start);

    while (stack.size() != 0) {
      //Sets the current point in the queue by retrieving and removing the list's head.
      currPoint = stack.pop();

      // If a neighbor has not yet been visited add
      // them to the queue and to visited. Additionally,
      // set their parent to the current node.
      for (Point neighbor : currPoint.getNeighbors()) {
        if (!visited.contains(neighbor)&&!neighbor.isBlocked) {
          neighbor.addParent(currPoint);
          stack.push(neighbor);
          visited.add(neighbor);
        }
      }

      // If the current point is the goal, use the helper function to return the path
      // and clear the queue and visited lists.
      if (currPoint == goal) {
        return findPath(start, goal);
      }
    }
    throw new NoPathException();
  }

  public String toString(){
    return "DFS";
  }
}

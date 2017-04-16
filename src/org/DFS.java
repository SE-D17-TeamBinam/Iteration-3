package org;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by Tom on 4/15/2017.
 */
public class DFS extends PathfindingStrategy {

  HashSet<Point> visited = new HashSet<>(); // Used to prevent stack overflow
  Point startingLoc; // Used to validate where we are in recursion

  /**
   * Recursively find the first path that connects start to goal using a DFS algorithm
   * @implNote this completely ignores any heuristics
   * @param start: starting Point
   * @param goal: ending Point
   * @return the path to get from the original `start` to `goal`
   * @throws NoPathException
   */
  @Override
  public ArrayList<Point> execute(Point start, Point goal) throws NoPathException {
    visited.add(start);

    if(startingLoc == null){
      startingLoc = start;
    }

    if(start.equals(goal)){
      return ListPath(goal, startingLoc);
    }

    for(Point neighbor: start.getNeighbors()){
      if(visited.contains(neighbor)) {
        continue;
      }

      neighbor.parent = start;
      ArrayList<Point> result =  execute(neighbor, goal);
      if(result != null){
        return result;
      }
    }

    if(start.equals(startingLoc)) {
      startingLoc = null;
      throw new NoPathException();
    }else {
      return null;
    }
  }
}

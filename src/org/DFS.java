package org;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by Tom on 4/15/2017.
 */
public class DFS extends PathfindingStrategy {

  HashSet<Point> visited = new HashSet<>();
  Point startingLoc;

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
      return execute(neighbor, goal);
    }

    if(start.equals(startingLoc)) {
      startingLoc = null;
      throw new NoPathException();
    }else {
      return null;
    }
  }
}

package org;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import org.junit.jupiter.api.Test;

/**
 * Created by Tom on 4/15/2017.
 */
class DFSTest {

  ListPoints test = new ListPoints(new ArrayList<Point>());
  ArrayList<ArrayList<Point>> floor = new ArrayList<ArrayList<Point>>();
  DFS strat = new DFS();

  @Test
  void singleFloor(){
    CentralController.getCurrSession().algorithm = strat;

    ArrayList<Point> floor = test.gridCreate(60, 60, 1);

    Point node1 = floor.get(0);
    Point node2 = floor.get(15);

    ArrayList<Point> path = new ArrayList<Point>();

    try {
      path = test.executeStrategy(node1, node2);
    } catch (Exception e) {
      assertTrue(false);
      e.printStackTrace();
    }
  }

  @Test
  void simpleMultiFloor(){
    CentralController.getCurrSession().algorithm = strat;

    ArrayList<ArrayList<Point>> floors = test.grid3dCreate(5, 5, 3);

    floors.get(0).get(3).connectTo(floors.get(1).get(2));
    floors.get(1).get(2).compareTo(floors.get(0).get(3));
    floors.get(1).get(2).connectTo(floors.get(2).get(0));
    floors.get(2).get(0).connectTo(floors.get(1).get(2));

    Point node1 = floors.get(0).get(2);
    Point node2 = floors.get(2).get(4);

    ArrayList<Point> path = new ArrayList<Point>();

    try {
      path = test.executeStrategy(node1, node2);
    } catch (Exception e) {
      assertTrue(false);
      e.printStackTrace();
    }
  }

  @Test
  void multiFloor() {
    CentralController.getCurrSession().algorithm = strat;

    ArrayList<ArrayList<Point>> floor = test.grid3dCreate(5,5,3);
    Point node1 = floor.get(0).get(4);
    Point node2 = floor.get(1).get(4);
    Point node3 = floor.get(2).get(4);
    ElevatorPoint elevator1 = new ElevatorPoint(node1.getXCoord(), node1.getYCoord(), node1.getName(),node1.getId(),
        node1.neighbors,node1.getFloor());
    ElevatorPoint elevator2 = new ElevatorPoint(node2.getXCoord(), node2.getYCoord(), node2.getName(),node2.getId(),
        node2.neighbors,node2.getFloor());
    ElevatorPoint elevator3 = new ElevatorPoint(node3.getXCoord(), node3.getYCoord(), node3.getName(),node3.getId(),
        node3.neighbors,node3.getFloor());


    elevator1.neighbors.remove(node2);
    elevator2.neighbors.remove(node1);
    elevator2.neighbors.remove(node3);
    elevator3.neighbors.remove(node2);
    elevator1.neighbors.add(elevator2);
    elevator2.neighbors.add(elevator3);
    elevator2.neighbors.add(elevator1);
    elevator3.neighbors.add(elevator2);


    floor.get(0).remove(4);
    floor.get(1).remove(4);
    floor.get(2).remove(4);

    floor.get(0).add(4,elevator1);
    floor.get(1).add(4,elevator2);
    floor.get(2).add(4,elevator3);

    floor.get(0).get(3).neighbors.remove(node1);
    floor.get(0).get(9).neighbors.remove(node1);
    floor.get(1).get(3).neighbors.remove(node2);
    floor.get(1).get(9).neighbors.remove(node2);
    floor.get(2).get(3).neighbors.remove(node3);
    floor.get(2).get(9).neighbors.remove(node3);

    floor.get(0).get(3).neighbors.add(elevator1);
    floor.get(0).get(9).neighbors.add(elevator1);
    floor.get(1).get(3).neighbors.add(elevator2);
    floor.get(1).get(9).neighbors.add(elevator2);
    floor.get(2).get(3).neighbors.add(elevator3);
    floor.get(2).get(9).neighbors.add(elevator3);


    Point start = floor.get(0).get(0); //Chose start and goal points and floors
    Point goal = floor.get(2).get(0);

    ArrayList<Point> path = new ArrayList<Point>();

    try {
      path = test.executeStrategy(start,goal);
    } catch (Exception e) {
      assertTrue(false);
      e.printStackTrace();
    }

    for(int i = 0; i < path.size(); i++){
      if(path.get(i).getFloor() != start.getFloor() && path.get(i).getFloor() != goal.getFloor()){
        if (path.get(i) instanceof ElevatorPoint){

        }else {
          assertTrue(false);
        }
      }
    }
    assertTrue(true);

  }

}
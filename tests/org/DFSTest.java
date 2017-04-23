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

    ArrayList<ArrayList<Point>> floors = test.grid3dCreate(2, 2, 3);

    floors.get(0).get(0).connectTo(floors.get(1).get(0));

    floors.get(2).get(0).connectTo(floors.get(1).get(0));

    Point node1 = floors.get(0).get(0);
    Point node2 = floors.get(2).get(1);

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

    ArrayList<ArrayList<Point>> floor = test.grid3dCreate(5, 5, 3);
    Point node1 = floor.get(0).get(4);
    Point node2 = floor.get(1).get(4);
    Point node3 = floor.get(2).get(4);
    Point node4 = floor.get(0).get(3); //second elevator
    Point node5 = floor.get(1).get(3);

    ElevatorPoint elevator1 = new ElevatorPoint(node1.getXCoord(), node1.getYCoord(),
        node1.getName(), node1.getId(),
        node1.neighbors, node1.getFloor());
    ElevatorPoint elevator2 = new ElevatorPoint(node2.getXCoord(), node2.getYCoord(),
        node2.getName(), node2.getId(),
        node2.neighbors, node2.getFloor());
    ElevatorPoint elevator3 = new ElevatorPoint(node3.getXCoord(), node3.getYCoord(),
        node3.getName(), node3.getId(),
        node3.neighbors, node3.getFloor());
    ElevatorPoint elevator4 = new ElevatorPoint(node4.getXCoord(), node4.getYCoord(),
        node4.getName(), node4.getId(),
        node4.neighbors, node4.getFloor());
    ElevatorPoint elevator5 = new ElevatorPoint(node5.getXCoord(), node5.getYCoord(),
        node5.getName(), node5.getId(),
        node5.neighbors, node5.getFloor());

    elevator1.neighbors.remove(node2);
    elevator2.neighbors.remove(node1);
    elevator2.neighbors.remove(node3);
    elevator3.neighbors.remove(node2);
    elevator1.neighbors.add(elevator2);
    elevator2.neighbors.add(elevator3);
    elevator2.neighbors.add(elevator1);
    elevator3.neighbors.add(elevator2);
    elevator4.neighbors.remove(node5);
    elevator5.neighbors.remove(node4);
    elevator4.neighbors.add(elevator5);
    elevator5.neighbors.add(elevator4);

    floor.get(0).remove(4);
    floor.get(1).remove(4);
    floor.get(2).remove(4);
    floor.get(0).remove(3);
    floor.get(1).remove(3);

    floor.get(0).add(4, elevator1);
    floor.get(1).add(4, elevator2);
    floor.get(2).add(4, elevator3);
    floor.get(0).add(3, elevator4);
    floor.get(1).add(4, elevator5);

    floor.get(0).get(3).neighbors.remove(node1);
    floor.get(0).get(9).neighbors.remove(node1);
    floor.get(1).get(3).neighbors.remove(node2);
    floor.get(1).get(9).neighbors.remove(node2);
    floor.get(2).get(3).neighbors.remove(node3);
    floor.get(2).get(9).neighbors.remove(node3);
    floor.get(0).get(2).neighbors.remove(node4);
    floor.get(0).get(4).neighbors.remove(node4);
    floor.get(0).get(7).neighbors.remove(node4);
    floor.get(1).get(2).neighbors.remove(node5);
    floor.get(1).get(2).neighbors.remove(node5);
    floor.get(1).get(2).neighbors.remove(node5);

    floor.get(0).get(3).neighbors.add(elevator1);
    floor.get(0).get(9).neighbors.add(elevator1);
    floor.get(1).get(3).neighbors.add(elevator2);
    floor.get(1).get(9).neighbors.add(elevator2);
    floor.get(2).get(3).neighbors.add(elevator3);
    floor.get(2).get(9).neighbors.add(elevator3);
    floor.get(0).get(2).neighbors.add(elevator4);
    floor.get(0).get(4).neighbors.add(elevator4);
    floor.get(0).get(7).neighbors.add(elevator4);
    floor.get(1).get(2).neighbors.add(elevator5);
    floor.get(1).get(4).neighbors.add(elevator5);
    floor.get(1).get(7).neighbors.add(elevator5);

    Point start = floor.get(0).get(0); //Chose start and goal points and floors
    Point goal = floor.get(2).get(0);

    ArrayList<Point> path = new ArrayList<Point>();

    try {
      path = test.executeStrategy(start, goal);
    } catch (Exception e) {
      assertTrue(false);
      e.printStackTrace();
    }

    assertNotEquals(null, path);
  }

  @Test
  void multiFloorErrorTest(){
    CentralController.getCurrSession().algorithm = strat;

    ArrayList<ArrayList<Point>> floor = test.grid3dCreate(5,5,3);
    Point node1 = floor.get(0).get(4);
    Point node2 = floor.get(1).get(4);
    Point node3 = floor.get(2).get(4);

    StairPoint stair1 = new StairPoint(node1.getXCoord(), node1.getYCoord(), node1.getName(),node1.getId(),
        node1.neighbors,node1.getFloor());
    StairPoint stair2 = new StairPoint(node2.getXCoord(), node2.getYCoord(), node2.getName(),node2.getId(),
        node2.neighbors,node2.getFloor());
    StairPoint stair3 = new StairPoint(node3.getXCoord(), node3.getYCoord(), node3.getName(),node3.getId(),
        node3.neighbors,node3.getFloor());


    stair1.neighbors.remove(node2);
    stair2.neighbors.remove(node1);
    stair2.neighbors.remove(node3);
    stair3.neighbors.remove(node2);
    stair1.neighbors.add(stair2);
    stair2.neighbors.add(stair3);
    stair2.neighbors.add(stair1);
    stair3.neighbors.add(stair2);


    floor.get(0).remove(4);
    floor.get(1).remove(4);
    floor.get(2).remove(4);

    floor.get(0).add(4,stair1);
    floor.get(1).add(4,stair2);
    floor.get(2).add(4,stair3);

    floor.get(0).get(3).neighbors.remove(node1);
    floor.get(0).get(9).neighbors.remove(node1);
    floor.get(1).get(3).neighbors.remove(node2);
    floor.get(1).get(9).neighbors.remove(node2);
    floor.get(2).get(3).neighbors.remove(node3);
    floor.get(2).get(9).neighbors.remove(node3);

    floor.get(0).get(3).neighbors.add(stair1);
    floor.get(0).get(9).neighbors.add(stair1);
    floor.get(1).get(3).neighbors.add(stair2);
    floor.get(1).get(9).neighbors.add(stair2);
    floor.get(2).get(3).neighbors.add(stair3);
    floor.get(2).get(9).neighbors.add(stair3);
//Elevators
    Point node4 = floor.get(0).get(0);
    Point node5 = floor.get(1).get(0);
    Point node6 = floor.get(2).get(0);

    ElevatorPoint elevator1 = new ElevatorPoint(node4.getXCoord(), node4.getYCoord(), node4.getName(),node4.getId(),
        node4.neighbors,node4.getFloor());
    ElevatorPoint elevator2 = new ElevatorPoint(node5.getXCoord(), node5.getYCoord(), node5.getName(),node5.getId(),
        node5.neighbors,node5.getFloor());
    ElevatorPoint elevator3 = new ElevatorPoint(node6.getXCoord(), node6.getYCoord(), node6.getName(),node6.getId(),
        node6.neighbors,node6.getFloor());


    elevator1.neighbors.remove(node5);
    elevator2.neighbors.remove(node4);
    elevator2.neighbors.remove(node6);
    elevator3.neighbors.remove(node5);
    elevator1.neighbors.add(elevator2);
    elevator2.neighbors.add(elevator3);
    elevator2.neighbors.add(elevator1);
    elevator3.neighbors.add(elevator2);


    floor.get(0).remove(0);
    floor.get(1).remove(0);
    floor.get(2).remove(0);

    floor.get(0).add(0,elevator1);
    floor.get(1).add(0,elevator2);
    floor.get(2).add(0,elevator3);

    floor.get(0).get(5).neighbors.remove(node4);
    floor.get(0).get(1).neighbors.remove(node4);
    floor.get(1).get(5).neighbors.remove(node5);
    floor.get(1).get(1).neighbors.remove(node5);
    floor.get(2).get(5).neighbors.remove(node6);
    floor.get(2).get(1).neighbors.remove(node6);

    floor.get(0).get(5).neighbors.add(elevator1);
    floor.get(0).get(1).neighbors.add(elevator1);
    floor.get(1).get(5).neighbors.add(elevator2);
    floor.get(1).get(1).neighbors.add(elevator2);
    floor.get(2).get(5).neighbors.add(elevator3);
    floor.get(2).get(1).neighbors.add(elevator3);

    Point start = floor.get(0).get(1); //Chose start and goal points and floors
    Point goal = new Point(4,4,"errorNode",3,new ArrayList<Point>(),5);

    ArrayList<Point> path = new ArrayList<Point>();

    try {
      path = test.executeStrategy(start,goal);
    } catch (NoPathException e) {
      assertTrue(true);

    }
    //assertTrue(false);
  }
  @Test
  void executeStrategyMultifloorStairBlockTest(){ // tests to see if it doesnt touch any other floors
    CentralController.getCurrSession().algorithm = strat;
    ArrayList<ArrayList<Point>> floor = test.grid3dCreate(5,5,3);
    Point node1 = floor.get(0).get(4);
    Point node2 = floor.get(1).get(4);
    Point node3 = floor.get(2).get(4);
    StairPoint stair1 = new StairPoint(node1.getXCoord(), node1.getYCoord(), node1.getName(),node1.getId(),
        node1.neighbors,node1.getFloor());
    StairPoint stair2 = new StairPoint(node2.getXCoord(), node2.getYCoord(), node2.getName(),node2.getId(),
        node2.neighbors,node2.getFloor());
    StairPoint stair3 = new StairPoint(node3.getXCoord(), node3.getYCoord(), node3.getName(),node3.getId(),
        node3.neighbors,node3.getFloor());


    stair1.neighbors.remove(node2);
    stair2.neighbors.remove(node1);
    stair2.neighbors.remove(node3);
    stair3.neighbors.remove(node2);
    stair1.neighbors.add(stair2);
    stair2.neighbors.add(stair3);
    stair2.neighbors.add(stair1);
    stair3.neighbors.add(stair2);


    floor.get(0).remove(4);
    floor.get(1).remove(4);
    floor.get(2).remove(4);

    floor.get(0).add(4,stair1);
    floor.get(1).add(4,stair2);
    floor.get(2).add(4,stair3);

    floor.get(0).get(3).neighbors.remove(node1);
    floor.get(0).get(9).neighbors.remove(node1);
    floor.get(1).get(3).neighbors.remove(node2);
    floor.get(1).get(9).neighbors.remove(node2);
    floor.get(2).get(3).neighbors.remove(node3);
    floor.get(2).get(9).neighbors.remove(node3);

    floor.get(0).get(3).neighbors.add(stair1);
    floor.get(0).get(9).neighbors.add(stair1);
    floor.get(1).get(3).neighbors.add(stair2);
    floor.get(1).get(9).neighbors.add(stair2);
    floor.get(2).get(3).neighbors.add(stair3);
    floor.get(2).get(9).neighbors.add(stair3);

    floor.get(0).get(1).setBlocked(true);
    floor.get(2).get(1).setBlocked(true);


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
        if (path.get(i).getBlocked()){
          assertTrue(false);
        }
      }
    }
    assertTrue(true);
  }

  @Test
  void executeStrategyMultifloorStairBlockErrorTest(){ // tests to see if it doesnt touch any other floors
    CentralController.getCurrSession().algorithm = strat;
    ArrayList<ArrayList<Point>> floor = test.grid3dCreate(5,5,3);
    Point node1 = floor.get(0).get(4);
    Point node2 = floor.get(1).get(4);
    Point node3 = floor.get(2).get(4);
    StairPoint stair1 = new StairPoint(node1.getXCoord(), node1.getYCoord(), node1.getName(),node1.getId(),
        node1.neighbors,node1.getFloor());
    StairPoint stair2 = new StairPoint(node2.getXCoord(), node2.getYCoord(), node2.getName(),node2.getId(),
        node2.neighbors,node2.getFloor());
    StairPoint stair3 = new StairPoint(node3.getXCoord(), node3.getYCoord(), node3.getName(),node3.getId(),
        node3.neighbors,node3.getFloor());


    stair1.neighbors.remove(node2);
    stair2.neighbors.remove(node1);
    stair2.neighbors.remove(node3);
    stair3.neighbors.remove(node2);
    stair1.neighbors.add(stair2);
    stair2.neighbors.add(stair3);
    stair2.neighbors.add(stair1);
    stair3.neighbors.add(stair2);


    floor.get(0).remove(4);
    floor.get(1).remove(4);
    floor.get(2).remove(4);

    floor.get(0).add(4,stair1);
    floor.get(1).add(4,stair2);
    floor.get(2).add(4,stair3);

    floor.get(0).get(3).neighbors.remove(node1);
    floor.get(0).get(9).neighbors.remove(node1);
    floor.get(1).get(3).neighbors.remove(node2);
    floor.get(1).get(9).neighbors.remove(node2);
    floor.get(2).get(3).neighbors.remove(node3);
    floor.get(2).get(9).neighbors.remove(node3);

    floor.get(0).get(3).neighbors.add(stair1);
    floor.get(0).get(9).neighbors.add(stair1);
    floor.get(1).get(3).neighbors.add(stair2);
    floor.get(1).get(9).neighbors.add(stair2);
    floor.get(2).get(3).neighbors.add(stair3);
    floor.get(2).get(9).neighbors.add(stair3);

    floor.get(0).get(1).setBlocked(true);
    floor.get(2).get(1).setBlocked(true);
    floor.get(2).get(5).setBlocked(true);

    Point start = floor.get(0).get(0); //Chose start and goal points and floors
    Point goal = floor.get(2).get(0);


    ArrayList<Point> path = new ArrayList<Point>();

    try {
      path = test.executeStrategy(start,goal);
    } catch (NoPathException e) {
      assertTrue(true);
      e.printStackTrace();
    }

  }
}
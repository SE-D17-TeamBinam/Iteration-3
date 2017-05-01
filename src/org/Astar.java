package org;

import java.util.ArrayList;

/**
 * Created by Tom on 4/15/2017.
 */
public class Astar extends PathfindingStrategy {


  /**
   * This is the A* algorithm to find the most efficient path
   * <p>
   *   A* is designed so that it finds the most cost efficient path. Now with multiple floors based
   *   on elevators and stairs
   * </p>
   * @param   start  starting point for the A* algorithm
   * @param   goal   desired location
   * @return  ListPoint it returns a class that contains the ArrayList of nodes generated from A*
   */

    @Override
    public ArrayList<Point> execute(Point start, Point goal) throws NoPathException {
      System.out.println("Using A*");
      boolean changeFloor = start.floor != goal.floor;
      boolean changeBuilding = start.getBuilding() != goal.getBuilding();
      if(changeBuilding){ // one fix
        changeFloor = true;
      }

      Point next = new Point(500,500,"start",0, new ArrayList<Point>(),4);
      start.parent = start;
      start.cost = 0;
      ArrayList<Point> open = new ArrayList<Point>(); //List of nodes that are seen but not checked
      ArrayList<Point> close = new ArrayList<Point>(); //List of nodes that are seen and checked
      int finding_lowest = 0; // helps find lowest total cost in open
      open.add(start);      //adds to open

      while(!(open.isEmpty())){
        int total = 10000; // comparing to function.
        int i;
        for(i = 0; i < open.size(); i++){ //finds the lowest total in the open
          if(total > (open.get(i).cost + open.get(i).Distance(goal))) {
            total = open.get(i).cost + open.get(i).Distance(goal);
            finding_lowest = i;
          }
        }
        if(open.get(finding_lowest) == goal){ //found path
          return ListPath(open.get(finding_lowest), start);
        }
        next = open.get(finding_lowest); //stores next option as next
        open.remove(next);
        int currFloor = next.floor; //current floor

        if(next.neighbors.size() == 0){
          throw new NoPathException();
        }
        close.add(next);      //add current to close
        for (int j = 0; j < next.neighbors.size(); j++ ){ //searching through neighbors
          if(next.neighbors.get(j).isBlocked){
            //doesnt update!!!
          }
          else if(open.contains(next.neighbors.get(j))){ // visited but seen
            if (next.neighbors.get(j).cost < next.cost){ // successor cost <= current cost

              next.neighbors.get(j).cost = next.cost + next.Distance(next.neighbors.get(j)); //update cost
              next.neighbors.get(j).parent = next;  //update parent
              break;
            }
          }else if(close.contains(next.neighbors.get(j))){ //visited and seen sucessor
            //Dont Update!!!!
          }else{// new to search
            if(next.neighbors.get(j) instanceof ElevatorPoint ){ //meets an elevator and changes node
              if(changeFloor  && !changeBuilding) {
                next.neighbors.get(j).cost =
                    next.cost + next.Distance(next.neighbors.get(j)); //update cost
                next.neighbors.get(j).parent = next;  //update parent

                ElevatorPoint elevator = (ElevatorPoint) next.neighbors.get(j); //cast to elevator
                close.add(elevator);
                //check to see if it can make it to desired floor
                if (elevator.canAchieveFloor(goal.floor)) {
                  int count = 0;
                  int move = goal.floor - elevator.floor; //if it needs to go higher or lower
                  while (elevator.floor != goal.floor) {
                    if (move < 0 && (elevator.neighbors.get(count).floor - elevator.floor
                        == -1)) { // goal is below current floor & neighbor is next elevator below
                      elevator.neighbors.get(count).cost = next.cost; //update cost
                      elevator.neighbors.get(count).parent = elevator;  //update parent

                      elevator = (ElevatorPoint) elevator.neighbors.get(count);
                      count = 0;
                    } else if ((move > 0 && (elevator.neighbors.get(count).floor - elevator.floor
                        == 1))) {// goal is above and neighbor goes higher
                      elevator.neighbors.get(count).cost = next.cost; //update cost
                      elevator.neighbors.get(count).parent = elevator;  //update parent

                      elevator = (ElevatorPoint) elevator.neighbors.get(count);
                      count = 0;
                    } else { //does nothing
                      count++;
                    }
                  }
                  open.add(elevator);
                  changeFloor = false;

                }
              }
              else if(changeBuilding){ // different building but different floors
                next.neighbors.get(j).cost = next.cost + next.Distance(next.neighbors.get(j)); //update cost
                next.neighbors.get(j).parent = next;  //update parent
                ElevatorPoint elevator = (ElevatorPoint) next.neighbors.get(j); //cast to elevator
                close.add(elevator);
                for (Point nextPosition : elevator.neighbors) {
                  if (open.contains(nextPosition)) {
                    //do nothing
                  } else if (close.contains(nextPosition)) {
                    //dont do anything
                  } else {
                    nextPosition.cost = elevator.Distance(nextPosition); //update cost
                    nextPosition.parent = elevator;  //update parent
                    open.add(nextPosition);
                  }
                }
              }
              else { // right floor(s).
                open.add(next.neighbors.get(j));    //add sucessor to open
                next.neighbors.get(j).cost = next.cost + next.Distance(next.neighbors.get(j)); //update cost
                next.neighbors.get(j).parent = next;  //update parent
              }
            }
            else if(next.neighbors.get(j) instanceof StairPoint){ //meets an elevator and changes node
              if(changeFloor && !changeBuilding) { // same building but different floors
                next.neighbors.get(j).cost = next.cost + next.Distance(next.neighbors.get(j)); //update cost
                next.neighbors.get(j).parent = next;  //update parent

                StairPoint stair = (StairPoint) next.neighbors.get(j); //cast to elevator
                //check to see if it can make it to desired floor
                close.add(stair);
                if (stair.canAchieveFloor(goal.floor)) {
                  int count = 0;
                  int move = goal.floor - stair.floor; //if it needs to go higher or lower
                  while (stair.floor != goal.floor) {
                    if (move < 0 && (stair.neighbors.get(count).floor - stair.floor
                        == -1)) { // goal is below current floor & neighbor is next elevator below
                      stair.neighbors.get(count).cost = next.cost; //update cost
                      stair.neighbors.get(count).parent = stair;  //update parent

                      stair = (StairPoint) stair.neighbors.get(count);
                      count = 0;
                    } else if ((move > 0 && (stair.neighbors.get(count).floor - stair.floor
                        == 1))) {// goal is above and neighbor goes higher
                      stair.neighbors.get(count).cost = next.cost; //update cost
                      stair.neighbors.get(count).parent = stair;  //update parent

                      stair = (StairPoint) stair.neighbors.get(count);
                      count = 0;
                    } else { //does nothing
                      count++;
                    }
                  }
                  open.add(stair);
                  changeFloor = false;
                }
              }
              else if(changeBuilding){ // different building but different floors
                next.neighbors.get(j).cost = next.cost + next.Distance(next.neighbors.get(j)); //update cost
                next.neighbors.get(j).parent = next;  //update parent
                StairPoint stair = (StairPoint) next.neighbors.get(j); //cast to elevator
                close.add(stair);
                for (Point nextPosition : stair.neighbors) {
                  if (open.contains(nextPosition)) {
                    //do nothing
                  } else if (close.contains(nextPosition)) {
                    //dont do anything
                  } else {
                    nextPosition.cost = stair.Distance(nextPosition); //update cost
                    nextPosition.parent = stair;  //update parent
                    open.add(nextPosition);
                  }
                }
              }
            }
            else { // right floor(s).
              open.add(next.neighbors.get(j));    //add sucessor to open
              next.neighbors.get(j).cost = next.cost + next.Distance(next.neighbors.get(j)); //update cost
              next.neighbors.get(j).parent = next;  //update parent
            }
          }
        }

      }

      throw new NoPathException();  //throw error
    }

  public String toString(){
    return "A*";
  }
}

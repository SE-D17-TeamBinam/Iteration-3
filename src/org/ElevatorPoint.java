package org;

import Database.FakePoint;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Alberto on 4/9/2017.
 */
public class ElevatorPoint extends VerticalPoint {

  public ElevatorPoint(int xCoord, int yCoord, String name, int id, ArrayList<Point> new_neighbors,
      int floor) {
    super(xCoord, yCoord, name, id, new_neighbors, floor);
  }

  public ElevatorPoint(int xCoord, int yCoord, ArrayList<String> names, int id,
      ArrayList<Point> new_neighbors, int floor) {
    super(xCoord, yCoord, names, id, new_neighbors, floor);
  }

  public ElevatorPoint(int xCoord, int yCoord, String name, int id, ArrayList<Point> new_neighbors,
      int floor, String building) {
    super(xCoord, yCoord, name, id, new_neighbors, floor, building);
  }

  public ElevatorPoint(int xCoord, int yCoord, ArrayList<String> names, int id,
      ArrayList<Point> new_neighbors, int floor, String building) {
    super(xCoord, yCoord, names, id, new_neighbors, floor, building);
  }

  public ElevatorPoint(double xCoord, double yCoord, int floor) {
    super((int) xCoord, (int) yCoord, floor);
  }

  public boolean isElevator() {
    return true;
  }

  public boolean isStair() {
    return false;
  }

  /**
   * canAchieveFloor will return a boolean stating if the elevator can reach the desired floor <p>
   * THIS method will use a recursive form to check to see if can reach the correct floor by
   * searching the neighbors </p>
   *
   * @param desiredFloor Goal's floor. stored as an integer
   * @return boolean     true is when it can reach the destination's floor
   */
  public boolean canAchieveFloor(int desiredFloor) {
    if (this.floor == desiredFloor) {
      return true;
    } else {
      int move = desiredFloor - this.floor;
      for (int i = 0; i < this.neighbors.size(); i++) {
        if (this.neighbors.get(i).floor - this.floor == 1
            && move > 0) { // if neighbor floor is higher and suppose to move up
          ElevatorPoint next = (ElevatorPoint) this.neighbors.get(i);
          return next.canAchieveFloor(desiredFloor);
        } else if (this.neighbors.get(i).floor - this.floor == -1
            && move < 0) { // if neighbor floor is lower and suppose to move down
          ElevatorPoint next = (ElevatorPoint) this.neighbors.get(i);
          return next.canAchieveFloor(desiredFloor);
        }
      }
      //reaches here if for loop doesnt work, so no more vertical points with desried floor
      return false;
    }
  }

  /**
   * Used for debugging, didnt want to change the original toString method
   * @return a string with lots of info
   */
  public String toStringMoreInfo() {
    return "Elevator Point!: " + this.getName() + "(" + this.id + ") at x:" + xCoord + ", y:"
        + yCoord + " on floor " + this.floor;
  }

  @Override
  public Object clone() {
    return new ElevatorPoint(xCoord, yCoord, names, id, neighbors, floor);
  }
}

package org;

import Database.FakePoint;
import java.util.ArrayList;

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

  public String toStringMoreInfo() {
    return "Elevator Point!: " + this.getName() + "(" + this.id + ") at x:" + xCoord + ", y:"
        + yCoord + " on floor " + this.floor;
  }


//  @Override
//  public boolean equals(Object obj) {
//    // test if the obj is null
//    if (obj == null) {
//      return false;
//    }
//
//    // test if the object isn't even the same type of class
//    if (obj.getClass() != this.getClass()) {
//      return super.equals(obj);
//    }
//    ElevatorPoint pobj = (ElevatorPoint) obj; // we can now safely assume that obj is a Point and not null
//    // test if the primitive attributes are different
//    if (pobj.xCoord != this.xCoord || pobj.yCoord != this.yCoord || pobj.id != this.id
//        || pobj.floor != this.floor) {
//      return false;
//    }
//
//    // next test the list of names
//    if (pobj.names != null && this.names != null && !pobj.names.equals(this.names)) {
//      return false;
//    }
//
//    //test the neighbors of each point
//    FakePoint fthis = new FakePoint(this);
//    FakePoint fpobj = new FakePoint(
//        pobj); // change to fake so that we can compare the list of ids not the list of Points
//    if (!fpobj.neighbors.equals(fthis.neighbors)) {
//      return false;
//    }
//
//    return true; // Everything checks out
//  }
}

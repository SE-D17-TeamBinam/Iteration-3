package org;

/**
 * Created by Tom on 4/17/2017.
 */
public class DisconnectedNodeException extends Exception {

  Point disconnected = null;

  public DisconnectedNodeException(){}

  public DisconnectedNodeException(Point p){
    disconnected = p;
  }

  @Override
  public String getMessage() {
    if(disconnected != null){
      return "Disconnected node named: " + disconnected.getName() +
          " on floor " + disconnected.getFloor() + " (id: " + disconnected.getId() + ")";
    }else{
      return "Unspecified node is unreachable from some parts of the tree";
    }
  }


}

package org;

import java.util.ArrayList;
import org.Point;

/**
 * Created by ajnag on 4/10/2017.
 */
/**
 * The purpose of this class is to take tha list of points from A*
 * and produce text directions.
 */
public class FindDirections {
  int pathValue;
  //int Angle;
  String right = "Turn right";
  String left = "Turn left";
  String straight = "Go straight until you reach";
  String reverse = "Turn around";
  String finished = "You are at your destination";
  String changeFloor = "Go to floor";
  public float currentAngle;
  public float reorient;
  int step=0;
  int count=0;

  /**
   * This creates an empty lists that stores the strings of directions.
   */
  private ArrayList<String> directions = new ArrayList<>();  //List of directions that relate to each other4

  /**
   * This calculates the angles between points.
   */
  private float getAngle(Point destination, Point start){
    return (float) (Math.toDegrees(Math.atan2((start.getXCoord()-destination.getXCoord()),(start.getYCoord()-destination.getYCoord()))));
  }
  /**
   * This takes in the points from A* and produces text directions between points.
   */
  public ArrayList<String> getTextDirections(ArrayList<Point> reversePath){

    //reverses the path to make it from start to end
    ArrayList<Point> path = new ArrayList<Point>();
    for(int i=reversePath.size()-1;  i>=0; i--){
      path.add(reversePath.get(i));
    }
    Point destination = path.get(path.size()-1);

    while(destination !=path.get(count)){
      Point current = path.get(count);
      Angle = getAngle(current, path.get(count+1));
      currentAngle=(reorient+Angle)%180;
      if(current.getFloor()==path.get(count+1).getFloor()){
        FloorDirections(path.get(count+1), current);
        step++;
      } else {

        //indicates floor change in text direction
        directions.add(changeFloor + " "+ path.get(count+1).getFloor());

      }
      count++;
    }
    count=0;
    directions.add(finished);
    //start filtering
    while(count != directions.size()-1){
      if(directions.get(count).contains(changeFloor)){
        if(directions.get(count+1).contains(changeFloor)){
          directions.remove(count);
        }
        else {
          count++;
        }
      }
      count++;
    }
    return directions;

  }

  /**
   * This is a helper function that adds directions in terms of turning and walking to a destination.
   */
  float Angle;
  private void FloorDirections(Point next, Point start) {
    float newY= next.getYCoord();
    float startY= start.getYCoord();
  //  if(CurrentAngle==0 && newY>startY){
    boolean previous;
      if (currentAngle <= 45 && currentAngle >= -45) {
        directions.add(straight + " " + next.getName());
        if(directions.get(step-1).equals((straight + " " + next.getName()))){
          directions.remove(step-1);
        }
      } else if (currentAngle < 135 && currentAngle > 45) {
        directions.add(right);
        directions.add(straight + " " + next.getName());
        step++;
        ChangeOrientationRight(Angle);

      } else if (currentAngle >= 135 || currentAngle <= -135) {
        directions.add(reverse);
        directions.add(straight + " " + next.getName());
        step++;
       // ChangeDirectionReverse(Angle);
      } else if (currentAngle > -135 && currentAngle < -45) {
        directions.add(left);
        directions.add(straight + " " + next.getName());
        step++;
        ChangeOrientationLeft(Angle);
      }
   // ResetDirection(Angle);

    }//else{
      //directions.add(reverse);
    //}
    //}

    public void ResetDirection(float Angle){
      this.reorient = 0;

    }
    public void ChangeOrientationRight(float Angle){
      this.reorient += 90;

    }
    public void ChangeOrientationLeft(float Angle){
      this.reorient =90;
    }
    public void ChangeOrientationReverse(float Angle){
      this.reorient =180;
    }
  /**
   * Takes in the directions and produces epeech.
   */
  public void sayDirections(ArrayList<String> directions){
    for(int i=0; i<directions.size()-1; i++){
      //DataController.textToSpeech(directions.get(i));
    }
  }

}

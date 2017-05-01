package org;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by ajnag on 4/10/2017.
 */
/**
 * The purpose of this class is to take tha list of points from A*
 * and produce text directions.
 */
public class FindDirections {

  int northAngle = 180;
  int pathValue;
  //int Angle;
  String right = "Turn right";
  String left = "Turn left";
  String straight = "Go straight until you reach";
  String reverse = "Turn around";
  String finished = "You are at your destination";
  String changeFloor = "Go to floor";

  private boolean outside = false;
  private int hallsPassed = 0;

  /**
   * This creates an empty lists that stores the strings of directions.
   */
  private ArrayList<String> directions = new ArrayList<>();  //List of directions that relate to each other4

  /**
   * Calculates distance between two points
   * @param here the starting point
   * @param there the destination point
   * @return the magnitude of the distance
   */
  private double pixelDistance(Point here, Point there){
    double xComponent = Math.pow(there.getXCoord() - here.getXCoord(), 2);
    double yComponent = Math.pow(there.getYCoord() - here.getYCoord(), 2);
    return Math.sqrt(xComponent + yComponent);
  }

  /**
   * This calculates the angles between points using law of cosines.
   */
  public double getAngle(Point A, Point B, Point C){
    double a = pixelDistance(B, C);
    double b = pixelDistance(A, C);
    double c = pixelDistance(A, B);
    double numerator = Math.pow(a, 2) - Math.pow(b, 2) + Math.pow(c, 2);
    double denominator = 2 * a * c;
    return Math.acos(numerator/denominator);
  }


  private Vector generateVector(Point a, Point b){
    double xComp = a.getXCoord() - b.getXCoord();
    double yComp = a.getYCoord() - b.getYCoord();
    return new Vector(xComp, yComp);
  }

  private double crossProduct(Vector a, Vector b){
    return (a.getX() * b.getY()) - (a.getY() * b.getX());
  }


  /**
   * This takes in the points from A* and produces text directions between points.
   */
  public ArrayList<String> getTextDirections(ArrayList<Point> reversePath){

    if(reversePath.size() == 1){
      ArrayList<String> path = new ArrayList<>();
      path.add(finished);
      System.out.println("what");
      return path;
    }

    if(reversePath.size() == 2){
      ArrayList<String> path = new ArrayList<>();
      path.add(straight + " " + reversePath.get(0));
      path.add(finished);
      return path;
    }

    directions.add("Start at " + reversePath.get(reversePath.size()-1).getName());

    //reverses the path to make it from start to end
    Collections.reverse(reversePath);
    int count = 1; // base case cannot calculate using beta angle, so we start on the second point
    Point destination = reversePath.get(reversePath.size()-1);

    while(destination !=reversePath.get(count)){
      Point current = reversePath.get(count);

      double angle = getAngle(reversePath.get(count-1), current, reversePath.get(count+1));

      //make vectors for cross product
      Vector ab = generateVector(reversePath.get(count-1), current);
      Vector bc = generateVector(current, reversePath.get(count+1));

      double crossProduct = crossProduct(ab, bc);

      if(current.getName().equals("INTERSECTION")){
        hallsPassed++;
      }

      //check if we are staring on an entrance
      if (count == 1 &&
          (reversePath.get(0).getName().equals("Atrium Main Entrance") ||
              reversePath.get(0).getName().equals("Belkin House Entrance") ||
              reversePath.get(0).getName().equals("Garage Entrance"))) {
        outside = true;
      }

      if(current.getName().equals("Atrium Main Entrance") ||
          current.getName().equals("Belkin House Entrance") ||
          current.getName().equals("Garage Entrance")){
        if(outside){
          directions.add("Enter at " + current.getName());
          outside = false;
        }else{
          directions.add("Exit the building at the " + current.getName());
          outside = true;
          hallsPassed = 0;
        }
      }

      if(outside){
        count++;
        continue;
      }

      if(current.getFloor()==reversePath.get(count+1).getFloor()){
        floorDirections(reversePath.get(count+1), current, angle, crossProduct);
        count++;
      } else {

        //indicates floor change in text direction
        if(count != 1){//if we are not starting from the elevator
          directions.add("Enter the elevator at " + reversePath.get(count-1).getName());
        }
        directions.add(changeFloor + " "+ reversePath.get(count+1).getFloor());
        directions.add("Exit the elevator");
        count++;

      }

    }
    count=0;
  /*  if(directions.size() == 0){//the kiosk is in a direct line with the destination
      directions.add(straight + " " + path.get(path.size()-1).getName());
    }*/
    directions.add(straight + " " + reversePath.get(reversePath.size()-1).getName());

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
  private void floorDirections(Point next, Point start, double angle, double crossProduct) {

    double degrees = Math.toDegrees(angle);

    System.out.println(degrees);

    String name = "";
    if(!next.getName().equals("null")){
      name = next.getName();
    }
    if (name.equals("Elevator")) {
      hallsPassed = 0;
      //if(directions.size() == 1 && directions.get(0).equals(straight))
      //directions.add(straight + " " + name);
      //hallsPassed++;
      //return;
    } else if (degrees < 135 && degrees > 45) {
      if (crossProduct > 0) {
        directions.add(right + " after " + hallsPassed + " hallways");
      } else if (crossProduct < 0) {
        directions.add(left + " after " + hallsPassed + " hallways");
      }
      hallsPassed = 0;
      // directions.add(straight + " " + name);
    } else if (degrees >= 315 || degrees <= 45){
      directions.add(reverse);
      //directions.add(straight + " " + name);
    } else if (degrees > -135 && degrees < -45) {
      //directions.add(straight + " " + name);
    }
//    System.out.println(directions.get(directions.size()-1));
  }

  /**
   * Takes in the directions and produces epeech.
   */
  public void sayDirections(ArrayList<String> directions){
    for(int i=0; i<directions.size()-1; i++){
      //DataController.textToSpeech(directions.get(i));
    }
  }

  private class Vector{

    private double x, y;

    public Vector(double x, double y){
      this.x = x;
      this.y = y;
    }

    public double getX() {
      return x;
    }

    public double getY() {
      return y;
    }
  }

}

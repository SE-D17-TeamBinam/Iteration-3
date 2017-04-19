import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.StringJoiner;
import org.DataController;
import org.FindDirections;
import org.ListPoints;
import org.NoPathException;
import org.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
/**
 * Created by ajnag on 4/11/2017.
 */
/**
 * Tests the text directions.
 */
public class FindDirectionsTest {

  @Test
     void FindDirectionsTest() {
    FindDirections basic = new FindDirections();
    ArrayList<Point> path = new ArrayList<Point>();
    Point point1 = new Point(0, 5, 4);
    Point point2 = new Point(20, 5, 4);
    Point point3 = new Point(20, 60, 4);
    Point point4 = new Point(10, 30, 6);
    Point point5 = new Point(20, 30, 6);
    //Points are created to test text directions
  //  path.add(point5);
   // path.add(point4);
    path.add(point3);
    path.add(point2);
    path.add(point1);
    //points are added to a list
    ArrayList<String> actualDirections = basic.getTextDirections(path);
    ArrayList<String> expect = new ArrayList<String>();
    expect.add("Turn right");
    expect.add("Go straight until you reach null");
    expect.add("Turn left");
    expect.add("Go straight until you reach null");
    expect.add("You are at your destination");
    assertEquals(expect,actualDirections);

  }

  @Test
  void FindDirectionsTest2() {
    FindDirections basic = new FindDirections();
    ArrayList<Point> path = new ArrayList<Point>();
    Point point1 = new Point(0, 5, 4);
    Point point2 = new Point(20, 5, 4);
    Point point3 = new Point(20, 60, 4);
    Point point4 = new Point(10, 30, 6);
    Point point5 = new Point(20, 30, 6);
    //Points are created to test text directions
    path.add(point5);
    path.add(point4);
    path.add(point3);
    path.add(point2);
    path.add(point1);
    //points are added to a list
    ArrayList<String> actualDirections = basic.getTextDirections(path);
    ArrayList<String> expect = new ArrayList<String>();
    expect.add("Turn right");
    expect.add("Go straight until you reach null");
    expect.add("Turn left");
    expect.add("Go straight until you reach null");
    expect.add("Go to floor 6");
    expect.add("Turn around");
    expect.add("Go straight until you reach null");
    expect.add("You are at your destination");
    assertEquals(expect,actualDirections);

  }
  @Test
  void FindDirectionsTest3() {
    FindDirections basic = new FindDirections();
    ArrayList<Point> path = new ArrayList<Point>();
    Point point1 = new Point(0, 50, 4);
    Point point2 = new Point(2, 5, 4);
    Point point3 = new Point(5, 5, 4);
    Point point4 = new Point(10, 30, 6);
    Point point5 = new Point(20, 30, 6);
    //Points are created to test text directions
    //  path.add(point5);
    // path.add(point4);;
   // path.add(point3);
    path.add(point2);
    path.add(point1);
    //points are added to a list
    ArrayList<String> actualDirections = basic.getTextDirections(path);
    ArrayList<String> expect = new ArrayList<String>();
    expect.add("Turn right");
    expect.add("Go straight until you reach null");
    expect.add("Turn left");
    expect.add("Go straight until you reach null");
    expect.add("You are at your destination");
    assertEquals(expect,actualDirections);

  }
}

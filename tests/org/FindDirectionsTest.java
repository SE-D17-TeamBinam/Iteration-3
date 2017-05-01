package org;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Created by Tom on 4/30/2017.
 */
public class FindDirectionsTest {

  @Test
  public void testGetAngle(){
    Point a = new Point(0.0, 0.0, 1);
    Point b = new Point(0.0, 1.0, 1);
    Point c = new Point(1.0, 2.0, 1);

    FindDirections fd = new FindDirections();

    double angle = fd.getAngle(a, b, c);

    Assertions.assertEquals(135,(int) Math.toDegrees(angle));
  }

  @Test
  public void testGetAngle2(){
    Point a = new Point(0.0, 0.0, 1);
    Point b = new Point(0.0, -1.0, 1);
    Point c = new Point(-1.0, -2.0, 1);

    FindDirections fd = new FindDirections();

    double angle = fd.getAngle(a, b, c);

    Assertions.assertEquals(135,(int) Math.toDegrees(angle));

  }

}

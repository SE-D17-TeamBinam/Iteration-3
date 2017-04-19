import Definitions.Physician;
import java.util.ArrayList;
import java.util.Arrays;
import org.Point;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Created by Evan on 4/18/2017.
 */
public class EqualsTests {

  Point p1 = new Point(100, 200, "P1", 1, new ArrayList<Point>(), 1);
  Point p2 = new Point(100, 200, "P2", 2, new ArrayList<Point>(), 1);
  Point p3 = new Point(100, 200, "", 2, new ArrayList<Point>(), 1);

  Physician ph1 = new Physician("Evan", "Duffy", "MD", 1, new ArrayList<Point>());
  Physician ph2 = new Physician("Evan", "Duffy", "MD", 1, new ArrayList<Point>());
  Physician ph3 = new Physician("Mark", "Duffy", "DD", 2, new ArrayList<Point>());

  @Test
  public void testPointEquals1() {
    Assertions.assertEquals(true, p1.equals(p1));
  }

  @Test
  public void testPointEquals2() {
    Assertions.assertEquals(false, p1.equals(p2));
  }


  @Test
  public void testPointEquals3() {
    Assertions.assertEquals(false, p2.equals(p3));
  }


  @Test
  public void testPhysEquals1() {
    Assertions.assertEquals(true, ph1.equals(ph1));
  }

  @Test
  public void testPhysEquals1_5() {
    Assertions.assertEquals(true, ph1.equals(ph2));
  }


  @Test
  public void testPhysEquals2() {
    ph2.setLocations(new ArrayList<Point>(Arrays.asList(p1,p2)));
    Assertions.assertEquals(true, ph2.equals(ph2));
    ph2.setLocations(new ArrayList<Point>());
  }

  @Test
  public void testPhysEquals3() {
    ph2.setLocations(new ArrayList<Point>(Arrays.asList(p1,p2)));
    ph1.setLocations(new ArrayList<Point>(Arrays.asList(p2)));
    Assertions.assertEquals(false, ph1.equals(ph2));

    ph2.setLocations(new ArrayList<Point>());
    ph1.setLocations(new ArrayList<Point>());
  }
}

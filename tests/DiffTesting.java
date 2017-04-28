import java.util.ArrayList;
import java.util.Arrays;
import org.Point;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Created by Evan on 4/26/2017.
 */
public class DiffTesting {
  Point p1 = new Point(0,10,new ArrayList<String>(Arrays.asList("Point 1")),1,new ArrayList<Point>(), 1);
  Point p2 = new Point(124,10,new ArrayList<String>(Arrays.asList("Point 2")),2,new ArrayList<Point>(), 1);
  Point p3 = new Point(125,10,new ArrayList<String>(Arrays.asList("Point 2")),2,new ArrayList<Point>(), 1);



  @Test
  public void testEqualsSelf(){
    Assertions.assertEquals(true, p1.equals(p1));
  }


  @Test
  public void testEqualsSomethingDifferent(){
    Assertions.assertEquals(false, p1.equals(p2));
    Assertions.assertEquals(false, p2.equals(p1));
  }

  @Test
  public void testEqualsClone(){
    Assertions.assertEquals(true, p1.equals((Point)p1.clone()));
  }

  @Test
  public void testSubtileChange(){
    Assertions.assertEquals(false, p2.equals(p3));
    Assertions.assertEquals(false, p3.equals(p2));
  }
}

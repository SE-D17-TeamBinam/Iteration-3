package Definitions;

import Database.FakePhysician;
import java.util.ArrayList;
import org.Point;

/**
 * Created by Leon Zhang on 2017/4/3.
 *
 * This class stores the object physician with it's details/fields for the database
 */
public class Physician {

  private String firstName;
  private String lastName;
  private String title;
  private long PID;
  private ArrayList<Point> locations;

  public Physician(String firstName, String lastName, String title, long PID,
      ArrayList<Point> locations) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.title = title;
    this.PID = PID;
    this.locations = locations;
  }

  public String getFirstName() {
    return this.firstName;
  }

  public String getLastName() {
    return this.lastName;
  }

  public String getTitle() {
    return this.title;
  }

  public ArrayList<Point> getLocations() {
    return this.locations;
  }

  public void setFirstName(String newFirstName) {
    this.firstName = newFirstName;
  }

  public void setLastName(String newLastName) {
    this.lastName = newLastName;
  }

  public void setTitle(String newTitle) {
    this.title = newTitle;
  }

  public void setLocations(ArrayList<Point> newLocations) {
    this.locations = newLocations;
  }

  public long getID() {
    return this.PID;
  }

  public boolean compareTo(Physician p2) {
    if (this.getTitle().equals(p2.getTitle()) && this.getFirstName().equals(p2.getFirstName()) &&
        this.getLastName().equals(p2.getLastName()) && this.getID() == p2.getID()) {

      FakePhysician p3 = new FakePhysician(this);
      FakePhysician p4 = new FakePhysician(p2);
      for (int k = 0; k < p4.getLocations().size(); k++) {
        if (!(p3.getLocations().contains(p4.getLocations().get(k)))) {
          System.out.println("Location " + p4.getLocations().get(k) + " not in the other");
          return false;
        }
      }

    } else {
      System.out.println("verification failed a field is different-physician");
      return false;
    }

    return true;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (obj != null && obj.getClass() != this.getClass()) {
      return super.equals(obj);
    }
    Physician pobj = (Physician) obj;
//    System.out.println("Comparing " + this.firstName + " -> " + pobj.firstName);
    if (this.firstName.equals(pobj.firstName) && this.lastName.equals(pobj.lastName) && this.title
        .equals(pobj.title) && this.PID == pobj.PID ) {
      if (this.locations != null && pobj.locations != null && this.locations.size() != pobj.locations.size())
        return false;

      for (Point p : this.locations) {
        if (!pobj.locations.contains(p)) {
          return false;
        }
      }
      return true;
    }
    return false;
  }


  @Override
  public Object clone() {
    return new Physician(firstName, lastName, title, PID, locations);
  }
}

package Database;

import Definitions.Physician;
import java.sql.SQLException;
import java.util.ArrayList;
import org.Point;

/**
 * Interface to make the interaction with the Database Editor cleaner
 * Created by Evan on 4/9/2017.
 */
public interface DatabaseInterface {

  public double progressBarPercentage = 0;

  /**
   * Pulls data from the datababse and populates the local copies of the data
   *
   * @throws SQLException If there was a problem communicating with the database
   */
  public void load() throws SQLException;

  /**
   * Package Protected Method to push the local copies into the database
   */
  void save();

  /**
   * Gets a sublist of the local copy of points that have a name (ie Destinations)
   *
   * @return ArrayList of Points that are all the local points that have names
   */
  public ArrayList<Point> getNamedPoints();

  /**
   * Tries to pull data from the database into the local copies then returns the local copy of
   * points
   *
   * @return ArrayList of Points that is the local copy of points
   */
  public ArrayList<Point> getPoints();

  /**
   * Set the local copy of points and then save them to the database
   *
   * @param points The ArrayList of Points to replace the local copy
   */
  public void setPoints(ArrayList<Point> points);

  /**
   * Tries to pull data from the database into the local copies then returns the local copy of
   * physicians
   *
   * @return The ArrayList of Physicians that is the local copy of physicians
   */
  public ArrayList<Physician> getPhysicians();

  /**
   * Set the local copy of points and then save them to the database
   *
   * @param physicians The ArrayList of Points to replace the local copy
   */
  public void setPhysicians(ArrayList<Physician> physicians);


  /**
   * removes the physician with the given id from the database and the local copy of physicians
   *
   * @param pid , integer id of the physican to remove
   * @return true if everything was successful
   */
  public boolean removePhysician(long pid);


  /**
   * adds a new physician to the database and the local copy
   *
   * @param real_ph , the physician to add
   * @return true if everything was successful
   */
  public boolean addPhysician(
      Physician real_ph
  );

  /**
   * undoList a physician, updating it with the new fields of the physician given to the function
   *
   * @param real_ph , the updated physician
   * @return true if everything was successful
   */
  public boolean editPhysician(
      Physician real_ph
  );

  /**
   * removes the physician with the given id from the database and the local copy of physicians
   *
   * @param pid , integer id of the point to remove
   * @return true if everything was successful
   */
  public boolean removePoint(long pid);


  /**
   * adds a new point to the database and the local copy
   *
   * @param real_po , the point to add
   * @return true if everything was successful
   */
  public boolean addPoint(
      Point real_po
  );

  /**
   * undoList a physician, updating it with the new fields of the physician given to the function
   *
   * @param real_po , the updated point
   * @return true if everything was successful
   */
  public boolean editPoint(
      Point real_po
  );


  //TODO Javadoc
  public ArrayList<Physician> fuzzySearchPhysicians(String searchTerm);


  //TODO Javadoc
  public ArrayList<Point> fuzzySearchPoints(String searchTerm);


}



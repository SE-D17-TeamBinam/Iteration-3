package Database;

import java.sql.SQLException;

/**
 * Created by Evan on 4/18/2017.
 */
class LoadThread implements Runnable {
  DatabaseController dbc;
  boolean running = false;

  LoadThread(DatabaseController _dbc){
    dbc = _dbc;
    System.out.println("Creating new load thread");
  }

  public void start() {
    (new Thread(this, "Load Thread")).start();
  }

  @Override
  public void run(){
    System.out.println("loading physicians and points from DB to local copies ");
    running = true;
    try {
      dbc.localPoints = dbc.getAllPoints();
      dbc.localPhysicians = dbc.getAllPhysicians();
      dbc.progressBarPercentage = 1;
    } catch (SQLException e){
      System.out.println("There was a problem loading from the database");
    }
    running = false;
  }
}

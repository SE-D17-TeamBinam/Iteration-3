package Database;

import java.sql.SQLException;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

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
      System.out.println(
          "Error Getting Data From The Database, failed to load, will return DB local physicians copy \n Query/Connection Error : "
              + e.getMessage());
      Alert alert = new Alert(AlertType.ERROR, "Message. Bad Things Happened! : " + "DB ERROR:  failed to load, will return DB local physicians copy \n Query/Connection Error " + e.getMessage()); //can add buttons if you want, or change to different popup types
      alert.showAndWait(); //this puts it in focus
      if (alert.getResult() == ButtonType.YES) {
        //do stuff, if neccesary, else, delete
      }
    }
    dbc.progressBarPercentage = 1;
    running = false;
  }
}

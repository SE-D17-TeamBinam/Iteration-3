package FileController;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import org.DataController;
import org.ListPoints;
import org.Point;

/**
 * Created by Tom on 4/20/2017.
 */
public class SettingsIO {

  private String settingsLoc;
  private File settingsFile;
  private Properties settings;

  public SettingsIO(String settingsLoc){
    this.settingsLoc = settingsLoc;
  }

  public SettingsIO(){
    settingsLoc = "kiosk.properties";
  }

  private void loadSettings(){
    settingsFile = new File(this.settingsLoc);
    try{
      FileReader in = new FileReader(settingsFile);
      settings.load(in);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public Point getDefaultKiosk(ListPoints graph) throws DefaultKioskNotInMemoryException{
    int kioskID = Integer.parseInt(settings.getProperty("startingKiosk"));
    for(Point p : graph.getPoints()){
      if(p.getId() == kioskID){
        return p;
      }
    }
    throw new DefaultKioskNotInMemoryException(kioskID);
  }

}

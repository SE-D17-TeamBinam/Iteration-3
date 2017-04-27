package FileController;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import org.Astar;
import org.BFS;
import org.DFS;
import org.ListPoints;
import org.PathfindingStrategy;
import org.Point;

/**
 * Created by Tom on 4/20/2017.
 */
public class SettingsIO {

  private String settingsLoc;
  private File settingsFile;
  private Properties settings;
  private static final HashSet<String> SETTINGS_KEYS = new HashSet<>(Arrays.asList(
      "startingKiosk", "algorithm", "timeoutLength", "fullscreen", "genericPointColor",
      "stairPointColor", "elevatorPointColor"));

  public SettingsIO(String settingsLoc){
    this.settingsLoc = settingsLoc;
    settings = new Properties();
    settingsFile = new File(this.settingsLoc);
    loadSettings();
  }

  public SettingsIO(){
    settingsLoc = "kiosk.properties";
    settings = new Properties();
    settingsFile = new File(this.settingsLoc);
    loadSettings();
  }

  private void loadSettings(){
    try{
      settingsFile.createNewFile();
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

  public boolean fullscreenPreference(){
    boolean fs = Boolean.parseBoolean(settings.getProperty("fullscreen", "false"));
    return fs;
  }

  public int getTimeout(){
    return Integer.parseInt(settings.getProperty("timeoutLength"));
  }


  public PathfindingStrategy getAlgorithm(){
    String strat = settings.getProperty("algorithm");
    switch (strat) {
      case "dfs":
        return new DFS();
      case "bsf":
        return new BFS();
      default:
        return new Astar();
    }
  }

  /**
   * TODO(tom): Might be a good idea to move the writing to another function
   * Update the settings file
   * @param tag: the key in the settings folder
   * @param value: the new setting value
   * @return if the change was successful
   */
  public boolean updateSetting(String tag, String value){
    if(!SETTINGS_KEYS.contains(tag)){
      return false;
    }
    settings.setProperty(tag, value);
    try{
      StringWriter out = new StringWriter();
      settings.store(new FileOutputStream(settingsLoc), null);
    } catch (IOException e) {
      return false;
    }
    return true;
  }
}

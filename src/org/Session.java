package org;

/**
 * Created by Tom on 4/2/2017.
 */
public class Session {

  //public ListNodes directionPath;
  public String authToken;
  //public Time sessionStart;
  public int zoomLevel;
  public Language currLang = Language.ENGLISH;
  public Dictionary dictionary = new Dictionary();
  public PathfindingStrategy algorithm = new Astar(); //Default to Astar

  public void setLanguage(Language lang) {
    this.currLang = lang;
  }

  public Language getLanguage() {
    return this.currLang;
  }

  public void setAlgorithm(PathfindingStrategy algorithm){
    this.algorithm = algorithm;
  }

  public Session() {
  }
}

package org;

import java.util.ArrayList;
import java.util.EmptyStackException;

/**
 * Created by Praneeth Appikatla on 4/30/2017.
 */
public class EditStack {

  private ArrayList<ArrayList<Point>> undoStack;
  private ArrayList<ArrayList<Point>> redoStack;

  public ArrayList<ArrayList<Point>> getUndoStack() {
    return undoStack;
  }

  public ArrayList<ArrayList<Point>> getRedoStack() {
    return redoStack;
  }

  public EditStack() {
    redoStack = new ArrayList<ArrayList<Point>>();
    undoStack = new ArrayList<ArrayList<Point>>();
  }

  public void pushToRedo(ArrayList<Point> item) {
    redoStack.add(item);
  }

  public void pushToUndo(ArrayList<Point> item) {
    undoStack.add(item);
  }

  public ArrayList<Point> popRedo() {
    if (!isRedoEmpty()) {
      return redoStack.remove(redoSize()-1);
    } else {
      throw new EmptyStackException();
    }
  }

  public ArrayList<Point> popUndo() {
    if (!isUndoEmpty()) {
      return undoStack.remove(undoSize()-1);
    } else {
      throw new EmptyStackException();
    }
  }

  public boolean isRedoEmpty() {
    return (undoStack.size() == 0);
  }

  public boolean isUndoEmpty() {
    return (redoStack.size() == 0);
  }

  public int redoSize() {
    return redoStack.size();
  }

  public int undoSize() {
    return undoStack.size();
  }

  public void undo() {
    if (!isUndoEmpty()) {
      ArrayList<Point> currState = popUndo();
      pushToRedo(currState);
    }
  }

  public void redo() {
    if (!isRedoEmpty()) {
      ArrayList<Point> finalState = popRedo();
      pushToUndo(finalState);
    }
  }

  public void clearRedo(){
    redoStack.clear();
  }

}

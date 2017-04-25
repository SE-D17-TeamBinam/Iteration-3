package Database;

/**
 * Created by Evan on 4/15/2017.
 */
class SaveThread implements Runnable {

  Thread t = null;
  DatabaseController dbe;
  public boolean running = false;

  SaveThread(DatabaseController _dbe) {
    dbe = _dbe;
    System.out.println("Creating new save thread");
  }

  @Override
  public void run() {
    System.out.println("Starting save thread");
    dbe.save();
    dbe.progressBarPercentage = 1;
    running = false;
  }

  public void start() {
    running = true;
    (new Thread(this, "Save Thread")).start();
  }
}

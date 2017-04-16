package Database;

/**
 * Created by Evan on 4/15/2017.
 */
class SaveThread implements Runnable {
  Thread t = null;
  DatabaseInterface dbe;
  public boolean running = false;

  SaveThread(DatabaseInterface _dbe){
    dbe = _dbe;
    System.out.println("Creating new save thread");
  }

  @Override
  public void run() {
    running = true;
    System.out.println("Starting save thread");
    dbe.save();
    running = false;
  }

  public void start(){
    if (t == null){
      t = new Thread(this,"Saving Thread");
      t.start();
    }
  }
}

package Networking;

/**
 * Created by Praneeth Appikatla on 4/11/2017.
 */
public enum Carrier {
  att ("AT&T"),
  sprint("Sprint"),
  tmobile("T-Mobile"),
  verizon("Verizon");

  private final String carrierName;

  private Carrier(String s){
    carrierName = s;
  }

  public boolean equalsName(String otherName){
    return carrierName.equals(otherName);
  }

  public String toString() {
    return this.carrierName;
  }
}


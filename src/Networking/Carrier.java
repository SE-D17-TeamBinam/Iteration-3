package Networking;

/**
 * Created by Praneeth Appikatla on 4/11/2017.
 */
public enum Carrier {
  ATT("AT&T"),
  SPRINT("Sprint"),
  TMOBILE("T-Mobile"),
  VERIZON("Verizon");

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

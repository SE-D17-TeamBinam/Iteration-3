package CredentialManager;

/**
 * Created by Praneeth Appikatla on 4/25/2017.
 */
public enum UserType {
  ADMIN ("ADMIN"),
  STAFF ("STAFF");

  private final String userType;

  private UserType(String s) {userType = s;}

  public boolean equalsName(String otherName){
    return userType.equals(otherName);
  }

  public String toString() {
    return this.userType;
  }
}





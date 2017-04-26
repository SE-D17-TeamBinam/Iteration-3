package CredentialManager;

import java.util.HashMap;

/**
 * Created by Praneeth Appikatla on 4/25/2017.
 */
public class CredentialEntry {

  public HashMap<String, String> login;

  public CredentialEntry(HashMap<String, String> login){
    this.login = login;
  }

  public void addCredentialEntry(String username, String pass) {
    this.login.put(username, pass);
  }

  public String getPass(String username) {
    if (this.login.containsKey(username)) {
      return this.login.get(username);
    }
    else {return null;}
  }

  public Boolean containsUsername(String username) {
    Boolean contains = false;
    for (String key: this.login.keySet()){
      if (key.equals(username)){
        contains = true;
      }
    }
    return contains;
  }

}


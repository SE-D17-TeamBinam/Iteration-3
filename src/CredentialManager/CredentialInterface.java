package CredentialManager;

import java.io.IOException;

/**
 * Created by Praneeth Appikatla on 4/25/2017.
 */
public interface CredentialInterface {

  void signup(String username, String password, UserType user) throws IOException;
  boolean login(String username, String password);

}

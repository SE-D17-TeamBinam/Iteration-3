package CredentialManager;

import java.io.IOException;

/**
 * Created by Praneeth Appikatla on 4/25/2017.
 */
public interface CredentialInterface {

  Boolean signup(String username, String password, UserType user) throws IOException;
  Boolean login(String username, String password);

}

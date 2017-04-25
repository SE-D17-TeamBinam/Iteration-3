package CredentialManager;

/**
 * Created by Praneeth Appikatla on 4/25/2017.
 */
public interface CredentialInterface {

  Boolean signup(String username, String password);
  Boolean login(String username, String password);
  String generateHash(String input);

}

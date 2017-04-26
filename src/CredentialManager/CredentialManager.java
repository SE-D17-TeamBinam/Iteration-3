package CredentialManager;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by Praneeth Appikatla on 4/25/2017.
 */
public class CredentialManager implements CredentialInterface{
  HashMap<HashMap<String, String>, UserType> users = new HashMap <HashMap<String,String>, UserType>();
  private String SALT = getSaltString();
  private static CredentialManager instance = new CredentialManager();

  public CredentialManager(){}

  public static CredentialManager getInstance() {
    return instance;
  }

  @Override
  public Boolean signup(String username, String password, UserType type) {
    HashMap<String, String> loginInfo = new HashMap<>();
    if (!(loginInfo.containsKey(username))) {
      String saltedPass = SALT + password;
      String hashedPass = generateHash(saltedPass);
      loginInfo.put(username, hashedPass);
      users.put(loginInfo, type);
      return false;
    }
    else {
      return true;
    }
  }

  @Override
  public Boolean login(String username, String password) {
    HashMap<String, String> loginInfo = new HashMap<>();
    loginInfo.put(username, password);
    UserType type = users.get(loginInfo);
    Boolean isAuthenticated = false;
    String saltedPass = SALT + password;
    String hashedPass = generateHash(saltedPass);

    String storedHash = null;
    if(hashedPass.equals(storedHash)){
      isAuthenticated = true;
    } else {
      isAuthenticated = false;
    }
    return isAuthenticated;
  }

  @Override
  public String generateHash(String input) {
    StringBuilder hash = new StringBuilder();
    // implements Secure Hash Algorithm 1
    try {
      MessageDigest sha = MessageDigest.getInstance("SHA-1");
      byte[] hashedBytes = sha.digest(input.getBytes());
      char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
          'a', 'b', 'c', 'd', 'e', 'f' };
      for (int idx = 0; idx < hashedBytes.length; ++idx) {
        byte b = hashedBytes[idx];
        hash.append(digits[(b & 0xf0) >> 4]);
        hash.append(digits[b & 0x0f]);
      }
    } catch (NoSuchAlgorithmException e) {}

    return hash.toString();
  }

  public String getSaltString(){
    // helper to generate a random string from certain characters
    String saltChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    StringBuilder salt = new StringBuilder();
    Random rand = new Random();
    while (salt.length() < 20) { // length of random string
      int index = (int) (rand.nextFloat() * saltChars.length());
      salt.append(saltChars.charAt(index));
    }
    String randStr = salt.toString();
    return randStr;
  }
}

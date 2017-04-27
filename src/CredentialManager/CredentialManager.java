package CredentialManager;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by Praneeth Appikatla on 4/25/2017.
 */
public class CredentialManager implements CredentialInterface{
  // outer map to store credentials with permissions levels
  private Map<HashMap<String, String>, UserType> users = new HashMap<HashMap<String, String>, UserType>();
  // inner map to store credentials
  private Map<String, String> credentials = new HashMap<>();
  // random string formed for another layer of security
  private static final String SALT = getSaltString(20);
  // Singleton class
  private static CredentialManager instance = new CredentialManager();

  public CredentialManager(){}

  public static CredentialManager getInstance() {
    return instance;
  }

  @Override
  public Boolean signup(String username, String password, UserType type) {
    if (!(credentials.containsKey(username))){
      HashMap<String, String> login = new HashMap<>();
      String saltPass = SALT + password;
      String hashedPass = generateHash(saltPass);
      login.put(username, hashedPass);
      this.credentials.put(username, hashedPass);
      this.users.put(login, type);
      return true;
    }
    else {
      return false;
    }
  }

  @Override
  public Boolean login(String username, String password) {
    Boolean isAuthenticated = false; // true if credentials are correct
    String saltPass = SALT + password;
    String hashedPass = generateHash(saltPass);

    String storedHash = this.credentials.get(username);

    // compare the string to what is stored in
    if(hashedPass.equals(storedHash)){
      isAuthenticated = true;
    } else {
      isAuthenticated = false;
    }
    return isAuthenticated;
  }

  public Boolean isAdmin(String username, String password){
    Boolean isAdmin = false;
    HashMap<String, String> login = new HashMap<>();
    String saltPass = SALT + password;
    String hashedPass = generateHash(saltPass);
    login.put(username, hashedPass);

    if(this.users.get(login).equals(UserType.ADMIN)){
      isAdmin = true;
    }
    return isAdmin;

  }

  public String generateHash(String input) {
    StringBuilder hash = new StringBuilder();
    // implements Secure Hash Algorithm 1 (SHA-1)
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

  /**
   *
   * @param length Specified length of the string you want to create
   * @return A random string of specified length
   */
  public static String getSaltString(int length){
    // helper to generate a random string from certain characters
    String saltChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    StringBuilder salt = new StringBuilder();
    Random rand = new Random();
    while (salt.length() < length) { // length of random string
      int index = (int) (rand.nextFloat() * saltChars.length());
      salt.append(saltChars.charAt(index));
    }
    String randStr = salt.toString();
    return randStr;
  }
}

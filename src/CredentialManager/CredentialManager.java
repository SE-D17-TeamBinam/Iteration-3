package CredentialManager;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by Praneeth Appikatla on 4/25/2017.
 */
public class CredentialManager implements CredentialInterface{
  private HashMap<String, String> credentials = new HashMap<>();
  private CredentialEntry entries = new CredentialEntry(credentials);
  private HashMap<CredentialEntry, UserType> users = new HashMap <CredentialEntry, UserType>();
  private String SALT = getSaltString(20);
  private static CredentialManager instance = new CredentialManager();

  public CredentialManager(){}

  public static CredentialManager getInstance() {
    return instance;
  }

  @Override
  public Boolean signup(String username, String password, UserType type) {
    if (!(entries.containsUsername(username))){
      String saltedPass = SALT + password;
      String hashedPass = generateHash(saltedPass);
      CredentialEntry loginInfo = new CredentialEntry(new HashMap<>());
      loginInfo.login.put(username, hashedPass);
      entries.login.put(username, hashedPass);
      users.put(loginInfo, type);
      return false;
    }
    else {
      return true;
    }
  }

  @Override
  public Boolean login(String username, String password) {
    CredentialEntry login = new CredentialEntry(new HashMap<>());
    Boolean isAuthenticated = false;
    String saltedPass = SALT + password;
    String hashedPass = generateHash(saltedPass);

    String storedHash = entries.getPass(username);

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

  /**
   *
   * @param length Specified length of the string you want to create
   * @return A random string of specified length
   */
  public String getSaltString(int length){
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

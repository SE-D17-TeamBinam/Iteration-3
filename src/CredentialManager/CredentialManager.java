package CredentialManager;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Properties;

/**
<<<<<<< HEAD
 * Created by Praneeth Appikatla on 4/25/2017.
=======
 * Created by Tom on 4/3/2017.
 *
 * this will check to see if you have admin credentials.
>>>>>>> b7cb43c5e1efd690e885fad010bd129ed4de9d37
 */
public class CredentialManager implements CredentialInterface{

  private static CredentialManager instance = new CredentialManager();
  private static Properties users = new Properties();
  private static final String salt = "salt"; // could change to make more secure
  private static final File out = new File("users.txt");

  private CredentialManager() {
  }

  public static CredentialManager getInstance() {
    return instance;
  }

  /**
   *
   * @param input The string being hashed
   * @return A string hashed with a hashing algorithm
   */
  public static String generateHash(String input) {
    StringBuilder hash = new StringBuilder();
    try {
      // uses Standard Hashing Algorithm 1 (SHA-1)
      MessageDigest sha = MessageDigest.getInstance("SHA-1");
      byte[] hashedBytes = sha.digest(input.getBytes());
      char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
          'a', 'b', 'c', 'd', 'e', 'f' };
      for (int i = 0; i < hashedBytes.length; i++) {
        byte b = hashedBytes[i];
        hash.append(digits[(b & 0xf0) >> 4]);
        hash.append(digits[b & 0x0f]);
      }
    } catch (NoSuchAlgorithmException e) {
      // handle error here.
    }
    return hash.toString();
  }

  public boolean containsUser(String username, String pass) {
    if (users.containsKey(username) && !(username.equals("") && !pass.equals(""))){
      return true;
    }
    return false;
  }

  public void signup(String username, String pass, UserType permissions) {
    if (!containsUser(username, pass)){
      String userType = permissions.toString();
      insertUser(username, pass, userType);
      writeToFile();
    }
  }

  public boolean login(String username, String pass) {
    String saltedPass = pass + salt;
    String hashedPass = generateHash(saltedPass);
    return verifyFromFile(username, hashedPass);
  }

  public void insertUser(String username, String pass, String userType) {
    String saltedPass = pass + salt;
    String hashedPass = generateHash(saltedPass);
    users.put(username, hashedPass + "," + userType);
  }

  public boolean userIsAdmin(String username) {
    String value = users.getProperty(username);
    ArrayList<String> values = new ArrayList<String>(Arrays.asList(value.split(",")));
    String permissions = values.get(1);
    if (permissions.equals("ADMIN")){
      return true;
    }
    else return false;
  }

  public void writeToFile(){
    try {
      users.store(new FileOutputStream(out, false), "This is a test");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public boolean verifyFromFile(String username, String pass) {
    try {
      FileInputStream fileInput = new FileInputStream(out);
      users.load(fileInput);
      fileInput.close();
      boolean isAuthenticated = false;

      Enumeration enuKeys = users.keys();
      while (enuKeys.hasMoreElements()) {
        String key = (String) enuKeys.nextElement();
        String value = users.getProperty(key);
        ArrayList<String> values = new ArrayList<String>(Arrays.asList(value.split(",")));
        if (values.get(0).equals(pass)) {
          isAuthenticated = true;
        }
      }
      return isAuthenticated;
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return false;
  }

//  public static String getSaltString(int length) {
//    // helper to generate a random string from certain characters
//    String saltChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
//    StringBuilder salt = new StringBuilder();
//    Random rand = new Random();
//    while (salt.length() < length) { // length of random string
//      int index = (int) (rand.nextFloat() * saltChars.length());
//      salt.append(saltChars.charAt(index));
//    }
//    String randStr = salt.toString();
//    return randStr;
//  }


}


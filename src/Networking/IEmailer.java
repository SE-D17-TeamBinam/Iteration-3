package Networking;

/**
 * Created by Praneeth Appikatla on 4/11/2017.
 */
public interface IEmailer {

  void email (String email, String text);
  void text (String number, Carrier carrier, String text);

}

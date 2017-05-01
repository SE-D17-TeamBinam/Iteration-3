package Networking;

import java.util.Properties;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Dialog;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;



/**
 * Created by Praneeth Appikatla on 4/11/2017.
 */


public class Emailer implements IEmailer{

  /**
   *
   * @param email Recipient email address
   * @param text Message to be sent
   */
  public void email(String email, String text){
    String from = "********@********"; // from changed accordingly
    final String username = "*******"; // username change accordingly
    final String password = "*******"; // password change accordingly

    String host = "smtp.gmail.com"; // Gmail smtp server address

    Properties props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", host);
    props.put("mail.smtp.port", "587");

    // Creates a session and authenticates email credentials
    Session session = Session.getInstance(props,
        new javax.mail.Authenticator() {
          protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password);
  }
        });

    try {
      Message message = new MimeMessage(session);

      // sets from field of email
      message.setFrom(new InternetAddress(from));

      // sets recipient of email.
      message.setRecipients(Message.RecipientType.TO,
          InternetAddress.parse(email));

      // sets subject field of email
      message.setSubject("Faulkner Hospital Directions");

      // sets message
      message.setText(text);

      // sends message
      Transport.send(message);

      System.out.println("Sent message successfully!");

    } catch (MessagingException e) {
      errorMessage();
    }
  }

  /**
   *
   * @param c Mobile phone carrier
   * @param number Mobile phone number
   * @return String This returns the format needed to send text message
   */
  public String format (Carrier c, String number) {
    switch(c) {
      case ATT: return number + "@txt.ATT.net";
      case SPRINT: return number + "@messaging.sprintpcs.com";
      case TMOBILE: return number + "@tmomail.net";
      case VERIZON: return number + "@vtext.com";
      default:return "Unexpected error" + c.name();
    }
  }

  /**
   *
   * @param number Mobile phone number
   * @param c Mobile phone carrier
   * @param text Message to be sent
   */
  public void text(String number, Carrier c, String text){
    Emailer e = new Emailer();
    String recepient = e.format(c, number);
    e.email(recepient, text);
  }

  public void errorMessage() {
    Dialog error = new Alert(AlertType.ERROR);
    error.setHeaderText("Email not found");
    error.setContentText("This is not a valid email.");
    error.showAndWait();
  }

}


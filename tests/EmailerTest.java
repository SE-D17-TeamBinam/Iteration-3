import Networking.Carrier;
import Networking.Emailer;
import org.junit.jupiter.api.Test;

/**
 * Created by Praneeth Appikatla on 4/18/2017.
 */
class EmailerTest {

  @Test
  void emailTest() {
    Emailer e = new Emailer();
    e.email("", "hi");
  }

  @Test
  void smsTest() {
    Emailer e = new Emailer();
    e.text("508-555-1212", Carrier.ATT, "hi");
  }

}
package FileController;

import static org.junit.jupiter.api.Assertions.*;

import org.Astar;
import org.DFS;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Created by Tom on 4/27/2017.
 */
class SettingsIOTest {

  SettingsIO s = new SettingsIO("test.properties");

  @Test
  void getDefaultKiosk() {
  }

  /**
   * Use the non updateSettings() for testing how the application reacts
   */
  @Test
  void fullscreenPreference() {
  }

  @Test
  void getTimeout() {
  }

  @Test
  void getAlgorithm() {
  }

  @Test
  void updateSetting() {
    s.updateSetting("startingKiosk", "1");
    s.updateSetting("algorithm", "dfs");
    s.updateSetting("timeoutLength", "30");
    s.updateSetting("fullscreen", "false");

    assertEquals(DFS.class, s.getAlgorithm().getClass());
    assertEquals(30, s.getTimeout());
    assertEquals(false, s.fullscreenPreference());

    s.updateSetting("algorithm", "astar");

    assertEquals(Astar.class, s.getAlgorithm().getClass());
  }

}
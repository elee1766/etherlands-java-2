import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import etherlandscore.etherlandscore.EtherlandsCore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;

public class EtherlandsCoreTest {

  private ServerMock server;
  private EtherlandsCore plugin;

  @Before
  public void setUp() {
    // Start the mock server
    server = MockBukkit.mock();
    // Load your plugin
    plugin = MockBukkit.load(EtherlandsCore.class);
  }

  @After
  public void tearDown() {
    // Stop the mock server
    MockBukkit.unmock();
  }

}

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import etherlandscore.etherlandscore.EtherlandsCore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EtherlandsCoreTest {
  private ServerMock server;
  private EtherlandsCore plugin;

  @BeforeEach
  void setUp()
  {
    server = MockBukkit.mock();
    plugin = MockBukkit.load(EtherlandsCore.class);
  }

  @AfterEach
  void tearDown()
  {
    MockBukkit.unmock();
  }

  @Test
  void serverNotNull()
  {
    Assertions.assertNotNull(server);
  }

}
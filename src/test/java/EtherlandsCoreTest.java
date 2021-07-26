import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import etherlandscore.etherlandscore.EtherlandsCore;
import org.bukkit.Bukkit;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.*;

import java.util.logging.Logger;

public class EtherlandsCoreTest {
  private ServerMock server;
  private EtherlandsCore plugin;

  @BeforeEach
  void init(){
    server = MockBukkit.mock();
  }

  @AfterEach
  void shutDown(){
    MockBukkit.unmock();
  }

  @Test
  void addition(){
    Assertions.assertNotNull(server);
  }

}
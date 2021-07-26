import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import etherlandscore.etherlandscore.EtherlandsCore;
import org.bukkit.Bukkit;
import org.bukkit.*;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.*;

import java.util.logging.Logger;

public class EtherlandsCoreTest {
  private ServerMock server;
  private EtherlandsCore plugin;

  @BeforeEach
  void init() {
    server = MockBukkit.mock();
    plugin = MockBukkit.load(EtherlandsCore.class);
  }

  @AfterEach
  void shutDown(){
    MockBukkit.unmock();
  }

  @Test
  void bukkitNull() {
    Assertions.assertNotNull(server);
  }

}
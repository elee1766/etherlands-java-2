package etherlandscore.etherlandscore;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.GameMode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

public class EtherlandsCoreTests {
  ServerMock server;
  EtherlandsCore plugin;
  PlayerMock playerMock;

  @BeforeEach
  public void setUp() {
    server = MockBukkit.mock();
    plugin = MockBukkit.load(EtherlandsCore.class);
    server.setPlayers(0);
    playerMock = server.addPlayer();
  }
  @After
  public void tearDown() {
    MockBukkit.unmock();
  }

  @Test
  public void playerTest() {
    playerMock.sendMessage("Hello");
  }
}

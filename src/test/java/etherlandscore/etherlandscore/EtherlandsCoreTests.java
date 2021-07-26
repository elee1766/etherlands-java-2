package etherlandscore.etherlandscore;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EtherlandsCoreTests<EtherlandsScore> {
  private ServerMock server;
  private EtherlandsCore plugin;

  @Before
  public void setUp() {
    server = MockBukkit.mock();
    plugin = (EtherlandsCore) MockBukkit.load(EtherlandsCore.class);
  }

  @After
  public void tearDown() {
    MockBukkit.unmock();
  }

  @Test
  public void playerJoin() {
    PlayerMock player = server.addPlayer();
  }
}

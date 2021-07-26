import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import etherlandscore.etherlandscore.EtherlandsCore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.*;

public class EtherlandsCoreTest {
    private ServerMock server;
    @BeforeEach
    public void setUp() {
      server = MockBukkit.mock();
    }
    @AfterEach
    public void tearDown() {
      MockBukkit.unmock();
    }
    @Test
    public void serverNotNull(){
      Assertions.assertNotNull(server);
    }
  }


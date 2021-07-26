import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import etherlandscore.etherlandscore.EtherlandsCore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

public class EtherlandsCoreTest {
      private ServerMock server;
      private EtherlandsCore plugin;

      @BeforeEach
      void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(EtherlandsCore.class);
      }

      @AfterEach
      void tearDown() {
        MockBukkit.unmock();
      }

      //@Test
      @DisplayName("Server should not be null")
      void serverShouldNotBeNull() {
        Assertions.assertNotNull(server);
      }
      // Methods removed to decrease message length.
      // Other tests can be:
      // Plugin should not be null
      // Plugin loader should not be null
      // Plugin should be enabled
      // Plugin environment should not be null
      // Plugin should be in test environment
}
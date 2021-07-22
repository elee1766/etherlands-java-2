package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPI;

public class CommandDisabler {
  public void CommandAPICommand() {}

  public void disable() {
    CommandAPI.unregister("team", true);
    CommandAPI.unregister("teammsg", true);
    CommandAPI.unregister("plugins", true);
  }
}

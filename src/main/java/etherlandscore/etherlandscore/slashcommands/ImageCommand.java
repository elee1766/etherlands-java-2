package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.TextArgument;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.services.ExternalMetadataService;
import etherlandscore.etherlandscore.singleton.SettingsSingleton;
import etherlandscore.etherlandscore.slashcommands.helpers.CommandProcessor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

import java.awt.image.BufferedImage;
import java.util.Map;

public class ImageCommand extends CommandProcessor {
  private final Channels channels;
  private final Map<String, String> settings = SettingsSingleton.getSettings().getSettings();

  public ImageCommand(Channels channels, Fiber fiber) {
    super(channels, fiber);
    this.channels = channels;
    register();
  }

  public void getImage(Player sender, Object[] args) {
    BufferedImage cachedBuffer = ExternalMetadataService.getCachedBuffer((String) args[0], (String) args[1]);
    if (cachedBuffer != null) {
      Bukkit.getLogger().info(cachedBuffer.toString());
    }
  }

  void help(Player sender, Object[] args) {
    sender.sendMessage("/image display [contract address|collection name] [tokenID] [size]");
  }

  public void register(){
    CommandAPICommand ImageCommand =
        new CommandAPICommand("image")
            .withPermission("etherlands.public")
            .executesPlayer(this::help);
    ImageCommand.withSubcommand(
        new CommandAPICommand("download")
            .withArguments(new TextArgument("contract address or collection name"))
            .withArguments(new TextArgument("tokenID"))
            .executesPlayer(this::getImage));
    ImageCommand.register();
  }
}

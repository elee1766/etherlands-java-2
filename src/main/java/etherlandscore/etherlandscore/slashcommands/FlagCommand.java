package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.enums.FlagValue;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.singleton.LocaleSingleton;
import etherlandscore.etherlandscore.singleton.LocaleStrings;
import etherlandscore.etherlandscore.state.Gamer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class FlagCommand extends ListenerClient {
  private final Fiber fiber;
  private final Channels channels;
  private final LocaleStrings locales = new LocaleStrings();

  public FlagCommand(Channels channels, Fiber fiber) {
    super(channels, fiber);
    this.fiber = fiber;
    this.channels = channels;
    register();
  }

  public void plotMenu(Channels channel, Gamer gamer){
    channels.master_command.publish(
            new Message<>(MasterCommand.flag_plot, gamer));
  }

  public static void plotMenu(Gamer gamer){
    Player player = gamer.getPlayer();
    ArrayList<TextComponent> tc = new ArrayList<TextComponent>();
    String sep = "";
    TextComponent allow = new TextComponent ("Allow");
    TextComponent deny = new TextComponent (" Deny");
    TextComponent none = new TextComponent (" None\n");
    TextComponent component = new TextComponent("");
    TextComponent topBorder = new TextComponent("============== FLAGS ==============\n");
    TextComponent next = new TextComponent("next");
    topBorder.setColor(ChatColor.YELLOW);
    allow.setColor(ChatColor.YELLOW);
    deny.setColor(ChatColor.YELLOW);
    none.setColor(ChatColor.YELLOW);
    allow.setUnderlined(true);
    deny.setUnderlined(true);
    none.setUnderlined(true);

    tc.add(topBorder);

    for(AccessFlags f : AccessFlags.values()){
      for(int i = 0; i<30 - String.valueOf(f).length();i++){
        sep = sep+"-";
      }
      TextComponent ff = new TextComponent(String.valueOf(f)+ " " + sep + " ");
      allow.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/flag set " + String.valueOf(f) + " allow"));
      deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/flag set " + String.valueOf(f) + " deny"));
      deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/flag set " + String.valueOf(f) + " none"));
      ff.setColor(ChatColor.YELLOW);
      tc.add(ff);
      tc.add(allow);
      tc.add(deny);
      tc.add(none);
      sep = "";
    }

    for(TextComponent comps : tc) {
      component.addExtra(comps);
    }
    player.sendMessage(component);
  }

  public void register() {
    CommandAPICommand FlagCommand =
        new CommandAPICommand("flags")
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  sender.sendMessage("global, plot");
                });
    FlagCommand.withSubcommand(
        new CommandAPICommand("global")
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  //idk wtf we gonn do w this
                }));

    FlagCommand.withSubcommand(
        new CommandAPICommand("plot")
            .withPermission("etherlands.public")
            .executesPlayer(
                (sender, args) -> {
                  Gamer gamer = context.getGamer(sender.getUniqueId());
                  plotMenu(this.channels,gamer);
                }));

    FlagCommand.register();
  }
}
package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
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

    TextComponent component = new TextComponent("");
    TextComponent space = new TextComponent(" ");
    TextComponent topBorder = new TextComponent("============== FLAGS ==============\n");
    TextComponent next = new TextComponent("next");
    topBorder.setColor(ChatColor.YELLOW);

    tc.add(topBorder);

    for(AccessFlags f : AccessFlags.values()){
      for(int i = 0; i<30 - String.valueOf(f).length();i++){
        sep = sep+"-";
      }
      String currentFlag = String.valueOf(f);
      TextComponent ff = new TextComponent(currentFlag+ " " + sep + " ");
      ff.setColor(ChatColor.YELLOW);
      tc.add(ff);
      for(FlagValue fv : FlagValue.values()){
        TextComponent value = new TextComponent(String.valueOf(fv));
        if(fv.toString()=="NONE") { //if flagvalue is set for the given accessflag
          value.setColor(ChatColor.YELLOW);
        }else{
          value.setColor(ChatColor.DARK_GRAY);
        }
        value.setUnderlined(true);
        value.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/flags set " + currentFlag + " " + fv));
        tc.add(value);
        tc.add(space);
      }
      TextComponent newLine = new TextComponent("\n");
      tc.add(newLine);
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
    FlagCommand.withSubcommand(
            new CommandAPICommand("set")
                    .withArguments(new StringArgument("flag").replaceSuggestions(info -> getAccessFlagStrings()),new StringArgument("value").replaceSuggestions(info -> getFlagValueStrings()))
                    .withPermission("etherlands.public")
                    .executesPlayer(
                            (sender, args) -> {
                              //set the flags here boiii
                            }));


    FlagCommand.register();
  }
}

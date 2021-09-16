package etherlandscore.etherlandscore.Menus;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.ChatTarget;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.Group;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

import static etherlandscore.etherlandscore.services.MasterService.state;

public class GroupPrinter extends ListenerClient {
  private final Fiber fiber;
  private final Channels channels;
  private final Group group;

  public GroupPrinter(Group writeGroup, Fiber fiber, Channels channels) {
    super(channels, fiber);
    this.fiber = fiber;
    this.channels = channels;
    this.group = writeGroup;
  }
  public void printGroup(Player sender) {
    MessageCreator builder = new MessageCreator();
    TextComponent title = ComponentCreator.ColoredText("Group: " +group.getName(), ChatColor.BLUE);
    builder.addHeader(title);
    builder.addField("group",ComponentCreator.Group(this.group.getName()));
    builder.addField("team",ComponentCreator.Team(this.group.getTeamObject()));
    builder.addField("members",ComponentCreator.UUIDs(this.group.getMembers()));
    builder.addField("priority",ComponentCreator.ColoredText(this.group.getPriority().toString(), ChatColor.GRAY));
    builder.addFooter();
    builder.finish();
    Gamer gamer = state().getGamer(sender.getUniqueId());
    channels.chat_message.publish(new Message<>(ChatTarget.gamer_base, gamer, builder.getMessage()));
  }
}

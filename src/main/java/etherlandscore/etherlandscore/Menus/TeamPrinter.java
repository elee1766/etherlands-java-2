package etherlandscore.etherlandscore.Menus;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.ChatTarget;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.Group;
import etherlandscore.etherlandscore.state.read.Team;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

import java.util.HashSet;
import java.util.Set;

import static etherlandscore.etherlandscore.services.MasterService.state;

public class TeamPrinter extends ListenerClient {
  private final Fiber fiber;
  private final Channels channels;
  private final Team team;

  public TeamPrinter(Team writeTeam, Fiber fiber, Channels channels) {
    super(channels, fiber);
    this.fiber = fiber;
    this.channels = channels;
    this.team = writeTeam;
  }

  public void printTeam(Player sender) {
    MessageCreator builder = new MessageCreator();
    if (this.team == null) {
      return;
    }
    TextComponent title = ComponentCreator.ColoredText(this.team.getName(), ChatColor.RED);
    builder.addHeader(title);
    builder.addField("team",ComponentCreator.Team(this.team));
    builder.addField("owner",ComponentCreator.UUID(this.team.getOwnerUUID(),ChatColor.GOLD));
    builder.addField("members",ComponentCreator.UUIDs(this.team.getMembers()));
    builder.addField("districts",ComponentCreator.Districts(this.team.getDistrictObjects()));
    Set<Group> groups = new HashSet<>(this.team.getGroups().values());
    builder.addField("groups",ComponentCreator.Groups(groups));
    builder.addFooter();
    builder.finish();

    Gamer gamer = state().getGamer(sender.getUniqueId());
    channels.chat_message.publish(new Message<>(ChatTarget.gamer_base, gamer, builder.getMessage()));
  }
}

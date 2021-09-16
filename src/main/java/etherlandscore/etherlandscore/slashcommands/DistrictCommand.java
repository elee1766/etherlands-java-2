package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.IntegerRangeArgument;
import dev.jorel.commandapi.wrappers.IntegerRange;
import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.enums.FlagValue;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.ChatTarget;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.slashcommands.helpers.CommandProcessor;
import etherlandscore.etherlandscore.slashcommands.helpers.SlashCommands;
import etherlandscore.etherlandscore.state.read.District;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.Group;
import etherlandscore.etherlandscore.state.read.Team;
import etherlandscore.etherlandscore.state.sender.DistrictSender;
import etherlandscore.etherlandscore.state.sender.TeamSender;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

import static etherlandscore.etherlandscore.services.MasterService.state;

public class DistrictCommand extends CommandProcessor {
  private final Fiber fiber;
  private final Channels channels;

  public DistrictCommand(Channels channels, Fiber fiber) {
    super(channels, fiber);
    this.fiber = fiber;
    this.channels = channels;
    register();
  }

  void delegate(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    Team team = gamer.getTeamObject();
    IntegerRange range = (IntegerRange) args[0];
    for (int i = range.getLowerBound();
         i <= range.getUpperBound();
         i++) {
      if(context.getDistrict(i) != null){
        if(context.getDistrict(i).isOwner(gamer)) {
          TeamSender.delegateDistrict(this.channels, context.getDistrict(i), team, new Message(ChatTarget.team_delegate_district, gamer, context.getDistrict(i)));
        } else {
          sender.sendMessage("You do not own this district");
        }
      }
    }
  }

  void delegateLocal(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    Team team = gamer.getTeamObject();
    Chunk chunk = gamer.getPlayer().getChunk();
    District writeDistrict = context.getDistrict(chunk.getX(), chunk.getZ());
    if (writeDistrict.getOwnerUUID().equals(gamer.getUuid())) {
      TeamSender.delegateDistrict(this.channels, writeDistrict, team, new Message(ChatTarget.team_delegate_district, gamer, writeDistrict));
    } else {
      sender.sendMessage("You do not own this district");
    }
  }

  void infoGiven(CommandSender sender, Object[] args) {
    District district = context.getDistrict((int) args[0]);
    if (sender instanceof Player) {
      Gamer gamer = state().getGamer(((Player) sender).getUniqueId());
      channels.chat_message.publish(
          new Message<>(ChatTarget.gamer_district_info, gamer, district));
    }
  }

  void infoLocal(Player sender, Object[] args) {
    Location loc = sender.getLocation();
    Chunk chunk = loc.getChunk();
    int x = chunk.getX();
    int z = chunk.getZ();
    District district = context.getDistrict(x, z);
    Gamer gamer = state().getGamer(sender.getUniqueId());
      channels.chat_message.publish(
          new Message<>(ChatTarget.gamer_district_info, gamer, district)
      );
  }

  void reclaim(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    IntegerRange range = (IntegerRange) args[0];
    for (int i = range.getLowerBound();
         i <= range.getUpperBound();
         i++) {

      District writeDistrict = context.getDistrict(i);
      if(writeDistrict != null) {
        if (writeDistrict.getOwnerAddress().equals(gamer.getAddress())) {
          DistrictSender.reclaimDistrict(this.channels, writeDistrict, sender);
        } else {
          sender.sendMessage("You do not own district:" + i);
        }
      }else{
        sender.sendMessage("That district is currently unclaimed");
      }
    }
  }

  void reclaimLocal(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    Chunk chunk = gamer.getPlayer().getChunk();
    District writeDistrict = context.getDistrict(chunk.getX(), chunk.getZ());
    if(writeDistrict != null) {
      if (writeDistrict.getOwnerAddress().equals(gamer.getAddress())) {
        DistrictSender.reclaimDistrict(this.channels, writeDistrict, sender);
      } else {
        sender.sendMessage("You do not own district:" + writeDistrict.getIdInt());
      }
    }else{
      sender.sendMessage("This district is currently unclaimed");
    }
  }

  void help(Player sender, Object[] args) {
    TextComponent help = new TextComponent("======District Help======\n\n");
    TextComponent delegate = new TextComponent("/district delegate [teamname] -> delegates plot to given team\n\n");
    TextComponent reclaim = new TextComponent("/district reclaim [DistrictIDs/Nothing] -> reclaims given district from team\n\n");
    TextComponent info = new TextComponent("/district info [DistrictID/Nothing] -> displays helpful info about the district");
    help.addExtra(delegate);
    help.addExtra(reclaim);
    help.addExtra(info);
    help.setColor(ChatColor.LIGHT_PURPLE);
    sender.sendMessage(help);
  }

  void update(CommandSender sender, Object[] args) {
    if(sender.isOp() || (!(sender instanceof Player))){
      sender.sendMessage(args[0] + " is being updated...");
      IntegerRange range = (IntegerRange) args[0];
      for (int i = range.getLowerBound(); i <= Math.min(1000000, range.getUpperBound()); i++) {
        DistrictSender.touchDistrict(this.channels, i, sender);
      }
    }
  }


  void setGroup(Player sender, Object[] args) {
    Gamer manager = context.getGamer(sender.getUniqueId());
    Team team = manager.getTeamObject();
    if(team == null){
    }
    if (team.isManager(manager)) {
      District writeDistrict = context.getDistrict((int) args[0]);
      Group member = (Group) args[1];
      AccessFlags flag = (AccessFlags) args[2];
      FlagValue value = (FlagValue) args[3];
      DistrictSender.setGroupPermission(channels, member, flag, value, writeDistrict,manager);
    }else{
    }
  }

  void setPlayer(Player sender, Object[] args) {
    Gamer manager = context.getGamer(sender.getUniqueId());
    Team team = manager.getTeamObject();
    if(team == null){
      sender.sendMessage("ur not in a team");
    }
    if (team.isManager(manager)) {
      District writeDistrict = context.getDistrict((int) args[0]);
      Gamer member = (Gamer) args[1];
      AccessFlags flag = (AccessFlags) args[2];
      FlagValue value = (FlagValue) args[3];
      DistrictSender.setGamerPermission(channels, member, flag, value, writeDistrict);
    }else{
    }
  }

  public void register() {
    CommandAPICommand DistrictCommand =
        createPlayerCommand("district", SlashCommands.infoLocal,this::infoLocal)
            .withAliases("d")
            .withPermission("etherlands.public");
    CommandAPICommand DistrictInfoCommand =
        createPlayerCommand("district", SlashCommands.infoGiven,this::infoGiven)
            .withAliases("d")
            .withPermission("etherlands.public")
            .withArguments(new IntegerArgument("DistrictID"));
    DistrictCommand.withSubcommand(
        createPlayerCommand("help", SlashCommands.help,this::help)
    );
    DistrictCommand.withSubcommand(
        createPlayerCommand("set_player", SlashCommands.setPlayer,this::setPlayer)
            .withAliases("setp", "setplayer", "setPlayer")
            .withArguments(new IntegerArgument("districtID"))
            .withArguments(teamMemberArgument("member"))
            .withArguments(accessFlagArgument("flag"))
            .withArguments(flagValueArgument("value"))
            .executesPlayer(this::setPlayer));
    DistrictCommand.withSubcommand(
        createPlayerCommand("set_group", SlashCommands.setGroup,this::setGroup)
            .withAliases("setg", "setgroup", "setGroup")
            .withArguments(new IntegerArgument("districtID"))
            .withArguments(teamGroupArgument("group"))
            .withArguments(accessFlagArgument("flag"))
            .withArguments(
                flagValueArgument("value").replaceSuggestions(info -> getFlagValueStrings()))
            .withPermission("etherlands.public")
        );
    DistrictCommand.withSubcommand(
        createPlayerCommand("info", SlashCommands.infoLocal,this::infoLocal)
    );
    DistrictCommand.withSubcommand(
        createPlayerCommand("info", SlashCommands.infoGiven,this::infoGiven)
            .withAliases("i")
            .withArguments(
                new IntegerArgument("District Id")
            )
    );
    DistrictCommand.withSubcommand(
        createPlayerCommand("update", SlashCommands.update,this::update)
            .withArguments(new IntegerRangeArgument("chunkId"))
            .executes(this::update)
    );

    DistrictCommand.withSubcommand(
        createPlayerCommand("reclaim", SlashCommands.reclaim,this::reclaim)
            .withArguments(new IntegerRangeArgument("chunkId"))
            .withArguments(new IntegerRangeArgument("chunkId"))
    );
    DistrictCommand.withSubcommand(
        createPlayerCommand("reclaim", SlashCommands.reclaimLocal,this::reclaimLocal)
            .executesPlayer(this::reclaimLocal)
    );
    DistrictCommand.withSubcommand(
        createPlayerCommand("delegate", SlashCommands.delegate,this::delegate)
            .withArguments(new IntegerRangeArgument("DEED id"))
            .executesPlayer(this::delegate)
    );
    DistrictCommand.withSubcommand(
        createPlayerCommand("delegate", SlashCommands.delegateLocal,this::delegateLocal)
    );
    DistrictCommand.register();
    DistrictInfoCommand.register();
  }
}

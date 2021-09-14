package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.IntegerRangeArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.wrappers.IntegerRange;
import etherlandscore.etherlandscore.Menus.DistrictPrinter;
import etherlandscore.etherlandscore.Menus.FlagMenu;
import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.enums.FlagValue;
import etherlandscore.etherlandscore.enums.MessageToggles;
import etherlandscore.etherlandscore.enums.ToggleValues;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.EthersCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.state.read.District;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.Group;
import etherlandscore.etherlandscore.state.read.Team;
import etherlandscore.etherlandscore.state.sender.DistrictSender;
import etherlandscore.etherlandscore.state.sender.GamerSender;
import etherlandscore.etherlandscore.state.sender.TeamSender;
import etherlandscore.etherlandscore.state.write.WriteGamer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;
import org.w3c.dom.Text;

public class DistrictCommand extends ListenerClient {
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
      if (context.getDistrict(i).getOwnerUUID().equals(gamer.getUuid())) {
        TeamSender.delegateDistrict(this.channels, context.getDistrict(i), team);
        sender.sendMessage("District: " + i + " has been delegated to " + team.getName());
      } else {
        sender.sendMessage("You do not own this district");
      }
    }
  }

  void delegateLocal(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    Team team = gamer.getTeamObject();
    Chunk chunk = gamer.getPlayer().getChunk();
    District writeDistrict = context.getDistrict(chunk.getX(), chunk.getZ());
    if (writeDistrict.getOwnerUUID().equals(gamer.getUuid())) {
      TeamSender.delegateDistrict(this.channels, writeDistrict, team);
      sender.sendMessage(writeDistrict.getIdInt() + " has been delegated to " + team.getName());
    } else {
      sender.sendMessage("You do not own this district");
    }
  }

  void infoGiven(CommandSender sender, Object[] args) {
    District writeDistrict = context.getDistrict((int) args[0]);
    if (writeDistrict == null) {
      TextComponent unclaimed = new TextComponent("This Land is unclaimed");
      unclaimed.setColor(ChatColor.YELLOW);
      sender.sendMessage(unclaimed);
    } else {
      DistrictPrinter printer = new DistrictPrinter(writeDistrict, fiber, channels);
      printer.printDistrict((Player) sender);
    }
  }

  void infoLocal(Player sender, Object[] args) {
    District writeDistrict;
    Location loc = sender.getLocation();
    Chunk chunk = loc.getChunk();
    int x = chunk.getX();
    int z = chunk.getZ();
    writeDistrict = context.getDistrict(x, z);
    if (writeDistrict == null) {
      TextComponent unclaimed = new TextComponent("This Land is unclaimed");
      unclaimed.setColor(ChatColor.YELLOW);
      sender.sendMessage(unclaimed);
    } else {
      DistrictPrinter printer = new DistrictPrinter(writeDistrict, fiber, channels);
      printer.printDistrict(sender);
    }
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
          DistrictSender.reclaimDistrict(this.channels, writeDistrict);
          sender.sendMessage("District: " + i + " has been reclaimed");
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
        DistrictSender.reclaimDistrict(this.channels, writeDistrict);
        sender.sendMessage("District: " + writeDistrict.getIdInt() + " has been reclaimed");
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
        this.channels.ethers_command.publish(new Message<>(EthersCommand.ethers_query_nft, i));
      }
      sender.sendMessage(args[0] + " have been updated");
    } else {
      sender.sendMessage("You do not have permission to run this command");
    }
  }

  void forceUpdate(CommandSender sender, Object[] args) {
    if(sender.isOp() || (!(sender instanceof Player))){
      sender.sendMessage(args[0] + " is being updated...");
      IntegerRange range = (IntegerRange) args[0];
      for (int i = range.getLowerBound(); i <= Math.min(1000000, range.getUpperBound()); i++) {
        this.channels.ethers_command.publish(new Message<>(EthersCommand.force_update, i));
      }
      sender.sendMessage(args[0] + " have been updated");
    } else {
      sender.sendMessage("You do not have permission to run this command");
    }
  }

  public void register() {
    CommandAPICommand DistrictCommand =
        new CommandAPICommand("district").withAliases("d")
            .withPermission("etherlands.public")
            .executesPlayer(this::infoLocal);
    CommandAPICommand DistrictInfoCommand =
        new CommandAPICommand("district").withAliases("d")
            .withPermission("etherlands.public")
            .withArguments(new IntegerArgument("DistrictID"))
            .executesPlayer(this::infoGiven);
    DistrictCommand.withSubcommand(new CommandAPICommand("help").executesPlayer(this::help));
    DistrictCommand.withSubcommand(
        new CommandAPICommand("set_player")
            .withAliases("setp", "setplayer", "setPlayer")
            .withArguments(new IntegerArgument("districtID"))
            .withArguments(teamMemberArgument("member"))
            .withArguments(accessFlagArgument("flag"))
            .withArguments(flagValueArgument("value"))
            .executesPlayer(this::setPlayer));
    DistrictCommand.withSubcommand(
        new CommandAPICommand("set_group")
            .withAliases("setg", "setgroup", "setGroup")
            .withArguments(new IntegerArgument("districtID"))
            .withArguments(teamGroupArgument("group"))
            .withArguments(accessFlagArgument("flag"))
            .withArguments(
                flagValueArgument("value").replaceSuggestions(info -> getFlagValueStrings()))
            .withPermission("etherlands.public")
            .executesPlayer(this::setGroup));
    DistrictCommand.withSubcommand(
        new CommandAPICommand("set_all_group")
            .withAliases("setag", "setallgroup", "setAllGroup")
            .withArguments(new IntegerArgument("districtID"))
            .withArguments(teamGroupArgument("group"))
            .withArguments(
                flagValueArgument("value").replaceSuggestions(info -> getFlagValueStrings()))
            .withPermission("etherlands.public")
            .executesPlayer(this::setAllGroup));
    DistrictCommand.withSubcommand(new CommandAPICommand("info").executesPlayer(this::infoLocal));

    DistrictCommand.withSubcommand(
        new CommandAPICommand("info").withAliases("i")
            .withArguments(
                new IntegerArgument("District Id").replaceSuggestions(info -> getChunkStrings()))
            .executes(this::infoGiven));
    DistrictCommand.withSubcommand(
        new CommandAPICommand("update")
            .withArguments(new IntegerRangeArgument("chunkId"))
            .executes(this::update));
    DistrictCommand.withSubcommand(
        new CommandAPICommand("forceupdate")
            .withArguments(new IntegerRangeArgument("chunkId"))
            .executes(this::forceUpdate));
    DistrictCommand.withSubcommand(
        new CommandAPICommand("reclaim")
            .withArguments(new IntegerRangeArgument("chunkId"))
            .executesPlayer(this::reclaim));
    DistrictCommand.withSubcommand(
        new CommandAPICommand("reclaim")
            .executesPlayer(this::reclaimLocal));
    DistrictCommand.withSubcommand(
        new CommandAPICommand("delegate")
            .withArguments(new IntegerRangeArgument("DEED id"))
            .executesPlayer(this::delegate));
    DistrictCommand.withSubcommand(
        new CommandAPICommand("delegate").executesPlayer(this::delegateLocal));

    DistrictCommand.register();
    DistrictInfoCommand.register();
  }

  void setGroup(Player sender, Object[] args) {
    Gamer manager = context.getGamer(sender.getUniqueId());
    Team team = manager.getTeamObject();
    if(team == null){
      sender.sendMessage("ur not in a team");
    }
    if (team.isManager(manager)) {
      District writeDistrict = context.getDistrict((int) args[0]);
      Group member = (Group) args[1];
      AccessFlags flag = (AccessFlags) args[2];
      FlagValue value = (FlagValue) args[3];
      DistrictSender.setGroupPermission(channels, member, flag, value, writeDistrict);
      sender.sendMessage("group permission set");
      FlagMenu.clickMenu(manager, "group", "district set_group", writeDistrict, member);
    }else{
      sender.sendMessage("only managers may set district permissions");
    }
  }

  void setAllGroup(Player sender, Object[] args) {
    Gamer manager = context.getGamer(sender.getUniqueId());
    Team team = manager.getTeamObject();
    if(team == null){
      sender.sendMessage("ur not in a team");
    }
    if (team.isManager(manager)) {
      District writeDistrict = context.getDistrict((int) args[0]);
      Group member = (Group) args[1];
      for(AccessFlags af : AccessFlags.values()){
        DistrictSender.setGroupPermission(channels, member, af, (FlagValue) args[2], writeDistrict);
      }
      sender.sendMessage("All permitions set to allow for group " + member.getName());
      FlagMenu.clickMenu(manager, "group", "district set_group", writeDistrict, member);
    }else{
      sender.sendMessage("only managers may set district permissions");
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
      sender.sendMessage("group permission set");
      FlagMenu.clickMenu(manager, "group", "district set_group", writeDistrict, member.getPlayer());
    }else{
      sender.sendMessage("only managers may set district permissions");
    }
  }
}

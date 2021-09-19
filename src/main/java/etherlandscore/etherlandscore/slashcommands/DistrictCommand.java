package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.enums.FlagValue;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.ChatTarget;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.slashcommands.helpers.CommandProcessor;
import etherlandscore.etherlandscore.slashcommands.helpers.SlashCommands;
import etherlandscore.etherlandscore.state.read.District;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.Team;
import etherlandscore.etherlandscore.state.read.Town;
import etherlandscore.etherlandscore.state.sender.StateSender;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

import static etherlandscore.etherlandscore.services.MasterService.state;

public class DistrictCommand extends CommandProcessor {
  private final Channels channels;

  public DistrictCommand(Channels channels, Fiber fiber) {
    super(channels, fiber);
    this.channels = channels;
    register();
  }

  void delegate(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    Town town = gamer.getTownObject();
    String i = (String) args[0];
    if (context.getDistrict(i) != null) {
      if (context.getDistrict(i).isOwner(gamer)) {
        StateSender.delegateDistrict(
            this.channels,
            context.getDistrict(i),
            town
        );
      } else {
        sender.sendMessage("You do not own this district");
      }
    }
  }

  void delegateLocal(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    Town town = gamer.getTownObject();
    Chunk chunk = gamer.getPlayer().getChunk();
    District writeDistrict = context.getDistrict(chunk.getX(), chunk.getZ());
    if (writeDistrict.getOwnerUUID().equals(gamer.getUuid())) {
      StateSender.delegateDistrict(this.channels, writeDistrict, town);
    } else {
      sender.sendMessage("You do not own this district");
    }
  }

  void infoGiven(CommandSender sender, Object[] args) {
    District district = context.getDistrict((String) args[0]);
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
    String i = (String) args[0];
    District writeDistrict = context.getDistrict(i);
    if(writeDistrict != null) {
      if (writeDistrict.getOwnerAddress().equals(gamer.getAddress())) {
        StateSender.reclaimDistrict(this.channels, writeDistrict, gamer);
      } else {
        sender.sendMessage("You do not own district:" + i);
      }
    }else{
      sender.sendMessage("That district is currently unclaimed");
    }
  }

  void reclaimLocal(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    Chunk chunk = gamer.getPlayer().getChunk();
    District writeDistrict = context.getDistrict(chunk.getX(), chunk.getZ());
    if(writeDistrict != null) {
      if (writeDistrict.getOwnerAddress().equals(gamer.getAddress())) {
        StateSender.reclaimDistrict(this.channels, writeDistrict, gamer);
      } else {
        sender.sendMessage("You do not own district:" + writeDistrict.getIdInt());
      }
    }else{
      sender.sendMessage("This district is currently unclaimed");
    }
  }

  void setTeam(Player sender, Object[] args) {
    Gamer manager = context.getGamer(sender.getUniqueId());
    Town town = manager.getTownObject();
    if(town == null){
      return;
    }
    if (town.isManager(manager)) {
      District writeDistrict = context.getDistrict((String) args[0]);
      Team member = (Team) args[2];
      AccessFlags flag = (AccessFlags) args[3];
      FlagValue value = (FlagValue) args[4];
      StateSender.setTeamPermission(channels, member, flag, value, writeDistrict,manager);
    }
  }

  void setPlayer(Player sender, Object[] args) {
    Gamer manager = context.getGamer(sender.getUniqueId());
    Town town = manager.getTownObject();
    if(town == null){
      return;
    }
    if (town.isManager(manager)) {
      District writeDistrict = context.getDistrict((String) args[0]);
      Gamer member = (Gamer) args[2];
      AccessFlags flag = (AccessFlags) args[3];
      FlagValue value = (FlagValue) args[4];
      StateSender.setGamerPermission(channels, member, flag, value, writeDistrict);
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
            .withArguments(new StringArgument("district"));

    hook(SlashCommands.setPlayer,this::setPlayer);
    hook(SlashCommands.setTeam,this::setTeam);
    createPlayerCommand("set", SlashCommands.modify, this::modify)
        .withArguments(districtArgument("district"))
        .withArguments(new MultiLiteralArgument("set_player","set_p","setp","setPlayer").replaceSuggestions(n-> new String[]{"set_player"}))
        .withArguments(townMemberArgument("member"))
        .withArguments(accessFlagArgument("flag"))
        .withArguments(flagValueArgument("value"))
        .executesPlayer(this::setPlayer).register();

    createPlayerCommand("district", SlashCommands.setTeam,this::setTeam)
        .withArguments(districtArgument("district"))
        .withArguments(new MultiLiteralArgument("set_team","set_g","setg","setTeam").replaceSuggestions(n-> new String[]{"set_player"}))
        .withArguments(townTeamArgument("team"))
        .withArguments(accessFlagArgument("flag"))
        .withArguments(flagValueArgument("value"))
        .withPermission("etherlands.public").register();

    createPlayerCommand("reclaim", SlashCommands.reclaim,this::reclaim)
        .withArguments(districtArgument("District")).register();
    createPlayerCommand("reclaim", SlashCommands.reclaimLocal,this::reclaimLocal)
        .executesPlayer(this::reclaimLocal).register();
    createPlayerCommand("delegate", SlashCommands.delegateLocal,this::delegateLocal).register();
    createPlayerCommand("delegate", SlashCommands.delegate,this::delegate)
        .withArguments(districtArgument("District"))
        .executesPlayer(this::delegate).register();
    DistrictCommand.register();
    DistrictInfoCommand.register();
  }

  void modify(Player sender, Object[] args) {
    switch((String) args[1]){
      case "set_player", "set_p","setp","setPlayer":
        runAsync(SlashCommands.setPlayer, sender, args);
        break;
      case "set_team", "set_g","setg","setTeam":
        runAsync(SlashCommands.setTeam, sender, args);
        break;
    }
  }
}

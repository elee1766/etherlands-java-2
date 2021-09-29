package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.enums.FlagValue;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.ChatTarget;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.services.ImpartialHitter;
import etherlandscore.etherlandscore.singleton.WorldAsker;
import etherlandscore.etherlandscore.slashcommands.helpers.CommandProcessor;
import etherlandscore.etherlandscore.slashcommands.helpers.SlashCommands;
import etherlandscore.etherlandscore.state.District;
import etherlandscore.etherlandscore.state.Gamer;
import etherlandscore.etherlandscore.state.Team;
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
    District district = (District) args[0];
    ImpartialHitter.HitWorld("district",district.getId(),"delegate", gamer.getUuidString());
  }

  void delegateLocal(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    Chunk chunk = gamer.getPlayer().getChunk();
    District district = WorldAsker.GetDistrict(chunk.getX(), chunk.getZ());
    ImpartialHitter.HitWorld("district",district.getId(),"delegate", gamer.getUuidString());

  }

  void infoGiven(CommandSender sender, Object[] args) {
    District district = (District) args[0];
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

  void reclaim(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    District district = (District) args[0];
    ImpartialHitter.HitWorld("district",district.getId(),"reclaim", gamer.getUuidString());
  }

  void reclaimLocal(Player sender, Object[] args) {
    Gamer gamer = context.getGamer(sender.getUniqueId());
    Chunk chunk = gamer.getPlayer().getChunk();
    District district = context.getDistrict(chunk.getX(), chunk.getZ());
    ImpartialHitter.HitWorld("district",district.getId(),"reclaim", gamer.getUuidString());

  }

  public void register() {
    CommandAPICommand DistrictCommand =
        createPlayerCommand("district", SlashCommands.infoLocal,this::infoLocal)
            .withAliases("d")
            .withPermission("etherlands.public");
    CommandAPICommand DistrictInfoCommand =
        createPlayerCommand("district", SlashCommands.infoGiven,this::infoGiven)
            .withPermission("etherlands.public")
            .withArguments(districtArgument("district"));

//  hook(SlashCommands.setPlayer,this::setPlayer);
//  hook(SlashCommands.setTeam,this::setTeam);
//  createPlayerCommand("set", SlashCommands.modify, this::modify)
//      .withArguments(districtArgument("district"))
//      .withArguments(new MultiLiteralArgument("set_player","set_p","setp","setPlayer").replaceSuggestions(n-> new String[]{"set_player"}))
//      .withArguments(townMemberArgument("member"))
//      .withArguments(accessFlagArgument("flag"))
//      .withArguments(flagValueArgument("value"))
//      .executesPlayer(this::setPlayer).register();

    createPlayerCommand("district", SlashCommands.setTeam,this::setTeam)
        .withArguments(districtArgument("district"))
        .withArguments(new MultiLiteralArgument("set_team","set_g","setg","setTeam"))
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
        .register();
    DistrictCommand.register();
    DistrictInfoCommand.register();
  }

  void setPlayer(Player sender, Object[] args) {
    Gamer manager = context.getGamer(sender.getUniqueId());
    District district = (District) args[0];
    Gamer member = (Gamer) args[2];
    AccessFlags flag = (AccessFlags) args[3];
    FlagValue value = (FlagValue) args[4];
    ImpartialHitter.HitWorld(
        "flags",
        "gamer",
        member.getUuidString(),
        district.getId(),
        flag.toString().toLowerCase(),
        value.toString().toLowerCase(),
        manager.getUuidString()
    );
  }

  void setTeam(Player sender, Object[] args) {
    Gamer manager = context.getGamer(sender.getUniqueId());
    District district = (District) args[0];
    Team team = (Team) args[2];
    AccessFlags flag = (AccessFlags) args[3];
    FlagValue value = (FlagValue) args[4];
    ImpartialHitter.HitWorld(
        "flags",
        "team",
        manager.getTown(),
        district.getId(),
        team.getName(),
        flag.toString().toLowerCase(),
        value.toString().toLowerCase(),
        manager.getUuidString()
    );
  }
}

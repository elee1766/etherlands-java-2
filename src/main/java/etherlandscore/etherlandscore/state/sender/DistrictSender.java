package etherlandscore.etherlandscore.state.sender;

import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.enums.FlagValue;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.ChatTarget;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.state.read.District;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.Group;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DistrictSender {

  public static void reclaimDistrict(Channels channels, District writeDistrict, Player sender) {
    channels.master_command.publish(
        new Message<>(MasterCommand.district_reclaim_district, writeDistrict).setChatResponse(ChatTarget.gamer_distric_reclaim, sender, writeDistrict));
  }

  public static void setGamerPermission(
      Channels channels, Gamer gamer, AccessFlags flag, FlagValue value, District writeDistrict) {
    channels.master_command.publish(
        new Message<>(
            MasterCommand.district_set_gamer_permission, writeDistrict, gamer, flag, value));
  }

  public static void setGroupPermission(
      Channels channels,
      Group writeGroup,
      AccessFlags flag,
      FlagValue value,
      District writeDistrict) {
    channels.master_command.publish(
        new Message<>(
            MasterCommand.district_set_group_permission, writeDistrict, writeGroup, flag, value));
  }

  public static void touchDistrict(Channels channels, int i, CommandSender sender) {
    channels.master_command.publish(new Message<>(MasterCommand.touch_district, i).setChatResponse(ChatTarget.district_touch_district, sender, i));
  }
}

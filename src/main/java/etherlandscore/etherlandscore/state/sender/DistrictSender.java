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

  public static void reclaimDistrict(Channels channels, District district, Player sender) {
    channels.master_command.publish(
        new Message<>(MasterCommand.district_reclaim_district, district)
            .setChatResponse(ChatTarget.gamer_district_reclaim, sender, district)
    );
  }

  public static void setGamerPermission(
      Channels channels, Gamer gamer, AccessFlags flag, FlagValue value, District district) {
    channels.master_command.publish(
        new Message<>(
            MasterCommand.district_set_gamer_permission, district, gamer, flag, value)
            .setChatResponse(ChatTarget.gamer_district_info, gamer, district)
    );
  }

  public static void setGroupPermission(
      Channels channels,
      Group group,
      AccessFlags flag,
      FlagValue value,
      District district,
      Gamer gamer
  ) {
    channels.master_command.publish(
        new Message<>(
            MasterCommand.district_set_group_permission, district, group, flag, value)
            .setChatResponse(ChatTarget.gamer_district_info, gamer, district)
    );
  }

  public static void touchDistrict(Channels channels, int i, CommandSender sender) {
    channels.master_command.publish(new Message<>(MasterCommand.touch_district, i).setChatResponse(ChatTarget.district_touch_district, sender, i));
  }
}

package etherlandscore.etherlandscore.state.sender;

import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.enums.FlagValue;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.state.read.District;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.Group;
import etherlandscore.etherlandscore.state.read.Plot;

public class DistrictSender {

  public static void addPlot(Channels channels, Plot writePlot, District writeDistrict) {
    channels.master_command.publish(
        new Message<>(MasterCommand.district_add_plot, writeDistrict, writePlot));
  }

  public static void removePlot(Channels channels, Plot writePlot, District writeDistrict) {
    channels.master_command.publish(
        new Message<>(MasterCommand.district_remove_plot, writeDistrict, writePlot));
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

  public static void setPriority(Channels channels, Integer priority, District writeDistrict) {
    channels.master_command.publish(
        new Message<>(MasterCommand.district_set_priority, writeDistrict, priority));
  }
}

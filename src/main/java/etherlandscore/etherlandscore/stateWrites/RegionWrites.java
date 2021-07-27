package etherlandscore.etherlandscore.stateWrites;

import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.enums.FlagValue;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.state.*;
import etherlandscore.etherlandscore.util.Map2;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.UUID;

import static etherlandscore.etherlandscore.services.MasterService.state;

public class RegionWrites {

  public static void addPlot(Channels channels, Plot plot, Region region) {
    channels.master_command.publish(new Message<>(MasterCommand.region_add_plot, region, plot));
  }

  public static void removePlot(Channels channels, Plot plot, Region region) {
    channels.master_command.publish(new Message<>(MasterCommand.region_remove_plot, region, plot));
  }

  public static void setGamerPermission(
      Channels channels, Gamer gamer, AccessFlags flag, FlagValue value, Region region) {
    channels.master_command.publish(
        new Message<>(MasterCommand.region_set_gamer_permission, region, gamer, flag, value));
  }

  public static void setGroupPermission(
      Channels channels, Group group, AccessFlags flag, FlagValue value, Region region) {
    channels.master_command.publish(
        new Message<>(MasterCommand.region_set_group_permission, region, group, flag, value));
  }

  public static void setPriority(Channels channels, Integer priority, Region region) {
    channels.master_command.publish(
        new Message<>(MasterCommand.region_set_priority, region, priority));
  }
}

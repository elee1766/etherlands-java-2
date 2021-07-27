package etherlandscore.etherlandscore.stateWrites;

import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.enums.FlagValue;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.state.*;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static etherlandscore.etherlandscore.services.MasterService.state;

public class PlotWrites {

  public static void reclaimPlot(Channels channels, Plot plot) {
    channels.master_command.publish(new Message<>(MasterCommand.plot_reclaim_plot, plot));
  }

  public static void setOwner(Channels channels, String ownerAddress, Plot plot) {
    channels.master_command.publish(new Message(MasterCommand.plot_set_owner, ownerAddress));
  }

}

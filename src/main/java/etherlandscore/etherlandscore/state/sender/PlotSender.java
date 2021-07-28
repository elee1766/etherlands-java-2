package etherlandscore.etherlandscore.state.sender;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.state.read.Plot;

public class PlotSender {

  public static void reclaimPlot(Channels channels, Plot writePlot) {
    channels.master_command.publish(new Message<>(MasterCommand.plot_reclaim_plot, writePlot));
  }

  public static void setOwner(Channels channels, String ownerAddress, Plot writePlot) {
    channels.master_command.publish(new Message(MasterCommand.plot_set_owner, ownerAddress));
  }
}

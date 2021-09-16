package etherlandscore.etherlandscore.Menus;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.ChatTarget;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.state.read.District;
import etherlandscore.etherlandscore.state.read.Gamer;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

import static etherlandscore.etherlandscore.services.MasterService.state;

public class DistrictPrinter extends ListenerClient {
  private final Fiber fiber;
  private final Channels channels;
  private final District district;

  public DistrictPrinter(District district, Fiber fiber, Channels channels) {
    super(channels, fiber);
    this.fiber = fiber;
    this.channels = channels;
    this.district = district;
  }

  public void printDistrict(Player sender) {
    Gamer gamer = state().getGamer(sender.getUniqueId());
    channels.chat_message.publish(new Message<>(ChatTarget.gamer_district_info, gamer, district));
  }

}

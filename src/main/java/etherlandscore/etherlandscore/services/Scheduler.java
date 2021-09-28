package etherlandscore.etherlandscore.services;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.ServerModule;
import etherlandscore.etherlandscore.singleton.Asker;
import etherlandscore.etherlandscore.singleton.Hitter;
import etherlandscore.etherlandscore.state.sender.StateSender;
import etherlandscore.etherlandscore.state.write.Gamer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

import java.util.concurrent.TimeUnit;

import static etherlandscore.etherlandscore.services.MasterService.state;

public class Scheduler extends ServerModule {
  private final Channels channels;
  private final Fiber fiber;

  private int best_district = 1;

  public Scheduler(Channels channels, Fiber fiber) {
    super(fiber);
    this.fiber = fiber;
    this.channels = channels;
    for (Integer district : Asker.GetDistricts()) {
      if (!state().getDistricts().containsKey(district)) {
        StateSender.touchDistrict(this.channels, best_district);
        if (district > best_district) {
          best_district = district;
        }
      }
    }
    this.fiber.scheduleAtFixedRate(
        () -> {
          for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            Gamer gamer = state().getGamer(onlinePlayer.getUniqueId());
            Hitter.SetGamerPosition(gamer);
          }
        },
        1,
        5,
        TimeUnit.SECONDS);
  }
}

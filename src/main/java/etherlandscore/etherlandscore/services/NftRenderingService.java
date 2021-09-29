package etherlandscore.etherlandscore.services;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.fibers.ServerModule;
import etherlandscore.etherlandscore.singleton.WorldAsker;
import etherlandscore.etherlandscore.state.read.NFT;
import etherlandscore.etherlandscore.state.Gamer;
import etherlandscore.etherlandscore.util.Map2;
import kotlin.Pair;
import kotlin.Triple;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static etherlandscore.etherlandscore.services.MasterService.state;

public class NftRenderingService extends ServerModule {
  private final Channels channels;
  private final Fiber fiber;

  private final Map<String, Pair<Long, NFT>> activated = new HashMap<>();
  private final Map2<UUID, String, Boolean> gamerHas = new Map2<>();

  public NftRenderingService(Channels channels, Fiber fiber) {
    super(fiber);
    this.fiber = fiber;
    this.channels = channels;

    this.fiber.scheduleAtFixedRate(
        () -> {
          if (1 == 1) {
            return;
          }
          Gamer gamer = new Gamer(UUID.randomUUID());
          Triple<Integer, Integer, Integer> loc = WorldAsker.GetGamerXYZ(gamer.getUuid());
          Integer x = loc.getFirst();
          Integer y = loc.getSecond();
          Integer z = loc.getThird();
          Integer range = 64;
          if (x == null || y == null || z == null) {
            return;
          }
          for (int i = (x - range); i < (x + range); i++) {
            for (int j = (y - range); j < (y + range); j++) {
              for (int k = (z - range); k < (z + range); k++) {
                NFT nft = state().getNFTs().get(i, j, k);
                if (nft != null) {
                  activate(nft);
                }
              }
            }
          }
          try {
            process();
          } catch (Exception e) {
            e.printStackTrace();
          }
        },
        5,
        1,
        TimeUnit.SECONDS);
  }

  private void activate(NFT nft) {
    activated.put(nft.getId(), new Pair<>(new Date().getTime() / 1000, nft));
  }

  private void process() {
    Long current = new Date().getTime() / 1000;
    for (Map.Entry<String, Pair<Long, NFT>> entry : this.activated.entrySet()) {
      if ((entry.getValue().getFirst() - current) > 5) {
        this.activated.remove(entry.getKey());
      } else {
        NFT nft = entry.getValue().getSecond();
        renderToNearby(state().getNfts().get(nft.getXloc(), nft.getYloc(), nft.getZloc()));
        if (nft.isAir()) {
          this.activated.remove(entry.getKey());
          channels.master_command.publish(new Message<>(MasterCommand.nft_delete_nft, nft));
        }
      }
    }
  }

  private void renderToNearby(NFT nft) {
    if (nft == null) {
      return;
    }
    for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
      Gamer gamer = state().getGamer(onlinePlayer.getUniqueId());
      Triple<Integer, Integer, Integer> loc = WorldAsker.GetGamerXYZ(gamer.getUuid());
      Integer x = loc.getFirst();
      Integer y = loc.getSecond();
      Integer z = loc.getThird();
      Integer range = 64;
      if (gamerHas.getOrDefault(gamer.getUuid(), nft.getId(), false)) {
        continue;
      }
      if (x == null || y == null || z == null) {
        continue;
      }
      if (Math.abs(nft.getXloc() - x) < range) {
        if (Math.abs(nft.getYloc() - y) < range) {
          if (Math.abs(nft.getZloc() - z) < range) {
            try {
              if (nft.sendGamer(gamer)) {
                gamerHas.put(gamer.getUuid(), nft.getId(), true);
              }
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        }
      }
    }
  }
}

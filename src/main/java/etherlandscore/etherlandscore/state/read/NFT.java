package etherlandscore.etherlandscore.state.read;

import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface NFT {
  String getContract();

  String getItem();

  String getPath();

  String getUrl();

}

package etherlandscore.etherlandscore.stateWrites;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.state.Gamer;
import etherlandscore.etherlandscore.state.Group;
import etherlandscore.etherlandscore.state.StateHolder;
import etherlandscore.etherlandscore.state.Team;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static etherlandscore.etherlandscore.services.MasterService.state;

public class GamerWrites {

  public static void addFriend(Channels channels, Gamer self, Gamer gamer) {
    channels.master_command.publish(new Message<>(MasterCommand.gamer_add_friend, self, gamer));
  }

  public static void removeFriend(Channels channels, Gamer self, Gamer newFriend) {
    channels.master_command.publish(
        new Message<>(MasterCommand.gamer_remove_friend, self, newFriend));
  }

}

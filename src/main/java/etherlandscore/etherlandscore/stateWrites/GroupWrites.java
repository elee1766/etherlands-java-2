package etherlandscore.etherlandscore.stateWrites;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.state.Gamer;
import etherlandscore.etherlandscore.state.Group;
import etherlandscore.etherlandscore.state.StateHolder;
import etherlandscore.etherlandscore.state.Team;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static etherlandscore.etherlandscore.services.MasterService.state;

public class GroupWrites {

  public static void addMember(Channels channels, Group group, Gamer gamer) {
    if(group.getDefault()) return;
    channels.master_command.publish(new Message<>(MasterCommand.group_add_gamer,group, gamer));
  }

  public static void removeMember(Channels channels, Group group, Gamer gamer) {
    if(group.getDefault()) return;
    channels.master_command.publish(new Message<>(MasterCommand.group_remove_gamer,group, gamer));
  }

  public static void setPriority(Channels channels, Group group, Integer priority) {
    if(group.getDefault()) return;
    channels.master_command.publish(new Message<>(MasterCommand.group_set_priority,group, priority));
  }
}

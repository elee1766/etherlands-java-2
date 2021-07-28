package etherlandscore.etherlandscore.state.sender;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.Group;

public class GroupSender {

  public static void addMember(Channels channels, Group writeGroup, Gamer gamer) {
    if (writeGroup.getDefault()) return;
    channels.master_command.publish(
        new Message<>(MasterCommand.group_add_gamer, writeGroup, gamer));
  }

  public static void removeMember(Channels channels, Group writeGroup, Gamer gamer) {
    if (writeGroup.getDefault()) return;
    channels.master_command.publish(
        new Message<>(MasterCommand.group_remove_gamer, writeGroup, gamer));
  }

  public static void setPriority(Channels channels, Group writeGroup, Integer priority) {
    if (writeGroup.getDefault()) return;
    channels.master_command.publish(
        new Message<>(MasterCommand.group_set_priority, writeGroup, priority));
  }
}

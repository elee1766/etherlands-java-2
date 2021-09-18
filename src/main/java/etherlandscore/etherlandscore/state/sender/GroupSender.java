package etherlandscore.etherlandscore.state.sender;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.ChatTarget;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.Group;

public class GroupSender {

  public static void addMember(Channels channels, Group group, Gamer gamer) {
    if (group.getDefault()) return;
    channels.master_command.publish(
        new Message<>(MasterCommand.group_add_gamer, group, gamer));
  }

  public static void removeMember(Channels channels, Group group, Gamer gamer) {
    if (group.getDefault()) return;
    channels.master_command.publish(
        new Message<>(MasterCommand.group_remove_gamer, group, gamer));
  }

  public static void setPriority(Channels channels, Group group, Integer priority) {
    if (group.getDefault()) return;
    channels.master_command.publish(
        new Message<>(MasterCommand.group_set_priority, group, priority));
  }


  public static void sendGroupInfo(Channels channels, Gamer gamer, Group target) {
    channels.chat_message.publish(
        new Message<>(ChatTarget.gamer_group_info, gamer, target));
  }
}

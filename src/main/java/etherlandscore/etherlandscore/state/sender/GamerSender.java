package etherlandscore.etherlandscore.state.sender;

import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.enums.FlagValue;
import etherlandscore.etherlandscore.enums.MessageToggles;
import etherlandscore.etherlandscore.enums.ToggleValues;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.state.read.District;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.Group;
import etherlandscore.etherlandscore.state.write.WriteGamer;

public class GamerSender {

  public static void addFriend(Channels channels, Gamer self, Gamer gamer) {
    channels.master_command.publish(new Message<>(MasterCommand.gamer_add_friend, self, gamer));
  }

  public static void removeFriend(Channels channels, Gamer self, Gamer newFriend) {
    channels.master_command.publish(
        new Message<>(MasterCommand.gamer_remove_friend, self, newFriend));
  }

  public static void setAddress(Channels channels, Gamer self, String address) {
    channels.master_command.publish(new Message<>(MasterCommand.gamer_link_address, self, address));
  }

  public static void setMessageToggle(
      Channels channels,
      MessageToggles flag,
      ToggleValues value,
      WriteGamer gamer) {
    channels.master_command.publish(
        new Message<>(
            MasterCommand.gamer_toggle_message, gamer, flag, value));
  }


}

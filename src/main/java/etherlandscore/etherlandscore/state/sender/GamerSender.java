package etherlandscore.etherlandscore.state.sender;

import etherlandscore.etherlandscore.enums.MessageToggles;
import etherlandscore.etherlandscore.enums.ToggleValues;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.ChatTarget;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.state.read.Gamer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class GamerSender {

  public static void addFriend(Channels channels, Gamer self, Gamer gamer) {
    channels.master_command.publish(
        new Message<>(MasterCommand.gamer_add_friend, self, gamer).setChatResponse(ChatTarget.gamer_add_friend_response,self,gamer)
    );
  }

  public static void removeFriend(Channels channels, Gamer self, Gamer newFriend) {
    channels.master_command.publish(
        new Message<>(MasterCommand.gamer_remove_friend, self, newFriend));
  }

  public static void setAddress(Channels channels, Gamer id, String address) {
    channels.master_command.publish(new Message<>(MasterCommand.gamer_link_address, id, address));
  }

  public static void setMessageToggle(
      Channels channels,
      MessageToggles flag,
      ToggleValues value,
      Gamer gamer) {
    channels.master_command.publish(
        new Message<>(
            MasterCommand.gamer_toggle_message, gamer, flag, value).setChatResponse(
                ChatTarget.gamer,gamer, new TextComponent("Automap "+ value.toString())
        )

    );
  }
  public static void sendGamerInfo(Channels channels, Gamer gamer, Gamer target) {
    channels.chat_message.publish(
        new Message<>(ChatTarget.gamer_gamer_info, gamer, target));
  }

  public static void sendMap(Channels channels, BaseComponent map, Gamer target) {
    channels.chat_message.publish(
        new Message<>(ChatTarget.gamer_send_map, map, target));
  }

}

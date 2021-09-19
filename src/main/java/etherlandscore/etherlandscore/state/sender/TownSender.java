package etherlandscore.etherlandscore.state.sender;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.ChatTarget;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.state.read.District;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.Town;

public class TownSender {

  public static void addMember(Channels channels, Gamer gamer, Town town) {
    channels.master_command.publish(new Message<>(MasterCommand.town_add_gamer, town, gamer));
  }

  public static void createTeam(Channels channels, String name, Town town) {
    channels.master_command.publish(
        new Message<>(MasterCommand.town_create_team, town, name));
  }

  public static void delegateDistrict(Channels channels, District district, Town town) {
    channels.master_command.publish(new Message<>(MasterCommand.town_delegate_district, town, district).setChatResponse(
        ChatTarget.town_delegate_district,district, town
    ));
  }

  public static void delete(Channels channels, Town town) {
    channels.master_command.publish(new Message<>(MasterCommand.town_delete_town, town));
  }

  public static void deleteDistrict(Channels channels, District arg, Town town) {
    channels.master_command.publish(
        new Message<>(MasterCommand.town_delete_district, town, arg));
  }

  public static void deleteTeam(Channels channels, String name, Town town) {
    channels.master_command.publish(
        new Message<>(MasterCommand.town_delete_team, town, name));
  }

  public static void removeMember(Channels channels, Gamer gamer, Town town) {
    channels.master_command.publish(
        new Message<>(MasterCommand.town_remove_gamer, town, gamer));
  }

  public static void sendInfo(Channels channels, Gamer gamer, Town town){
    channels.chat_message.publish(
        new Message<>(ChatTarget.gamer_town_info, gamer, town));
  }
}

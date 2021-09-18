package etherlandscore.etherlandscore.state.sender;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.ChatTarget;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.state.read.District;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.Team;

public class TeamSender {

  public static void addMember(Channels channels, Gamer gamer, Team team) {
    channels.master_command.publish(new Message<>(MasterCommand.team_add_gamer, team, gamer));
  }

  public static void createDistrict(Channels channels, String name, Team team) {
    channels.master_command.publish(
        new Message<>(MasterCommand.team_create_district, team, name));
  }

  public static void createGroup(Channels channels, String name, Team team) {
    channels.master_command.publish(
        new Message<>(MasterCommand.team_create_group, team, name));
  }

  public static void delegateDistrict(Channels channels, District writeDistrict, Team team, Message message) {
    channels.master_command.publish(
        new Message<>(MasterCommand.team_delegate_district, team, writeDistrict).setChatResponse(ChatTarget.team_delegate_district, message));
  }

  public static void delete(Channels channels, Team team) {
    channels.master_command.publish(new Message<>(MasterCommand.team_delete_team, team));
  }

  public static void deleteDistrict(Channels channels, District arg, Team team) {
    channels.master_command.publish(
        new Message<>(MasterCommand.team_delete_district, team, arg));
  }

  public static void deleteGroup(Channels channels, String name, Team team) {
    channels.master_command.publish(
        new Message<>(MasterCommand.team_delete_group, team, name));
  }

  public static void removeMember(Channels channels, Gamer gamer, Team team) {
    channels.master_command.publish(
        new Message<>(MasterCommand.team_remove_gamer, team, gamer));
  }

  public static void sendInfo(Channels channels, Gamer gamer, Team team){
    channels.chat_message.publish(
        new Message<>(ChatTarget.gamer_team_info, gamer, team));
  }
}

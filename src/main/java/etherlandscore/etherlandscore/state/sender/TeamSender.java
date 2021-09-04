package etherlandscore.etherlandscore.state.sender;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.state.read.District;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.Plot;
import etherlandscore.etherlandscore.state.read.Team;

public class TeamSender {

  public static void addMember(Channels channels, Gamer gamer, Team writeTeam) {
    channels.master_command.publish(new Message<>(MasterCommand.team_add_gamer, writeTeam, gamer));
  }

  public static void createDistrict(Channels channels, String name, Team writeTeam) {
    channels.master_command.publish(
        new Message<>(MasterCommand.team_create_district, writeTeam, name));
  }

  public static void createGroup(Channels channels, String name, Team writeTeam) {
    channels.master_command.publish(
        new Message<>(MasterCommand.team_create_group, writeTeam, name));
  }

  public static void delegateDistrict(Channels channels, District writeDistrict, Team writeTeam) {
    channels.master_command.publish(
        new Message<>(MasterCommand.team_delegate_plot, writeTeam, writeDistrict));
  }

  public static void delete(Channels channels, Team writeTeam) {
    channels.master_command.publish(new Message<>(MasterCommand.team_delete_team, writeTeam));
  }

  public static void deleteDistrict(Channels channels, District arg, Team writeTeam) {
    channels.master_command.publish(
        new Message<>(MasterCommand.team_delete_district, writeTeam, arg));
  }

  public static void deleteGroup(Channels channels, String name, Team writeTeam) {
    channels.master_command.publish(
        new Message<>(MasterCommand.team_delete_group, writeTeam, name));
  }

  public static void removeMember(Channels channels, Gamer gamer, Team writeTeam) {
    channels.master_command.publish(
        new Message<>(MasterCommand.team_remove_gamer, writeTeam, gamer));
  }
}

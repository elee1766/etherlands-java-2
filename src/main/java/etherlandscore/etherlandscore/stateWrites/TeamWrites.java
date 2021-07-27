package etherlandscore.etherlandscore.stateWrites;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.state.*;

public class TeamWrites {

  public static void addMember(Channels channels, Gamer gamer, Team team) {
    channels.master_command.publish(new Message<>(MasterCommand.team_add_gamer, team, gamer));
  }

  public static void createGroup(Channels channels, String name, Team team) {
    channels.master_command.publish(new Message<>(MasterCommand.team_create_group,team, name));
  }

  public static void createRegion(Channels channels, String name, Team team) {
    channels.master_command.publish(new Message<>(MasterCommand.team_create_region,team, name));
  }

  public static void delegatePlot(Channels channels, Plot plot, Team team) {
    channels.master_command.publish(new Message<>(MasterCommand.team_delegate_plot, team, plot));
  }

  public static void delete(Channels channels, Team team) {
    channels.master_command.publish(new Message<>(MasterCommand.team_delete_team, team));
  }

  public static void deleteGroup(Channels channels, String name, Team team) {
    channels.master_command.publish(new Message<>(MasterCommand.team_delete_group,team, name));
  }

  public static void deleteRegion(Channels channels, Region arg, Team team) {
    channels.master_command.publish(new Message<>(MasterCommand.team_delete_region,team,arg));
  }

  public static void removeMember(Channels channels, Gamer gamer, Team team) {
    channels.master_command.publish(new Message<>(MasterCommand.team_remove_gamer, team, gamer));
  }

}

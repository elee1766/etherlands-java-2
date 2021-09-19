package etherlandscore.etherlandscore.state.sender;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.ChatTarget;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.Team;

public class TeamSender {

  public static void addMember(Channels channels, Team team, Gamer gamer) {
    if (team.getDefault()){
      if(!team.getName().equals("manager")){
        return;
      }
    }
    channels.master_command.publish(
        new Message<>(MasterCommand.team_add_gamer, team, gamer));
  }

  public static void removeMember(Channels channels, Team team, Gamer gamer) {
    if (team.getDefault()){
      if(!team.getName().equals("manager")){
        return;
      }
    }
    channels.master_command.publish(
        new Message<>(MasterCommand.team_remove_gamer, team, gamer));
  }


  public static void setPriority(Channels channels, Team team, Integer priority) {
    if (team.getDefault()) return;
    channels.master_command.publish(
        new Message<>(MasterCommand.team_set_priority, team, priority));
  }


  public static void sendTeamInfo(Channels channels, Gamer gamer, Team target) {
    channels.chat_message.publish(
        new Message<>(ChatTarget.gamer_team_info, gamer, target));
  }
}

package etherlandscore.etherlandscore.readonly;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.state.Gamer;
import etherlandscore.etherlandscore.state.Plot;
import etherlandscore.etherlandscore.state.Team;

public class ReadTeam {

  private final Team team;

  public ReadTeam(Team team){
    this.team = team;
  }

  public boolean canAction(ReadGamer manager, ReadGamer subject) {
    return team.canAction(manager.obj(),subject.obj());
  }

  public ReadGroup getGroup(String arg) {
    return new ReadGroup(team.getGroup(arg));
  }

  public boolean isManager(ReadGamer gamer) {
    return team.isManager(gamer.obj());
  }

  @Override
  public String toString(){
    return team.getName();
  }
  public void delegatePlot(Channels channels, ReadPlot plot) {
    channels.master_command.publish(new Message<>(MasterCommand.team_delegate_plot, obj(), plot));
  }

  public Team obj() {
    return team;
  }

  public void delete(Channels channels) {
    channels.master_command.publish(new Message<>(MasterCommand.team_delete_team, obj()));
  }

  public void deleteGroup(Channels channels, String name) {
    channels.master_command.publish(new Message<>(MasterCommand.team_delete_group,obj(), name));
  }

  public void deleteRegion(Channels channels, ReadRegion arg) {
    channels.master_command.publish(new Message<>(MasterCommand.team_delete_region,obj(),arg));
  }

  public void createRegion(Channels channels, String name) {
    channels.master_command.publish(new Message<>(MasterCommand.team_create_region,this, name));
  }

  public void createGroup(Channels channels, String name) {
    channels.master_command.publish(new Message<>(MasterCommand.team_create_group,this, name));
  }
  public void addMember(Channels channels, Gamer gamer) {
    channels.master_command.publish(new Message<>(MasterCommand.team_add_gamer, this, gamer));
  }
}

package etherlandscore.etherlandscore.readonly;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.state.Gamer;

public class ReadGamer{
  private final Gamer gamer;

  public ReadGamer(Gamer gamer){
    this.gamer = gamer;
  }

  public ReadGamer(Object arg) {
    this.gamer = (Gamer) arg;
  }

  public String getTeamName() {
    return gamer.getTeamName();
  }

  public ReadTeam getTeamObject() {
    return new ReadTeam(gamer.getTeamObject());
  }

  public Gamer obj(){
    return this.gamer;
  }

  public void addFriend(Channels channels, ReadGamer gamer) {
    channels.master_command.publish(new Message<>(MasterCommand.gamer_add_friend, this.gamer, gamer.obj()));
  }
  public void removeFriend(Channels channels, ReadGamer newFriend) {
    channels.master_command.publish(
        new Message<>(MasterCommand.gamer_remove_friend, this.obj(), newFriend.obj()));
  }

  public boolean isFriend(ReadGamer friend){
    return gamer.getFriends().contains(friend.obj().getUuid());
  }

  public boolean teamIs(ReadTeam team) {
    return gamer.getTeamName().equals(team.toString());
  }
}

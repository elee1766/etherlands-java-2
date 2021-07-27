package etherlandscore.etherlandscore.readonly;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.state.Group;

public class ReadGroup {

  private final Group group;

  public ReadGroup(Group group){this.group=group;}

  public ReadGroup(Object arg) {
    this.group = (Group) arg;
  }

  public Group obj(){return this.group;}
  public void removeMember(Channels channels, ReadGamer gamer) {
    if(group.isDefault()) return;
    channels.master_command.publish(new Message<>(MasterCommand.group_remove_gamer,obj(), gamer.obj()));
  }
  public void addMember(Channels channels, ReadGamer gamer) {
    if(group.isDefault()) return;
    channels.master_command.publish(new Message<>(MasterCommand.group_add_gamer,obj(), gamer.obj()));
  }
}

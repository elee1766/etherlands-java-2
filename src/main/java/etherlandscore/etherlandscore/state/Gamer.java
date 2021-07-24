package etherlandscore.etherlandscore.state;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static etherlandscore.etherlandscore.services.MasterService.state;

public class Gamer extends StateHolder {

  private final UUID uuid;

  private String team = "";

  private Set<String> groups = new HashSet<>();

  private String address;
  private Set<UUID> friends;

  public Gamer(UUID uuid) {
    this.uuid = uuid;
  }

  public void addFriend(Channels channels, Gamer gamer) {
    channels.master_command.publish(new Message<>(MasterCommand.gamer_add_friend, this, gamer));
  }

  public void removeFriend(Channels channels, Gamer newFriend) {
    channels.master_command.publish(
        new Message<>(MasterCommand.gamer_remove_friend, this, newFriend));
  }

  public void friendList(){
    Set<UUID> flist = this.getFriends();
    StringBuilder friends = new StringBuilder();
    for (UUID value : flist) {
      friends.append(Bukkit.getPlayer(value).getName()).append(", ");
    }
    this.getPlayer().sendMessage(friends.toString());
  }
  public void addFriend(Gamer gamer) {
    friends.add(gamer.getUuid());
  }

  public void removeFriend(Gamer gamer) {
    friends.remove(gamer.getUuid());
  }

  public Set<UUID> getFriends() {
    if (friends == null) {
      friends = new HashSet<>();
    }
    return friends;
  }

  public UUID getUuid() {
    return uuid;
  }

  public String getTeamName() {
    return team;
  }

  public Team getTeamObject(){
    return state().getTeam(team);
  }

  public void setTeam(String team) {
    this.team = team;
  }
  public boolean hasTeam(){
    if(team == null){
      return false;
    }
    return team.equals("");
  }


  public Player getPlayer() {
    return Bukkit.getPlayer(uuid);
  }

  public Group getGroupObject(String name) {
    return state().getTeam(this.getTeamName()).getGroup(name);
  }

  public void setAddress(String address) {
    this.address = address;
  }
  public String getAddress(){
    return address;
  }

  public void removeGroup(String name) {
    groups.remove(name);
  }

  public void addGroup(Group group) {
    groups.add(group.getName());
  }

  public void clearGroups(){
    groups.clear();
  }

  public TextComponent info(){
    TextComponent info = new TextComponent("");
    Field[] fields = this.getClass().getDeclaredFields();
    for(Field field : fields) {
      TextComponent f = new TextComponent("");
      f.addExtra(" ");
      try {
        f.addExtra(field.getName());
        f.addExtra(": ");
        f.addExtra(String.valueOf(field.get(this)));
      } catch (IllegalAccessException ex) {
        System.out.println(ex);
      }
      f.addExtra("\n");
      info.addExtra(f);
    }
    return info;
  }
}

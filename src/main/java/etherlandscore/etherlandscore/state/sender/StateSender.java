package etherlandscore.etherlandscore.state.sender;

import etherlandscore.etherlandscore.actions.PermissionedAction;
import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.enums.FlagValue;
import etherlandscore.etherlandscore.enums.MessageToggles;
import etherlandscore.etherlandscore.enums.ToggleValues;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.ChatTarget;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.state.read.District;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.Team;
import etherlandscore.etherlandscore.state.read.Town;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class StateSender {

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
  public static void sendGamerComponent(Channels channels, Gamer gamer, TextComponent component) {
    channels.chat_message.publish(
        new Message<>(ChatTarget.gamer, gamer, component));
  }
  public static void sendGamerInfo(Channels channels, Gamer gamer, Gamer target) {
    channels.chat_message.publish(
        new Message<>(ChatTarget.gamer_gamer_info, gamer, target));
  }

  public static void sendMap(Channels channels, BaseComponent map, Gamer target) {
    channels.chat_message.publish(
        new Message<>(ChatTarget.gamer_send_map, map, target));
  }

  public static void reclaimDistrict(Channels channels, District district, Gamer sender) {
    channels.master_command.publish(
        new Message<>(MasterCommand.district_reclaim_district, district)
            .setChatResponse(ChatTarget.gamer_district_reclaim, sender, district)
    );
  }

  public static void setGamerPermission(
      Channels channels, Gamer gamer, AccessFlags flag, FlagValue value, District district) {
    channels.master_command.publish(
        new Message<>(
            MasterCommand.district_set_gamer_permission, district, gamer, flag, value)
            .setChatResponse(ChatTarget.gamer_district_info, gamer, district)
    );
  }

  public static void setTeamPermission(
      Channels channels,
      Team team,
      AccessFlags flag,
      FlagValue value,
      District district,
      Gamer gamer
  ) {
    channels.master_command.publish(
        new Message<>(
            MasterCommand.district_set_team_permission, district, team, flag, value)
            .setChatResponse(ChatTarget.gamer_district_info, gamer, district)
    );
  }

  public static void touchDistrict(Channels channels, int i) {
    channels.master_command.publish(new Message<>(MasterCommand.touch_district, i));
  }

  public static void gamerFailAction(Channels channels, PermissionedAction action) {
    channels.chat_message.publish(
        new Message<>(ChatTarget.gamer_fail_action, action));
  }

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

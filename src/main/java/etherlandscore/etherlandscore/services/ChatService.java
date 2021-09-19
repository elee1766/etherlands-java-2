package etherlandscore.etherlandscore.services;

import etherlandscore.etherlandscore.Menus.ComponentCreator;
import etherlandscore.etherlandscore.Menus.MessageCreator;
import etherlandscore.etherlandscore.actions.PermissionedAction;
import etherlandscore.etherlandscore.enums.MessageToggles;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.ChatTarget;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.singleton.RedisGetter;
import etherlandscore.etherlandscore.state.read.District;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.Team;
import etherlandscore.etherlandscore.state.read.Town;
import etherlandscore.etherlandscore.state.sender.GamerSender;
import etherlandscore.etherlandscore.state.write.WriteGamer;
import kotlin.Triple;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.UUID;

import static etherlandscore.etherlandscore.services.MasterService.state;

public class ChatService extends ListenerClient {

  private final Channels channels;


  public ChatService(Channels channels, Fiber fiber){
    super(channels,fiber);
    this.channels = channels;

    this.channels.chat_message.subscribe(fiber,this::process_chat);
  }

  private void process_chat(Message<ChatTarget> message){
    try{
    if(message!=null){
      Bukkit.getLogger().info(message.toString());
        Object[] _args = message.getArgs();
        switch(message.getCommand()) {
          case global -> this.send_global((String) _args[0], (Gamer) _args[1]);
          case local -> this.send_local((Gamer) _args[0], (Integer) _args[1], (String) _args[2], (Gamer) _args[3]);
          case gamer -> this.send_gamer((Gamer) _args[0], (TextComponent) _args[1]);
          case town_delegate_district -> this.town_delegate_district((District) _args[0], (Town) _args[1]);
          case gamer_base -> this.send_gamer_base((Gamer) _args[0], (BaseComponent[]) _args[1]);
          case town -> this.send_town((Town) _args[0], (String) _args[1], (Gamer) _args[2]);
          case gamer_add_friend_response -> this.gamer_add_friend_response((Gamer) _args[0], (Gamer) _args[1]);
          case gamer_district_reclaim -> this.gamer_district_reclaim((Gamer) _args[0], (District) _args[1]);
          case district_touch_district -> this.district_touch_district((CommandSender) _args[0], (Integer) _args[1]);
          case gamer_district_info -> this.gamer_district_info((Gamer) _args[0], (District) _args[1] );
          case gamer_town_info -> this.gamer_town_info((Gamer) _args[0], (Town) _args[1] );
          case gamer_gamer_info -> this.gamer_gamer_info((Gamer) _args[0], (Gamer) _args[1] );
          case gamer_team_info -> this.gamer_team_info((Gamer) _args[0], (Team) _args[1] );
          case gamer_land_unclaimed -> this.gamer_land_unclaimed((Gamer) _args[0]);
          case gamer_send_map -> this.gamer_send_map((TextComponent) _args[0], (Gamer) _args[1]);
          case gamer_fail_action -> this.gamer_fail_action((PermissionedAction) _args[0]);
        }
      }
    }catch(Exception e){
      Bukkit.getLogger().warning("Failed to process ChatMessage" + message.getCommand());
      e.printStackTrace();
    }
  }

  private void gamer_fail_action(PermissionedAction action) {
    if(action.hasFailed()){
        if(action.getGamer() == null){
          return;
        }
      if(action.getDistrict() != null){
        TextComponent component = ComponentCreator.ColoredText("You do not have permission to "+action.getFlag().toString()+" in ", ChatColor.WHITE);
        component.addExtra(ComponentCreator.District(action.getDistrict()));
        GamerSender.sendGamerComponent(
            channels,
            action.getGamer(),
            component
        );
      }else{
        GamerSender.sendGamerComponent(
            channels,
            action.getGamer(),
            ComponentCreator.ColoredText(
                "The Chunk at [" + action.getChunkX()+ ", " + action.getChunkZ() + "] is unclaimed",
                ChatColor.WHITE
            )
        );
      }
    }
  }

  private void town_delegate_district(District district, Town town) {
    Gamer gamer = district.getOwnerObject();
    if(gamer != null && town != null){
      if(district.getTown().equals(town.getName())){
        TextComponent prefix = ComponentCreator.ColoredText("District ", ChatColor.WHITE);
        TextComponent id = ComponentCreator.District(district);
        TextComponent suffix = ComponentCreator.ColoredText(" has been delegated to ", ChatColor.WHITE);
        TextComponent townname = ComponentCreator.Town(town.getName());
        prefix.addExtra(id);
        prefix.addExtra(suffix);
        prefix.addExtra(townname);
        channels.chat_message.publish(new Message<>(ChatTarget.gamer, gamer, prefix));
      }else{
        TextComponent message = ComponentCreator.ColoredText("Error delegating district!",ChatColor.YELLOW);
        channels.chat_message.publish(new Message<>(ChatTarget.gamer, gamer, message));
      }
    }
  }

  private void gamer_send_map(TextComponent map, Gamer gamer) {
    Player player = gamer.getPlayer();
    if(player != null){
      player.sendMessage(map);
    }
  }

  private void gamer_team_info(Gamer gamer, Team team) {
    if(team == null){
      TextComponent message = ComponentCreator.ColoredText("Team not found",ChatColor.YELLOW);
      channels.chat_message.publish(new Message<>(ChatTarget.gamer, gamer, message));
      return;
    }
    MessageCreator builder = new MessageCreator();
    TextComponent title = ComponentCreator.Team(team.getName());
    builder.addHeader(title);
    builder.addField("team",ComponentCreator.Team(team.getName()));
    builder.addField("town",ComponentCreator.Town(team.getTownObject().getName()));
    Bukkit.getLogger().info("team size:" + team.getMembers().size());
    builder.addField("members",ComponentCreator.UUIDs(team.getMembers()));
    builder.addField("priority",ComponentCreator.ColoredText(team.getPriority().toString(), ChatColor.GRAY));
    builder.addFooter();
    builder.finish();
    channels.chat_message.publish(new Message<>(ChatTarget.gamer_base, gamer, builder.getMessage()));
  }

  private void gamer_town_info(Gamer gamer, Town target) {
    if(target == null){
      TextComponent message = ComponentCreator.ColoredText("You are not in a town",ChatColor.YELLOW);
      channels.chat_message.publish(new Message<>(ChatTarget.gamer, gamer, message));
      return;
    }
    MessageCreator builder = new MessageCreator();
    TextComponent title = ComponentCreator.Town(target.getName());
    builder.addHeader(title);
    builder.addField("town",ComponentCreator.Town(target.getName()));
    builder.addField("owner",ComponentCreator.UUID(target.getOwnerUUID(),ChatColor.GOLD));
    builder.addField("members",ComponentCreator.UUIDs(target.getMembers()));
    builder.addField("districts",ComponentCreator.Districts(target.getDistrictObjects()));
    builder.addField("teams",ComponentCreator.Teams(target.getTeams().keySet()));
    builder.finish();

    channels.chat_message.publish(new Message<>(ChatTarget.gamer_base, gamer, builder.getMessage()));
  }

  private void gamer_gamer_info(Gamer gamer, Gamer target) {
    if(target == null){
      TextComponent message = ComponentCreator.ColoredText("Could not locate player",ChatColor.YELLOW);
      channels.chat_message.publish(new Message<>(ChatTarget.gamer, gamer, message));
      return;
    }
    MessageCreator builder = new MessageCreator();
    TextComponent title = ComponentCreator.UUID(target.getUuid());
    builder.addHeader(title);
    builder.addField("nickname",ComponentCreator.UUID(target.getUuid(),ChatColor.GOLD));
    builder.addField("address",ComponentCreator.Address(target.getAddress()));
    if(target.hasTown()){
      builder.addField("town",ComponentCreator.Town(target.getTown()));
      builder.addField("teams",ComponentCreator.Teams(target.getTeams()));
    }
    if(target.getFriends().size() > 0){
      builder.addField("friends",ComponentCreator.UUIDs(target.getFriends()));
    }
    if(!target.getUuid().equals(gamer.getUuid())){
      TextComponent component = new TextComponent("Click Here!");
      if(gamer.hasFriend(target.getUuid())){
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend remove "+target.getName()));
        component.setColor(ChatColor.DARK_RED);
        builder.addField(ComponentCreator.ColoredText("remove friend", ChatColor.GRAY),component);
      }else{
        component.setColor(ChatColor.GREEN);
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend add "+target.getName()));
        builder.addField(ComponentCreator.ColoredText("add friend", ChatColor.GRAY),component);
      }
    }
    builder.finish();
    channels.chat_message.publish(new Message<>(ChatTarget.gamer_base, gamer, builder.getMessage()));
  }

  private void gamer_land_unclaimed(Gamer gamer) {
    TextComponent message = ComponentCreator.ColoredText("This Land is Unclaimed",ChatColor.YELLOW);
    channels.chat_message.publish(new Message<>(ChatTarget.gamer, gamer, message));
  }

  private void gamer_district_info(Gamer gamer, District district) {
    if(district == null){
      gamer_land_unclaimed(gamer);
      return;
    }
    ArrayList<Triple<Integer, Integer, Integer>> clusters = RedisGetter.ClustersOfDistrict(district.getIdInt());
    MessageCreator builder = new MessageCreator();
    TextComponent title = ComponentCreator.ColoredText(district.getNickname(),ChatColor.DARK_GREEN);
    builder.addHeader(title);
    builder.addField("nickname",ComponentCreator.ColoredText(district.getNickname(),ChatColor.DARK_GREEN));
    builder.addField("owner",ComponentCreator.UUID(district.getOwnerUUID(),ChatColor.GOLD));
    builder.addField("address",ComponentCreator.Address(district.getOwnerAddress()));
    if(district.hasTown()){
      builder.addField("town",ComponentCreator.Town(district.getTown()));
      LinkedHashSet<String> all_teams = new LinkedHashSet<>();
      all_teams.add("member");
      all_teams.add("outsiders");
      all_teams.addAll(district.getTownObject().getTeams().keySet());
      all_teams.remove("manager");
      builder.addBody("team permissions",ComponentCreator.TeamPermissions(district.getTeamPermissionMap(),district.getIdInt(),all_teams.stream().toList()));
      if(district.getGamerPermissionMap().getMap().size() > 0){
        builder.addBody("gamer permissions",ComponentCreator.GamerPermissions(district.getGamerPermissionMap(),district.getIdInt()));
      }
    }
    if(clusters != null){
      builder.addField("location",ComponentCreator.Clusters(clusters));
    }
    builder.finish();
    channels.chat_message.publish(new Message<>(ChatTarget.gamer_base, gamer, builder.getMessage()));
  }

  private void district_touch_district(CommandSender player, Integer id) {
    if(state().getDistrict(id)==null){
      player.sendMessage("District does not exist");
    }else{
      player.sendMessage("District: " + id + "  has been updated");
    }
  }

  private void gamer_district_reclaim(Gamer gamer, District district) {
    TextComponent message;
    if(!district.hasTown()){
      message= ComponentCreator.ColoredText("You have reclaimed ",ChatColor.WHITE);
      message.addExtra(ComponentCreator.District(district));
    }else{
      message= ComponentCreator.ColoredText("Failed to reclaim district",ChatColor.RED);
    }
    channels.chat_message.publish(new Message<>(ChatTarget.gamer, gamer, message));
  }

  private void gamer_add_friend_response(Gamer arg, Gamer arg1) {
    if(arg.hasFriend(arg1.getUuid())){
      arg.getPlayer().sendMessage("You are now friends with " + arg1.getName());
    }else{
      arg.getPlayer().sendMessage("Friend failed to be added");
    }
  }

  private void send_global(String message, Gamer arg){
    Bukkit.getLogger().info("Sending global message");
    TextComponent combined = new TextComponent("");
    TextComponent prefix = new TextComponent("[g]");
    TextComponent name = new TextComponent(arg.getPlayer().getName());
    name.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new Text("[Click to see info]")));
    name.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/gamer info "+arg.getPlayer().getName()));
    TextComponent at = new TextComponent("@");
    at.setColor(ChatColor.RED);
    TextComponent town = new TextComponent("");
    name.setColor(ChatColor.WHITE);
    prefix.setColor(ChatColor.GOLD);
    combined.addExtra(prefix);
    combined.addExtra(name);
    if(arg.hasTown()){
      town.addExtra(arg.getTown());
      town.setColor(ChatColor.DARK_GRAY);
      town.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new Text("[Click to see info]")));
      town.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/town info "+arg.getTown()));
      combined.addExtra(at);
      combined.addExtra(town);
    }
    TextComponent messageComp = new TextComponent(message);
    TextComponent carrot;
    if(message.startsWith(">")){
      carrot = new TextComponent(" >");
      carrot.setColor(ChatColor.DARK_GREEN);
      message=(message.substring(1));
      messageComp.setText(message);
      messageComp.setColor(ChatColor.DARK_GREEN);
    }else{
      carrot = new TextComponent(" > ");
    }
    combined.addExtra(carrot);
    combined.addExtra(messageComp);
    for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
      WriteGamer gamer = (WriteGamer) state().getGamer(onlinePlayer.getUniqueId());
      if(gamer.preferences.checkPreference(MessageToggles.GLOBAL_CHAT)){
        onlinePlayer.sendMessage(combined);
      }
    }
  }
  private void send_town(Town town, String message, Gamer arg){
    if(town == null){
      arg.getPlayer().sendMessage("you are not in a town");
      return;
    }
    Bukkit.getLogger().info("Sending town message");
    String town_name = town.getName();
    if(town_name.length()>12){
      town_name = town_name.substring(0, 11);
    }
    TextComponent combined = new TextComponent("");
    TextComponent prefix = new TextComponent("["+ town_name +"]");
    prefix.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new Text("[Click to see info]")));
    prefix.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/town info "+town.getName()));
    TextComponent name = new TextComponent(arg.getPlayer().getName());
    name.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new Text("[Click to see info]")));
    name.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/gamer info "+arg.getPlayer().getName()));
    name.setColor(ChatColor.WHITE);
    prefix.setColor(ChatColor.AQUA);
    combined.addExtra(prefix);
    combined.addExtra(name);
    TextComponent messageComp = new TextComponent(message);
    TextComponent carrot;
    if(message.startsWith(">")){
      carrot = new TextComponent(" >");
      carrot.setColor(ChatColor.DARK_GREEN);
      message=(message.substring(1));
      messageComp.setText(message);
      messageComp.setColor(ChatColor.DARK_GREEN);
    }else{
      carrot = new TextComponent(" > ");
    }
    combined.addExtra(carrot);
    combined.addExtra(messageComp);
    Player owner = Bukkit.getPlayer(town.getOwnerUUID());
    if(owner!=null){
      owner.sendMessage(combined);
    }
    for (UUID member : town.getMembers()) {
      Player player = Bukkit.getServer().getPlayer(member);
      if(player != null){
        player.sendMessage(combined);
      }
    }
  }
  private void send_local(Gamer gamer, Integer range, String message, Gamer arg){
    Bukkit.getLogger().info("Sending local message");
    TextComponent local = new TextComponent("[L] ");
    local.setColor(ChatColor.LIGHT_PURPLE);
    local.addExtra(message);
    Player player = gamer.getPlayer();
    for(Player check : Bukkit.getOnlinePlayers()){
      Location checkLocal = state().getGamerLocation(state().getGamer(check.getUniqueId()));
      if(Math.abs(checkLocal.getX()-player.getLocation().getX())<range && Math.abs(checkLocal.getX()-player.getLocation().getX())<range){
        check.sendMessage(local);
      }
    }
  }
  private void send_gamer(Gamer gamer,TextComponent message) {
    Player player = gamer.getPlayer();
    if(player != null){
      player.sendMessage(message);
    }
  }
  private void send_gamer_base(Gamer gamer,BaseComponent[] message) {
    Player player = gamer.getPlayer();
    if(player != null){
      player.sendMessage(message);
    }
  }


}

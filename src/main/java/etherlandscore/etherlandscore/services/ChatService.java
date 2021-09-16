package etherlandscore.etherlandscore.services;

import etherlandscore.etherlandscore.Menus.ComponentCreator;
import etherlandscore.etherlandscore.Menus.MessageCreator;
import etherlandscore.etherlandscore.enums.MessageToggles;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.ChatTarget;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.state.read.District;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.Team;
import etherlandscore.etherlandscore.state.write.WriteGamer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

import java.util.Collection;
import java.util.UUID;

public class ChatService extends ListenerClient {

  private final Channels channels;
  private final Fiber fiber;


  public ChatService(Channels channels, Fiber fiber){
    super(channels,fiber);
    this.channels = channels;
    this.fiber = fiber;

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
          case gamer_base -> this.send_gamer_base((Gamer) _args[0], (BaseComponent[]) _args[1]);
          case team -> this.send_team((Team) _args[0], (String) _args[1], (Gamer) _args[2]);

          case gamer_add_friend_response -> this.gamer_add_friend_response((Gamer) _args[0], (Gamer) _args[1]);
          case gamer_distric_reclaim -> this.gamer_district_reclaim((Player) _args[0], (District) _args[1]);
          case district_touch_district -> this.district_touch_district((CommandSender) _args[0], (Integer) _args[1]);
          case gamer_district_info -> this.gamer_district_info((Gamer) _args[0], (District) _args[1] );
          case gamer_land_unclaimed -> this.gamer_land_unclaimed((Gamer) _args[0]);
        }
      }
    }catch(Exception e){
      Bukkit.getLogger().warning("Failed to process ChatMessage" + message.getCommand());
      e.printStackTrace();
    }
  }

  private void gamer_land_unclaimed(Gamer gamer) {
    TextComponent message = ComponentCreator.ColoredText("This Land is Unclaimed",ChatColor.YELLOW);
    channels.chat_message.publish(new Message<>(ChatTarget.gamer_base, gamer, message));
  }

  private void gamer_district_info(Gamer gamer, District district) {
    if(district == null){
      gamer_land_unclaimed(gamer);
      return;
    }
    MessageCreator builder = new MessageCreator();
    TextComponent title = ComponentCreator.ColoredText("District: " +district.getIdInt(),ChatColor.DARK_GREEN);
    builder.addHeader(title);
    builder.addField("owner",ComponentCreator.Address(district.getOwnerAddress()));
    builder.addField("plots",ComponentCreator.Plots(district.getPlots()));
    if(district.hasTeam()){
      builder.addField("team",ComponentCreator.Team(district.getTeamObject()));
      builder.addBody("group permissions",ComponentCreator.GroupPermissions(district.getGroupPermissionMap(),district.getIdInt()));
      if(district.getGamerPermissionMap().getMap().size() > 0){
        builder.addBody("gamer permissions",ComponentCreator.GamerPermissions(district.getGamerPermissionMap(),district.getIdInt()));
      }
    }
    builder.finish();
    channels.chat_message.publish(new Message<>(ChatTarget.gamer_base, gamer, builder.getMessage()));
  }

  private void district_touch_district(CommandSender player, Integer id) {
    if(context.getDistrict(id)==null){
      player.sendMessage("District does not exist");
    }else{
      player.sendMessage("District: " + id+ "  has been updated");
    }
  }

  private void gamer_district_reclaim(Player arg, District district) {
    if(district.hasTeam()){
      arg.sendMessage("District " + district.getIdInt() + " has been reclaimed");
    }else{
      arg.sendMessage("Reclaim has failed");
    }
  }

  private void gamer_add_friend_response(Gamer arg, Gamer arg1) {
    if(arg.hasFriend(arg1.getPlayer())){
      arg.getPlayer().sendMessage("You are now friends with " + arg1.getPlayer().getName());
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
    TextComponent team = new TextComponent("");
    name.setColor(ChatColor.WHITE);
    prefix.setColor(ChatColor.GOLD);
    combined.addExtra(prefix);
    combined.addExtra(name);
    if(arg.hasTeam()){
      team.addExtra(arg.getTeam());
      team.setColor(ChatColor.DARK_GRAY);
      team.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new Text("[Click to see info]")));
      team.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/team info "+arg.getTeam()));
      combined.addExtra(at);
      combined.addExtra(team);
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
      WriteGamer gamer = (WriteGamer) context.getGamer(onlinePlayer.getUniqueId());
      if(gamer.preferences.checkPreference(MessageToggles.GLOBAL_CHAT)){
        onlinePlayer.sendMessage(combined);
      }
    }
  }
  private void send_team(Team team, String message, Gamer arg){
    if(team == null){
      arg.getPlayer().sendMessage("you are not in a team");
      return;
    }
    Bukkit.getLogger().info("Sending team message");
    String teamname = team.getName();
    if(teamname.length()>12){
      teamname = teamname.substring(0, 11);
    }
    TextComponent combined = new TextComponent("");
    TextComponent prefix = new TextComponent("["+teamname+"]");
    prefix.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new Text("[Click to see info]")));
    prefix.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/team info "+team.getName()));
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
    Player owner = Bukkit.getPlayer(team.getOwnerUUID());
    if(owner!=null){
      owner.sendMessage(combined);
    }
    for (UUID member : team.getMembers()) {
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
    player.sendMessage(local);
    if(player != null){
      Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("EtherlandsCore"), new Runnable() {
        @Override
        public void run() {
          Collection<Entity> entities = player.getNearbyEntities(range,range,range);
          for (Entity entity : entities) {
            if(entity.getType() == EntityType.PLAYER){
              entity.sendMessage(local);
            }
          }
        }
      });
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

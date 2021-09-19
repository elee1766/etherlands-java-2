package etherlandscore.etherlandscore.Menus;

import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.enums.FlagValue;
import etherlandscore.etherlandscore.singleton.RedisGetter;
import etherlandscore.etherlandscore.state.read.District;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.util.Map2;
import kotlin.Triple;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

import java.util.*;

import static etherlandscore.etherlandscore.services.MasterService.state;

public class ComponentCreator {


  public static TextComponent ColoredText(String string, ChatColor colour) {
    TextComponent component = new TextComponent(string);
    component.setColor(colour);
    return component;
  }

  public static TextComponent Address(String string) {
    TextComponent component = new TextComponent(abbreviate(string));
    component.setColor(ChatColor.GRAY);
    component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new Text(string)));
    component.setClickEvent(
        new ClickEvent(ClickEvent.Action.OPEN_URL,
            "https://polygonscan.com/address/" + string
        )
    );
    return component;
  }

  public static TextComponent Districts(Set<District> districts){
    return ComponentCreator.Districts(districts, ChatColor.DARK_GREEN);
  }
  public static TextComponent Districts(Set<District> districts, ChatColor colour) {
    TextComponent combined = new TextComponent();
    for(District district : districts){
      TextComponent component = ComponentCreator.District(district, colour);
      combined.addExtra(component);
      combined.addExtra(" ");
    }
    return combined;
  }

  public static TextComponent District(District district){
    return ComponentCreator.District(district,ChatColor.DARK_GREEN);
  }
  public static TextComponent District(District district, ChatColor colour) {
    TextComponent component = ComponentCreator.ColoredText(district.getNickname(), colour);
    component.setClickEvent(
        new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/district info " + district.getIdInt()));
    return component;
  }
  public static TextComponent Teams(Set<String> teamObjects){
    return ComponentCreator.Teams(teamObjects,ChatColor.BLUE);
  }
  public static TextComponent Teams(Set<String> teamObjects,ChatColor colour) {
    teamObjects.remove(null);
    TextComponent combined = new TextComponent();
    for(String team : teamObjects){
      TextComponent component = ComponentCreator.Team(team, colour);
      combined.addExtra(component);
      combined.addExtra(" ");
    }
    return combined;
  }


  public static TextComponent Cluster(Triple<Integer,Integer,Integer> cluster, ChatColor color){
    String x = cluster.getFirst().toString();
    String z = cluster.getSecond().toString();
    String size = cluster.getThird().toString();

    TextComponent component = new TextComponent();
    TextComponent prefix = ComponentCreator.ColoredText("[",ChatColor.DARK_GRAY);
    TextComponent pre_body =  ComponentCreator.ColoredText(size,color);
    TextComponent body = ComponentCreator.ColoredText("@" + x +","+z,color);
    TextComponent postfix= ComponentCreator.ColoredText("]",ChatColor.DARK_GRAY);

    component.addExtra(prefix);
    component.addExtra(pre_body);
    component.addExtra(body);
    component.addExtra(postfix);

    component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
        "/map coord " + x + " " + z ));
    return component;
  }

  public static TextComponent Clusters(ArrayList<Triple<Integer, Integer, Integer>> clusters,ChatColor color) {
    TextComponent combined = new TextComponent();
    for(Triple<Integer,Integer,Integer> cluster : clusters){
      TextComponent component = ComponentCreator.Cluster(cluster, color);
      combined.addExtra(component);
      combined.addExtra("  ");
    }
    return combined;
  }

  public static TextComponent Clusters(ArrayList<Triple<Integer, Integer, Integer>> clusters) {
    return Clusters(clusters,ChatColor.AQUA);
  }

  public static TextComponent Team(String team){
    return ComponentCreator.Team(team,ChatColor.BLUE);
  }
  public static TextComponent Team(String team, ChatColor colour) {
    TextComponent component = ComponentCreator.ColoredText(team,colour);
    component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/team info "+team));
    return component;
  }

  public static TextComponent Plots(Set<Integer> plots) {
    TextComponent combined = new TextComponent();
    for(Integer plot : plots){
      TextComponent component = ComponentCreator.ColoredText(String.valueOf(plot), ChatColor.BLUE);
      component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new Text("("+ RedisGetter.GetPlotX(plot) + ", " + RedisGetter.GetPlotZ(plot)+")")));
      combined.addExtra(component);
      combined.addExtra(" ");
    }
    return combined;
  }

  public static TextComponent Town(String town) {
    return ComponentCreator.Town(town,ChatColor.RED);
  }
  public static TextComponent Town(String town, ChatColor color){
    TextComponent component = ComponentCreator.ColoredText(town,color);
    component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/town info "+town));
    return component;
  }
  public static TextComponent UUIDs(Set<UUID> members) {
    return ComponentCreator.UUIDs(members, ChatColor.DARK_AQUA);
  }
  public static TextComponent UUIDs(Set<UUID> members, ChatColor colour) {
    TextComponent combined = new TextComponent();
    for(UUID id : members){
      TextComponent component = ComponentCreator.UUID(id, colour);
      combined.addExtra(component);
      combined.addExtra(" ");
    }
    return combined;
  }
  public static TextComponent UUID(UUID id) {
    return ComponentCreator.UUID(id,ChatColor.DARK_AQUA);
  }
  public static TextComponent UUID(UUID id, ChatColor colour) {
    Gamer gamer = state().getGamer(id);
    if(gamer == null){
      return ComponentCreator.ColoredText("[Unlinked]",colour);
    }
    if(gamer.getName() == null){
      return ComponentCreator.ColoredText("[Unlinked]",colour);
    }
    TextComponent component = ComponentCreator.ColoredText(gamer.getName(),colour);
    component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/gamer "+ gamer.getName()));
    return component;
  }

  public static TextComponent TeamPermissions(Map2<String,AccessFlags,FlagValue> perms, Integer district, List<String> teams){
    TextComponent combined = new TextComponent();

    int current_line = 0;
    for(String team: teams){
      TextComponent component =  ComponentCreator.Team(team);
      component.addExtra("-");
      TextComponent display =FlagDisplay(perms.getMap().get(team),"team",team, district);
      combined.addExtra(component);
      combined.addExtra(display);
      if(current_line > 40){
        current_line=0;
        combined.addExtra("\n");
      }else{
        combined.addExtra("      ");
        current_line = current_line + component.getText().length();
      }
    }
    return combined;
  }


  public static TextComponent GamerPermissions(Map2<UUID,AccessFlags,FlagValue> perms, Integer district){
    TextComponent combined = new TextComponent();

    int current_line = 0;
    for(UUID uuid: perms.getMap().keySet()){
      TextComponent component = ComponentCreator.UUID(uuid);
      String name = state().getGamer(uuid).getName();
      component.addExtra(ColoredText(": ",ChatColor.BLUE));
      component.addExtra(FlagDisplay(perms.getMap().get(uuid),"gamer", name, district ));
      combined.addExtra(component);
      if(current_line > 40){
        combined.addExtra("\n");
      }else{
        combined.addExtra("      ");
        current_line = current_line + component.getText().length();
      }
    }
    return combined;
  }

  public static TextComponent FlagDisplay(Map<AccessFlags, FlagValue> permissions,String type,String target, Integer district){
    if(permissions == null){
      permissions = new HashMap<>();
    }
    TextComponent component = new TextComponent();
    TextComponent d = createPermissionLetter("DESTROY",permissions.getOrDefault(AccessFlags.DESTROY,FlagValue.NONE),type,target,district);
    TextComponent b = createPermissionLetter("BUILD", permissions.getOrDefault(AccessFlags.BUILD, FlagValue.NONE),type,target,district);
    TextComponent i = createPermissionLetter("INTERACT", permissions.getOrDefault(AccessFlags.INTERACT, FlagValue.NONE),type,target,district);
    TextComponent s = createPermissionLetter("SWITCH", permissions.getOrDefault(AccessFlags.SWITCH, FlagValue.NONE),type,target,district);
    TextComponent prefix = ComponentCreator.ColoredText("[",ChatColor.DARK_GRAY);
    TextComponent postfix= ComponentCreator.ColoredText("]",ChatColor.DARK_GRAY);
    component.addExtra(prefix);
    component.addExtra(d);
    component.addExtra(b);
    component.addExtra(i);
    component.addExtra(s);
    component.addExtra(postfix);
    return component;
  }

  private static TextComponent createPermissionLetter(String flag, FlagValue value, String type, String target, Integer district){
    TextComponent component = new TextComponent();
    if(value.equals(FlagValue.ALLOW)){
      component.setText(String.valueOf(flag.charAt(0)));
      component.setColor(ChatColor.GREEN);
      component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/district "+district + " " + "set_" + type +" "+ target + " " + flag + " DENY"  ));
    }else if(value.equals(FlagValue.NONE)){
      component.setText("-");
      component.setColor(ChatColor.DARK_GRAY);
      component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/district "+district + " " + "set_" + type +" "+ target + " " + flag + " ALLOW"  ));
    }else if(value.equals(FlagValue.DENY)){
      component.setText(String.valueOf(flag.charAt(0)));
      component.setColor(ChatColor.DARK_RED);
      component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/district "+district + " " + "set_" + type +" "+ target + " " + flag + " NONE"  ));
    }
    return component;
  }


  private static String abbreviate(String value) {
    if(value == null){
      return "";
    }
    if(value.length() < 16){
      return value;
    }
    return value.substring(0, 10 / 2 - 2) + ".." + value.substring(value.length() - 10 / 4);
  }
}
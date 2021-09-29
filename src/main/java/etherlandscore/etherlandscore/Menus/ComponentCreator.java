package etherlandscore.etherlandscore.Menus;

import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.enums.FlagValue;
import etherlandscore.etherlandscore.singleton.WorldAsker;
import etherlandscore.etherlandscore.state.District;
import etherlandscore.etherlandscore.state.Gamer;
import etherlandscore.etherlandscore.state.Town;
import etherlandscore.etherlandscore.util.Map2;
import kotlin.Pair;
import kotlin.Triple;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

import java.util.*;

import static etherlandscore.etherlandscore.services.MasterService.state;

public class ComponentCreator {

  public static TextComponent AddHyperlinks(String target){

    boolean active = false;
    int count = 0;
    List<List<Character>> text_parts = new ArrayList<>();
    text_parts.add(new ArrayList<>());
    for (Character c : target.toCharArray()) {
      if(active){
        if(c == ']'){
          count = count + 1;
          text_parts.add(new ArrayList<>());
          active = false;
        }else{
          text_parts.get(count).add(c);
        }
      }else{
        if(c == '['){
          count = count + 1;
          text_parts.add(new ArrayList<>());
          active = true;
        }else{
          text_parts.get(count).add(c);
        }
      }
    }

    List<String> phrases = new ArrayList<>();
    for (List<Character> text_part : text_parts) {
      StringBuilder builder = new StringBuilder();
      for (Character character : text_part) {
        builder.append(character);
      }
      phrases.add(builder.toString());
    }
    TextComponent component = new TextComponent();

    for (String phrase : phrases) {
      String[] split = phrase.split("\\.");
      String s = split[0];
      switch(s) {
        case "uuid":
          if(split.length == 2){
            UUID uuid = UUID.fromString(split[1]);
            component.addExtra(UUID(uuid));
          }
          break;
        case "district":
          if(split.length == 2){
            Integer district_id= Integer.parseInt(split[1]);
            component.addExtra(District(new District(district_id)));
          }
          break;
        case "town":
          if(split.length == 2){
            component.addExtra(Town(split[1]));
          }
          break;
        case "team":
          if(split.length == 2){
            component.addExtra(Team(split[1]));
          }
          break;
        case "invite":
          if(split.length == 2){
            component.addExtra(TownInvite(split[1]));
          }
          break;
        case "Error":
          component.addExtra(ColoredText("Error:",ChatColor.DARK_RED));
          break;
        default:
          component.addExtra(phrase);
          break;
      }
    }
    return component;
  }

  private static String TownInvite(String s) {
    TextComponent component = Town(s);
    component.addExtra(ColoredText(" [Click Here]",ChatColor.LIGHT_PURPLE));
    component.addExtra(ColoredText(" or type `/team join "+"s"+"`",ChatColor.WHITE));
    component.setClickEvent(
        new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/team join " + s));
    return null;
  }

  public static TextComponent Address(String string) {
    TextComponent component = new TextComponent(abbreviate(string));
    component.setColor(ChatColor.GRAY);
    component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(string)));
    component.setClickEvent(
        new ClickEvent(ClickEvent.Action.OPEN_URL, "https://polygonscan.com/address/" + string));
    return component;
  }

  public static TextComponent Cluster(Triple<Integer, Integer, Integer> cluster, ChatColor color) {
    String x = cluster.getFirst().toString();
    String z = cluster.getSecond().toString();
    String size = cluster.getThird().toString();

    TextComponent component = new TextComponent();
    TextComponent prefix = ComponentCreator.ColoredText("[", ChatColor.DARK_GRAY);
    TextComponent pre_body = ComponentCreator.ColoredText(size, color);
    TextComponent body = ComponentCreator.ColoredText("@" + x + "," + z, color);
    TextComponent postfix = ComponentCreator.ColoredText("]", ChatColor.DARK_GRAY);

    component.addExtra(prefix);
    component.addExtra(pre_body);
    component.addExtra(body);
    component.addExtra(postfix);

    component.setClickEvent(
        new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/map coord " + x + " " + z));
    return component;
  }

  public static TextComponent Clusters(
      ArrayList<Triple<Integer, Integer, Integer>> clusters, ChatColor color) {
    TextComponent combined = new TextComponent();
    for (Triple<Integer, Integer, Integer> cluster : clusters) {
      TextComponent component = ComponentCreator.Cluster(cluster, color);
      combined.addExtra(component);
      combined.addExtra("  ");
    }
    return combined;
  }

  public static TextComponent Clusters(ArrayList<Triple<Integer, Integer, Integer>> clusters) {
    return Clusters(clusters, ChatColor.AQUA);
  }

  public static TextComponent ColoredText(String string, ChatColor colour) {
    TextComponent component = new TextComponent(string);
    component.setColor(colour);
    return component;
  }

  public static TextComponent District(District district) {
    return ComponentCreator.District(district, ChatColor.DARK_GREEN);
  }

  public static TextComponent District(District district, ChatColor colour) {
    TextComponent component = ComponentCreator.ColoredText(district.getNickname(), colour);
    component.setClickEvent(
        new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/district " + district.getIdInt()));
    return component;
  }

  public static TextComponent Districts(Set<District> districts) {
    return ComponentCreator.Districts(districts, ChatColor.DARK_GREEN);
  }

  public static TextComponent Districts(Set<District> districts, ChatColor colour) {
    TextComponent combined = new TextComponent();
    for (District district : districts) {
      TextComponent component = ComponentCreator.District(district, colour);
      combined.addExtra(component);
      combined.addExtra(" ");
    }
    return combined;
  }



  private static ChatColor GetPermissionColor(
      FlagValue flag
  ){
    switch (flag) {
      case NONE -> {
        return ChatColor.DARK_GRAY;
      }
      case ALLOW -> {
        return ChatColor.GREEN;
      }
      case DENY -> {
        return ChatColor.RED;
      }
    }
    return ChatColor.DARK_GRAY;
  }

  public static TextComponent FlagDisplay(
      Pair<Map<AccessFlags, FlagValue>,Map<AccessFlags, FlagValue>> permissions, String type, String target, Integer district) {
    if (permissions == null) {
      permissions = new Pair<>(new HashMap<>(),new HashMap<>());
    }
    TextComponent component = new TextComponent();
    ChatColor destroyColor =
        GetPermissionColor(permissions.getSecond().getOrDefault(AccessFlags.DESTROY, FlagValue.NONE));
    ChatColor buildColor = GetPermissionColor(permissions.getSecond().getOrDefault(AccessFlags.BUILD, FlagValue.NONE));
    ChatColor interactColor = GetPermissionColor(permissions.getSecond().getOrDefault(AccessFlags.INTERACT, FlagValue.NONE));
    ChatColor switchColor = GetPermissionColor(permissions.getSecond().getOrDefault(AccessFlags.SWITCH, FlagValue.NONE));
    TextComponent d =
        createPermissionLetter(
            "DESTROY",
            permissions.getFirst().getOrDefault(AccessFlags.DESTROY, FlagValue.NONE),
            type,
            target,
            district,
            destroyColor
        );
    TextComponent b =
        createPermissionLetter(
            "BUILD",
            permissions.getFirst().getOrDefault(AccessFlags.BUILD, FlagValue.NONE),
            type,
            target,
            district,
            buildColor);
    TextComponent i =
        createPermissionLetter(
            "INTERACT",
            permissions.getFirst().getOrDefault(AccessFlags.INTERACT, FlagValue.NONE),
            type,
            target,
            district,
            interactColor);
    TextComponent s =
        createPermissionLetter(
            "SWITCH",
            permissions.getFirst().getOrDefault(AccessFlags.SWITCH, FlagValue.NONE),
            type,
            target,
            district,
            switchColor);
    TextComponent prefix = ComponentCreator.ColoredText("[", ChatColor.DARK_GRAY);
    TextComponent postfix = ComponentCreator.ColoredText("]", ChatColor.DARK_GRAY);
    component.addExtra(prefix);
    component.addExtra(d);
    component.addExtra(b);
    component.addExtra(i);
    component.addExtra(s);
    component.addExtra(postfix);
    return component;
  }

  public static TextComponent GamerPermissions(
      Map2<UUID, AccessFlags, FlagValue> perms, Integer district) {
    TextComponent combined = new TextComponent();

    int current_line = 0;
    for (UUID uuid : perms.getMap().keySet()) {
      TextComponent component = ComponentCreator.UUID(uuid);
      String name = state().getGamer(uuid).getName();
      component.addExtra(ColoredText(": ", ChatColor.BLUE));
      component.addExtra(FlagDisplay(new Pair<>(perms.getMap().get(uuid), new HashMap<>()), "gamer", name, district));
      combined.addExtra(component);
      if (current_line > 40) {
        combined.addExtra("\n");
      } else {
        combined.addExtra("      ");
        current_line = current_line + component.getText().length();
      }
    }
    return combined;
  }

  public static TextComponent Plots(Set<Integer> plots) {
    TextComponent combined = new TextComponent();
    for (Integer plot : plots) {
      TextComponent component = ComponentCreator.ColoredText(String.valueOf(plot), ChatColor.BLUE);
      component.setHoverEvent(
          new HoverEvent(
              HoverEvent.Action.SHOW_TEXT,
              new Text("(" + WorldAsker.GetPlotX(plot) + ", " + WorldAsker.GetPlotZ(plot) + ")")));
      combined.addExtra(component);
      combined.addExtra(" ");
    }
    return combined;
  }

  public static TextComponent Team(String team) {
    return ComponentCreator.Team(team, ChatColor.BLUE);
  }

  public static TextComponent Team(String team, ChatColor colour) {
    TextComponent component = ComponentCreator.ColoredText(team, colour);
    component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/team " + team));
    return component;
  }

  public static TextComponent TeamPermissions(
      String town, Integer district, List<String> teams) {
    TextComponent combined = new TextComponent();
    Town townObj = new Town(town);
    int current_line = 0;
    for (String team : teams) {
      TextComponent component = ComponentCreator.Team(team);
      component.addExtra("-");
      TextComponent display = FlagDisplay(townObj.getPermissions(team,district), "team", team, district);
      combined.addExtra(component);
      combined.addExtra(display);
      if (current_line > 40) {
        current_line = 0;
        combined.addExtra("\n");
      } else {
        combined.addExtra("   ");
        current_line = current_line + component.getText().length();
      }
    }
    return combined;
  }

  public static TextComponent Teams(Set<String> teamObjects) {
    return ComponentCreator.Teams(teamObjects, ChatColor.BLUE);
  }

  public static TextComponent Teams(Set<String> teamObjects, ChatColor colour) {
    teamObjects.remove(null);
    TextComponent combined = new TextComponent();
    List<String> defaultObjects = new ArrayList<>();
    if(teamObjects.remove("manager")){
    }
    if (teamObjects.remove("member")) {
      defaultObjects.add("member");
    }
    if (teamObjects.remove("outsider")){
      defaultObjects.add("outsider");
    }
    for (String team : defaultObjects) {
      TextComponent component = ComponentCreator.Team(team, colour);
      combined.addExtra(component);
      combined.addExtra(" ");
    }
    for (String team : teamObjects) {
      TextComponent component = ComponentCreator.Team(team, colour);
      combined.addExtra(component);
      combined.addExtra(" ");
    }
    return combined;
  }

  public static TextComponent Town(String town) {
    return ComponentCreator.Town(town, ChatColor.RED);
  }

  public static TextComponent Town(String town, ChatColor color) {
    TextComponent component = ComponentCreator.ColoredText(town, color);
    component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/town " + town));
    return component;
  }

  public static TextComponent UUID(UUID id) {
    return ComponentCreator.UUID(id, ChatColor.DARK_AQUA);
  }

  public static TextComponent UUID(UUID id, ChatColor colour) {
    Gamer gamer = state().getGamer(id);
    if (gamer == null) {
      return ComponentCreator.ColoredText("[Unlinked]", colour);
    }
    if (gamer.getName() == null) {
      return ComponentCreator.ColoredText("[Unlinked]", colour);
    }
    TextComponent component = ComponentCreator.ColoredText(gamer.getName(), colour);
    component.setClickEvent(
        new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/gamer " + gamer.getName()));
    return component;
  }

  public static TextComponent UUIDs(Set<UUID> members) {
    return ComponentCreator.UUIDs(members, ChatColor.DARK_AQUA);
  }

  public static TextComponent UUIDs(Set<UUID> members, ChatColor colour) {
    TextComponent combined = new TextComponent();
    for (UUID id : members) {
      TextComponent component = ComponentCreator.UUID(id, colour);
      combined.addExtra(component);
      combined.addExtra(" ");
    }
    return combined;
  }
  public static TextComponent Gamers(Set<Gamer> members) {
    return ComponentCreator.Gamers(members, ChatColor.DARK_AQUA);
  }

  public static TextComponent Gamers(Set<Gamer> members, ChatColor colour) {
    TextComponent combined = new TextComponent();
    for (Gamer id : members) {
      TextComponent component = ComponentCreator.UUID(id.getUuid(), colour);
      combined.addExtra(component);
      combined.addExtra(" ");
    }
    return combined;
  }

  private static String abbreviate(String value) {
    if (value == null) {
      return "";
    }
    if (value.length() < 16) {
      return value;
    }
    return value.substring(0, 10 / 2 - 2) + ".." + value.substring(value.length() - 10 / 4);
  }


  private static TextComponent createPermissionLetter(
      String flag, FlagValue value, String type, String target, Integer district, ChatColor color) {
    TextComponent component = new TextComponent();
    if (value.equals(FlagValue.ALLOW)) {
      component.setText(String.valueOf(flag.charAt(0)));
      component.setColor(ChatColor.GREEN);
      component.setClickEvent(
          new ClickEvent(
              ClickEvent.Action.RUN_COMMAND,
              "/district " + district + " " + "set_" + type + " " + target + " " + flag + " DENY"));
    } else if (value.equals(FlagValue.NONE)) {
      component.setText("-");
      component.setColor(color);
      component.setClickEvent(
          new ClickEvent(
              ClickEvent.Action.RUN_COMMAND,
              "/district "
                  + district
                  + " "
                  + "set_"
                  + type
                  + " "
                  + target
                  + " "
                  + flag
                  + " ALLOW"));
    } else if (value.equals(FlagValue.DENY)) {
      component.setText(String.valueOf(flag.charAt(0)));
      component.setColor(ChatColor.DARK_RED);
      component.setClickEvent(
          new ClickEvent(
              ClickEvent.Action.RUN_COMMAND,
              "/district " + district + " " + "set_" + type + " " + target + " " + flag + " NONE"));
    }
    return component;
  }

  private static TextComponent createPermissionLetter(
      String flag, FlagValue value, String type, String target, Integer district) {
    TextComponent component = new TextComponent();
    if (value.equals(FlagValue.ALLOW)) {
      component.setText(String.valueOf(flag.charAt(0)));
      component.setColor(ChatColor.GREEN);
      component.setClickEvent(
          new ClickEvent(
              ClickEvent.Action.RUN_COMMAND,
              "/district " + district + " " + "set_" + type + " " + target + " " + flag + " DENY"));
    } else if (value.equals(FlagValue.NONE)) {
      component.setText("-");
      component.setColor(ChatColor.DARK_GRAY);
      component.setClickEvent(
          new ClickEvent(
              ClickEvent.Action.RUN_COMMAND,
              "/district "
                  + district
                  + " "
                  + "set_"
                  + type
                  + " "
                  + target
                  + " "
                  + flag
                  + " ALLOW"));
    } else if (value.equals(FlagValue.DENY)) {
      component.setText(String.valueOf(flag.charAt(0)));
      component.setColor(ChatColor.DARK_RED);
      component.setClickEvent(
          new ClickEvent(
              ClickEvent.Action.RUN_COMMAND,
              "/district " + district + " " + "set_" + type + " " + target + " " + flag + " NONE"));
    }
    return component;
  }
}

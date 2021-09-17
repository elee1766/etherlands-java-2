package etherlandscore.etherlandscore.Menus;

import etherlandscore.etherlandscore.state.read.Gamer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.apache.commons.lang.StringUtils;
import org.bukkit.block.BlockFace;

public class MessageCreator {

  private final  ComponentBuilder output;
  private BaseComponent[] message;
  private TextComponent[] mapArray;
  private boolean map;

  private Integer footer_length = 0;

  public MessageCreator(){
    this.output = new ComponentBuilder();
    this.map = false;
  }

  public MessageCreator addHeader(TextComponent title){
    clear();
    int initial_length = 50;
    int title_length = title.getText().length();
    int new_length = initial_length - title_length;
    int half_length = new_length / 2;
    String left = StringUtils.repeat("_",half_length) + ",[ ";
    String right = " ],"+StringUtils.repeat("_",half_length);
    TextComponent componentLeft = ComponentCreator.ColoredText(left, ChatColor.GOLD);
    TextComponent componentRight = ComponentCreator.ColoredText(right,ChatColor.GOLD);
    output.append(componentLeft).append(title).append(componentRight).append("\n").append("\n");
    this.footer_length = 1;
    return this;
  }

  public MessageCreator addFooter(){
    clear();
    if (footer_length> 0) {
      output.append("\n");
    }
    return this;
  }

  public MessageCreator addField(TextComponent name, TextComponent field){
    clear();
    output.append(name).append(": ").append(field).append("\n");
    return this;
  }

  public MessageCreator addField(String name, TextComponent field){
    clear();
    return addField(ComponentCreator.ColoredText(name,ChatColor.DARK_GRAY), field);
  }

  public MessageCreator addBody(TextComponent name, TextComponent field){
    clear();
    output.append(name).append(":\n").append(field).append("\n");
    return this;
  }

  public MessageCreator addBody(String name, TextComponent field){
    clear();
    return addBody(ComponentCreator.ColoredText(name,ChatColor.DARK_GRAY), field);
  }

  public void clear(){
    this.output.event((ClickEvent) null).event((HoverEvent) null).color(ChatColor.RESET);
  }

  public void addMap(Gamer gamer, BlockFace facing, int x, int z){
    MapCreator mapCreator = new MapCreator(gamer, facing, x, z, 9);
    this.mapArray = mapCreator.mapMenu();
  }

  public boolean hasMap(){
    return this.map;
  }

  public void finish() {
    this.message = output.create();
  }

  public BaseComponent[] getMessage() {
    return message;
  }

  public void addMap(){

  }
}

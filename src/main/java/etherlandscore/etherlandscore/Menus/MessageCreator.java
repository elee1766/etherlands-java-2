package etherlandscore.etherlandscore.Menus;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.apache.commons.lang.StringUtils;

public class MessageCreator {

  private final  ComponentBuilder output;
  private BaseComponent[] message;

  private Integer footer_length = 0;

  public MessageCreator(){
    this.output = new ComponentBuilder();
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


  public void finish() {
    this.message = output.create();
  }

  public BaseComponent[] getMessage() {
    return message;
  }

  public void addMap(){

  }
}

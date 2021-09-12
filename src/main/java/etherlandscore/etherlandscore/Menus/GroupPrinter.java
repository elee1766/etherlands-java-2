package etherlandscore.etherlandscore.Menus;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.state.read.Group;
import etherlandscore.etherlandscore.state.write.WriteDistrict;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

import java.lang.reflect.Field;

public class GroupPrinter extends ListenerClient {
  private final Fiber fiber;
  private final Channels channels;
  private final Group writeGroup;

  public GroupPrinter(Group writeGroup, Fiber fiber, Channels channels) {
    super(channels, fiber);
    this.fiber = fiber;
    this.channels = channels;
    this.writeGroup = writeGroup;
  }

  public void printGroup(Player sender, WriteDistrict district) {
    TextComponent print = new TextComponent("");
    MessageFormatter prettyPrint = new MessageFormatter(print, fiber, channels);
    prettyPrint.addBar("=", "GroupInfo");

    Field[] fields = writeGroup.getDeclaredFields();
    for (Field field : fields) {
      try {
        prettyPrint.addField(field.getName(), String.valueOf(field.get(this.writeGroup)));
      } catch (IllegalAccessException ex) {
        System.out.println(ex);
      }
    }
    TextComponent settings = new TextComponent("Click to edit flags");
    settings.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/flag menu district_group " + district.getId() + " " + this.writeGroup.getName()));
    prettyPrint.printOut(sender);
    sender.sendMessage(settings);
  }
}

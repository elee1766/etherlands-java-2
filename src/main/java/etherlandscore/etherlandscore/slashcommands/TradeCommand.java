package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.state.bank.GamerTransaction;
import etherlandscore.etherlandscore.state.read.BankRecord;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.write.WriteBankRecord;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bouncycastle.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetlang.fibers.Fiber;

import java.util.HashSet;
import java.util.Set;

public class TradeCommand extends ListenerClient {
  private final Fiber fiber;
  private final Channels channels;

  public TradeCommand(Channels channels, Fiber fiber) {
    super(channels, fiber);
    this.fiber = fiber;
    this.channels = channels;
    register();
  }

  public void tradeMenu(Player sender, Object[] args){
    Player player = (Player) args[0];
    Gamer from = context.getGamer(sender.getUniqueId());
    Gamer to = context.getGamer(player.getUniqueId());
    Set<ItemStack> itemsToSend = new HashSet<>();
    ItemStack itemStack = sender.getEquipment().getItemInMainHand();
    itemsToSend.add(itemStack);
    sender.getInventory().setItemInMainHand(null);
    Integer payment = (Integer) args[2];
    Inventory tempFrom = Bukkit.createInventory(null, 2);
    tempFrom.addItem(itemStack);

    GamerTransaction gt = new GamerTransaction(from, to, payment, 0,
                                        tempFrom, player.getInventory(),
        itemsToSend, null);

    TextComponent confirm = new TextComponent("Click to approve:\n"+gt.getGamers()+" "+gt.getItemStacks()+" "+gt.getDeltas());
    confirm.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/help"));
    player.sendMessage(confirm);
  }

  public void register() {
    CommandAPICommand TradeCommand =
        new CommandAPICommand("offer")
            .withArguments(new PlayerArgument("player"))
            .withArguments(new IntegerArgument("requested payment"))
            .withPermission("etherlands.public")
            .executesPlayer(this::tradeMenu);
    TradeCommand.register();
  }
}

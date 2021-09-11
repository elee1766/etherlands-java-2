package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.state.bank.GamerTransaction;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.util.Map2;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
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
  private final Map2<Gamer, Gamer, GamerTransaction> transactions;

  public TradeCommand(Channels channels, Fiber fiber) {
    super(channels, fiber);
    this.fiber = fiber;
    this.channels = channels;
    this.transactions = new Map2<>();
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
    Integer payment = (Integer) args[1];
    Inventory tempFrom = Bukkit.createInventory(null, 9);
    tempFrom.addItem(itemStack);
    GamerTransaction gt = new GamerTransaction(from, to, 0, payment, tempFrom, player.getInventory(), itemsToSend, null);
    transactions.put(from, to, gt);
    TextComponent confirm = new TextComponent("Click bellow to approve the following trade\n");
    TextComponent trade = new TextComponent(itemStack.getAmount() + " " + itemStack.getDisplayName() + " for " + payment + " monies.");
    trade.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/approve " + from.getPlayer().getName()));
    player.sendMessage(confirm);
    player.sendMessage(trade);
  }

  public void approve(Player sender, Object[] args){
    Player player = (Player) args[0];
    Gamer from = context.getGamer(player.getUniqueId());
    Gamer to = context.getGamer(sender.getUniqueId());
    GamerTransaction gt = this.transactions.get(from, to);
    if(gt!=null){
      Bukkit.getLogger().info("sending process message");
      this.channels.master_command.publish(new Message<>(MasterCommand.context_process_gamer_transaction, gt));
    }else{
      sender.sendMessage("The transaction has expired");
    }
  }

  public void register() {
    CommandAPICommand TradeCommand =
        new CommandAPICommand("offer")
            .withArguments(new PlayerArgument("player"))
            .withArguments(new IntegerArgument("requested payment"))
            .withPermission("etherlands.public")
            .executesPlayer(this::tradeMenu);
    CommandAPICommand ApproveCommand =
        new CommandAPICommand("approve")
            .withArguments(new PlayerArgument("player"))
            .executesPlayer(this::approve);
    ApproveCommand.register();
    TradeCommand.register();
  }
}

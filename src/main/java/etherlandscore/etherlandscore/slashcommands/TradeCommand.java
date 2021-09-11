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
import etherlandscore.etherlandscore.state.write.WriteShop;
import etherlandscore.etherlandscore.util.Map2;
import jnr.ffi.annotations.Meta;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetlang.fibers.Fiber;

import java.awt.*;
import java.util.Collections;
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

  public void pay(Player sender, Object[] args){
    Player p = (Player) args[0];
    Gamer to = context.getGamer(p.getUniqueId());
    GamerTransaction gt = new GamerTransaction(context.getGamer(sender.getUniqueId()), to, (Integer) args[1], 0, null, null, null, null);
    this.channels.master_command.publish(new Message<>(MasterCommand.context_process_gamer_transaction, gt));
  }

  void mint(Object o,Object[] args) {
    Player p = (Player) args[0];
    Gamer to = context.getGamer(p.getUniqueId());
    Integer amount = (Integer) args[1];
    this.channels.master_command.publish(new Message<>(MasterCommand.context_mint_tokens, to,amount));
  }

  void balanceSelf(Player sender, Object[]args){
    sender.sendMessage("you have "+ context.getBalance(sender.getUniqueId()) + " monies");
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
    TextComponent confirm = new TextComponent("Click above or type /approve " + sender.getName() + " to approve the trade above");
    confirm.setColor(ChatColor.GOLD);
    TextComponent trade = new TextComponent("====REQUESTED TRADE====\n\n" + itemStack.getAmount() + " " + itemStack.getType() + " for " + payment + " monies.\n");
    trade.setColor(ChatColor.GOLD);
    trade.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/approve " + from.getPlayer().getName()));
    player.sendMessage(trade);
    player.sendMessage(confirm);
  }

  public void approve(Player sender, Object[] args){
    Player player = (Player) args[0];
    Gamer from = context.getGamer(player.getUniqueId());
    Gamer to = context.getGamer(sender.getUniqueId());
    GamerTransaction gt = this.transactions.get(from, to);
    if(gt!=null){
      this.channels.master_command.publish(new Message<>(MasterCommand.context_process_gamer_transaction, gt));
      sender.sendMessage("Transaction has been complete items have been added to your inventory");
    }else{
      sender.sendMessage("The transaction has expired");
    }
  }

  public void addItem(Player sender, Object[] args){
    Integer price = (Integer) args[0];
    Block shop = sender.getTargetBlock(null, 10);
    if(shop.getState() instanceof Chest){
      WriteShop writeShop = context.getShop(shop.getLocation());
      Inventory shopInventory = writeShop.getInventory();
      ItemStack item = sender.getEquipment().getItemInMainHand();
      if(shopInventory.firstEmpty()==-1){
        sender.sendMessage("This shop is full");
      }else{
        sender.getInventory().removeItem(item);
        shopInventory.addItem(item);
        item.setLore(Collections.singletonList(price.toString()));
      }
    }
  }

  public void register() {
    CommandAPICommand TradeCommand =
        new CommandAPICommand("offer")
            .withArguments(new PlayerArgument("player"))
            .withArguments(new IntegerArgument("requested payment"))
            .withPermission("etherlands.public")
            .executesPlayer(this::tradeMenu);
    CommandAPICommand AddItem =
        new CommandAPICommand("additem")
            .withArguments(new IntegerArgument("price"))
            .withPermission("etherlands.public")
            .executesPlayer(this::addItem);
    CommandAPICommand ApproveCommand =
        new CommandAPICommand("approve")
            .withArguments(new PlayerArgument("player"))
            .executesPlayer(this::approve);
    CommandAPICommand PayCommand =
        new CommandAPICommand("pay")
            .withArguments(new PlayerArgument("player"))
            .withArguments(new IntegerArgument("amount"))
            .executesPlayer(this::pay);
    CommandAPICommand MintCommand =
        new CommandAPICommand("mint")
            .withArguments(new PlayerArgument("player"))
            .withArguments(new IntegerArgument("amount"))
            .executesConsole(this::mint);
    CommandAPICommand BalCommand =
        new CommandAPICommand("balance")
            .withAliases("bal")
            .executesPlayer(this::balanceSelf);
    BalCommand.register();
    MintCommand.register();
    PayCommand.register();
    AddItem.register();
    ApproveCommand.register();
    TradeCommand.register();
  }
}

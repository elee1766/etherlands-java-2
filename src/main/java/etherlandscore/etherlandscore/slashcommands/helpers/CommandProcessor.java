package etherlandscore.etherlandscore.slashcommands.helpers;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandExecutor;
import dev.jorel.commandapi.executors.IExecutorNormal;
import dev.jorel.commandapi.executors.PlayerCommandExecutor;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.services.ListenerClient;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetlang.channels.MemoryChannel;
import org.jetlang.fibers.Fiber;

import java.util.HashMap;
import java.util.Map;

public class CommandProcessor extends ListenerClient {
  private final Fiber fiber;
  private final Channels channels;
  private final MemoryChannel<CommandParameters> execution_channel;
  private final Map<SlashCommands, IExecutorNormal> executor_map = new HashMap<>();

  public CommandProcessor(Channels channels, Fiber fiber) {
    super(channels, fiber);
    this.fiber = fiber;
    this.channels = channels;
    this.execution_channel = new MemoryChannel<>();
    this.execution_channel.subscribe(fiber,this::execute);
  }

  private void execute(CommandParameters msg) {
    Bukkit.getLogger().info("attempting to execute command: " + msg.getCommand().toString());
    if(executor_map.containsKey(msg.getCommand())){
      IExecutorNormal exec = executor_map.get(msg.getCommand());
      try {
        exec.run(msg.getSender(),msg.getArgs());
      } catch (Exception e) {
        Bukkit.getLogger().warning("Failed to execute command: " + e.getMessage());
        e.printStackTrace();
      }
    }
  }

  protected void hook(SlashCommands command, CommandExecutor executor){
    this.executor_map.put(command,executor);
  }

  protected void hook(SlashCommands command, PlayerCommandExecutor executor){
    this.executor_map.put(command,executor);
  }

  private void runAsync(SlashCommands name, CommandSender sender, Object[] args){
    this.execution_channel.publish(new CommandParameters(name, sender, args));
  }

  protected CommandAPICommand createPlayerCommand(String commandName, SlashCommands signal, PlayerCommandExecutor executor){
    hook(signal,executor);
    return new CommandAPICommand(commandName).executesPlayer(
        (sender, args)->{runAsync(signal,sender,args);}
    );
  }
  protected CommandAPICommand createPlayerCommand(String commandName, SlashCommands signal){
    return new CommandAPICommand(commandName).executesPlayer(
        (sender, args)->{runAsync(signal,sender,args);}
    );
  }
}
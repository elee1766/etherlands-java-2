package etherlandscore.etherlandscore.slashcommands.helpers;

import org.bukkit.command.CommandSender;

public class CommandParameters {

  private final SlashCommands command_name;
  private final CommandSender sender;
  private final Object[] args;

  public CommandParameters(SlashCommands name, CommandSender sender, Object[] args) {
    this.command_name = name;
    this.sender = sender;
    this.args = args;
  }

  public Object[] getArgs() {
    return args;
  }

  public SlashCommands getCommand() {
    return command_name;
  }

  public CommandSender getSender() {
    return sender;
  }
}

package etherlandscore.etherlandscore.slashcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EtherlandsCommandExecutor implements CommandExecutor{
    public void _run(){
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Bukkit.getServer().getConsoleSender().sendMessage(command.toString());
        if(sender instanceof Player){
            if(command.getName().equals("team")){
                sender.sendMessage("team " + args[0]);
            }
        }


        return false;
    }
}

package etherlandscore.etherlandscore.slashcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.services.ListenerClient;
import org.bouncycastle.util.Arrays;
import org.bukkit.entity.Player;
import org.jetlang.fibers.Fiber;

public class PlotCommand extends ListenerClient {
    private final Fiber fiber;
    private final Channels channels;

    public PlotCommand(Channels channels, Fiber fiber) {
        super(channels, fiber);
        this.fiber = fiber;
        this.channels = channels;
    }

    public void register() {
        CommandAPICommand ChunkCommand = new CommandAPICommand("plot").withPermission("etherlands.public").executesPlayer(this::runHelpCommand);
        ChunkCommand.withSubcommand(new CommandAPICommand("help")
                .withPermission("etherlands.public")
                .executesPlayer(this::runHelpCommand)
        );
        ChunkCommand.withSubcommand(new CommandAPICommand("set")
                .withPermission("etherlands.public")
                .withArguments(new StringArgument("flag").replaceSuggestions(info -> getAccessFlagStrings()))
                .withArguments(new StringArgument("value").replaceSuggestions(info -> getFlagValueStrings()))
                .executesPlayer((sender, args) -> {
                })
        );
        ChunkCommand.withSubcommand(new CommandAPICommand("set")
                .withPermission("etherlands.public")
                .withArguments(new IntegerArgument("chunkId").replaceSuggestions(info -> getChunkStrings()))
                .withArguments(new StringArgument("player").includeSuggestions(info -> Arrays.append(getPlayerStrings(), "__global__")))
                .withArguments(new StringArgument("flag").replaceSuggestions(info -> getAccessFlagStrings()))
                .withArguments(new StringArgument("value").replaceSuggestions(info -> getFlagValueStrings()))
                .executesPlayer((sender, args) -> {
                })
        );
        ChunkCommand.withSubcommand(new CommandAPICommand("info")
                .withPermission("etherlands.public")
                .executesPlayer((sender, args) -> {
                }));
        ChunkCommand.withSubcommand(new CommandAPICommand("info")
                .withArguments(new IntegerArgument("chunkId").replaceSuggestions(info -> getChunkStrings()))
                .withPermission("etherlands.public")
                .executes((sender, args) -> {
                }));
        ChunkCommand.withSubcommand(new CommandAPICommand("update")
                .withArguments(new IntegerArgument("chunkId"))
                .withPermission("etherlands.public")
                .executes((sender, args) -> {
                })
        );
        ChunkCommand.register();
    }

    void runHelpCommand(Player sender, Object[] args) {
        sender.sendMessage("update info invite join delete");
    }
}


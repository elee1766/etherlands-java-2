package etherlandscore.etherlandscore.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.enums.FlagValue;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.fibers.ServerModule;
import etherlandscore.etherlandscore.state.Context;
import etherlandscore.etherlandscore.state.read.ReadContext;
import etherlandscore.etherlandscore.state.write.*;
import org.bukkit.Bukkit;
import org.jetlang.fibers.Fiber;

import java.io.IOException;
import java.util.UUID;

public class Scheduler extends ServerModule {
    private static Context context;
    private final Channels channels;

    public Scheduler(Channels channels, Fiber fiber) {
        super(fiber);
        this.channels = channels;
        context = new Context(this.channels);
    }

    public void newEthSchedule(EthereumService e, int tickDelay){
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Bukkit.getPluginManager().getPlugin("EtherlandsCore"), new Runnable() {
            @Override
            public void run() {
                try {
                    e.queryHistory();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 1L , (long) tickDelay * 20);
    }

}

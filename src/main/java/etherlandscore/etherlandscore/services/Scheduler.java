package etherlandscore.etherlandscore.services;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.ServerModule;
import etherlandscore.etherlandscore.state.Context;
import org.bukkit.Bukkit;
import org.jetlang.fibers.Fiber;

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
                    e.update_districts();
                } catch (Exception e) {
                    Bukkit.getLogger().warning("ethProxy is currently unavailable");
                }
            }
        }, 1L , (long) tickDelay * 20);
    }
}

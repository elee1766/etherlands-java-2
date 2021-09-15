package etherlandscore.etherlandscore.services;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.ServerModule;
import org.jetlang.fibers.Fiber;

import java.util.concurrent.TimeUnit;

public class Scheduler extends ServerModule {
    private final Channels channels;
    private final Fiber fiber;

    public Scheduler(Channels channels, Fiber fiber) {
        super(fiber);
        this.fiber = fiber;
        this.channels = channels;

        this.fiber.scheduleAtFixedRate(
            () -> {
            },
            1,
            5,
            TimeUnit.SECONDS);
    }
}

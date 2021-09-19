package etherlandscore.etherlandscore.services;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.ServerModule;
import etherlandscore.etherlandscore.singleton.RedisGetter;
import etherlandscore.etherlandscore.state.sender.DistrictSender;
import org.jetlang.fibers.Fiber;

import java.util.concurrent.TimeUnit;

import static etherlandscore.etherlandscore.services.MasterService.state;

public class Scheduler extends ServerModule {
    private final Channels channels;
    private final Fiber fiber;

    private int best_district = 1;

    public Scheduler(Channels channels, Fiber fiber) {
        super(fiber);
        this.fiber = fiber;
        this.channels = channels;
        for (Integer district: RedisGetter.GetDistricts()) {
            if(!state().getDistricts().containsKey(district)){
                DistrictSender.touchDistrict(this.channels,district);
                if(district > best_district){
                    best_district = district;
                }
            }
        }

        this.fiber.scheduleAtFixedRate(
            () -> {
                if(state().getDistricts().containsKey(best_district)){
                    best_district = best_district + 1;
                }
                DistrictSender.touchDistrict(this.channels,best_district);
            },
            1,
            5,
            TimeUnit.SECONDS);
    }
}

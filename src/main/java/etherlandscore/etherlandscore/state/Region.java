package etherlandscore.etherlandscore.state;

import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.enums.FlagValue;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Region extends StateHolder implements Comparable<Region> {
    private final Set<Integer> plotIds;
    private final String team;
    private final String name;
    private final boolean isGlobal;
    private Integer priority = 0;

    private final Map<UUID,Map<AccessFlags, FlagValue>> gamerFlags = new HashMap<>();

    Region(Team team, String name, Set<Integer> plotIds) {
        this.team = team.getName();
        this.plotIds = plotIds;
        this.name = name;
        this.isGlobal = name.equals("__global__");
    }

    public void addPlot(Channels channels, Plot plot) {
        channels.master_command.publish(new Message(MasterCommand.region_add_plot, this, plot));
    }

    public void addPlot(Plot plot) {
        this.plotIds.add(plot.getId());
    }

    public void removePlot(Channels channels, Plot plot) {
        channels.master_command.publish(new Message(MasterCommand.region_remove_plot, this, plot));
    }

    public void removePlot(Plot plot) {
        this.plotIds.remove(plot.getId());
    }

    public void setPriority(Channels channels, Integer priority) {
        channels.master_command.publish(new Message(MasterCommand.region_set_priority, this, priority));
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getPriority() {
        return this.priority;
    }

    @Override
    public int compareTo(Region r){
        return getPriority().compareTo(r.getPriority());
    }

    public FlagValue checkFlags(AccessFlags flag, Gamer gamer) {
        Map<AccessFlags, FlagValue> flagMap = gamerFlags.getOrDefault(gamer.getUuid(),new HashMap<>());
        return flagMap.getOrDefault(flag,FlagValue.NONE);
    }
}

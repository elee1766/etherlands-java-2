package etherlandscore.etherlandscore.state;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.Message;

import java.util.Set;

public class Region extends StateHolder{
    private final Set<Integer> plotIds;
    private final String team;
    private Integer priority = 0;
    private String name;

    private final boolean isGlobal;

    Region(Team team, String name,Set<Integer> plotIds){
        this.team = team.getName();
        this.plotIds = plotIds;
        this.name = name;
        this.isGlobal = name.equals("__global__");
    }

    public void addPlot(Channels channels, Plot plot){
        channels.master_command.publish(new Message("region_add_plot",this,plot));
    }

    public void addPlot(Plot plot){
        this.plotIds.add(plot.getId());
    }

    public void removePlot(Channels channels, Plot plot){
        channels.master_command.publish(new Message("region_remove_plot",this,plot));
    }

    public void removePlot(Plot plot){
        this.plotIds.remove(plot.getId());
    }

    public void setPriority(Channels channels, Integer priority){
        channels.master_command.publish(new Message("region_set_priority",this,priority));
    }
    public void setPriority(Integer priority){
        this.priority = priority;
    }
}

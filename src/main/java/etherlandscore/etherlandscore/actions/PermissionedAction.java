package etherlandscore.etherlandscore.actions;

import etherlandscore.etherlandscore.state.Context;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public class PermissionedAction{
    private final Event event;
    private final Context context;
    public PermissionedAction(Context context, Event event) {
        this.event = event;
        this.context = context;
    }
    public boolean rollback(){
        if(event instanceof Cancellable) {
            ((Cancellable) event).setCancelled(true);
        }
        return false;
    }

    public Context getContext(){
        return context;
    }

    public boolean process(){
        return true;
    }

}

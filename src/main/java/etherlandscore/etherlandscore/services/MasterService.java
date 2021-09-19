package etherlandscore.etherlandscore.services;

import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.enums.FlagValue;
import etherlandscore.etherlandscore.enums.MessageToggles;
import etherlandscore.etherlandscore.enums.ToggleValues;
import etherlandscore.etherlandscore.fibers.*;
import etherlandscore.etherlandscore.persistance.Couch.CouchPersister;
import etherlandscore.etherlandscore.state.Context;
import etherlandscore.etherlandscore.state.bank.GamerTransaction;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.ReadContext;
import etherlandscore.etherlandscore.state.write.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetlang.fibers.Fiber;
import org.jetlang.fibers.ThreadFiber;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.UUID;

public class MasterService extends ServerModule {
    private static Context context;
    private static Channels channels;

    public MasterService(Channels channels, Fiber fiber) {
        super(fiber);
        CouchPersister couchPersister;
        MasterService.channels = channels;
        context = new Context(channels);
        try {
            Fiber couchFiber = new ThreadFiber();
            couchPersister = new CouchPersister(channels, couchFiber);
            context.addPersister(couchPersister);
            couchPersister.start();
            couchPersister.populateContext(context);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        channels.global_update.publish(context);
        channels.master_command.subscribe(fiber, this::process_command);
    }

    public static void setChannels(Channels channels) {
        MasterService.channels = channels;
    }

    public static ReadContext state() {
        return new ReadContext(context, channels);
    }

    private void global_update(){
        channels.global_update.publish(context);
    }

    private void process_command(Message<MasterCommand> message) {
        Object[] _args = message.getArgs();
        Bukkit.getLogger().info("MasterService: "+ message.getCommand() + " " + Arrays.toString(_args));
        try{
        switch (message.getCommand()) {
            case context_create_gamer -> context.context_create_gamer((UUID) _args[0]);
            case context_save_all -> context.saveAll();
            // gamer commands
            case gamer_add_friend ->context. gamer_add_friend((WriteGamer) _args[0], (WriteGamer) _args[1]);
            case gamer_remove_friend -> context.gamer_remove_friend((WriteGamer) _args[0],(WriteGamer) _args[1]);
            case gamer_link_address -> context.gamer_link_address((WriteGamer) _args[0], (String) _args[1]);
            //town commands
            case town_add_gamer -> context.town_add_gamer((WriteTown) _args[0], (WriteGamer) _args[1]);
            case town_remove_gamer -> context.town_remove_gamer((WriteTown) _args[0], (WriteGamer) _args[1]);
            case town_create_town -> context.town_create_town((WriteGamer) _args[0], (String) _args[1]);
            case town_delete_town -> context.town_delete_town((WriteTown) _args[0]);
            case town_create_team -> context.town_create_team((WriteTown) _args[0], (String) _args[1]);
            case town_delete_team -> context.town_delete_team((WriteTown) _args[0], (WriteTeam) _args[1]);
            case town_delete_district -> context.town_delete_district((WriteTown) _args[0], (WriteDistrict) _args[1]);
            case town_delegate_district -> context.town_delegate_district((WriteTown)_args[0], (WriteDistrict) _args[1]);
            case district_reclaim_district -> context.district_reclaim_district((WriteDistrict) _args[0]);
            // district commands
            //team commands
            case team_add_gamer -> context.team_add_gamer((WriteTeam) _args[0], (WriteGamer) _args[1]);
            case team_remove_gamer -> context.team_remove_gamer((WriteTeam) _args[0], (WriteGamer) _args[1]);
            case team_set_priority -> context.team_set_priority((WriteTeam) _args[0], (Integer) _args[1]);
            //flag commands
            case district_set_team_permission -> context.district_set_team_permission((WriteDistrict) _args[0], (WriteTeam) _args[1], (AccessFlags) _args[2], (FlagValue) _args[3]);
            case district_set_gamer_permission -> context.district_set_gamer_permission((WriteDistrict) _args[0], (WriteGamer) _args[1], (AccessFlags) _args[2], (FlagValue) _args[3]);
            case nft_create_nft -> context.nft_create_nft((WriteNFT) _args[0]);
            case map_create_map -> context.map_create_map((WriteMap) _args[0]);
            case map_render_maps -> context.map_rerender_maps();
            case context_process_gamer_transaction -> context.context_process_gamer_transaction((GamerTransaction) _args[0]);
            case context_mint_tokens -> context.context_mint_tokens((WriteGamer) _args[0], (Integer)_args[1]);
            case shop_create_shop -> context.shop_create_shop((WriteShop) _args[0]);
            case gamer_toggle_message -> context.gamer_toggle_message((WriteGamer) _args[0], (MessageToggles) _args[1], (ToggleValues) _args[2]);
            case touch_district -> context.touch_district((Integer) _args[0]);
            case touch_gamer -> context.touch_gamer((UUID) _args[0]);
            case store_gamer_location -> context.storeGamerLocation((Gamer) _args[0], (Location) _args[1]);
        }
        if(message.hasChatResponse()){
                forward_chat_message(message.getChatResponse());

        }
            global_update();
        }catch(Exception e){
            Bukkit.getLogger().warning("Failed to process MasterCommand" + message.getCommand());
            e.printStackTrace();
        }
    }

    private void forward_chat_message(Message<ChatTarget> message){
        channels.chat_message.publish(message);
    }

}

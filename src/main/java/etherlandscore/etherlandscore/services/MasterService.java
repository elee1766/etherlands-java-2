package etherlandscore.etherlandscore.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.enums.FlagValue;
import etherlandscore.etherlandscore.enums.MessageToggles;
import etherlandscore.etherlandscore.enums.ToggleValues;
import etherlandscore.etherlandscore.fibers.*;
import etherlandscore.etherlandscore.state.Context;
import etherlandscore.etherlandscore.state.bank.GamerTransaction;
import etherlandscore.etherlandscore.state.read.ReadContext;
import etherlandscore.etherlandscore.state.write.*;
import org.bukkit.Bukkit;
import org.jetlang.fibers.Fiber;

import java.util.UUID;

public class MasterService extends ServerModule {
    private static Context context;
    private static Channels channels;
    private final Gson gson;

    public MasterService(Channels channels, Fiber fiber) {
        super(fiber);
        this.channels = channels;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        context = new Context(this.channels);
        this.channels.global_update.publish(context);
        this.channels.master_command.subscribe(fiber, this::process_command);
    }

    public static ReadContext state() {
        return new ReadContext(context, channels);
    }

    private void global_update(){
        this.channels.global_update.publish(context);
    }

    private void process_command(Message<MasterCommand> message) {
        Object[] _args = message.getArgs();
        Bukkit.getLogger().info(message.getCommand() + _args.toString());
        switch (message.getCommand()) {
            case context_create_gamer -> context.context_create_gamer((UUID) _args[0]);
            case context_save_all -> context.saveAll();
            // gamer commands
            case gamer_add_friend ->context. gamer_add_friend((WriteGamer) _args[0], (WriteGamer) _args[1]);
            case gamer_remove_friend -> context.gamer_remove_friend((WriteGamer) _args[0],(WriteGamer) _args[1]);
            case gamer_link_address -> context.gamer_link_address((WriteGamer) _args[0], (String) _args[1]);
            //team commands
            case team_add_gamer -> context.team_add_gamer((WriteTeam) _args[0], (WriteGamer) _args[1]);
            case team_remove_gamer -> context.team_remove_gamer((WriteTeam) _args[0], (WriteGamer) _args[1]);
            case team_create_team -> context.team_create_team((WriteGamer) _args[0], (String) _args[1]);
            case team_delete_team -> context.team_delete_team((WriteTeam) _args[0]);
            case team_create_group -> context.team_create_group((WriteTeam) _args[0], (String) _args[1]);
            case team_delete_group -> context.team_delete_group((WriteTeam) _args[0], (WriteGroup) _args[1]);
            case team_delete_district -> context.team_delete_district((WriteTeam) _args[0], (WriteDistrict) _args[1]);
            case team_delegate_district -> context.team_delegate_district((WriteTeam)_args[0], (WriteDistrict) _args[1]);
            // district commands
            //group commands
            case group_add_gamer -> context.group_add_gamer((WriteGroup) _args[0], (WriteGamer) _args[1]);
            case group_remove_gamer -> context.group_remove_gamer((WriteGroup) _args[0], (WriteGamer) _args[1]);
            case group_set_priority -> context.group_set_priority((WriteGroup) _args[0], (Integer) _args[1]);
            //flag commands
            case district_set_group_permission -> context.district_set_group_permission((WriteDistrict) _args[0], (WriteGroup) _args[1], (AccessFlags) _args[2], (FlagValue) _args[3]);
            case district_set_gamer_permission -> context.district_set_gamer_permission((WriteDistrict) _args[0], (WriteGamer) _args[1], (AccessFlags) _args[2], (FlagValue) _args[3]);
            case nft_create_nft -> context.nft_create_nft((WriteNFT) _args[0]);
            case map_create_map -> context.map_create_map((WriteMap) _args[0]);
            case map_rerender_maps -> context.map_rerender_maps();
            case context_process_gamer_transaction -> context.context_process_gamer_transaction((GamerTransaction) _args[0]);
            case context_mint_tokens -> context.context_mint_tokens((WriteGamer) _args[0], (Integer)_args[1]);
            case shop_create_shop -> context.shop_create_shop((WriteShop) _args[0]);
            case gamer_toggle_message -> context.gamer_toggle_message((WriteGamer) _args[0], (MessageToggles) _args[1], (ToggleValues) _args[2]);
            case touch_district -> context.touch_district((Integer) _args[0]);
        }
        Bukkit.getLogger().info("Checking if needs to forward message");
        if(message.hasChatResponse()){
                Bukkit.getLogger().info("Forwarding message");
                forward_chat_message(message.getChatResponse());
        }
        global_update();
    }

    private void forward_chat_message(Message<ChatTarget> message){
        channels.chat_message.publish(message);
    }



    public void save() {
      context.saveAll();
    }

}

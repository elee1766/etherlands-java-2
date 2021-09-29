package etherlandscore.etherlandscore.services;

import etherlandscore.etherlandscore.fibers.*;
import etherlandscore.etherlandscore.state.Context;
import etherlandscore.etherlandscore.state.bank.GamerTransaction;
import etherlandscore.etherlandscore.state.read.ReadContext;
import etherlandscore.etherlandscore.state.Gamer;
import etherlandscore.etherlandscore.state.WriteNFT;
import etherlandscore.etherlandscore.state.WriteShop;
import org.bukkit.Bukkit;
import org.jetlang.fibers.Fiber;

import java.util.Arrays;

public class MasterService extends ServerModule {
    private static Context context;
    private static Channels channels;

    public MasterService(Channels channels, Fiber fiber) {
        super(fiber);
        MasterService.channels = channels;
        context = new Context();
        channels.global_update.publish(context);
        channels.master_command.subscribe(fiber, this::process_command);
    }

    public static void setChannels(Channels channels) {
        MasterService.channels = channels;
    }

    public static ReadContext state() {
        return new ReadContext(context);
    }

    private void forward_chat_message(Message<ChatTarget> message){
        channels.chat_message.publish(message);
    }

    private void global_update(){
        channels.global_update.publish(context);
    }

    private void process_command(Message<MasterCommand> message) {
        Object[] _args = message.getArgs();
        Bukkit.getLogger().info("MasterService: "+ message.getCommand() + " " + Arrays.toString(_args));
        try{
        switch (message.getCommand()) {
            case nft_create_nft -> context.nft_create_nft((WriteNFT) _args[0]);
            case nft_delete_nft -> context.nft_delete_nft((WriteNFT) _args[0]);
            case context_process_gamer_transaction -> context.context_process_gamer_transaction((GamerTransaction) _args[0]);
            case context_mint_tokens -> context.context_mint_tokens((Gamer) _args[0], (Integer)_args[1]);
            case shop_create_shop -> context.shop_create_shop((WriteShop) _args[0]);
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

}

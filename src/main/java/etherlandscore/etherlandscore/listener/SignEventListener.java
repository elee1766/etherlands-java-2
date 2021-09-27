package etherlandscore.etherlandscore.listener;

import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.services.ListenerClient;
import etherlandscore.etherlandscore.singleton.SettingsSingleton;
import etherlandscore.etherlandscore.state.read.District;
import etherlandscore.etherlandscore.state.write.WriteNFT;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.jetlang.fibers.Fiber;

import java.util.Map;

public class SignEventListener extends ListenerClient implements Listener {

  public final Fiber fiber;
  public final Channels channels;
  private final Map<String, String> settings = SettingsSingleton.getSettings().getSettings();

  public SignEventListener(Channels channels, Fiber fiber) {
    super(channels, fiber);
    this.fiber = fiber;
    this.channels = channels;
  }

  @EventHandler
  public void onSignEdit(SignChangeEvent signChangeEvent) {
    String[] lines = signChangeEvent.getLines();
    if (lines.length > 0) {
      if (lines[0].toLowerCase().contains("nft")) {
        Integer item_id = -1;
        Integer width = -1;
        String[] strArr = lines[0].split("\\s+");
        for (String s : strArr) {
          try{
            Integer a = Integer.parseInt(s);
            if(item_id == -1){
              item_id = a;
            }else{
              width = a;
            }
          } catch (NumberFormatException e){
            e.printStackTrace();
          }
        }
        if(item_id > -1 && width > -1){
          String slug = (lines[1] + lines[2] + lines[3]).replaceAll(" ","").replaceAll("\n","");
          Block placed = signChangeEvent.getBlock();
          WriteNFT writeNft = new WriteNFT(slug,
              item_id.toString(),
              width,
              signChangeEvent.getPlayer().getUniqueId(),
              placed.getX(),placed.getY(),placed.getZ()
          );
          channels.master_command.publish(new Message<>(MasterCommand.nft_create_nft, writeNft));
        }
      }
    }
  }


  private BlockFace facing(Block placed){
    return ((Directional) placed.getBlockData()).getFacing();
  }

  //x+ is east
  //z+ is south
  private boolean canBuildHere(String width, Block placed, Player player, BlockFace blockFace) {
    int size = Integer.parseInt(width);
    int x = placed.getX();
    int z = placed.getZ();
    switch (blockFace) {
      case WEST:
        //check north
        for(int i = 0; i<size; i++){
          District p = context.getDistrict(x,z-i);
          if(p==null){
            if(player.isOp()){
              break;
            }
            return false;
          }
          if(!p.canGamerPerform(AccessFlags.BUILD, context.getGamer(player.getUniqueId()))){
            return false;
          }
        }
        break;
      case EAST:
        //check south
        for(int i = 0; i<size; i++){
          District p = context.getDistrict(x,z+i);
          if(p==null){
            if(player.isOp()){
              break;
            }
            return false;
          }
          if(!p.canGamerPerform(AccessFlags.BUILD, context.getGamer(player.getUniqueId()))){
            return false;
          }
        }
        break;
      case NORTH:
        //check east
        for(int i = 0; i<size; i++){
          District p = context.getDistrict(x+i,z);
          if(p==null){
            if(player.isOp()){
              break;
            }
            return false;
          }
          if(!p.canGamerPerform(AccessFlags.BUILD, context.getGamer(player.getUniqueId()))){
            return false;
          }
        }
        break;
      case SOUTH:
        //check west
        for(int i = 0; i<size; i++){
          District p = context.getDistrict(x+i,z);
          if(p==null){
            if(player.isOp()){
              break;
            }
            return false;
          }
          if(p.canGamerPerform(AccessFlags.BUILD, context.getGamer(player.getUniqueId()))){
            return false;
          }
        }
        break;
      default:
        break;
    }
    return true;
  }

}

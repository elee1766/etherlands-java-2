package etherlandscore.etherlandscore.state.write;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import etherlandscore.etherlandscore.nms.WrapperPlayServerEntityMetadata;
import etherlandscore.etherlandscore.nms.WrapperPlayServerSpawnEntity;
import etherlandscore.etherlandscore.services.ExternalMetadataService;
import etherlandscore.etherlandscore.state.read.NFT;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.EntityType;
import org.bukkit.map.MapPalette;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static etherlandscore.etherlandscore.services.MasterService.state;

public class WriteNFT  implements NFT {
    public static final int DEFAULT_STARTING_ID = Integer.MAX_VALUE / 4;
    private static final AtomicInteger ID_COUNTER = new AtomicInteger(DEFAULT_STARTING_ID);
    private final String contract;
    private final String item;
    private final Integer width;
    private final UUID placer;
    private final Integer xloc;
    private final Integer yloc;
    private final Integer zloc;
    private List<PacketContainer> packets = new ArrayList<>();

    private String _id;

    public WriteNFT(
         String contract,
         String item,
         Integer width,
        UUID placer,
         Integer xloc,
        Integer yloc,
         Integer zloc
    ){
        this.contract = contract;
        this.item = item;
        this.width = width;
        this.placer = placer;
        this.xloc = xloc;
        this.yloc = yloc;
        this.zloc = zloc;
    }

    private String calculate_id(){
        return this.xloc + "_" + this.yloc + "_" + this.zloc;
    }

    private void createMaps(){
        BufferedImage image = getImage();
        BlockFace direction = getSignDirection();
        if(direction == null || image == null){
            return;
        }
        List bag = new ArrayList<>();
        for(int x = 0; x < this.getWidth(); x++){
            for (int y = 0; y < this.getWidth(); y++) {
                BufferedImage subimage = image.getSubimage(x * 128, y * 128, 128, 128);
                byte[] pixels = createPixels(subimage);
                UUID itemframe_uuid = UUID.randomUUID();
                Integer itemframe_id = ID_COUNTER.incrementAndGet();
                WrapperPlayServerSpawnEntity framePacket = new WrapperPlayServerSpawnEntity();
                framePacket.setUniqueId(itemframe_uuid);
                framePacket.setEntityID(itemframe_id);
                framePacket.setType(EntityType.ITEM_FRAME);
                switch (direction) {
                    case NORTH -> {
                        framePacket.setX(this.getXloc() - x);
                        framePacket.setY(this.getYloc() + y);
                        framePacket.setZ(this.getZloc());
                    }
                    case EAST -> {
                        framePacket.setX(this.getXloc());
                        framePacket.setY(this.getYloc() + y);
                        framePacket.setZ(this.getZloc() - x);
                    }
                    case SOUTH -> {
                        framePacket.setX(this.getXloc() + x);
                        framePacket.setY(this.getYloc() + y);
                        framePacket.setZ(this.getZloc());
                    }
                    case WEST -> {
                        framePacket.setX(this.getXloc());
                        framePacket.setY(this.getYloc() + y);
                        framePacket.setZ(this.getZloc()+ x);
                    }
                }
                framePacket.setYaw(0);
                framePacket.setPitch(0);
                List<WrappedWatchableObject> watcher = framePacket.getHandle().getWatchableCollectionModifier().readSafely(0);
                switch (direction) {
                    case NORTH -> framePacket.setObjectData(2);
                    case EAST -> framePacket.setObjectData(5);
                    case SOUTH -> framePacket.setObjectData(3);
                    case WEST -> framePacket.setObjectData(4);
                }
                PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.MAP);
                //packetContainer.getModifier().writeDefaults();
                packetContainer.getIntegers().write(0,itemframe_id);
   //             packetContainer.getBytes().write(0,(byte) 3);
 //               packetContainer.getBooleans().write(0,false);
                for (Field field : packetContainer.getModifier().getFields()){
                    Bukkit.getLogger().info(field.getType().toString());
                    Bukkit.getLogger().info(field.toString());
                    Bukkit.getLogger().info(field.toGenericString());
                }
                //packetContainer.getModifier().getField(4);
                //packetContainer.getBytes().write(1,(byte) 128);
              //packetContainer.getIntegers().write(2,128);
              //packetContainer.getIntegers().write(3,0);
              //packetContainer.getIntegers().write(4,0);

                //mapPacket.setScale((byte) 3);
                //mapPacket.setLocked(true);
                //mapPacket.setTrackingPosition(false);
                //mapPacket.setColumns(128);
                //mapPacket.setRows(128);
                //mapPacket.setData(pixels);
                WrapperPlayServerEntityMetadata entityPacket = new WrapperPlayServerEntityMetadata();
                entityPacket.setEntityID(itemframe_id);
                entityPacket.setMetadata(watcher);

                bag.add(framePacket.getHandle());
               // bag.add(packetContainer);
                //bag.add(mapPacket);
                //bag.add(entityPacket.getHandle());
            }
        }
        this.packets = bag;
    }

    private byte[] createPixels(BufferedImage image) {

        int pixelCount = image.getWidth() * image.getHeight();
        int[] pixels = new int[pixelCount];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        byte[] colors = new byte[pixelCount];
        for (int i = 0; i < pixelCount; i++) {
            colors[i] = MapPalette.matchColor(new Color(pixels[i], true));
        }

        return colors;
    }

    @Override
    public String getContract() {
        return contract;
    }

    public Gamer getGamer(){
        return state().getGamer(placer);
    }

    public String getId() {
        return calculate_id();
    }

    public void setId(String string) {
        this._id = calculate_id();
    }

    private BufferedImage getImage(){
        BufferedImage cachedBuffer = ExternalMetadataService.getCachedBuffer(this.getContract(), this.getItem());
        if(cachedBuffer == null){
            return null;
        }
        Image scaledInstance = cachedBuffer.getScaledInstance(this.getWidth() * 128, this.getWidth() * 128, BufferedImage.SCALE_SMOOTH);
        BufferedImage image = new BufferedImage(scaledInstance.getWidth(null), scaledInstance.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2D = image.createGraphics();
        g2D.drawImage(scaledInstance,0,0,null);
        g2D.dispose();
        return image;
    }

    @Override
    public String getItem() {
        return item;
    }

    public UUID getPlacer() {
        return this.placer;
    }

    private BlockFace getSignDirection(){
        Block block = Bukkit.getWorld("world").getBlockAt(this.getXloc(), this.getYloc(), this.getZloc());
        if(block.getBlockData().getMaterial().toString().contains("WALL_SIGN")){
            Bukkit.getLogger().info(String.valueOf(block.getBlockData()));
            return ((WallSign) block.getBlockData()).getFacing();
        }
        return null;
    }

    @Override
    public Integer getWidth() {return width;}

    public Integer getXloc(){return xloc;}

    public Integer getYloc(){return yloc;}

    public Integer getZloc(){return zloc;}

    public boolean isAir(){
        Block block = Bukkit.getWorld("world").getBlockAt(this.getXloc(), this.getYloc(), this.getZloc());
        return block.getBlockData().getMaterial().toString().contains("AIR");
    }

    public boolean sendGamer(Gamer gamer){
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        Bukkit.getLogger().info(this.packets.size() + " size");
        if(this.packets.size() > 0){
            for (PacketContainer packet : this.packets) {
                try {
                    Bukkit.getLogger().info(packet.toString());
                    protocolManager.sendServerPacket(gamer.getPlayer(),packet);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
        try{
            createMaps();
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }


}

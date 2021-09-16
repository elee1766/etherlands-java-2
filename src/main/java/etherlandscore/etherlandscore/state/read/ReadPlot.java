package etherlandscore.etherlandscore.state.read;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;

import static etherlandscore.etherlandscore.services.MasterService.state;

public class ReadPlot  {
    private final Integer id;
    private final Integer x;
    private final Integer z;
    private transient Chunk chunk;

    public ReadPlot(
        Integer id,
        Integer x,
        Integer z) {
      this.id = id;
      this.x = x;
      this.z = z;
    }
    public Chunk getChunk() {
      if (chunk == null) {
        this.chunk = Bukkit.getWorld("world").getChunkAt(x, z);
      }
      return chunk;
    }

    public District getDistrict() {
      if(this.id == null){
        return null;
      }
      return state().getDistrict(this.x, this.z);
    }

    public Integer getIdInt() {
      return this.id;
    }

    public Integer getX() {
      return x;
    }

    public Integer getZ() {
      return z;
    }

}

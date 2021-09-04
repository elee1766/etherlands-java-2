package etherlandscore.etherlandscore.state;

import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.enums.FlagValue;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.persistance.Couch.CouchPersister;
import etherlandscore.etherlandscore.state.read.District;
import etherlandscore.etherlandscore.state.write.WriteDistrict;
import etherlandscore.etherlandscore.state.read.Gamer;
import etherlandscore.etherlandscore.state.read.Team;
import etherlandscore.etherlandscore.state.write.*;
import etherlandscore.etherlandscore.util.Map2;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.jetlang.fibers.Fiber;
import org.jetlang.fibers.ThreadFiber;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.util.*;

public class Context<WriteMaps> {

  private CouchPersister couchPersister;

  public final Map<UUID, WriteGamer> gamers = new HashMap<>();
  public final Map<Integer, WriteDistrict> districts = new HashMap<>();
  public final Map<WritePlot, WriteDistrict> districtLocations = new HashMap<>();
  public final Map<String, WriteTeam> teams = new HashMap<>();
  public final Map<String, UUID> linked = new HashMap<>();
  public final Map<Integer, WritePlot> plots = new HashMap<>();
  public final Map2<Integer, Integer, Integer> plotLocations = new Map2<>();
  public final Map<String, WriteNFT> nftUrls = new HashMap<>();
  public final Map2<String,String,WriteNFT> nfts = new Map2<>();
  public final Set<WriteMap> maps = new HashSet<>();

  public Context(Channels channels){
    try {
      Fiber couchFiber = new ThreadFiber();
      this.couchPersister = new CouchPersister(channels, couchFiber);
      this.couchPersister.start();
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    couchPersister.populateContext(this);
  }

  public void saveAll(){
    couchPersister.saveContext(this);
  }

  public void context_create_gamer(UUID uuid) {
    if (!this.getGamers().containsKey(uuid)) {
      WriteGamer gamer = new WriteGamer(uuid);
      this.getGamers().put(uuid, gamer);
      couchPersister.update(gamer);
    }
  }

  public void district_add_plot(WriteDistrict district, WritePlot plot) {
    plot.addDistrict(district);
    district.addPlot(plot);
    couchPersister.update(plot);
    System.out.println("Plot updates");
    couchPersister.update((WriteTeam) plot.getTeamObject());
    System.out.println("Team Updated");
  }

  public void district_remove_plot(WriteDistrict district, WritePlot plot) {
    WriteTeam t = (WriteTeam) plot.getTeamObject();
    plot.removeDistrict(district);
    district.removePlot(plot);
    couchPersister.update(plot);
    if(t!=null){
      couchPersister.update(t);
    }
  }

  public void district_set_gamer_permission(
      WriteDistrict district, WriteGamer gamer, AccessFlags flag, FlagValue value) {
    district.setGamerPermission(gamer, flag, value);
    couchPersister.update((WriteTeam) district.getTeamObject());
  }

  public void district_set_group_permission(
      WriteDistrict district, WriteGroup writeGroup, AccessFlags flag, FlagValue value) {
    district.setGroupPermission(writeGroup, flag, value);
    couchPersister.update((WriteTeam) district.getTeamObject());
  }

  public void district_set_priority(WriteDistrict district, Integer priority) {
    district.setPriorityBound(priority);
    couchPersister.update((WriteTeam) district.getTeamObject());
  }

  public void gamer_add_friend(WriteGamer a, Gamer b) {
    a.addFriend(b);
    couchPersister.update(a);
  }

  public void gamer_link_address(WriteGamer gamer, String address) {
    gamer.setAddress(address);
    getLinks().put(address, gamer.getUuid());
    Map<Integer, WriteDistrict> district = this.getDistricts();
    for(Map.Entry mapElement : district.entrySet()){
      WriteDistrict wd = (WriteDistrict) mapElement.getValue();
      Bukkit.getLogger().info(String.valueOf(wd.getOwnerUUID()));
      if(wd.getOwnerUUID()==null){
        Bukkit.getLogger().info("Checking plot: " + wd.getId());
        Bukkit.getLogger().info(wd.getOwnerAddress() + " should equal " + address);
        if(wd.getOwnerAddress().equals(address)){
          Bukkit.getLogger().info("they are equal");
          wd.setOwner(address, gamer.getUuid());
          couchPersister.update(wd);
        }
      }
    }
    couchPersister.update(gamer);
  }

  public void gamer_remove_friend(WriteGamer a, Gamer b) {
    a.removeFriend(b);
    couchPersister.update(a);
  }

  public WriteGamer getGamer(UUID uuid) {
    return gamers.get(uuid);
  }

  public Map<UUID, WriteGamer> getGamers() {
    return gamers;
  }

  public Map2<String, String, WriteNFT> getNfts() {return nfts; }

  public Map<String, WriteNFT> getNftUrls() {return nftUrls; }

  public Set<WriteMap> getMaps() {return maps; }

  public Map<String, UUID> getLinks() {
    return linked;
  }

  public WritePlot getPlot(Integer x, Integer z) {
    return getPlot(plotLocations.get(x, z));
  }

  public WritePlot getPlot(Integer id) {
    return plots.get(id);
  }

  public Map2<Integer, Integer, Integer> getPlotLocations() {
    return plotLocations;
  }

  public Map<Integer, WritePlot> getPlots() {
    return plots;
  }

  public Map<Integer, WriteDistrict> getDistricts() {
    return districts;
  }

  public Team getTeam(String team) {
    return teams.get(team);
  }

  public Map<String, WriteTeam> getTeams() {
    return teams;
  }

  public void group_add_gamer(WriteGroup group, WriteGamer gamer) {
    gamer.addGroup(group);
    group.addMember(gamer);
    couchPersister.update((WriteTeam) group.getTeamObject());
    couchPersister.update(gamer);
  }

  public void group_remove_gamer(WriteGroup group, WriteGamer gamer) {
    group.removeMember(gamer);
    gamer.removeGroup(group.getName());
    couchPersister.update((WriteTeam) group.getTeamObject());
    couchPersister.update(gamer);
  }

  public void group_set_priority(WriteGroup group, Integer b) {
    group.setPrioritySafe(b);
    couchPersister.update((WriteTeam) group.getTeamObject());
  }

  public void district_reclaim_district(WriteDistrict district) {
    WriteTeam t = (WriteTeam) getTeam(district.getTeam());
    if(t!=null) {
      System.out.println("updating team");
      t.deleteDistrict(district.getId());
    }
    district.removeTeam();
    couchPersister.update(district);
    couchPersister.update(t);
  }

  public void district_set_owner(WriteDistrict district, String address) {
    UUID ownerUUID = this.getLinks().getOrDefault(address, null);
    district.setOwner(address, ownerUUID);
    couchPersister.update(district);
  }

  public void district_update_district(Integer id, Integer x, Integer z, String owner) {
    if (!this.getDistricts().containsKey(id)) {
      //this.getDistricts().put(id, new WriteDistrict());
    }
    WriteDistrict district = (WriteDistrict) this.getDistrict(id);
    WritePlot plot = this.getPlot(x,z);
    this.districtLocations.put(plot, district);
    district_set_owner(district, owner);
    couchPersister.update(district);
    couchPersister.update(plot);
  }

  public void team_add_gamer(WriteTeam team, WriteGamer gamer) {
    team.addMember(gamer);
    gamer.setTeam(team.getName());
    couchPersister.update(team);
    couchPersister.update(gamer);
  }

  public void team_create_group(WriteTeam team, String name) {
    team.createGroup(name);
    couchPersister.update(team);
  }

  public void team_create_team(WriteGamer gamer, String name) {
    WriteTeam team = new WriteTeam(gamer, name);
    if (!this.getTeams().containsKey(name)) {
      this.getTeams().put(name, team);
      gamer.setTeam(team.getName());
      couchPersister.update(gamer);
      couchPersister.update(team);
      System.out.println("Team created");
    }
    System.out.println("Team failed to create");
  }

  public void team_delegate_district(WriteTeam team, WriteDistrict district) {
    district_reclaim_district(district);
    team.addDistrict(district);
    district.setTeam(team.getName());
    couchPersister.update(team);
    couchPersister.update(district);
  }

  public void team_delete_district(WriteTeam team, WriteDistrict writeDistrict) {
    for (Integer plotId : team.getPlots()) {
      getPlot(plotId).removeDistrict(writeDistrict);
    }
    team.deleteDistrict(writeDistrict.getName());
    couchPersister.update(team);
  }

  public void team_delete_group(WriteTeam team, WriteGroup writeGroup) {
    for (UUID member : team.getMembers()) {
      WriteGamer gamer = getGamer(member);
      gamer.setTeam("");
      couchPersister.update(gamer);
    }
    WriteGamer gamer = getGamer(team.getOwnerUUID());
    gamer.setTeam("");
    team.deleteGroup(writeGroup.getName());
    couchPersister.update(team);
    couchPersister.update(gamer);
  }

  public void team_delete_team(WriteTeam writeTeam) {
    for (UUID member : writeTeam.getMembers()) {
      WriteGamer gamer = getGamer(member);
      gamer.setTeam("");
      gamer.clearGroups();
      couchPersister.update(gamer);
    }
    WriteGamer gamer = getGamer(writeTeam.getOwnerUUID());
    gamer.setTeam("");
    gamer.clearGroups();
    couchPersister.update(gamer);
    for (Integer id : writeTeam.getPlots()) {
      WritePlot plot = getPlot(id);
      plot.removeTeam();
      couchPersister.update(plot);
    }
    for (District d: writeTeam.getDistricts().values()) {
      WriteDistrict wd = (WriteDistrict) getDistrict(d.getIdInt());
      wd.removeTeam();
      couchPersister.update(wd);
    }
    couchPersister.delete(writeTeam);
    teams.remove(writeTeam.getName());
  }

  public void team_remove_gamer(WriteTeam team, WriteGamer gamer) {
    team.removeMember(gamer);
    gamer.setTeam("");
    gamer.clearGroups();
    couchPersister.update(team);
    couchPersister.update(gamer);
  }

  public void nft_create_nft(WriteNFT entity){
    this.getNftUrls().put(entity.getUrl(), entity);
    this.getNfts().put(entity.getContract(), entity.getItem(), entity);
    couchPersister.update(entity);
  }

  public void map_create_map(WriteMap entity){
    if(entity!=null) {
      couchPersister.update(entity);
    }
    this.getMaps().add(entity);
  }

  public void map_rerender_maps() {
    for(WriteMap map : this.maps) {
      Image image = null;
      try {
        image = ImageIO.read(map.getUrl());
      }catch(Exception ex){
        ex.printStackTrace();
      }
      Set<Integer> mapIds = map.getMaps();
      renderHelper(image, mapIds);
    }
  }

  public void renderHelper(Image image, Set<Integer> mapIds) {
    int width = (int) Math.sqrt(mapIds.size());
    Image tmp = image.getScaledInstance(width * 128, width * 128, Image.SCALE_SMOOTH);
    BufferedImage photo = new BufferedImage(width * 128, width * 128, BufferedImage.TYPE_INT_ARGB);
    ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
    ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < width; j++) {
        stacks.add(new ItemStack(Material.FILLED_MAP, 1));
        Graphics2D g2d = photo.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        images.add(photo.getSubimage(j * 128, i * 128, 128, 128));
      }
    }
    int i = 0;
    for (int map : mapIds) {
      for (MapRenderer render : Bukkit.getMap(map).getRenderers()) {
        Bukkit.getMap(map).removeRenderer(render);
      }
      int finalI = i;
      MapRenderer mr = new MapRenderer() {
        @Override
        public void render(MapView map, MapCanvas canvas, Player player) {
          canvas.drawImage(0, 0, images.get(finalI));
        }
      };
      Bukkit.getMap(map).addRenderer(mr);
      MapMeta meta = ((MapMeta) stacks.get(i).getItemMeta());
      meta.setMapView(Bukkit.getMap(map));
      stacks.get(i).setItemMeta(meta);
      i++;
    }
  }

  public District getDistrict(int i) {
    return this.districts.get(i);
  }

  public District getDistrict(int x, int z) {
    return this.districtLocations.get(this.plotLocations.get(x,z));
  }
}

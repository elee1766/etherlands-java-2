package etherlandscore.etherlandscore.persistance.Couch;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.ServerModule;
import etherlandscore.etherlandscore.persistance.Couch.state.*;
import etherlandscore.etherlandscore.singleton.SettingsSingleton;
import etherlandscore.etherlandscore.state.Context;
import etherlandscore.etherlandscore.state.write.*;
import org.bukkit.Bukkit;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbInstance;
import org.jetlang.fibers.Fiber;

import java.net.MalformedURLException;
import java.util.Map;

public class CouchPersister extends ServerModule {

  private final CouchDbInstance instance;
  private final GamerRepo gamerRepo;
  private final TownRepo townRepo;
  private final DistrictRepo districtRepo;
  private final NFTRepo nftRepo;
  private final MapRepo mapRepo;
  private final BankRecordRepo bankRecordRepo;
  private final CouchDbConnector linkConnector;
  private final Map<String, String> settings = SettingsSingleton.getSettings().getSettings();
  Channels channels;

  public CouchPersister(Channels channels, Fiber fiber) throws MalformedURLException {
    super(fiber);
    this.channels = channels;
    HttpClient httpClient =
        new StdHttpClient.Builder()
            .url(settings.get("CouchUrl"))
            .username(settings.get("CouchUsername"))
            .password(settings.get("CouchPassword"))
            .build();
    this.instance = new StdCouchDbInstance(httpClient);
    this.gamerRepo =
        new GamerRepo(this.instance.createConnector("gamers", true), WriteGamer.class);
    this.districtRepo = new DistrictRepo(this.instance.createConnector("districts", true), WriteDistrict.class);
    this.townRepo = new TownRepo(this.instance.createConnector("towns", true), WriteTown.class);
    this.mapRepo = new MapRepo(this.instance.createConnector("maps", true), WriteMap.class);
    this.nftRepo = new NFTRepo(this.instance.createConnector("nfts", true), WriteNFT.class);
    this.bankRecordRepo = new BankRecordRepo(this.instance.createConnector("bankrecord", true), WriteBankRecord.class);
    this.linkConnector = this.instance.createConnector("linked",true);

    channels.db_gamer.subscribe(fiber, this::write);
    channels.db_town.subscribe(fiber, this::write);
    channels.db_district.subscribe(fiber, this::write);
    channels.db_nft.subscribe(fiber, this::write);
    channels.db_map.subscribe(fiber, this::write);


    channels.db_gamer_delete.subscribe(fiber, this::remove);
    channels.db_town_delete.subscribe(fiber, this::remove);
    channels.db_district_delete.subscribe(fiber, this::remove);
    channels.db_nft_delete.subscribe(fiber, this::remove);
    channels.db_map_delete.subscribe(fiber, this::remove);
  }

  public void saveContext(Context context) {
      gamerRepo.save(context.getGamers().values());
      districtRepo.save(context.getDistricts().values());
      townRepo.save(context.getTowns().values());
      nftRepo.save(context.getNftUrls().values());
      bankRecordRepo.save(context.getBankRecords().values());
      mapRepo.save(context.getMaps());
  }

  public void populateContext(Context empty){
    Bukkit.getLogger().info("doing gamers");
    for (WriteGamer writeGamer : this.gamerRepo.getAll()) {
      empty.gamers.put(writeGamer.getUuid(),writeGamer);
      if(!writeGamer.getAddress().equals("")){
        if (writeGamer.getAddress() != null) {
          empty.linked.put(writeGamer.getAddress(), writeGamer.getUuid());
        }
      }
    }
    Bukkit.getLogger().info("doing towns");
    for (WriteTown writeTown : this.townRepo.getAll()) {
      empty.towns.put(writeTown.getName(),writeTown);
    }
    Bukkit.getLogger().info("doing districts");
    for (WriteDistrict writeDistrict : this.districtRepo.getAll()) {
      empty.districts.put(writeDistrict.getIdInt(),writeDistrict);
    }
    Bukkit.getLogger().info("doing maps");
    for (WriteMap writeMap: this.mapRepo.getAll()) {
      empty.maps.add(writeMap);
    }
    Bukkit.getLogger().info("doing nfts");
    for (WriteNFT writeNFT: this.nftRepo.getAll()) {
      empty.nfts.put(writeNFT.getContract(), writeNFT.getItem(), writeNFT);
      empty.nftUrls.put(writeNFT.getUrl(), writeNFT);
    }
    Bukkit.getLogger().info("doing bankRecords");
    for (WriteBankRecord bankRecord: this.bankRecordRepo.getAll()) {
      empty.bankRecords.put(bankRecord.getId(),bankRecord);
    }
    Bukkit.getLogger().info("done reading from db");
  }

  public void write(WriteGamer gamer){
    if (gamer.getUuid() != null) {
      this.gamerRepo.save(gamer);
    }
  }
  public void write(WriteDistrict district){
    this.districtRepo.save(district);
  }
  public void write(WriteTown town){
    this.townRepo.save(town);
  }
  public void write(WriteMap map){
    this.mapRepo.save(map);
  }
  public void write(WriteNFT nft){
    this.nftRepo.save(nft);
  }
  public void write(WriteBankRecord bankRecord){
    this.bankRecordRepo.save(bankRecord);
  }

  public void update(WriteGamer gamer){
    this.channels.db_gamer.publish(gamer);
  }
  public void update(WriteDistrict district){
    this.channels.db_district.publish(district);
  }
  public void update(WriteTown town){
    this.channels.db_town.publish(town);
  }
  public void update(WriteNFT nft) {this.channels.db_nft.publish(nft); }
  public void update(WriteMap map) {this.channels.db_map.publish(map); }

  public void update(WriteBankRecord bankRecord){this.channels.db_bank_record.publish(bankRecord);}


  public void remove(WriteGamer gamer){
    this.gamerRepo.delete(gamer);
  }
  public void remove(WriteDistrict district){
    this.districtRepo.delete(district);
  }
  public void remove(WriteTown town){
    this.townRepo.delete(town);
  }
  public void remove(WriteMap map){
    this.mapRepo.delete(map);
  }
  public void remove(WriteNFT nft){
    this.nftRepo.delete(nft);
  }

  public void delete(WriteGamer gamer){
    this.channels.db_gamer_delete.publish(gamer);
  }
  public void delete(WriteDistrict district){
    this.channels.db_district_delete.publish(district);
  }
  public void delete(WriteTown town){
    this.channels.db_town_delete.publish(town);
  }

}

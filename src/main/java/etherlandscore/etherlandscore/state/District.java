package etherlandscore.etherlandscore.state;

import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.enums.FlagValue;
import etherlandscore.etherlandscore.services.ImpatientAsker;
import etherlandscore.etherlandscore.singleton.WorldAsker;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

public class District {

  @NotNull
  private final Integer id;

  public District(int id) {
    this.id = id;
  }


  public boolean canGamerPerform(AccessFlags flag, Gamer gamer) {
    return ImpatientAsker.AskWorldBool(
        "district",
        this.getId(),
        "can_gamer",
        gamer.getUuidString(),
        flag.toString().toLowerCase()
    );
  }


  public FlagValue checkFlags(AccessFlags flag, Gamer gamer, FlagValue def) {
    return FlagValue.NONE;
  }


  public FlagValue checkFlags(AccessFlags flag, Team writeTeam, FlagValue def) {
    return FlagValue.NONE;
  }


  public String getId() {
    return this.getIdInt().toString();
  }

  @NotNull
  public Integer getIdInt() {
    return this.id;
  }


  public String getNickname() {
    return WorldAsker.GetNameOfDistrict(this.getIdInt());
  }


  public String getOwnerAddress() {
    return WorldAsker.GetOwnerOfDistrict(getId());
  }
  public Gamer getOwnerObject() {
    return new Gamer(WorldAsker.GetAddressUUID(this.getOwnerAddress()));
  }

  public UUID getOwnerUUID() {
    return WorldAsker.GetAddressUUID(this.getOwnerAddress());
  }

  public Set<Integer> getPlots() {
    return WorldAsker.GetPlotsInDistrict(getId());
  }

  public String getTown() {
    return ImpatientAsker.AskWorld(
        15,
        "district",
        this.getId(),
        "town"
    );
  }

  public Town getTownObject() {
    return new Town(getTown());
  }


  public boolean hasTown() {
    return getTown() != null;
  }


  public boolean isOwner(Gamer gamer) {
    String addr = this.getOwnerAddress();
    if (addr == null){
      return false;
    }
    return addr.equals(gamer.getAddress());
  }


  public String toString() {
    return this.getId();
  }

}

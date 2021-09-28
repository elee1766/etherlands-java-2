package etherlandscore.etherlandscore.state.write;

import etherlandscore.etherlandscore.services.ImpatientAsker;

import java.util.Set;

public class Team  {
  private final String name;

  private String town;

  public Team(String town, String name) {
    this.town = town;
    this.name = name;
  }

  public void addMember(Gamer gamer) {}

  public Set<Gamer> getMembers() {
    if(this.getName().equals("member")){
      return this.getTownObject().getMembers();
    }
    return ImpatientAsker.AskWorldGamerSet("town", this.getTown(), "team", this.getName(),"members");
  }


  public String getName() {
    return name;
  }


  public Integer getPriority() {
    return ImpatientAsker.AskWorldInteger("town",this.getTown(),"team",this.getName(),"priority");
  }

  public String getTown() {
    return town;
  }

  public void setTown(String town) {
    this.town = town;
  }


  public Town getTownObject() {
    return new Town(this.town);
  }

  public String toString() {
    return this.getName();
  }
}

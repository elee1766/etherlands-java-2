package etherlandscore.etherlandscore.persistance.Redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.dynamic.Commands;
import io.lettuce.core.dynamic.RedisCommandFactory;
import io.lettuce.core.dynamic.annotation.Command;
import io.lettuce.core.dynamic.annotation.CommandNaming;
import io.lettuce.core.dynamic.annotation.Param;
import io.lettuce.core.dynamic.annotation.Value;

import java.util.List;

public class RedisReader {
  RedisClient client;
  RedisCommandFactory factory;
  KeyCommands commands;

  public RedisReader() {
    this.client = RedisClient.create("redis://localhost");
    this.factory = new RedisCommandFactory(client.connect());
    this.commands = factory.getCommands(KeyCommands.class);
  }

  public interface KeyCommands extends Commands {
    @Command("GET plot:id:z")
    List<String> getPlotZ(@Param("id") String... id);

    @Command("GET plot:id:x")
    List<String> getPlotX(@Param("id") String... id);

    @Command("GET district:id:address")
    List<String> getDistrict(@Param("id") String... id);

    @Command("ZMSCORE districtZplot key")
    String districtOfPlot(String keys);

    @Command("ZARANGEBYSCORE districtZplot id")
    List<String> plotsInDistrict(String... keys);
  }

  public List<String> getPlotZ(String... id) {
    return this.commands.getPlotZ(id);
  }

  public List<String> getPlotX(String... id) {
    return this.commands.getPlotX(id);
  }

  public List<String> getDistrict(String... id) {
    return this.commands.getDistrict(id);
  }

  public String districtOfPlot(String key){
    return this.commands.districtOfPlot(key);
  }

  public List<String> plotsInDistrict(String... keys){
    return this.commands.plotsInDistrict(keys);
  }
}
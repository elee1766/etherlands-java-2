package etherlandscore.etherlandscore.services;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.enums.FlagValue;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.state.District;
import etherlandscore.etherlandscore.state.Gamer;
import etherlandscore.etherlandscore.state.Team;
import kotlin.Pair;
import org.jetlang.channels.MemoryChannel;
import org.jetlang.fibers.Fiber;
import org.jetlang.fibers.PoolFiberFactory;
import org.jetlang.fibers.ThreadFiber;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static etherlandscore.etherlandscore.singleton.SettingsSingleton.getSettings;

public class ImpatientAsker extends ListenerClient {

  private static final MemoryChannel<Pair<String, String>> message_channel = new MemoryChannel<>();
  private static final MemoryChannel<String> command_channel = new MemoryChannel<>();
  private static ZMQ.Socket subscriber;
  private static ZMQ.Socket publisher;
  private static PoolFiberFactory fact;
  private static boolean init = false;

  private static Cache<String, String> cache = Caffeine.newBuilder()
      .expireAfterWrite(15, TimeUnit.MINUTES)
      .maximumSize(10_000_000)
      .build();

  public ImpatientAsker(Channels channels, Fiber fiber) {
    super(channels, fiber);
    ZContext context = new ZContext();
    subscriber = context.createSocket(SocketType.SUB);
    subscriber.connect("tcp://127.0.0.1:"+getSettings().get("subscriber_port"));
    subscriber.subscribe("");

    fiber.execute(poll_subscribe());
    ThreadFiber publisherFiber = new ThreadFiber();
    ExecutorService service = Executors.newCachedThreadPool();
    fact = new PoolFiberFactory(service);
    publisher = context.createSocket(SocketType.PUB);
    publisher.connect("tcp://127.0.0.1:"+getSettings().get("publisher_port"));

    command_channel.subscribe(publisherFiber, this::publish);
    publisherFiber.start();
  }

  public static String Ask(Integer duration, String key){
    if (duration == 0) {
      return Ask(key);
    }
    String output = cache.getIfPresent(key);
    if (output == null) {
      output = ForceAsk(key);
    }
    if (duration > 0) {
      Fiber timeout = fact.create();
      timeout.schedule(() -> cache.invalidate(key), duration, TimeUnit.SECONDS);
      timeout.start();
    }
    if(output.equals("")){
      Fiber timeout = fact.create();
      timeout.schedule(() -> cache.invalidate(key), 5, TimeUnit.SECONDS);
      timeout.start();
      cache.put(key,output);
      return null;
    }
    return output;
  }
  public static String Ask(String key){
    String out = ForceAsk(key);
    if(out.equals("")){
      return null;
    }
    return out;
  }
  public static String ForceAsk(String key){
    CompletableFuture<String> completableFuture = new CompletableFuture<>();
    Fiber fiber = fact.create();
    Fiber timeout = fact.create();
    command_channel.publish(key);
    timeout.schedule(() -> completableFuture.complete(null), 20, TimeUnit.MILLISECONDS);
    message_channel.subscribe(
        fiber,
        x -> {
          if (x.getFirst().equals(key)) {
            if (x.getSecond().startsWith("error")) {
              completableFuture.complete("");
            }
            completableFuture.complete(x.getSecond());
            cache.put(key,x.getSecond());
          }
        });
    fiber.start();
    timeout.start();
    String answer;
    try {
      answer = completableFuture.get();
    } catch (Exception e) {
      timeout.dispose();
      fiber.dispose();
      return "";
    }
    fiber.dispose();
    timeout.dispose();
    if (answer == null) {
      return "";
    }
    return answer;
  }

  public static String AskWorld(String... keys) {
    return Ask("world:" + String.join(":", keys));
  }
  public static String AskWorld(Integer duration, String... keys) {
    return Ask(duration, "world:" + String.join(":", keys));
  }

  public static District AskWorldDistrict(String ...keys){
    return AskWorldDistrict(0,keys);
  }
  public static District AskWorldDistrict(Integer duration, String ...keys){
    String str = AskWorld(duration, keys);
    if (str == null) {
      return null;
    }
    try {
      return new District(Integer.parseInt(str));
    } catch (Exception e) {
      return null;
    }
  }

  public static Gamer AskWorldGamer(Integer duration, String... keys) {
    String str = AskWorld(duration, keys);
    if (str == null) {
      return null;
    }
    try {
      return new Gamer(UUID.fromString(str));
    } catch (Exception e) {
      return null;
    }
  }
  public static boolean AskWorldBool(String... keys) {
    return AskWorldBool(0,keys);
  }
  public static boolean AskWorldBool(Integer duration, String... keys) {
    String str = AskWorld(duration,keys);
    return str.equals("yes");
  }
  public static Set<Gamer> AskWorldGamerSet(Integer duration, String... keys) {
    String str = AskWorld(duration, keys);
    if (str == null) {
      return new HashSet<>();
    }
    Set<Gamer> output = new HashSet<>();
    for (String s : str.split(";")) {
      try {
        Gamer gamer = new Gamer(UUID.fromString(s));
        output.add(gamer);
      } catch (Exception ignored) {
      }
    }
    return output;
  }
  public static Set<Gamer> AskWorldGamerSet(String... keys) {
   return AskWorldGamerSet(0, keys);
  }

  public static Integer AskWorldInteger(Integer duration, String... keys){
    String str = AskWorld(duration, keys);
    if (str == null) {
      return null;
    }
    try {
      return Integer.parseInt(str);
    } catch (Exception e) {
      return null;
    }
  }
  public static Integer AskWorldInteger(String... keys) {
    return AskWorldInteger(0,keys);
  }

  public static Set<Integer> AskWorldIntegerSet(String... keys) {
    return AskWorldIntegerSet(0,keys);
  }
  public static Set<Integer> AskWorldIntegerSet(Integer duration, String... keys) {
    String str = AskWorld(duration, keys);
    if (str == null) {
      return new HashSet<>();
    }
    Set<Integer> output = new HashSet<>();
    for (String s : str.split(";")) {
      try {
        output.add(Integer.parseInt(s));
      } catch (Exception ignored) {
      }
    }
    return output;
  }

  public static Pair<Map<AccessFlags, FlagValue>,Map<AccessFlags, FlagValue>> AskWorldPermissionMaps(String...keys) {
    return AskWorldPermissionMaps(0,keys);
  }
  public static Pair<Map<AccessFlags, FlagValue>,Map<AccessFlags, FlagValue>> AskWorldPermissionMaps(Integer duration, String...keys) {
    String str = AskWorld(duration, keys);
    Map<AccessFlags,FlagValue> target = new HashMap<>();
    Map<AccessFlags,FlagValue> def= new HashMap<>();
    if (str == null) {
      return new Pair<>(target, def);
    }
    String[] both = str.split("%");
    for (String single  :both[0].split(";")) {
      String[] split = single.split("@");
      if(split.length == 2){
        try{
          target.put(AccessFlags.valueOf(split[0].toUpperCase()),FlagValue.valueOf(split[1].toUpperCase()));
        }catch (Exception ignored){}
      }
    }
    for (String single  :both[1].split(";")) {
      String[] split = single.split("@");
      if(split.length == 2){
        try{
          def.put(AccessFlags.valueOf(split[0].toUpperCase()),FlagValue.valueOf(split[1].toUpperCase()));
        }catch (Exception ignored){}
      }
    }
    return new Pair<>(target, def);
  }

  public static Map<String, Team> GetWorldTeams(String town) {
    return GetWorldTeams(0,town);
  }
  public static Map<String, Team> GetWorldTeams(Integer duration, String town) {
    String str = AskWorld(duration, "town", town, "teams");
    Map<String, Team> output = new HashMap<>();
    if(str == null){
      return output;
    }
    for (String s : str.split(";")) {
      try {
        output.put(s, new Team(town, s));
      } catch (Exception ignored) {
      }
    }
    return output;
  }


  public static UUID AskWorldUUID(Integer duration, String... keys) {
    String str = AskWorld(duration, keys);
    if (str == null) {
      return null;
    }
    try {
      return UUID.fromString(str);
    } catch (Exception e) {
      return null;
    }
  }
  public static UUID AskWorldUUID(String... keys) {
    return AskWorldUUID(0,keys);
  }

  public static Set<UUID> AskWorldUUIDSet(String... keys) {
    String str = AskWorld(keys);
    if (str == null) {
      return new HashSet<>();
    }
    Set<UUID> output = new HashSet<>();
    for (String s : str.split(";")) {
      try {
        output.add(UUID.fromString(s));
      } catch (Exception ignored) {
      }
    }
    return output;
  }

  private Runnable poll_subscribe() {
    return () -> {
      while (!Thread.currentThread().isInterrupted()) {
        String key = subscriber.recvStr();
        String content = subscriber.recvStr();
        //Bukkit.getLogger().info(key + "  -  " + content);
        message_channel.publish(new Pair<>(key, content));
      }
    };
  }

  private void publish(String s) {
    publisher.sendMore("ASK".getBytes(StandardCharsets.UTF_8));
    publisher.send(s.getBytes(StandardCharsets.UTF_8));
  }
}

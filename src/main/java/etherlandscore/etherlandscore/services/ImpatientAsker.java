package etherlandscore.etherlandscore.services;

import etherlandscore.etherlandscore.enums.AccessFlags;
import etherlandscore.etherlandscore.enums.FlagValue;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.state.write.District;
import etherlandscore.etherlandscore.state.write.Gamer;
import etherlandscore.etherlandscore.state.write.Team;
import kotlin.Pair;
import org.bukkit.Bukkit;
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

public class ImpatientAsker extends ListenerClient {

  private static final MemoryChannel<Pair<String, String>> message_channel = new MemoryChannel<>();
  private static final MemoryChannel<String> command_channel = new MemoryChannel<>();
  private static ZMQ.Socket subscriber;
  private static ZMQ.Socket publisher;
  private static PoolFiberFactory fact;
  private static boolean init = false;

  public ImpatientAsker(Channels channels, Fiber fiber) {
    super(channels, fiber);
    ZContext context = new ZContext();
    subscriber = context.createSocket(SocketType.SUB);
    subscriber.connect("tcp://127.0.0.1:10105");
    subscriber.subscribe("");

    fiber.execute(poll_subscribe());
    ThreadFiber publisherFiber = new ThreadFiber();
    ExecutorService service = Executors.newCachedThreadPool();
    fact = new PoolFiberFactory(service);
    publisher = context.createSocket(SocketType.PUB);
    publisher.connect("tcp://127.0.0.1:10106");

    command_channel.subscribe(publisherFiber, this::publish);
    init = true;
    publisherFiber.start();
  }

  public static String Ask(String key) {
    if (!init) {
      return null;
    }
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
              completableFuture.complete(null);
            }
            if(x.getSecond().equals("")){
              completableFuture.complete(null);
            }
            completableFuture.complete(x.getSecond());
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
      return null;
    }
    fiber.dispose();
    timeout.dispose();
    if (answer == null) {
      return null;
    }
    if (answer.equals("")) {
      return null;
    }
    return answer;
  }

  public static String AskWorld(String... keys) {
    return Ask("world:" + String.join(":", keys));
  }

  public static District AskWorldDistrict(String ...keys){
    String str = AskWorld(keys);
    if (str == null) {
      return null;
    }
    try {
      return new District(Integer.parseInt(str));
    } catch (Exception e) {
      return null;
    }
  }

  public static Gamer AskWorldGamer(String... keys) {
    String str = AskWorld(keys);
    if (str == null) {
      return null;
    }
    try {
      return new Gamer(UUID.fromString(str));
    } catch (Exception e) {
      return null;
    }
  }

  public static Set<Gamer> AskWorldGamerSet(String... keys) {
    String str = AskWorld(keys);
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

  public static Integer AskWorldInteger(String... keys) {
    String str = AskWorld(keys);
    if (str == null) {
      return null;
    }
    try {
      return Integer.parseInt(str);
    } catch (Exception e) {
      return null;
    }
  }

  public static Set<Integer> AskWorldIntegerSet(String... keys) {
    String str = AskWorld(keys);
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

  public static Map<AccessFlags, FlagValue> AskWorldPermissionMap(String...keys) {
    String str = AskWorld(keys);
    if (str == null) {
      return new HashMap<>();
    }
    Map<AccessFlags,FlagValue> output = new HashMap<>();
    for (String s : str.split(";")) {
      String[] split = s.split("@");
      if(split.length == 2){
        try{
          output.put(AccessFlags.valueOf(split[0].toUpperCase()),FlagValue.valueOf(split[1].toUpperCase()));
        }catch (Exception ignored){}
      }
    }
    return output;
  }

  public static Map<String, Team> AskWorldTeams(String town) {
    String str = AskWorld("town", town, "teams");
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

  public static UUID AskWorldUUID(String... keys) {
    String str = AskWorld(keys);
    if (str == null) {
      return null;
    }
    try {
      return UUID.fromString(str);
    } catch (Exception e) {
      return null;
    }
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
        Bukkit.getLogger().info(key + "  -  " + content);
        message_channel.publish(new Pair<>(key, content));
      }
    };
  }

  private void publish(String s) {
    publisher.sendMore("ASK".getBytes(StandardCharsets.UTF_8));
    publisher.send(s.getBytes(StandardCharsets.UTF_8));
  }
}

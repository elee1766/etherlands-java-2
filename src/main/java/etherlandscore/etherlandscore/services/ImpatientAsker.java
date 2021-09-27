package etherlandscore.etherlandscore.services;

import etherlandscore.etherlandscore.fibers.Channels;
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
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ImpatientAsker extends ListenerClient {

  static private ZMQ.Socket subscriber;
  static private ZMQ.Socket publisher;
  static private PoolFiberFactory fact;

  static private final MemoryChannel<Pair<String, String>> message_channel = new MemoryChannel<>();
  static private final MemoryChannel<String> command_channel = new MemoryChannel<>();

  static private boolean init = false;

  public ImpatientAsker(Channels channels, Fiber fiber){
    super(channels,fiber);
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

    command_channel.subscribe(publisherFiber,this::publish);
    init = true;
    publisherFiber.start();
  }

  private void publish(String s) {
    publisher.sendMore("ASK".getBytes(StandardCharsets.UTF_8));
    publisher.send(s.getBytes(StandardCharsets.UTF_8));
  }

  public static String AskWorld(String... keys){
    return Ask("world:" + String.join(":",keys));
  }

  public static Integer AskWorldInteger(String... keys){
    String str = AskWorld(keys);
    if(str == null){
      return null;
    }
    try{
      return Integer.parseInt(str);
    }catch(Exception e){
      return null;
    }
  }

  public static UUID AskWorldUUID(String... keys){
    String str = AskWorld(keys);
    if(str == null){
      return null;
    }
    try{
      return UUID.fromString(str);
    }catch(Exception e){
      return null;
    }
  }
  public static String Ask(String key){
    if(!init){
      return null;
    }
    CompletableFuture<String> completableFuture = new CompletableFuture<>();
    Fiber fiber = fact.create();
    Fiber timeout = fact.create();
    command_channel.publish(key);
    timeout.schedule(()-> completableFuture.complete(null),20, TimeUnit.MILLISECONDS);
    message_channel.subscribe(fiber, x->{
      if(x.getFirst().equals(key)){
        if(x.getSecond().startsWith("error")){
          completableFuture.complete(null);
        }
        completableFuture.complete(x.getSecond());
      }
      timeout.dispose();
    });
    timeout.start();
    fiber.start();
    String answer;
    try {
      answer = completableFuture
          .get();
    } catch (Exception e) {
      return null;
    }
    fiber.dispose();
    return answer;
  }

  private Runnable poll_subscribe(){
    return () -> {
      while(!Thread.currentThread().isInterrupted()){
        String key = subscriber.recvStr();
        String content = subscriber.recvStr();
        Bukkit.getLogger().info(key+"  -  "+content);
        message_channel.publish(new Pair<>(key,content));
      }
    };
  }

}

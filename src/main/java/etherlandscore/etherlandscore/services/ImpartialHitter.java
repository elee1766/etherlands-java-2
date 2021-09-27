package etherlandscore.etherlandscore.services;

import etherlandscore.etherlandscore.fibers.Channels;
import org.jetlang.channels.MemoryChannel;
import org.jetlang.fibers.Fiber;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.nio.charset.StandardCharsets;

public class ImpartialHitter extends ListenerClient {

  static private ZMQ.Socket publisher;

  static private final MemoryChannel<String> command_channel = new MemoryChannel<>();

  static private boolean init = false;

  public ImpartialHitter(Channels channels, Fiber fiber){
    super(channels,fiber);
    ZContext context = new ZContext();
    publisher = context.createSocket(SocketType.PUB);
    publisher.connect("tcp://127.0.0.1:10106");

    command_channel.subscribe(fiber,this::publish);
    init = true;
  }



  private void publish(String s) {
    publisher.sendMore("HIT".getBytes(StandardCharsets.UTF_8));
    publisher.send(s.getBytes(StandardCharsets.UTF_8));
  }

  public static void HitWorld(String... keys){
     Hit("world:" + String.join(":",keys));
  }


  public static void Hit(String key){
    if (init) {
      command_channel.publish(key);
    }
  }

}

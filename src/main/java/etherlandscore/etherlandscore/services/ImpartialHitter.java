package etherlandscore.etherlandscore.services;

import etherlandscore.etherlandscore.Menus.ComponentCreator;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.ChatTarget;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.state.sender.StateSender;
import etherlandscore.etherlandscore.state.District;
import etherlandscore.etherlandscore.state.Gamer;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetlang.channels.MemoryChannel;
import org.jetlang.fibers.Fiber;
import org.jetlang.fibers.ThreadFiber;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static etherlandscore.etherlandscore.services.MasterService.state;
import static etherlandscore.etherlandscore.singleton.SettingsSingleton.getSettings;

public class ImpartialHitter extends ListenerClient {

  private static final MemoryChannel<String> command_channel = new MemoryChannel<>();
  private static ZMQ.Socket publisher;
  private static ZMQ.Socket subscriber;
  private static boolean init = false;

  public ImpartialHitter(Channels channels, Fiber fiber) {
    super(channels, fiber);
    ZContext context = new ZContext();
    publisher = context.createSocket(SocketType.PUB);
    publisher.connect("tcp://127.0.0.1:"+getSettings().get("publisher_port"));

    ThreadFiber subscriberFiber = new ThreadFiber();
    subscriber = context.createSocket(SocketType.SUB);
    subscriber.connect("tcp://127.0.0.1:"+getSettings().get("subscriber_port"));
    subscriber.subscribe("CHAT");
    subscriberFiber.execute(poll_subscribe());
    subscriberFiber.start();
    command_channel.subscribe(fiber, this::publish);
    init = true;
  }

  public static void Hit(String key) {
    if (init) {
      command_channel.publish(key);
    }
  }

  public static void HitWorld(String... keys) {
    Hit("world:" + String.join(":", keys));
  }

  private Runnable poll_subscribe() {
    return () -> {
      while (!Thread.currentThread().isInterrupted()) {
        String key = subscriber.recvStr();
        String content = subscriber.recvStr();
        try {
        //  Bukkit.getLogger().info(key + "  -  " + content);
          String[] args = content.split(":");
          UUID uuid;
          uuid = UUID.fromString(args[1]);
          Gamer gamer = state().getGamer(uuid);
          switch (args[0]) {
            case "gamer":
              TextComponent component = ComponentCreator.AddHyperlinks(args[2]);
              StateSender.sendGamerComponent(channels, gamer, component);
              break;
            case "modal":
              String modal_type = args[2];
              if (modal_type.equals("district")) {
                int district_id = Integer.parseInt(args[3]);
                if (district_id != 0) {
                  this.channels.chat_message.publish(
                      new Message<>(
                          ChatTarget.gamer_district_info,
                          new Gamer(uuid),
                          new District(district_id)));
                }else{
                  this.channels.chat_message.publish(
                      new Message<>(
                          ChatTarget.gamer_town_info,
                          gamer,
                          gamer.getTownObject()
                          ));

                  }
                }
              break;
            default:
              break;
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    };
  }

  private void publish(String s) {
    publisher.sendMore("HIT".getBytes(StandardCharsets.UTF_8));
    publisher.send(s.getBytes(StandardCharsets.UTF_8));
  }
}

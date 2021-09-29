package etherlandscore.etherlandscore.state.sender;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.ChatTarget;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.singleton.CaptchaCreator;
import etherlandscore.etherlandscore.singleton.WorldHitter;
import etherlandscore.etherlandscore.state.Gamer;
import etherlandscore.etherlandscore.state.Team;
import etherlandscore.etherlandscore.state.Town;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class StateSender {

  public static void captcha(Channels channels, Gamer self) {
    String[] captcha = CaptchaCreator.createCaptcha();
    WorldHitter.CreateLinkRequest(self, captcha[0], captcha[1], captcha[2]);
    // send link to linking endpoint here
    channels.chat_message.publish(new Message<>(ChatTarget.gamer_captcha, self, captcha));
  }

  public static void sendGamerComponent(Channels channels, Gamer gamer, TextComponent component) {
    channels.chat_message.publish(new Message<>(ChatTarget.gamer, gamer, component));
  }

  public static void sendGamerInfo(Channels channels, Gamer gamer, Gamer target) {
    channels.chat_message.publish(new Message<>(ChatTarget.gamer_gamer_info, gamer, target));
  }

  public static void sendInfo(Channels channels, Gamer gamer, Town town) {
    channels.chat_message.publish(new Message<>(ChatTarget.gamer_town_info, gamer, town));
  }

  public static void sendMap(Channels channels, BaseComponent map, Gamer target) {
    channels.chat_message.publish(new Message<>(ChatTarget.gamer_send_map, map, target));
  }

  public static void sendTeamInfo(Channels channels, Gamer gamer, Team target) {
    channels.chat_message.publish(new Message<>(ChatTarget.gamer_team_info, gamer, target));
  }



}

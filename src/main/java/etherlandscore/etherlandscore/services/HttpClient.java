package etherlandscore.etherlandscore.services;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import etherlandscore.etherlandscore.eth.LinkInformation;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.fibers.ServerModule;
import org.bukkit.Bukkit;
import org.jetlang.fibers.Fiber;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpClient extends ServerModule implements HttpHandler {
  private final Channels channels;

  public HttpClient(Channels channels, Fiber fiber) {
    super(fiber);
    this.channels = channels;
  }

  @Override
  public void handle(HttpExchange httpExchange) throws IOException {
    Map<String, String> requestParamValue = null;
    if ("GET".equals(httpExchange.getRequestMethod())) {
      requestParamValue = handleGetRequest(httpExchange);
      handleResponse(httpExchange, requestParamValue);
    }
  }

  private Map<String, String> handleGetRequest(HttpExchange httpExchange)
      throws UnsupportedEncodingException {
    Map<String, String> queryMap = new HashMap<>();
    String[] pairs = httpExchange.getRequestURI().getQuery().split("&");
    for (String pair : pairs) {
      int idx = pair.indexOf("=");
      queryMap.put(
          URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8),
          URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8));
    }
    return queryMap;
  }

  private void handleResponse(HttpExchange httpExchange, Map<String, String> queryArgs)
      throws IOException {
    OutputStream outputStream = httpExchange.getResponseBody();
    StringBuilder htmlBuilder = new StringBuilder();
    htmlBuilder.append("<html>").append("<body>");
    try {
      if (!queryArgs.containsKey("signature")) {
        htmlBuilder.append("you did not include a signature with your message.");
      } else if (!queryArgs.containsKey("message")) {
        htmlBuilder.append("you did not include a signature with your message.");
      } else if (!queryArgs.containsKey("timestamp")) {
        htmlBuilder.append("you did not include a timestamp with your message.");
      } else if (!queryArgs.containsKey("address")) {
        htmlBuilder.append("you did not include an address with your message.");
      } else {
        htmlBuilder.append("attempting to register: ");
        String signature = queryArgs.get("signature");
        String address = queryArgs.get("address");
        String message = queryArgs.get("message");
        LinkInformation info = new LinkInformation(message, signature, address);
        long timestamp = 0;
        try {
          timestamp = Integer.parseInt(queryArgs.get("timestamp"));
        } catch (Exception e) {
          htmlBuilder.append("distorted timestamp");
        }
        long currenttime = System.currentTimeMillis() / 1000;
        if (timestamp <= currenttime) {
          htmlBuilder.append("signature expired");
        } else {
          try {
            info.pubkey();
          } catch (Exception e) {
            htmlBuilder.append("failed to verify your message-signature combination");
            Bukkit.getLogger().info(e.toString());
            Bukkit.getLogger().info(e.getMessage());
            e.printStackTrace();
          }
          if (info.didPass()) {
            Bukkit.getLogger().info(info.getPubkey());
            htmlBuilder
                .append("attempting to link address")
                .append(info.getPubkey())
                .append("<br>")
                .append("with minecraft player UUID")
                .append(info.uuid())
                .append("<br>");
            channels.master_command.publish(new Message(MasterCommand.player_link_address, info));
          } else {
            htmlBuilder.append("fields did not match");
          }
        }
      }
    } catch (Exception e) {
      Bukkit.getLogger().info(e.toString());
      Bukkit.getLogger().info(e.getMessage());
      e.printStackTrace();
    }
    htmlBuilder.append("</body>").append("</html>");
    // encode HTML content
    String htmlResponse =
        htmlBuilder.toString(); // StringEscapeUtils.escapeHtml(htmlBuilder.toString());
    // this line is a must
    httpExchange.sendResponseHeaders(200, htmlResponse.length());
    outputStream.write(htmlResponse.getBytes());
    outputStream.flush();
    outputStream.close();
  }
}

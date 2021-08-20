package etherlandscore.etherlandscore.eth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpsExchange;
import etherlandscore.etherlandscore.fibers.Channels;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class LinkHandler implements HttpHandler {
  Channels channels;

  public LinkHandler(Channels channels) {
    this.channels = channels;
  }

  @Override
  public void handle(HttpExchange httpExchange) throws IOException {
    HttpsExchange httpsExchange = (HttpsExchange) httpExchange;
    if (httpsExchange.getRequestMethod().equals("POST")) {
      StringBuilder sb = new StringBuilder();
      InputStream ios = httpsExchange.getRequestBody();
      int i;
      while ((i = ios.read()) != -1) {
        sb.append((char) i);
      }
      String jsonstring = sb.toString();
      ObjectMapper mapper = new ObjectMapper();
      Map<String, String> requestParamValue = mapper.readValue(jsonstring, Map.class);
      httpsExchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
      OutputStream outputStream = httpsExchange.getResponseBody();
      JsonObject returnjson = handleResponse(requestParamValue);
      byte[] returnbytes = returnjson.toString().getBytes(StandardCharsets.UTF_8);
      if (returnjson.get("error").getAsInt() == 0) {
        httpsExchange.sendResponseHeaders(200, returnbytes.length);
      }else{
        httpsExchange.sendResponseHeaders(400, returnbytes.length);
      }
      outputStream.write(returnbytes);
      outputStream.flush();
      outputStream.close();
    }
  }

  private JsonObject handleResponse(Map<String, String> queryArgs) {
    JsonObject jsonBuilder = new JsonObject();
    String signature = queryArgs.getOrDefault("signature", null);
    String address = queryArgs.getOrDefault("address", null);
    String message = queryArgs.getOrDefault("message", null);
    if (!(signature == null || address == null || message == null)) {
      LinkInformation info = new LinkInformation(message, signature, address);
      long timestamp;
      try {
        timestamp = Integer.parseInt(queryArgs.get("timestamp"));
      } catch (Exception e) {
        jsonBuilder.addProperty("error", 1);
        jsonBuilder.addProperty("data", "Malformed Timestamp");
        return jsonBuilder;
      }
      long currenttime = System.currentTimeMillis() / 1000;
      if (timestamp <= currenttime) {
        jsonBuilder.addProperty("error", 1);
        jsonBuilder.addProperty("data", "signature expired");
      } else {
        try {
          info.pubkey();
        } catch (Exception e) {
          Bukkit.getLogger().info(e.toString());
          Bukkit.getLogger().info(e.getMessage());
          e.printStackTrace();
          jsonBuilder.addProperty("error", 1);
          jsonBuilder.addProperty("data", e.getMessage());
          return jsonBuilder;
        }
        if (info.didPass()) {
          Bukkit.getLogger().info(info.getPubkey());
          jsonBuilder.addProperty("error", 0);
          jsonBuilder.addProperty("data", "");
        } else {
          jsonBuilder.addProperty("error", 1);
          jsonBuilder.addProperty("data", "invalid msg-sig combination");
        }
      }
      return jsonBuilder;
    }
    jsonBuilder.addProperty("error", 1);
    jsonBuilder.addProperty("data", "missing fields");
    return jsonBuilder;
  }
}

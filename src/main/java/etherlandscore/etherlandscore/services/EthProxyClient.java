package etherlandscore.etherlandscore.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import etherlandscore.etherlandscore.singleton.SettingsSingleton;
import kotlin.Pair;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Dsl;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EthProxyClient {
    private final Map<String, String> settings = SettingsSingleton.getSettings().getSettings();
    static private final AsyncHttpClient client = Dsl.asyncHttpClient();
    static private final String root = "http://localhost:10100/";

    public static Pair<String, Set<Integer>> view_district(Integer id) throws Exception{
        Set<Integer> res = new HashSet<>();
        String endpoint = root + "district/" + id.toString();
        Request req = Dsl.get(endpoint).build();
        Response resp = client.executeRequest(req).get();
        //Bukkit.getLogger().info(endpoint+ " " + resp.getResponseBody());
        JsonElement jsonElement = new JsonParser().parse(resp.getResponseBody());
        String addr = jsonElement.getAsJsonObject().get("owner").getAsString();
        JsonArray array = jsonElement.getAsJsonObject().get("contains").getAsJsonArray();
        for(int i = 0; i < array.size(); i ++){
            res.add(Integer.valueOf(array.get(i).getAsString()));
        }
        return new Pair<>(addr,res);
    }

    public static Pair<String, Set<Integer>> force_district(Integer id) throws Exception{
        Set<Integer> res = new HashSet<>();
        String endpoint = root + "distict_force/" + id.toString();
        Request req = Dsl.get(endpoint).build();
        Response resp = client.executeRequest(req).get();
        Bukkit.getLogger().info(endpoint+ " " + resp.getResponseBody());
        JsonElement jsonElement = new JsonParser().parse(resp.getResponseBody());
        String addr = jsonElement.getAsJsonObject().get("owner").getAsString();
        JsonArray array = jsonElement.getAsJsonObject().get("contains").getAsJsonArray();
        for(int i = 0; i < array.size(); i ++){
            res.add(Integer.valueOf(array.get(i).getAsString()));
        }
        return new Pair<>(addr,res);
    }

    public static Pair<Set<Integer>,Integer> find_districts(Integer block) throws Exception{
        Set<Integer> res = new HashSet<>();
        String endpoint = root + "since/" + block.toString();
        Request req = Dsl.get(endpoint).build();
        Response resp = client.executeRequest(req).get();
        Bukkit.getLogger().info(endpoint+ " " + resp.getResponseBody());
        JsonElement jsonElement = new JsonParser().parse(resp.getResponseBody());
        JsonArray array = jsonElement.getAsJsonObject().get("update").getAsJsonArray();
        for(int i = 0; i < array.size(); i ++){
            res.add(Integer.valueOf(array.get(i).getAsString()));
        }
        Integer blockNum = Integer.valueOf(jsonElement.getAsJsonObject().get("block").getAsString());
        return new Pair(res,blockNum);
    }

    public static Pair<Integer, Integer> locate_plot(Integer plot_id) throws Exception{
        String endpoint = root + "plot/" + plot_id.toString();
        Request req = Dsl.get(endpoint).build();
        Response resp = client.executeRequest(req).get();
        Bukkit.getLogger().info(endpoint+ " " + resp.getResponseBody());
        JsonElement jsonElement = new JsonParser().parse(resp.getResponseBody());
        JsonArray array = jsonElement.getAsJsonObject().get("coord").getAsJsonArray();
        return new Pair<>(array.get(0).getAsInt(),array.get(1).getAsInt());
    }
}

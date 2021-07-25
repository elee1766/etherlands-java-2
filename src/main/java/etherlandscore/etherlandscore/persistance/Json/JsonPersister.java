package etherlandscore.etherlandscore.persistance.Json;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import etherlandscore.etherlandscore.persistance.Persister;

import java.io.StringReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JsonPersister<DtoType> extends Persister {

  public JsonPersister(String filepath) {
    super(filepath);
  }

  public Set<JsonObject> loadSet(Gson gson) {
    Set<JsonObject> result = new HashSet<>();
    List<String> lines = readLines();
    for (String arrayString : lines) {
      result.add(gson.fromJson(arrayString, JsonObject.class));
    }
    return result;
  }

  @SuppressWarnings("unchecked")
  public DtoType readJson(Gson gson, Class clazz) {
    try {
      JsonReader reader = new JsonReader(new StringReader(read()));
      reader.setLenient(true);
      return gson.fromJson(reader, clazz);
    } catch (Exception e) {
      return null;
    }
  }
}

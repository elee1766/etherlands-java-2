package etherlandscore.etherlandscore.state;

import java.util.HashMap;
import java.util.Map;

public class LocaleStrings {

    private final Map<String, String> friends = new HashMap<>();
    private final Map<String, String> plots = new HashMap<>();
    private final Map<String, String> teams = new HashMap<>();

    public LocaleStrings localeStrings(LocaleStrings state) {
        return state;
    }

    public Map<String, String> getFriends() {
        return friends;
    }

    public Map<String, String> getPlots() {
        return plots;
    }

    public Map<String, String> getTeams() {
        return teams;
    }
}


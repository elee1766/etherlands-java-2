package etherlandscore.etherlandscore.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.ServerModule;
import etherlandscore.etherlandscore.persistance.Json.JsonPersister;
import etherlandscore.etherlandscore.state.*;
import org.bukkit.Bukkit;
import org.jetlang.fibers.Fiber;

public class LocaleServices extends ServerModule {
    private final Channels channels;
    private final Fiber fiber;

    private final JsonPersister<LocaleStrings> localeStringsPersister;

    private final Gson gson;

    public LocaleServices(Channels channels, Fiber fiber) {
        super(fiber);
        this.channels = channels;
        this.fiber = fiber;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        String root = Bukkit.getServer().getPluginManager().getPlugin("EtherlandsCore").getDataFolder().getAbsolutePath();
        String locale = Bukkit.getServer().getPluginManager().getPlugin("EtherlandsCore").getDataFolder().getAbsolutePath();
        this.localeStringsPersister = new JsonPersister<>(root + "/locale.json");
        LocaleStrings locales = localeStringsPersister.readJson(gson, LocaleStrings.class);
    }
}

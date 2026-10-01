package plugin;

import plugin.commands.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import plugin.handlers.ChunkDamageHandler;

public final class CustomPlugin extends JavaPlugin {

    PluginConfig pluginConfig = new PluginConfig();

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getLogger().info("starting");

        //Challenges
        getCommand("randomizeRecipes").setExecutor(new RandomizeRecipes());
        new ChunkDamageHandler(this);

        // Minigames
        getCommand("startDeathSwap").setExecutor(new StartDeathSwap(this));
        getCommand("startBlockShuffle").setExecutor(new StartBlockShuffle(this));

        // QOL commands
        getCommand("setDefaultRecipes").setExecutor(new SetDefaultRecipes());
        getCommand("stopAllEvents").setExecutor(new StopAllEvents(this));

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getLogger().info("shutting down");
    }
}

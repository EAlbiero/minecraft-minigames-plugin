package plugin;

import plugin.commands.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import plugin.handlers.ChunkDamageHandler;
import plugin.handlers.PlayerDeathHandler;

public final class CustomPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getLogger().info("starting");

        // Handlers
        new ChunkDamageHandler(this);
        new PlayerDeathHandler(this);

        // Challenges
        getCommand("randomizeRecipes").setExecutor(new RandomizeRecipes());
        getCommand("toggleDeleteChunkOnDamage").setExecutor(new ToggleDeleteChunkOnDamage());
        getCommand("setDeleteChunkDelay").setExecutor(new SetDeleteChunkDelay());
        getCommand("setDeleteChunkPenalty").setExecutor(new SetDeleteChunkPenalty());

        // Minigames
        getCommand("startDeathSwap").setExecutor(new StartDeathSwap(this));
        getCommand("startBlockShuffle").setExecutor(new StartBlockShuffle(this));

        // QOL commands
        getCommand("setDefaultRecipes").setExecutor(new SetDefaultRecipes());
        getCommand("stopAllEvents").setExecutor(new StopAllEvents(this));
        getCommand("coord").setExecutor(new Coord());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getLogger().info("shutting down");
    }
}

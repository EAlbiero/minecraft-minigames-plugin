package plugin;

import plugin.commands.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import plugin.commands.Coord;
import plugin.commands.reciperandomizer.RandomizeRecipes;
import plugin.commands.reciperandomizer.SetDefaultRecipes;
import plugin.commands.chunkdeletion.SetDeleteChunkDelay;
import plugin.commands.chunkdeletion.SetDeleteChunkPenalty;
import plugin.commands.blockshuffle.StartBlockShuffle;
import plugin.commands.deathswap.StartDeathSwap;
import plugin.commands.chunkdeletion.ToggleDeleteChunkOnDamage;
import plugin.handlers.chunkdeletion.PlayerDamageHandler;
import plugin.handlers.PlayerDeathHandler;

public final class CustomPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getLogger().info("starting");

        // Handlers
        new PlayerDamageHandler(this);
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

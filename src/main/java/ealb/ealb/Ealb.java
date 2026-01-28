package ealb.ealb;

import ealb.ealb.commands.*;
import ealb.ealb.handlers.WoodHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public final class Ealb extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getLogger().info("starting");

        //Challenges
        getCommand("randomizeRecipes").setExecutor(new RandomizeRecipes());

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

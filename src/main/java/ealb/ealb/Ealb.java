package ealb.ealb;

import ealb.ealb.commands.StartBlockShuffle;
import ealb.ealb.commands.StartDeathSwap;
import ealb.ealb.commands.StopAllEvents;
import ealb.ealb.handlers.WoodHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public final class Ealb extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getLogger().info("starting");

        ItemStack bottle = new ItemStack(Material.EXPERIENCE_BOTTLE, 1);

        ShapedRecipe expBottle = new ShapedRecipe(bottle);

        expBottle.shape("*%*","%B%","*%*");

        expBottle.setIngredient('*', Material.INK_SAC, 2);
        expBottle.setIngredient('%', Material.SUGAR);
        expBottle.setIngredient('B', Material.GLASS_BOTTLE);

        getServer().addRecipe(expBottle);

        getCommand("startDeathSwap").setExecutor(new StartDeathSwap(this));
        getCommand("startBlockShuffle").setExecutor(new StartBlockShuffle(this));
        getCommand("stopAllEvents").setExecutor(new StopAllEvents(this));

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getLogger().info("shutting down");
    }
}

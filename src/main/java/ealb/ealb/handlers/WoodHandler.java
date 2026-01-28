package ealb.ealb.handlers;

import ealb.ealb.Ealb;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class WoodHandler implements Listener {
    public WoodHandler(Ealb plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onWoodPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();

        if (block.getType() != Material.BIRCH_WOOD) {
            return;
        }
        block.setType(Material.TNT);
        Bukkit.getLogger().info("Block was placed!");
    }
}

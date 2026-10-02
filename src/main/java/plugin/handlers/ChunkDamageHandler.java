package plugin.handlers;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import plugin.CustomPlugin;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import plugin.PluginConfig;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

public class ChunkDamageHandler implements Listener {
    public static List<Chunk> deletedChunks = new ArrayList<Chunk>();
    public static Chunk chunkToBeDeleted;
    private CustomPlugin plugin;

    public ChunkDamageHandler(CustomPlugin customPlugin) {
        plugin = customPlugin;
        Bukkit.getPluginManager().registerEvents(this, customPlugin);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) throws InterruptedException {
        if (!PluginConfig.getInstance().isDeleteChunkEnabled()) {return;}
        EntityType entityType = event.getEntityType();
        if (entityType != EntityType.PLAYER) return;

        Entity player = event.getEntity();
        chunkToBeDeleted = getEntityChunk(player);

        Bukkit.getLogger().info("Deleting chunk");
        Bukkit.getScheduler().runTaskLater(plugin, this::deleteChunk, 20*PluginConfig.getInstance().getDeleteChunkDelaySeconds());
        Bukkit.getLogger().info("Chunk deleted");

    }

    private Chunk getEntityChunk(Entity entity) {
        return entity.getLocation().getChunk();
    }

    private void deleteChunk() {
        Chunk chunk = chunkToBeDeleted;
        if (deletedChunks.contains(chunk)) return;
        deletedChunks.add(chunk);

        World world = chunk.getWorld();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = world.getMinHeight(); y < world.getMaxHeight(); y++) {
                    if (chunk.getBlock(x, y, z).getType() == Material.AIR) {continue;}
                    chunk.getBlock(x, y, z).setType(Material.AIR, false);
                }
            }
        }

    }

}

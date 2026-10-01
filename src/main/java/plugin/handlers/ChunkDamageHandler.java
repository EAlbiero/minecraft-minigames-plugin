package plugin.handlers;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import plugin.CustomPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

public class ChunkDamageHandler implements Listener {
    public static List<Chunk> deletedChunks = new ArrayList<Chunk>();

    public ChunkDamageHandler(CustomPlugin customPlugin) {
        Bukkit.getPluginManager().registerEvents(this, customPlugin);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) throws InterruptedException {
        Bukkit.getLogger().info("Event detected");
        EntityType entityType = event.getEntityType();
        if (entityType != EntityType.PLAYER) return;
        Entity player = event.getEntity();
        Bukkit.getLogger().info("Player damage detected");
        Chunk c = getEntityChunk(player);
        Bukkit.getLogger().info("Deleting chunk");

        deleteChunk(c);
        Bukkit.getLogger().info("Chunk deleted");

    }

    private Chunk getEntityChunk(Entity entity) {
        return entity.getLocation().getChunk();
    }

    private void deleteChunk(Chunk chunk) throws InterruptedException {
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

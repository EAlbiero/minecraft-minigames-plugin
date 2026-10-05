package plugin.handlers;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.generator.WorldInfo;
import org.bukkit.scheduler.BukkitTask;
import plugin.CustomPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import plugin.PluginConfig;
import plugin.common.ProgressBar;
import plugin.common.Tasks;

import java.util.ArrayList;
import java.util.List;

public class ChunkDamageHandler implements Listener {
    public static List<Chunk> deletedChunks = new ArrayList<>();
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
        Chunk chunkToBeDeleted = getEntityChunk(player);
        if (deletedChunks.contains(chunkToBeDeleted)) return;
        if (event.getFinalDamage() <=0) return;

        String playerName = event.getEntity().getName();
        Bukkit.broadcastMessage(
                ChatColor.RED + "" + ChatColor.BOLD + "Warning! Player " + playerName + " took damage"
        );

        deletedChunks.add(chunkToBeDeleted);
        BukkitTask highlightTask = Bukkit.getScheduler().runTaskTimer(plugin
                , () -> highlightChunkToBeDeleted(chunkToBeDeleted)
                ,0, PluginConfig.getInstance().getChunkHighlightIntervalTicks());

        ProgressBar progressBar = new ProgressBar(""
                , (chunkToBeDeleted
                .getWorld()
                .getEnvironment()
                .equals(World.Environment.NETHER)) ? BarColor.GREEN : BarColor.RED
                , chunkToBeDeleted
                , PluginConfig.getInstance().getDeleteChunkDelaySeconds());
        progressBar.lockToTask(highlightTask);

        Tasks.cancelTaskLater(plugin
                , highlightTask
                , 20*PluginConfig.getInstance().getDeleteChunkDelaySeconds());

        Bukkit.getScheduler().runTaskLater(plugin
                , () -> deleteChunk(chunkToBeDeleted)
                , 20L *PluginConfig.getInstance().getDeleteChunkDelaySeconds());
        Bukkit.getLogger().info("Chunk deleted");

    }

    private Chunk getEntityChunk(Entity entity) {
        return entity.getLocation().getChunk();
    }

    private void deleteChunk(Chunk chunk) {
        World world = chunk.getWorld();
        int minY = world.getMinHeight();
        int maxY = world.getMaxHeight();
        long startedAt = System.nanoTime();
        int changedBlocks = 0;

        try {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = minY; y < maxY; y++) {
                        Block block = chunk.getBlock(x, y, z);
                        if (block.getType().isAir()) {
                            continue;
                        }
                        block.setType(Material.AIR, false);
                        changedBlocks++;
                    }
                }
            }
        } finally {
            deletedChunks.remove(chunk);
        }

        double elapsedMs = (System.nanoTime() - startedAt) / 1_000_000.0;
        plugin.getLogger().info(
                "Deleted chunk (%d, %d) in %s: %,d blocks, %.2f ms"
                        .formatted(chunk.getX(), chunk.getZ(), world.getName(), changedBlocks, elapsedMs)
        );
    }

    private void highlightChunkToBeDeleted(Chunk chunk) {
        World world = chunk.getWorld();
        int minX = chunk.getX() * 16;
        int minZ = chunk.getZ() * 16;
        int maxX = minX + 16;
        int maxZ = minZ + 16;
        double viewDistance = 48.0;
        double viewDistanceSquared = viewDistance * viewDistance;

        Color color = world.getEnvironment() == World.Environment.NETHER
                ? Color.LIME : Color.RED;
        Particle.DustOptions dust = new Particle.DustOptions(color, 1.0f);

        for (Player player : world.getPlayers()) {
            Location location = player.getLocation();
            double nearestX = Math.max(minX, Math.min(location.getX(), maxX));
            double nearestZ = Math.max(minZ, Math.min(location.getZ(), maxZ));
            double dx = location.getX() - nearestX;
            double dz = location.getZ() - nearestZ;
            if (dx * dx + dz * dz > viewDistanceSquared) {
                continue;
            }

            double startY = location.getY();
            for (double y = startY; y < startY + 20; y += 3) {
                highlightChunk(player, minX, minZ, maxX, maxZ, y, dust);
            }
        }
    }

    private void highlightChunk(Player player, int minX, int minZ, int maxX, int maxZ,
                                double y, Particle.DustOptions dust) {
        for (int offset = 0; offset < 16; offset++) {
            player.spawnParticle(
                    Particle.DUST, minX + offset, y, minZ,
                    1, 0, 0, 0, 0, dust);

            player.spawnParticle(
                    Particle.DUST, maxX, y, minZ + offset,
                    1, 0, 0, 0, 0, dust);

            player.spawnParticle(
                    Particle.DUST, maxX - offset, y, maxZ,
                    1, 0, 0, 0, 0, dust);

            player.spawnParticle(
                    Particle.DUST, minX, y, maxZ - offset,
                    1, 0, 0, 0, 0, dust);
        }
    }
}

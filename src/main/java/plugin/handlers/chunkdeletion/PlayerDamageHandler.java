package plugin.handlers.chunkdeletion;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import plugin.CustomPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import plugin.config.PluginConfig;
import plugin.common.ProgressBar;
import plugin.common.Tasks;

import java.util.ArrayList;
import java.util.List;

public class PlayerDamageHandler implements Listener {
    private static List<Chunk> deletedChunks = new ArrayList<>();
    private final CustomPlugin plugin;

    public PlayerDamageHandler(CustomPlugin customPlugin) {
        plugin = customPlugin;
        Bukkit.getPluginManager().registerEvents(this, customPlugin);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!isEventValid(event)) return;

        Player player = (Player) event.getEntity();
        broadcastPlayerDamageMessage(player);

        Chunk chunkToBeDeleted = getEntityChunk(player);
        processChunkToBeDeleted(chunkToBeDeleted);
        Bukkit.getLogger().info("Chunk deleted");

    }

    private void processChunkToBeDeleted(Chunk chunk) {
        deletedChunks.add(chunk);
        BukkitTask highlightTask = Bukkit.getScheduler().runTaskTimer(plugin
                , () -> highlightChunkForNearbyPlayers(chunk)
                ,0, PluginConfig.getInstance().getChunkHighlightIntervalTicks());

        ProgressBar progressBar = createChunkDeletionProgressBar(chunk);
        progressBar.lockToTask(highlightTask);

        Tasks.cancelTaskLater(plugin
                , highlightTask
                , 20*PluginConfig.getInstance().getDeleteChunkDelaySeconds());

        Bukkit.getScheduler().runTaskLater(plugin
                , () -> deleteChunk(chunk)
                , 20L *PluginConfig.getInstance().getDeleteChunkDelaySeconds());

    }

    private void broadcastPlayerDamageMessage(Player player) {
        String playerName = player.getName();
        Bukkit.broadcastMessage(
                ChatColor.RED + "" + ChatColor.BOLD + "Warning! Player " + playerName + " took damage"
        );
    }

    private ProgressBar createChunkDeletionProgressBar(Chunk chunk) {
        BarColor color = getChunkDeletionProgressBarColor(chunk);
        int deleteChunkDelay = PluginConfig.getInstance().getDeleteChunkDelaySeconds();
        return new ProgressBar(""
                , color
                , chunk
                , deleteChunkDelay);
    }

    private Chunk getEntityChunk(Entity entity) {
        return entity.getLocation().getChunk();
    }

    private Color getChunkHighlightColor(Chunk chunk) {
        if (chunk.getWorld().getEnvironment().equals(World.Environment.NETHER))
                return Color.LIME;
        return Color.RED;
    }

    private BarColor getChunkDeletionProgressBarColor(Chunk chunk) {
        if (chunk.getWorld().getEnvironment().equals(World.Environment.NETHER))
            return BarColor.GREEN;
        return BarColor.RED;
    }

    private boolean isEventValid(EntityDamageEvent event) {
        if (!PluginConfig.getInstance().isDeleteChunkEnabled()) return false;

        EntityType entityType = event.getEntityType();
        if (entityType != EntityType.PLAYER) return false;

        Entity player = event.getEntity();
        Chunk chunkToBeDeleted = getEntityChunk(player);
        if (deletedChunks.contains(chunkToBeDeleted)) return false;

        if (event.getFinalDamage() <=0) return false;
        return true;
    }

    private void deleteChunk(Chunk chunk) {
        World world = chunk.getWorld();
        int minY = world.getMinHeight();
        int maxY = world.getMaxHeight();

        try {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = minY; y < maxY; y++) {
                        Block block = chunk.getBlock(x, y, z);
                        if (block.getType().isAir())
                            continue;
                        block.setType(Material.AIR, false);
                    }
                }
            }
        } finally {
            deletedChunks.remove(chunk);
        }
    }

    private void highlightChunkForNearbyPlayers(Chunk chunk) {
        World world = chunk.getWorld();
        double viewDistance = 48.0;
        for (Player player : world.getPlayers()) {
            if (!playerCanSeeChunk(player, chunk, viewDistance)) return;
            highlightChunkForPlayer(chunk, player);
        }
    }

    private void highlightChunkForPlayer(Chunk chunk, Player player) {
        int minX = chunk.getX() * 16;
        int minZ = chunk.getZ() * 16;
        int maxX = minX + 16;
        int maxZ = minZ + 16;
        Particle.DustOptions highlightEffect = getHighlightParticleOptionsForChunk(chunk);
        highlightRangeForPlayer(highlightEffect, player, minX, maxX, minZ, maxZ);
    }

    private void highlightRangeForPlayer(Particle.DustOptions highlightEffect, Player player, int minX, int maxX, int minZ, int maxZ) {
        double startY = player.getLocation().getY();
        for (double y = startY; y < startY + 20; y += 3) {
            for (int offset = 0; offset < 16; offset++) {
                player.spawnParticle(
                        Particle.DUST, minX + offset, y, minZ,
                        1, 0, 0, 0, 0, highlightEffect);

                player.spawnParticle(
                        Particle.DUST, maxX, y, minZ + offset,
                        1, 0, 0, 0, 0, highlightEffect);

                player.spawnParticle(
                        Particle.DUST, maxX - offset, y, maxZ,
                        1, 0, 0, 0, 0, highlightEffect);

                player.spawnParticle(
                        Particle.DUST, minX, y, maxZ - offset,
                        1, 0, 0, 0, 0, highlightEffect);
            }
        }
    }

    private boolean playerCanSeeChunk(Player player, Chunk chunk, double viewDistance) {
        int minX = chunk.getX() * 16;
        int minZ = chunk.getZ() * 16;
        int maxX = minX + 16;
        int maxZ = minZ + 16;

        Location location = player.getLocation();
        double nearestX = Math.max(minX, Math.min(location.getX(), maxX));
        double nearestZ = Math.max(minZ, Math.min(location.getZ(), maxZ));

        double dx = location.getX() - nearestX;
        double dz = location.getZ() - nearestZ;
        return dx * dx + dz * dz < viewDistance*viewDistance;
    }

    private Particle.DustOptions getHighlightParticleOptionsForChunk(Chunk chunk) {
        Color color = getChunkHighlightColor(chunk);
        float particleSize = 1;
        return new Particle.DustOptions(color, particleSize);
    }
}

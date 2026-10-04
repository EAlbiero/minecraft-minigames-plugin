package plugin.handlers;

import org.bukkit.*;
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
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = world.getMinHeight(); y < world.getMaxHeight(); y++) {
                    if (chunk.getBlock(x, y, z).getType() == Material.AIR) {continue;}
                    chunk.getBlock(x, y, z).setType(Material.AIR, false);
                }
            }
        }
        deletedChunks.remove(chunk);

    }
    private void highlightChunkToBeDeleted(Chunk chunk) {
        double dh = 20;
        double yMax;
        double yMin;
        World world = chunk.getWorld();
        List<Player> players = world.getPlayers();
        for (Player player : players) {
            yMin=player.getLocation().getY();
            yMax=yMin+dh;
            for (double i = yMin; i < yMax; i+=3) {
                highlightChunk(chunk, i);
            }
        }

    }

    private void highlightChunk(Chunk chunk, double y) {
        World world = chunk.getWorld();

        int minX = chunk.getX() * 16;
        int minZ = chunk.getZ() * 16;
        int maxX = minX + 16;
        int maxZ = minZ + 16;

        Particle.DustOptions dust =
                new Particle.DustOptions(Color.RED, 1.0f);
        if (world.getEnvironment().equals(World.Environment.NETHER)) {
            dust = new Particle.DustOptions(Color.LIME, 1.0f);
        }

        for (double offset = 0; offset < 16; offset += 0.5) {
            world.spawnParticle(
                    Particle.DUST, minX + offset, y, minZ,
                    1, 0, 0, 0, 0, dust);

            world.spawnParticle(
                    Particle.DUST, maxX, y, minZ + offset,
                    1, 0, 0, 0, 0, dust);

            world.spawnParticle(
                    Particle.DUST, maxX - offset, y, maxZ,
                    1, 0, 0, 0, 0, dust);

            world.spawnParticle(
                    Particle.DUST, minX, y, maxZ - offset,
                    1, 0, 0, 0, 0, dust);
        }
    }
}

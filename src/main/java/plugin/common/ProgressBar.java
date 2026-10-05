package plugin.common;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.CompletableFuture;

@Getter
@Setter
public class ProgressBar {

    protected BossBar bar;
    protected Chunk chunk;
    protected int lifetime;

    public ProgressBar(String title, BarColor color, Chunk chunk, int lifetime) {
        this.bar = Bukkit.createBossBar(title, color, BarStyle.SOLID);
        this.chunk = chunk;
        this.bar.setVisible(true);
        this.bar.setProgress(1);
        this.setLifetime(lifetime);
        this.startUpdate();
    }

    public CompletableFuture<Integer> lockToTask(BukkitTask task) {
        CompletableFuture<Integer> completion = new CompletableFuture<>();
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if (task.isCancelled()) {
                        ProgressBar.this.removeBar();
                        this.cancel();
                        completion.complete(0);
                        return;
                    }
                    ProgressBar.this.updateVisibility();
                } catch (Exception exception) {
                    this.cancel();
                    completion.completeExceptionally(exception);
                }
            }
        }.runTaskTimer(task.getOwner(), 0L, 5L);
        return completion;
    }

    private CompletableFuture<Integer> startUpdate() {
        int duration = this.getLifetime();
        if (duration <= 0) {
            throw new IllegalArgumentException("Progress bar lifetime must be positive");
        }

        CompletableFuture<Integer> completion = new CompletableFuture<>();
        new BukkitRunnable() {
            private int remainingSeconds = duration;

            @Override
            public void run() {
                try {
                    if (!ProgressBar.this.getBar().isVisible()
                            || ProgressBar.this.getBar().getProgress() <= 0) {
                        this.cancel();
                        completion.complete(0);
                        return;
                    }

                    remainingSeconds--;
                    ProgressBar.this.getBar().setProgress(
                            (double) remainingSeconds / duration);
                    ProgressBar.this.playUpdateSound();
                    if (remainingSeconds == 0) {
                        this.cancel();
                        completion.complete(0);
                    }
                } catch (Exception exception) {
                    this.cancel();
                    completion.completeExceptionally(exception);
                }
            }
        }.runTaskTimer(JavaPlugin.getProvidingPlugin(ProgressBar.class), 20L, 20L);
        return completion;
    }

    private void playUpdateSound() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (this.isPlayerInsideChunk(player)) {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.7f, 1.0f);
            }
        }
    }

    private boolean isPlayerInsideChunk(Player player) {
        Location location = player.getLocation();

        return this.getChunk().getWorld().equals(location.getWorld())
                && (location.getBlockX() >> 4) == this.getChunk().getX()
                && (location.getBlockZ() >> 4) == this.getChunk().getZ();
    }

    private void addPlayer(Player player) {
        if (this.getBar().getPlayers().contains(player)) {return;}
        this.getBar().addPlayer(player);
    }

    private void removePlayer(Player player) {
        if (!this.getBar().getPlayers().contains(player)) {return;}
        this.getBar().removePlayer(player);
    }

    private void updateVisibility() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (this.isPlayerInsideChunk(player)) {
                this.addPlayer(player);
            } else {
                this.removePlayer(player);
            }
        }
    }

    private void removeBar() {
        this.getBar().setProgress(0);
        this.getBar().removeAll();
        this.getBar().setVisible(false);
    }
}

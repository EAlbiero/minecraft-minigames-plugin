package plugin.common;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class Tasks {

    protected static BukkitTask task;

    public static void cancelTaskLater(Plugin plugin, BukkitTask task, int delay) {
        Bukkit.getScheduler().runTaskLater(plugin, () ->cancelTask(task), delay);
    }

    private static void cancelTask(BukkitTask task) {
        Bukkit.getScheduler().cancelTask(task.getTaskId());
    }
}

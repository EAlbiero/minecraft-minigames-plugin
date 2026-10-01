package plugin.commands;

import plugin.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StopAllEvents implements CommandExecutor {
    private Plugin plugin;

    public StopAllEvents(Plugin p) {
        plugin = p;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String [] args) {
        if (!(sender instanceof Player))
            return true;

       Bukkit.getScheduler().cancelTasks(plugin);
       Bukkit.broadcastMessage("Cancelling all tasks");
       return true;
    }
}

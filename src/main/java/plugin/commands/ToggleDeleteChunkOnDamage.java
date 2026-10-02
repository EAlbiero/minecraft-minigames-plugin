package plugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import plugin.PluginConfig;

public class ToggleDeleteChunkOnDamage implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String [] args) {
        if (!(sender instanceof Player))
            return true;

        boolean current = PluginConfig.getInstance().isDeleteChunkEnabled();
        PluginConfig.getInstance().setDeleteChunkEnabled(!current);

        return true;
    }
}

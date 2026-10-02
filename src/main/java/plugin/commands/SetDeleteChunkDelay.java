package plugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import plugin.PluginConfig;

public class SetDeleteChunkDelay implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String [] args) {
        if (!(sender instanceof Player))
            return true;

        PluginConfig.getInstance().setDeleteChunkDelaySeconds(Integer.parseInt(args[0]));

        return true;
    }
}

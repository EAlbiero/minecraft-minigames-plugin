package plugin.commands.chunkdeletion;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import plugin.config.PluginConfig;

public class SetDeleteChunkPenalty implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String [] args) {
        if (!(sender instanceof Player))
            return true;

        PluginConfig.getInstance().setDeleteChunkPenaltySeconds(Integer.parseInt(args[0]));

        return true;
    }
}

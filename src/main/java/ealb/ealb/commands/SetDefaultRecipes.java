package ealb.ealb.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getServer;

public class SetDefaultRecipes implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String [] args) {
        if (!(sender instanceof Player))
            return true;

        getServer().resetRecipes();
        Bukkit.broadcastMessage("Set all recipes to default");

        return true;
    }
}

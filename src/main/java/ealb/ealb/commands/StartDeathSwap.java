package ealb.ealb.commands;

import ealb.ealb.Ealb;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.io.BukkitObjectInputStream;

import java.util.Collection;

public class StartDeathSwap implements CommandExecutor {
    private Ealb plugin2;

    private int timeSwapMsg = 10;
    private int swapTimerSeconds = 5*60;
    private BukkitTask announcementTimer;

    public StartDeathSwap(Ealb plugin){
        plugin2 = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String [] args) {
        if (!(sender instanceof Player))
            return true;

        Bukkit.broadcastMessage("Starting Death Swap!");

        Bukkit.getScheduler().runTaskTimer(plugin2, this::Swap, 20*swapTimerSeconds, 20*swapTimerSeconds);

        Bukkit.getScheduler().runTaskTimer(plugin2, this::announceSwap, 0, 20*swapTimerSeconds);
        return true;
    }

    private void sendMessage() {
        Bukkit.broadcastMessage(String.format("Swapping in %s seconds...", timeSwapMsg));
        timeSwapMsg--;
        if (timeSwapMsg == 0){
            timeSwapMsg = 10;
            announcementTimer.cancel();
        }
    }

    private void announceSwap(){
        int t = timeSwapMsg;
        announcementTimer = Bukkit.getScheduler().runTaskTimer(plugin2, this::sendMessage, 20*(swapTimerSeconds - t), 20);
    }

    private void Swap(){
        Bukkit.broadcastMessage("SWAPPING PLAYERS");
        Collection<Player> players = (Collection<Player>) Bukkit.getOnlinePlayers();
        Player p1 = (Player) players.toArray()[0];
        Player p2 = (Player) players.toArray()[1];

        Location p1_location = p1.getLocation();
        Location p2_location = p2.getLocation();

        p1.teleport(p2_location);
        p2.teleport(p1_location);


    }
}

package ealb.ealb.commands;

import ealb.ealb.Ealb;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class StartBlockShuffle implements CommandExecutor {
    private Ealb plugin2;

    private HashMap<String, Boolean> playerBlockFound = new HashMap<>();
    private HashMap<String, Material> playerBlock = new HashMap<>();

    private int timeSwapMsg = 10;
    private int swapTimerSeconds = 5*5;
    private BukkitTask announcementTimer;
    private BukkitTask timeEndTask;

    private List<Material> shuffleBlocks = Arrays.stream(Material.values())
            .filter(Material::isBlock)
            .filter(Material::isItem)
            .filter(Material::isSolid)
            .filter(m -> !m.isAir())
            .filter(m -> !m.name().contains("COMMAND"))
            .filter(m -> !m.name().contains("STRUCTURE"))
            .filter(m -> !m.name().contains("PORTAL"))
            .filter(m -> !m.name().contains("SPAWNER"))
            .filter(m -> !m.name().contains("WAXED"))
            .filter(m -> !m.name().contains("WEATHERED"))
            .filter(m -> !m.name().contains("EXPOSED"))
            .filter(m -> !m.name().contains("OXIDIZED"))
            .filter(m -> m != Material.BEDROCK)
            .filter(m -> m != Material.BARRIER)
            .toList();


    public StartBlockShuffle(Ealb plugin){
        plugin2 = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String [] args) {
        if (!(sender instanceof Player))
            return true;

        Collection<Player> players = (Collection<Player>) Bukkit.getOnlinePlayers();
        for (Player player : players) {
            playerBlockFound.put(player.getDisplayName(), false);
            playerBlock.put(player.getDisplayName(), Material.AIR);
        }

        Bukkit.broadcastMessage("Starting Block Shuffle!");
        startRound();

        Bukkit.getScheduler().runTaskTimer(plugin2, this::checkBlock, 0, 10);
        Bukkit.getScheduler().runTaskTimer(plugin2, this::checkAllPlayersFoundTheirBlocks, 0, 10);


        return true;
    }

    private void startRound() {

        if (timeEndTask != null)
           timeEndTask.cancel();

        announceBlock();
        timeEndTask = Bukkit.getScheduler().runTaskLater(plugin2, this::timeEndsLoss, swapTimerSeconds*20);
    }

    private void timeEndsLoss() {
        checkBlock();
        if (checkAllPlayersFoundTheirBlocks())
            return;

        for (Map.Entry<String, Material> entry : playerBlock.entrySet() ) {
            if (playerBlockFound.get(entry.getKey()) == true)
                continue;

            Player player = Bukkit.getPlayer(entry.getKey());
            player.setHealth(0);
            Bukkit.broadcastMessage(String.format("Player %s has lost", player.getDisplayName()));
            // TODO: remove player from hashmap
        }
        startRound();
    }

    private void sendMessage() {
        Bukkit.broadcastMessage(String.format("Timer ends in %s seconds...", timeSwapMsg));
        timeSwapMsg--;
        if (timeSwapMsg == 0){
            timeSwapMsg = 10;
            announcementTimer.cancel();
        }
    }

    private void announceBlock(){
        Random random = new Random();
        for (Map.Entry<String, Material> entry : playerBlock.entrySet() ){
            String player = entry.getKey();
            int randomNumber = random.nextInt(shuffleBlocks.size())-1;
            Material newBlock = shuffleBlocks.get(randomNumber);
            entry.setValue(newBlock);
            playerBlockFound.put(entry.getKey(), false);

            Bukkit.broadcastMessage(String.format("Player %s has to find %s", player, newBlock.name()));
        }
    }

    private void checkBlock() {
        for (Map.Entry<String, Material> entry : playerBlock.entrySet() ){
            if (playerBlockFound.get(entry.getKey()) == true)
                continue;

            Player player = Bukkit.getPlayer(entry.getKey());
            Material block = entry.getValue();

            Material standingBlock = player.getLocation().clone().add(0, -1, 0).getBlock().getType();

            if (standingBlock == block){
                playerBlockFound.put(entry.getKey(), true);
                Bukkit.broadcastMessage(String.format("Player %s has found their block!", player.getDisplayName()));
            }
        }
    }

    private boolean checkAllPlayersFoundTheirBlocks() {
        for (Map.Entry<String, Boolean> entry : playerBlockFound.entrySet()) {
            if (entry.getValue() == false)
                return false;
        }
        startRound();
        return true;
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

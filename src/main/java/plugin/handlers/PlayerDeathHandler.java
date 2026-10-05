package plugin.handlers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import plugin.CustomPlugin;

public class PlayerDeathHandler implements Listener {

    private CustomPlugin plugin;

    public PlayerDeathHandler(CustomPlugin customPlugin) {
        plugin = customPlugin;
        Bukkit.getPluginManager().registerEvents(this, customPlugin);
    }

    @EventHandler
    public void onPlayerDeath(EntityDeathEvent event) throws InterruptedException {
        if (event.getEntityType() != EntityType.PLAYER) return;

        Player player = (Player) event.getEntity();
        Location loc = player.getLocation();
        int x = (int) loc.getX();
        int y = (int) loc.getY();
        int z = (int) loc.getZ();
        player.chat(x + " " + y + " " + z + " " + loc.getWorld().getName());
    }
}

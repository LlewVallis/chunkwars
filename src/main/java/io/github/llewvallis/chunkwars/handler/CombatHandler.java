package io.github.llewvallis.chunkwars.handler;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class CombatHandler implements Listener {

    @EventHandler
    private void onPlayerHit(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getDamage() < 2) {
            e.setDamage(2 * e.getDamage());
        }
    }
}

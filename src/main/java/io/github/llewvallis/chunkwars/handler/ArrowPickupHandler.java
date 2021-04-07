package io.github.llewvallis.chunkwars.handler;

import io.github.llewvallis.chunkwars.ItemBuilder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupArrowEvent;

public class ArrowPickupHandler implements Listener {

    @EventHandler
    private void onArrowLand(PlayerPickupArrowEvent e) {
        e.getItem().setItemStack(ItemBuilder.arrow().build());
    }
}

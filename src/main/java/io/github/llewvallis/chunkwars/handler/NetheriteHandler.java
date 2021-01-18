package io.github.llewvallis.chunkwars.handler;

import io.github.llewvallis.chunkwars.world.ArenaPool;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class NetheriteHandler implements Listener {

    @EventHandler
    private void onNetheriteBreak(BlockBreakEvent e) {
        Block block = e.getBlock();

        if (e.getPlayer().getGameMode() != GameMode.CREATIVE && block.getType() == Material.NETHERITE_BLOCK &&
                ArenaPool.instance.inArena(block)) {
            e.setCancelled(true);
        }
    }
}

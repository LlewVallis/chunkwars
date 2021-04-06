package io.github.llewvallis.chunkwars.handler;

import io.github.llewvallis.chunkwars.ItemBuilder;
import io.github.llewvallis.chunkwars.world.ArenaPool;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.util.Vector;

public class MelonHandler implements Listener {

    private static final int MELON_DROP_DELAY = 15 * 20;
    private static final int MELON_DROP_COUNT = 3;
    private static final int MELON_LIFETIME = 10 * 20;

    @EventHandler
    private void onBreakMelon(BlockBreakEvent e) {
        if (e.getBlock().getType() == Material.MELON_STEM &&
                ArenaPool.instance.inArena(e.getPlayer()) &&
                e.getPlayer().getGameMode() == GameMode.SURVIVAL) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    private void onEatMelon(PlayerItemConsumeEvent e) {
        if (e.getItem().getType() == Material.MELON_SLICE) {
            double health = e.getPlayer().getHealth();
            health = Math.max(health, Math.min(health + 2, 20));
            e.getPlayer().setHealth(health);
        }
    }

    @EventHandler
    private void onPlaceMelon(BlockPlaceEvent e) {
        Block block = e.getBlock();
        if (block.getType() == Material.MELON_STEM) {
            Ageable stemBlockData = (Ageable) block.getBlockData();
            stemBlockData.setAge(7);
            block.setBlockData(stemBlockData);

            dropMelonsPeriodically(block.getLocation());
        }
    }

    public static void dropMelonsPeriodically(Location location) {
        BlockCallbackHandler.instance.register(location, MELON_DROP_DELAY, () -> {
            World world = location.getWorld();

            for (int i = 0; i < MELON_DROP_COUNT; i++) {
                BlockCallbackHandler.instance.register(location, i * 20, () -> {
                    Item item = world.dropItemNaturally(location, ItemBuilder.melon().build());

                    item.setTicksLived(5 * 60 * 20 - MELON_LIFETIME);

                    Vector velocity = item.getVelocity();
                    velocity.add(new Vector(0, 0.125, 0));
                    item.setVelocity(velocity);
                }, () -> {});
            }

            dropMelonsPeriodically(location);
        }, () -> {});
    }
}

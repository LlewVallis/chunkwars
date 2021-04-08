package io.github.llewvallis.chunkwars.handler;

import io.github.llewvallis.chunkwars.ItemBuilder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class SporeHandler implements Listener {

    private static final int GROW_TIME = 15 * 20;

    @EventHandler
    private void onSporeBreak(BlockBreakEvent e) {
        if (e.getBlock().getType() == Material.NETHER_WART && e.getPlayer().getGameMode() == GameMode.SURVIVAL) {
            e.setCancelled(true);

            if (breakSpore(e.getBlock())) {
                e.getPlayer().getInventory().addItem(ItemBuilder.spore().build());
                e.getPlayer().sendActionBar(ChatColor.RED + "+1 Spore");
            }
        }
    }

    public static boolean breakSpore(Block block) {
        Ageable blockData = (Ageable) block.getBlockData();

        if (blockData.getAge() >= blockData.getMaximumAge()) {
            blockData.setAge(0);
            block.setBlockData(blockData);

            Location location = block.getLocation();
            location.getWorld().playSound(location, Sound.ITEM_NETHER_WART_PLANT, SoundCategory.BLOCKS, 1, 0.25f);

            growSpore(block.getLocation());
            return true;
        }

        return false;
    }

    @EventHandler
    public void onSporePlace(BlockPlaceEvent e) {
        if (e.getBlock().getType() == Material.NETHER_WART) {
            growSpore(e.getBlock().getLocation());
        }
    }

    public static void growSpore(Location location) {
        BlockCallbackHandler.instance.register(location, GROW_TIME, () -> {
            Block block = location.getBlock();
            BlockData blockData = block.getBlockData();

            if (!(blockData instanceof Ageable)) {
                return;
            }

            Ageable blockDataAgeable = (Ageable) blockData;

            int newAge = blockDataAgeable.getAge() == 0 ? 1 : blockDataAgeable.getMaximumAge();
            blockDataAgeable.setAge(newAge);
            block.setBlockData(blockData);

            if (newAge < blockDataAgeable.getMaximumAge()) {
                growSpore(location);
            }

            location.getWorld().playSound(location, Sound.ITEM_NETHER_WART_PLANT, SoundCategory.BLOCKS, 1, 1.66f);
        }, () -> {});
    }
}

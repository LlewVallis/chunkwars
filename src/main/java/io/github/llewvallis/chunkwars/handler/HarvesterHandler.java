package io.github.llewvallis.chunkwars.handler;

import io.github.llewvallis.chunkwars.ItemBuilder;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.Set;

public class HarvesterHandler implements Listener {

    private static final int DROP_LIFETIME = 15 * 20;

    private static final Set<Material> UNHARVESTABLES = Set.of(
            Material.NETHERITE_BLOCK,
            Material.MELON_STEM,
            Material.CRIMSON_ROOTS,
            Material.WARPED_ROOTS,
            Material.DEAD_BUSH,
            Material.CORNFLOWER,
            Material.BLUE_ORCHID,
            Material.ALLIUM,
            Material.RED_MUSHROOM,
            Material.BROWN_MUSHROOM
    );

    @EventHandler
    private void onObserverUpdate(BlockPhysicsEvent e) {
        Block harvester = e.getBlock();

        if (harvester.getType() != Material.OBSERVER) {
            return;
        }

        BlockFace facing = ((Directional) harvester.getBlockData()).getFacing();
        Block harvested = harvester.getRelative(facing);

        if (ResourceHandler.RESOURCE_MAPPINGS_INVERSE.containsKey(harvested.getType())) {
            return;
        }

        if (harvested.isEmpty() || UNHARVESTABLES.contains(harvested.getType())) {
            return;
        }

        ItemStack drop = null;
        ResourceHandler.Resource resource = ResourceHandler.RESOURCE_MAPPINGS.get(harvested.getType());

        if (resource != null) {
            if (resource.getBroken() != null) {
                harvested.setType(resource.getBroken());
                new BlockRegenerator(harvested.getLocation(), resource.getSource(), resource.getRegenTime()).register();
            } else {
                breakBlock(harvested);
            }

            drop = resource.getItem().build();
        } else if (harvested.getType() == Material.NETHER_WART) {
            if (SporeHandler.breakSpore(harvested)) {
                drop = ItemBuilder.spore().build();
            } else {
                return;
            }
        } else {
            breakBlock(harvested);
        }

        if (drop != null) {
            BlockFace dropFace = facing.getOppositeFace();
            Block dropBlock = harvester.getRelative(dropFace);
            BlockState dropBlockState = dropBlock.getState(false);

            if (dropBlockState instanceof InventoryHolder) {
                Inventory inventory = ((Container) dropBlockState).getInventory();
                Map<Integer, ItemStack> overflows = inventory.addItem(drop);

                for (ItemStack overflow : overflows.values()) {
                    dropItem(dropBlock, dropFace, overflow);
                }
            } else {
                dropItem(dropBlock, dropFace, drop);
            }
        }

        harvested.getLocation().getWorld().playSound(harvested.getLocation(), Sound.BLOCK_PUMPKIN_CARVE, SoundCategory.BLOCKS, 1, 0.8f);
    }

    private void breakBlock(Block block) {
        Location particleLocation = block.getLocation().toCenterLocation();
        particleLocation.getWorld().spawnParticle(Particle.BLOCK_CRACK, particleLocation, 40, 0.33, 0.33, 0.33, 1, block.getBlockData());
        block.setType(Material.AIR);
    }

    private void dropItem(Block dropBlock, BlockFace dropFace, ItemStack drop) {
        Location dropLocation = dropBlock.getLocation();

        Item item = dropLocation.getWorld().dropItemNaturally(dropLocation, drop);
        item.setTicksLived(5 * 60 * 20 - DROP_LIFETIME);

        Vector velocity = new Vector(dropFace.getModX(), dropFace.getModY(), dropFace.getModZ()).multiply(0.15);
        item.setVelocity(velocity);
    }
}

package io.github.llewvallis.chunkwars.handler;

import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.util.Vector;

import java.util.Collection;

public class HopperHandler implements Listener {

    private static final int ATTRACTION_RADIUS = 5;
    private static final double MAX_ACCELERATION = 4.0 / 20;
    private static final double MAX_SPEED = 0.5;

    @EventHandler
    private void onHopperPlace(BlockPlaceEvent e) {
        if (e.getBlock().getType() != Material.HOPPER) {
            return;
        }

        pullItems(e.getBlock().getLocation());
    }

    private void pullItems(Location location) {
        BlockCallbackHandler.instance.register(location, 1, () -> {
            Collection<Item> nearbyItems = location.getNearbyEntitiesByType(Item.class, ATTRACTION_RADIUS);

            Location focalPoint = location.clone().add(0.5, 1.5, 0.5);
            Location repulsionPoint = location.clone().add(0.5, 0, 0.5);

            for (Item item : nearbyItems) {
                double speed = item.getVelocity().length();
                double speedDamper =  Math.sqrt(Math.max(0, MAX_SPEED - speed));
                double distanceDamper = 1 / (location.distance(item.getLocation()) + 1);

                double repulsionFactor = Math.min(1, repulsionPoint.distance(item.getLocation()) - 1);
                double verticalityBias = 1 + Math.max(0, focalPoint.getY() - item.getLocation().getY());

                Vector influence = focalPoint.clone()
                        .subtract(item.getLocation())
                        .toVector()
                        .multiply(new Vector(1, verticalityBias, 1))
                        .normalize()
                        .multiply(speedDamper)
                        .multiply(distanceDamper)
                        .multiply(new Vector(repulsionFactor, 1, repulsionFactor))
                        .multiply(MAX_ACCELERATION)
                        .multiply(new Vector(1, Math.sqrt(verticalityBias), 1));

                double dragFactor = 1 / (influence.length() + 1);

                influence.multiply(1 / dragFactor);

                Vector newVelocity = item.getVelocity()
                        .multiply(dragFactor)
                        .add(influence);

                item.setVelocity(newVelocity);

                Location particleLocation = item.getLocation().add(0, 0.25, 0);
                item.getWorld().spawnParticle(Particle.CRIT_MAGIC, particleLocation, 1, 0.15, 0.15, 0.15, 0);
            }

            pullItems(location);
        }, () -> {});
    }
}

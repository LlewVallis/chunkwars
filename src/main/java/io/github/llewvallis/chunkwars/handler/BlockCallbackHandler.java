package io.github.llewvallis.chunkwars.handler;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import lombok.Value;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;

import java.util.*;
import java.util.logging.Level;

public class BlockCallbackHandler implements Listener {

    public static BlockCallbackHandler instance;

    private final Map<Location, Set<Registration>> registrationsByLocation = new HashMap<>();
    private final Queue<Registration> registrationQueue = new PriorityQueue<>();

    @Value
    private static class Registration implements Comparable<Registration> {

        Location location;
        Material expectedMaterial;
        int tickDue;

        Runnable onComplete;
        Runnable onAbort;

        @Override
        public int compareTo(Registration o) {
            return Integer.compare(tickDue, o.tickDue);
        }
    }

    public void register(Location location, int delay, Runnable onComplete, Runnable onAbort) {
        location = location.clone();

        Registration registration = new Registration(
                location,
                location.getBlock().getType(),
                Bukkit.getCurrentTick() + delay,
                onComplete, onAbort
        );

        registrationsByLocation.computeIfAbsent(location, key -> new HashSet<>()).add(registration);
        registrationQueue.add(registration);
    }

    private void deregister(Registration registration) {
        if (!registrationQueue.remove(registration)) {
            throw new IllegalStateException();
        }

        Set<Registration> registrationSet = registrationsByLocation.get(registration.location);
        if (!registrationSet.remove(registration)) {
            throw new IllegalStateException();
        }
    }

    @EventHandler
    private void onBlockPhysics(BlockPhysicsEvent e) {
        Block block = e.getBlock();
        Location location = block.getLocation();

        if (registrationsByLocation.containsKey(location)) {
            Set<Registration> toDeregister = new HashSet<>();

            for (Registration registration : registrationsByLocation.get(location)) {
                if (registration.expectedMaterial != block.getType()) {
                    toDeregister.add(registration);
                }
            }

            toDeregister.forEach(this::deregister);
            toDeregister.forEach(registration -> registration.onAbort.run());
        }
    }

    @EventHandler
    private void onTick(ServerTickStartEvent e) {
        int tick = Bukkit.getCurrentTick();

        while (true) {
            Registration registration = registrationQueue.peek();
            if (registration == null) break;

            if (registration.tickDue <= tick) {
                deregister(registration);

                try {
                    registration.onComplete.run();
                } catch (Throwable t) {
                    Bukkit.getLogger().log(Level.SEVERE, "Exception in block callback " , t);
                }
            } else {
                break;
            }
        }
    }
}

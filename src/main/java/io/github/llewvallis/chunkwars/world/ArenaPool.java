package io.github.llewvallis.chunkwars.world;

import io.github.llewvallis.chunkwars.map.MapGenerator;
import io.github.llewvallis.chunkwars.team.TeamManager;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class ArenaPool implements AutoCloseable {

    public static final String DEFAULT_ARENA = "common";

    public static ArenaPool instance;

    private final Map<String, Arena> arenas = new HashMap<>();

    public Arena create(String name) throws ArenaAlreadyExistsException {
        if (arenas.containsKey(name)) {
            Bukkit.getLogger().info("Failed to create arena " + name + " as it already exists");
            throw new ArenaAlreadyExistsException();
        }

        Bukkit.getLogger().info("Creating arena " + name);

        World world = MapGenerator.generate();
        Arena arena = new Arena(world);

        arenas.put(name, arena);

        return arena;
    }

    public void reset(Arena arena) {
        Optional<String> nameOptional = arenas.entrySet().stream()
                .filter(entry -> entry.getValue() == arena)
                .map(Map.Entry::getKey)
                .findFirst();

        nameOptional.ifPresent(name -> {
            Bukkit.getLogger().info("Resetting arena " + name);

            World world = MapGenerator.generate();
            Arena newArena = new Arena(world);

            for (Player player : arena.getWorld().getPlayers()) {
                newArena.sendPlayerAndSpectate(player);
            }

            delete(name);
            arenas.put(name, newArena);
        });
    }

    public void delete(String name) throws NoSuchArenaException {
        Arena arena = arenas.remove(name);

        if (arena == null) {
            throw new NoSuchArenaException(name, name + " does not exist");
        } else {
            Bukkit.getLogger().info("Deleting arena " + name);
            arena.close();
        }
    }

    public boolean inArena(Entity entity) {
        if (entity instanceof Player && TeamManager.instance.getPlayerTeam((Player) entity).isEmpty()) {
            return false;
        }

        return getArenaForWorld(entity.getWorld()).isPresent();
    }

    public boolean inArena(Block block) {
        return getArenaForWorld(block.getWorld()).isPresent();
    }

    public Optional<NamedArena> getPlayerArena(Player player) {
        return getArenaForWorld(player.getWorld());
    }

    public Optional<NamedArena> getArenaForWorld(World world) {
        for (NamedArena arena : getAllArenas()) {
            if (arena.arena.isAttachedToWorld(world)) {
                return Optional.of(arena);
            }
        }

        return Optional.empty();
    }

    public Optional<Arena> get(String name) {
        return Optional.ofNullable(arenas.get(name));
    }

    public Set<NamedArena> getAllArenas() {
        return arenas.entrySet().stream()
                .map(entry -> new NamedArena(entry.getKey(), entry.getValue()))
                .collect(Collectors.toSet());
    }

    public boolean isArena(World world) {
        return arenas.values().stream()
                .anyMatch(arena -> arena.isAttachedToWorld(world));
    }

    @SneakyThrows({ ArenaAlreadyExistsException.class })
    public void resetArena(String name) throws NoSuchArenaException {
        String tempName = "temp-" + UUID.randomUUID().toString();

        get(name).orElseThrow(() -> new NoSuchArenaException(name, name + " does not exist"));

        Arena newArena = create(tempName);
        delete(name);

        arenas.remove(tempName);
        arenas.put(name, newArena);
    }

    @Override
    @SneakyThrows({ NoSuchArenaException.class })
    public void close() {
        // Avoid modification during iteration
        Set<String> arenaNames = new HashSet<>(arenas.keySet());

        for (String name : arenaNames) {
            delete(name);
        }
    }
}

package io.github.llewvallis.chunkwars.handler;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import io.github.llewvallis.chunkwars.team.GameTeam;
import io.github.llewvallis.chunkwars.team.TeamChangeEvent;
import io.github.llewvallis.chunkwars.team.TeamManager;
import io.github.llewvallis.chunkwars.world.ArenaPool;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class WorldBorderHandler implements Listener {

    public static WorldBorderHandler instance;

    private enum WorldBorderAction {
        SET_SIZE,
        LERP_SIZE,
        SET_CENTER,
        INITIALIZE,
        SET_WARNING_TIME,
        SET_WARNING_BLOCKS
    }

    public void refreshBorder(Player player) {
        Optional<GameTeam> team = TeamManager.instance.getPlayerTeam(player);

        boolean enemyBorderIntact = ArenaPool.instance.getPlayerArena(player)
                .map(arena -> {
                    if (team.equals(Optional.of(GameTeam.LIGHT))) {
                        return arena.arena.getDarkBorderBrokenTicks() == 0;
                    }

                    if (team.equals(Optional.of(GameTeam.DARK))) {
                        return arena.arena.getLightBorderBrokenTicks() == 0;
                    }

                    return true;
                })
                .orElse(true);

        if (enemyBorderIntact) {
            sendBorderPacket(player, team, WorldBorderAction.SET_SIZE);
            sendBorderPacket(player, team, WorldBorderAction.SET_CENTER);
        } else {
            sendBorderPacket(player, Optional.empty(), WorldBorderAction.SET_SIZE);
            sendBorderPacket(player, Optional.empty(), WorldBorderAction.SET_CENTER);
        }
    }

    @EventHandler
    private void onTeamChange(TeamChangeEvent e) {
        refreshBorder(e.getPlayer());
    }

    @SneakyThrows({ InvocationTargetException.class })
    private void sendBorderPacket(Player player, Optional<GameTeam> teamOptional, WorldBorderAction action) {
        double size = 1000000;
        double z = teamOptional
                .map(team -> team == GameTeam.LIGHT ? size / 2 - 16 : -size / 2 + 16)
                .orElse(0.0);

        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.WORLD_BORDER);

        packet.getEnumModifier(WorldBorderAction.class, 0).write(0, action);
        packet.getIntegers().write(0, 29999984);
        packet.getDoubles().write(0, 0.0);
        packet.getDoubles().write(1, z);
        packet.getDoubles().write(2, size);
        packet.getDoubles().write(3, size);
        packet.getLongs().write(0, 0L);
        packet.getIntegers().write(1, 15);
        packet.getIntegers().write(2, 5);

        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
    }
}

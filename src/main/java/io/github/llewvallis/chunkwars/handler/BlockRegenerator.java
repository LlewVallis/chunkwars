package io.github.llewvallis.chunkwars.handler;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.Random;

@RequiredArgsConstructor
public class BlockRegenerator {

    private final Location location;
    public final Material newMaterial;

    private final int totalDuration;
    private int currentDuration = 0;

    private static final Random ENTITY_ID_RANDOM = new Random();
    private final int entityId = ENTITY_ID_RANDOM.nextInt();
    private int lastBreakStageShown = -1;

    public void register() {
        BlockCallbackHandler.instance.register(location, 0, this::update, this::cancel);
    }

    private void update() {
        if (currentDuration == totalDuration) {
            broadcastBreakStage(-1);
            location.getBlock().setType(newMaterial);
        } else {
            int breakStage = breakStage();
            if (lastBreakStageShown != breakStage) {
                broadcastBreakStage(breakStage);
                lastBreakStageShown = breakStage;
            }

            BlockCallbackHandler.instance.register(location, 1, this::update, this::cancel);
        }

        currentDuration++;
    }

    private void cancel() {
        broadcastBreakStage(-1);
    }

    @SneakyThrows({ InvocationTargetException.class })
    private void broadcastBreakStage(int breakStage) {
        PacketContainer breakPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.BLOCK_BREAK_ANIMATION);

        breakPacket.getIntegers().write(0, entityId);
        breakPacket.getIntegers().write(1, breakStage);
        breakPacket.getBlockPositionModifier().write(0, new BlockPosition(location.toVector()));

        for (Player player : location.getWorld().getPlayers()) {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, breakPacket);
        }
    }

    private int breakStage() {
        return (int) (9 - ((double) currentDuration) / totalDuration * 9);
    }
}

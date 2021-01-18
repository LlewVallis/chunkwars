package io.github.llewvallis.chunkwars.world;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class Hub {

    public static Hub instance;

    public void sendPlayer(Player player) {
        WorldManager.instance.send(player, WorldManager.instance.getDefaultWorld(), GameMode.ADVENTURE);
    }
}

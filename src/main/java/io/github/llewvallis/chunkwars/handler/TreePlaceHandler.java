package io.github.llewvallis.chunkwars.handler;

import io.github.llewvallis.chunkwars.map.TreePopulator;
import io.github.llewvallis.chunkwars.team.GameTeam;
import io.github.llewvallis.chunkwars.team.TeamManager;
import io.github.llewvallis.chunkwars.world.ArenaPool;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockCanBuildEvent;

public class TreePlaceHandler implements Listener {

    @EventHandler
    private void onTreePlace(BlockCanBuildEvent e) {
        Player player = e.getPlayer();
        if (player == null) {
            return;
        }

        Material material = e.getMaterial();

        if (ArenaPool.instance.inArena(player)) {
            if (material == Material.BAMBOO_SAPLING) {
                Location location = e.getBlock().getLocation();
                location.add(0.5, 0, 0.5);

                GameTeam team = TeamManager.instance.getPlayerTeam(player).orElseThrow();
                TreePopulator.spawnTree(location, team);
            } else if (material == Material.BAMBOO) {
                e.setBuildable(false);
            }
        }
    }
}

package io.github.llewvallis.chunkwars;

import io.github.llewvallis.chunkwars.command.ArenaArgument;
import io.github.llewvallis.chunkwars.command.TeamArgument;
import io.github.llewvallis.chunkwars.handler.*;
import io.github.llewvallis.chunkwars.team.GameTeam;
import io.github.llewvallis.chunkwars.team.TeamManager;
import io.github.llewvallis.chunkwars.world.ArenaPool;
import io.github.llewvallis.chunkwars.world.Hub;
import io.github.llewvallis.chunkwars.world.NamedArena;
import io.github.llewvallis.chunkwars.world.WorldManager;
import io.github.llewvallis.commandbuilder.AutoCommandBuilder;
import io.github.llewvallis.commandbuilder.DefaultInferenceProvider;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class ChunkWarsPlugin extends JavaPlugin {

    public static ChunkWarsPlugin instance;

    @Override
    public void onEnable() {
        ItemStack item = new ItemStack(Material.DIAMOND_AXE);
        ItemMeta meta = item.getItemMeta();
        item.setItemMeta(meta);

        instance = this;

        TeamManager.instance = new TeamManager();
        TeamManager.instance.configScoreboard();

        WorldManager.instance = new WorldManager();
        Hub.instance = new Hub();
        ArenaPool.instance = new ArenaPool();

        BlockCallbackHandler.instance = new BlockCallbackHandler();
        register(BlockCallbackHandler.instance);

        WorldBorderHandler.instance = new WorldBorderHandler();
        register(WorldBorderHandler.instance);

        register(new ArenaInitHandler());
        register(new DeathHandler());
        register(new CraftingHandler());
        register(new ArtifactHandler());
        register(new ResourceHandler());
        register(new LogStrippingHandler());
        register(new PathDiggingHandler());
        register(new NetheriteHandler());
        register(new MapEdgeHandler());
        register(new TreePlaceHandler());
        register(new CombatHandler());
        register(new ToolHandler());
        register(new BorderTomeHandler());
        register(new TramplingHandler());
        register(new MelonHandler());
        register(new HoeHandler());
        register(new HopperHandler());
        register(new RottenCoreHandler());
        register(new PotionHandler());
        register(new ArrowPickupHandler());
        register(new SporeHandler());
        register(new ExplosionHandler());
        register(new Restoration());
        new BorderOutlineHandler();

        WorldManager.instance.cleanArenas();
        ArenaPool.instance.create("cw1");

        DefaultInferenceProvider.getGlobal().register(GameTeam.class, new TeamArgument());
        DefaultInferenceProvider.getGlobal().register(NamedArena.class, new ArenaArgument());

        new AutoCommandBuilder(this)
                .jarSource(getFile(), "^io.github.llewvallis.chunkwars.")
                .register();
    }

    private void register(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, this);
    }

    @Override
    public void onDisable() {
        ArenaPool.instance.close();
    }
}

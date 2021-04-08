package io.github.llewvallis.chunkwars.handler;

import io.github.llewvallis.chunkwars.ItemBuilder;
import io.github.llewvallis.chunkwars.team.GameTeam;
import io.github.llewvallis.chunkwars.team.TeamManager;
import io.github.llewvallis.chunkwars.world.ArenaPool;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.*;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Supplier;

public class ArtifactHandler implements Listener {

    private static final int SHOP_WIDTH = 9;
    private static final int SHOP_HEIGHT = 6;

    private final Map<Inventory, Player> shopInventories = new WeakHashMap<>();

    private final List<SoldItem> soldItems = Arrays.asList(
            new SoldItem(
                    ItemBuilder::basicSword,
                    List.of(new Cost(Currency.WOOD, 25)),
                    List.of(new Cost(Currency.WOOD, 5))
            ),
            new SoldItem(
                    ItemBuilder::basicPickaxe,
                    List.of(new Cost(Currency.WOOD, 50)),
                    List.of(new Cost(Currency.WOOD, 10))
            ),
            new SoldItem(
                    ItemBuilder::basicAxe,
                    List.of(new Cost(Currency.WOOD, 10)),
                    List.of(new Cost(Currency.WOOD, 5))
            ),
            new SoldItem(
                    ItemBuilder::basicShovel,
                    List.of(new Cost(Currency.WOOD, 10)),
                    List.of(new Cost(Currency.WOOD, 5))
            ),
            new SoldItem(
                    ItemBuilder::crossbow,
                    List.of(new Cost(Currency.STONE, 5), new Cost(Currency.WOOD, 30)),
                    List.of(new Cost(Currency.STONE, 5), new Cost(Currency.WOOD, 10))
            ),
            new SoldItem(ItemBuilder::arrowPair, List.of(new Cost(Currency.WOOD, 15))),
            new SoldItem(
                    ItemBuilder::hardenedSword,
                    List.of(new Cost(Currency.STONE, 30)),
                    List.of(new Cost(Currency.STONE, 10))
            ),
            new SoldItem(
                    ItemBuilder::hardenedPickaxe,
                    List.of(new Cost(Currency.STONE, 30)),
                    List.of(new Cost(Currency.STONE, 10))
            ),
            new SoldItem(
                    ItemBuilder::hardenedAxe,
                    List.of(new Cost(Currency.STONE, 30)),
                    List.of(new Cost(Currency.STONE, 10))
            ),
            new SoldItem(
                    ItemBuilder::hardenedShovel,
                    List.of(new Cost(Currency.STONE, 10)),
                    List.of(new Cost(Currency.STONE, 5))
            ),
            new SoldItem(
                    ItemBuilder::hardenedHoe,
                    List.of(new Cost(Currency.STONE, 10))
            ),
            null,
            new SoldItem(
                    ItemBuilder::refinedSword,
                    List.of(new Cost(Currency.IRON, 30)),
                    List.of(new Cost(Currency.IRON, 10))
            ),
            new SoldItem(
                    ItemBuilder::refinedPickaxe,
                    List.of(new Cost(Currency.IRON, 40)),
                    List.of(new Cost(Currency.IRON, 5))
            ),
            new SoldItem(
                    ItemBuilder::refinedAxe,
                    List.of(new Cost(Currency.IRON, 40)),
                    List.of(new Cost(Currency.IRON, 5))
            ),
            new SoldItem(ItemBuilder::refinedShovel, List.of(new Cost(Currency.IRON, 10))),
            null,
            null,
            new SoldItem(
                    ItemBuilder::tntBundle,
                    List.of(new Cost(Currency.SPORE, 50), new Cost(Currency.DIRT, 50), new Cost(Currency.IRON, 15)),
                    List.of(new Cost(Currency.SPORE, 40), new Cost(Currency.DIRT, 20), new Cost(Currency.IRON, 5))
            ),
            new SoldItem(ItemBuilder::activatedSporeBlockBundle, List.of(new Cost(Currency.SPORE, 20))),
            new SoldItem(
                    ItemBuilder::congealedMelonBundle,
                    List.of(new Cost(Currency.MELON, 25), new Cost(Currency.SPORE, 5))
            ),
            new SoldItem(
                    ItemBuilder::piston,
                    List.of(new Cost(Currency.STONE, 50), new Cost(Currency.SPORE, 10))
            ),
            new SoldItem(
                    ItemBuilder::stickyPiston,
                    List.of(new Cost(Currency.STONE, 50), new Cost(Currency.MELON, 20), new Cost(Currency.SPORE, 10))
            ),
            null,
            new SoldItem(
                    ItemBuilder::melonSeeds,
                    List.of(new Cost(Currency.DIRT, 30)),
                    List.of(new Cost(Currency.MELON, 15))
            ),
            new SoldItem(
                    ItemBuilder::juice,
                    List.of(new Cost(Currency.MELON, 15)),
                    List.of(new Cost(Currency.MELON, 30))
            ),
            new SoldItem(
                    ItemBuilder::rottenCore,
                    List.of(new Cost(Currency.WOOD, 40), new Cost(Currency.DIRT, 30), new Cost(Currency.MELON, 20)),
                    List.of(new Cost(Currency.WOOD, 20), new Cost(Currency.DIRT, 40), new Cost(Currency.MELON, 60))
            ),
            new SoldItem(ItemBuilder::sapling, List.of(new Cost(Currency.DIRT, 20))),
            null, null,
            new SoldItem(ItemBuilder::crate, List.of(new Cost(Currency.WOOD, 5), new Cost(Currency.DIRT, 5))),
            new SoldItem(ItemBuilder::hopper, List.of(new Cost(Currency.IRON, 5), new Cost(Currency.STONE, 20))),
            new SoldItem(
                    ItemBuilder::harvester,
                    List.of(new Cost(Currency.IRON, 15), new Cost(Currency.STONE, 30)),
                    List.of(new Cost(Currency.IRON, 20), new Cost(Currency.SPORE, 10), new Cost(Currency.STONE, 40))
            ),
            null, null, null,
            new SoldItem(ItemBuilder::borderTome, List.of(
                    new Cost(Currency.IRON, 50),
                    new Cost(Currency.SPORE, 50),
                    new Cost(Currency.DIRT, 200)
            )),
            new SoldItem(ItemBuilder::restorationKit, List.of(
                    new Cost(Currency.DIRT, 150),
                    new Cost(Currency.STONE, 100)
            ))
    );

    @Getter
    public static class SoldItem {

        private final Supplier<ItemBuilder> item;
        private final List<Cost> initialCosts;
        private final List<Cost> recurringCosts;

        private SoldItem(Supplier<ItemBuilder> item, List<Cost> initialCosts, List<Cost> recurringCosts) {
            this.item = item;
            this.initialCosts = initialCosts;
            this.recurringCosts = recurringCosts;
        }

        private SoldItem(Supplier<ItemBuilder> item, List<Cost> costs) {
            this(item, costs, costs);
        }

        private List<Cost> getApplicableCosts(Player player) {
            if (doInitialCostsApply(player)) {
                return initialCosts;
            } else {
                return recurringCosts;
            }
        }

        private boolean doInitialCostsApply(Player player) {
            return ArenaPool.instance.getPlayerArena(player).flatMap(arena ->
                    TeamManager.instance.getPlayerTeam(player).map(team -> {
                        if (team == GameTeam.LIGHT) {
                            return !arena.arena.getLightBoughtItems().contains(this);
                        } else {
                            return !arena.arena.getDarkBoughtItems().contains(this);
                        }
                    })
            ).orElse(true);
        }
    }

    @Getter
    @EqualsAndHashCode
    @RequiredArgsConstructor
    private static class Cost {

        private final Currency currency;
        private final int amount;
    }

    @RequiredArgsConstructor
    private enum Currency {
        WOOD(Material.STICK, ChatColor.DARK_GREEN, "wood"),
        DIRT(Material.BROWN_DYE, ChatColor.GOLD, "dirt"),
        STONE(Material.FIREWORK_STAR, ChatColor.GRAY, "stone"),
        IRON(Material.IRON_INGOT, ChatColor.WHITE, "iron"),
        MELON(Material.MELON_SLICE, ChatColor.GREEN, "melon"),
        SPORE(Material.NETHER_WART, ChatColor.RED, "spore");

        private final Material material;
        private final ChatColor color;
        private final String name;
    }

    private void displayShop(Player player) {
        Inventory inventory = Bukkit.createInventory(null, SHOP_WIDTH * SHOP_HEIGHT, "Artifact shop");
        setShopSlots(inventory, player);
        shopInventories.put(inventory, player);
        player.openInventory(inventory);
    }

    private void setShopSlots(Inventory inventory, Player player) {
        int slot = 0;
        for (SoldItem item : soldItems) {
            if (item != null) {
                ItemBuilder builder = item.item.get();
                builder.lore("");

                for (Cost cost : item.getApplicableCosts(player)) {
                    builder.lore(cost.currency.color.toString() + cost.amount + " " + cost.currency.name);
                }

                if (!item.initialCosts.equals(item.recurringCosts) && item.doInitialCostsApply(player)) {
                    builder.lore("");
                    builder.lore(ChatColor.GRAY.toString() + ChatColor.UNDERLINE + "On subsequent purchases");
                    for (Cost cost : item.recurringCosts) {
                        builder.lore(cost.currency.color.toString() + cost.amount + " " + cost.currency.name);
                    }
                }

                ItemStack stack = builder.build();

                inventory.setItem(slot, stack);
            }

            slot += SHOP_WIDTH;
            if (slot >= SHOP_WIDTH * SHOP_HEIGHT) {
                slot = slot - SHOP_WIDTH * SHOP_HEIGHT + 1;
            }
        }
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        Inventory inventory = e.getInventory();

        if (shopInventories.containsKey(inventory)) {
            e.setCancelled(true);

            if (inventory == e.getClickedInventory() && e.getCurrentItem() != null) {
                Material material = e.getCurrentItem().getType();

                soldItems.stream()
                        .filter(item -> item != null && item.item.get().material() == material)
                        .findFirst()
                        .ifPresent(soldItem -> purchase(player, soldItem));
            }
        }
    }

    private void purchase(Player player, SoldItem item) {
        Inventory inventory = player.getInventory();

        if (player.getGameMode() == GameMode.CREATIVE) {
            inventory.addItem(item.item.get().build());
            return;
        }

        List<Cost> costs = item.getApplicableCosts(player);

        for (Cost cost : costs) {
            int balance = 0;
            for (ItemStack stack : inventory.getContents()) {
                if (stack == null) {
                    continue;
                }

                if (stack.getType() == cost.currency.material) {
                    balance += stack.getAmount();
                }
            }

            if (balance < cost.amount) {
                Location soundLocation = player.getLocation();
                soundLocation.add(0, Float.MAX_VALUE / 256, 0);
                player.playSound(soundLocation, Sound.ENTITY_VILLAGER_NO, SoundCategory.MASTER, Float.MAX_VALUE, 1);

                player.sendMessage(ChatColor.RED + "Insufficient funds");
                return;
            }
        }

        for (Cost cost : costs) {
            int deducted = 0;

            while (deducted < cost.amount) {
                ItemStack stack = smallestStack(cost.currency.material, inventory);
                int delta = Math.min(cost.amount - deducted, stack.getAmount());
                stack.setAmount(stack.getAmount() - delta);

                deducted += delta;
            }
        }

        Location soundLocation = player.getLocation();
        soundLocation.add(0, Float.MAX_VALUE / 256, 0);
        player.playSound(soundLocation, Sound.BLOCK_TRIPWIRE_ATTACH, SoundCategory.MASTER, Float.MAX_VALUE, 1);

        inventory.addItem(item.item.get().build());
        setCostsToRecurring(player, item);
    }

    private void setCostsToRecurring(Player player, SoldItem item) {
        ArenaPool.instance.getPlayerArena(player).ifPresent(arena -> {
            TeamManager.instance.getPlayerTeam(player).ifPresent(team -> {
                if (team == GameTeam.LIGHT) {
                    arena.arena.getLightBoughtItems().add(item);
                } else {
                    arena.arena.getDarkBoughtItems().add(item);
                }
            });
        });

        shopInventories.forEach(this::setShopSlots);
    }

    private ItemStack smallestStack(Material material, Inventory inventory) {
        return Arrays.stream(inventory.getContents())
                .filter(Objects::nonNull)
                .filter(stack -> stack.getType() == material)
                .sorted(Comparator.comparingInt(ItemStack::getAmount))
                .findAny()
                .orElseThrow();
    }

    @EventHandler
    private void onEndCrystalInteractEvent(PlayerInteractEntityEvent e) {
        Entity entity = e.getRightClicked();
        Player player = e.getPlayer();

        if (entity instanceof EnderCrystal && ArenaPool.instance.inArena(entity) && ArenaPool.instance.inArena(player)) {
            interactWithCrystal(entity, player);
        }
    }

    @EventHandler
    private void onEndCrystalHit(EntityDamageByEntityEvent e) {
        Entity entity = e.getEntity();
        Entity damager = e.getDamager();

        if (entity instanceof EnderCrystal && ArenaPool.instance.inArena(entity) && ArenaPool.instance.inArena(damager)) {
            e.setCancelled(true);

            if (damager instanceof Player) {
                interactWithCrystal(entity, (Player) damager);
            }
        }
    }

    private void interactWithCrystal(Entity crystal, Player player) {
        GameTeam crystalTeam = crystal.getLocation().getZ() > 0 ? GameTeam.LIGHT : GameTeam.DARK;

        ArenaPool.instance.getPlayerArena(player).ifPresent(arena -> {
            TeamManager.instance.getPlayerTeam(player).ifPresent(playerTeam -> {
                if (playerTeam != crystalTeam) {
                    arena.arena.processWinner(playerTeam);
                } else {
                    displayShop(player);
                }
            });
        });
    }
}

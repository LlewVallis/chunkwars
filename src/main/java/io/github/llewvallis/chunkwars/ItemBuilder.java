package io.github.llewvallis.chunkwars;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@Accessors(fluent = true)
@RequiredArgsConstructor
public class ItemBuilder {

    public static ItemBuilder wood() {
        return new ItemBuilder(Material.STICK)
                .name(ChatColor.DARK_GREEN + "Wood");
    }

    public static ItemBuilder basicSword() {
        return new ItemBuilder(Material.WOODEN_SWORD)
                .name(ChatColor.DARK_GREEN + "Basic Sword")
                .damage(4.5)
                .lore(ChatColor.GRAY + "Deals 2.25 hearts");
    }

    public static ItemBuilder basicPickaxe() {
        return new ItemBuilder(Material.WOODEN_PICKAXE)
                .name(ChatColor.DARK_GREEN + "Basic Pick")
                .damage(1.0)
                .lore(ChatColor.GRAY + "Deals no extra damage");
    }

    public static ItemBuilder basicAxe() {
        return new ItemBuilder(Material.WOODEN_AXE)
                .name(ChatColor.DARK_GREEN + "Basic Axe")
                .damage(3.0)
                .lore(ChatColor.GRAY + "Deals 1.5 hearts");
    }

    public static ItemBuilder basicShovel() {
        return new ItemBuilder(Material.WOODEN_SHOVEL)
                .name(ChatColor.DARK_GREEN + "Basic Spade")
                .damage(1.0)
                .lore(ChatColor.GRAY + "Deals no extra damage");
    }

    public static ItemBuilder stone() {
        return new ItemBuilder(Material.FIREWORK_STAR)
                .name(ChatColor.GRAY + "Stone");
    }

    public static ItemBuilder hardenedSword() {
        return new ItemBuilder(Material.STONE_SWORD)
                .name(ChatColor.GRAY + "Hardened Sword")
                .damage(6.0)
                .lore(ChatColor.GRAY + "Deals 3 hearts");
    }

    public static ItemBuilder hardenedPickaxe() {
        return new ItemBuilder(Material.STONE_PICKAXE)
                .name(ChatColor.GRAY + "Hardened Pickaxe")
                .damage(1.0)
                .lore(ChatColor.GRAY + "Deals no extra damage");
    }

    public static ItemBuilder hardenedAxe() {
        return new ItemBuilder(Material.STONE_AXE)
                .name(ChatColor.GRAY + "Hardened Axe")
                .damage(3.0)
                .lore(ChatColor.GRAY + "Deals 1.5 hearts");
    }

    public static ItemBuilder hardenedShovel() {
        return new ItemBuilder(Material.STONE_SHOVEL)
                .name(ChatColor.GRAY + "Hardened Spade")
                .damage(1.0)
                .lore(ChatColor.GRAY + "Deals no extra damage");
    }

    public static ItemBuilder crossbow() {
        return new ItemBuilder(Material.CROSSBOW)
                .name(ChatColor.GRAY + "Crossbow")
                .unbreakable(true)
                .lore(ChatColor.GRAY + "Deals 6-11 hearts");
    }

    public static ItemBuilder arrowPair() {
        return new ItemBuilder(Material.ARROW)
                .name(ChatColor.GRAY + "Arrow")
                .amount(2);
    }

    public static ItemBuilder iron() {
        return new ItemBuilder(Material.IRON_INGOT)
                .name(ChatColor.WHITE + "Iron");
    }

    public static ItemBuilder refinedSword() {
        return new ItemBuilder(Material.IRON_SWORD)
                .name(ChatColor.WHITE + "Refined Sword")
                .damage(8.0)
                .lore(ChatColor.GRAY + "Deals 4 hearts");
    }

    public static ItemBuilder refinedPickaxe() {
        return new ItemBuilder(Material.IRON_PICKAXE)
                .name(ChatColor.WHITE + "Refined Pickaxe")
                .damage(1.0)
                .lore(ChatColor.GRAY + "Deals no extra damage");
    }

    public static ItemBuilder refinedAxe() {
        return new ItemBuilder(Material.IRON_AXE)
                .name(ChatColor.WHITE + "Refined Axe")
                .damage(3.0)
                .lore(ChatColor.GRAY + "Deals 1.5 damage");
    }

    public static ItemBuilder refinedShovel() {
        return new ItemBuilder(Material.IRON_SHOVEL)
                .name(ChatColor.WHITE + "Refined Spade")
                .damage(1.0)
                .lore(ChatColor.GRAY + "Drops 16 dirt for every block mined")
                .lore(ChatColor.GRAY + "Deals no extra damage");
    }

    public static ItemBuilder borderTome() {
        return new ItemBuilder(Material.ENCHANTED_BOOK)
                .name(ChatColor.LIGHT_PURPLE + "Border Tome")
                .lore(ChatColor.GRAY + "Disables the enemy's border for 10 seconds");
    }

    public static ItemBuilder dirt() {
        return new ItemBuilder(Material.BROWN_DYE)
                .name(ChatColor.GOLD + "Dirt");
    }

    public static ItemBuilder crate() {
        return new ItemBuilder(Material.BARREL)
                .name(ChatColor.GOLD + "Barrel")
                .lore(ChatColor.GRAY + "Simple item storage");
    }

    public static ItemBuilder sapling() {
        return new ItemBuilder(Material.BAMBOO)
                .name(ChatColor.GOLD + "Sapling")
                .lore(ChatColor.GRAY + "Generate a tree on demand");
    }

    public static ItemBuilder melon() {
        return new ItemBuilder(Material.MELON_SLICE)
                .name(ChatColor.GREEN + "Melon")
                .lore(ChatColor.GRAY + "Restores 1 heart and 1 shank");
    }

    @Getter
    private final Material material;

    @Setter
    private String name = null;
    @Setter
    private Double damage = null;
    @Setter
    private int amount = 1;
    @Setter
    private boolean unbreakable = false;

    private final List<String> lore = new ArrayList<>();

    public ItemStack build() {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();

        meta.setLore(lore);
        stack.setAmount(amount);

        if (name != null) {
            meta.setDisplayName(ChatColor.RESET + name);
        }

        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        if (unbreakable) {
            meta.setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        }

        if (damage != null) {
            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(
                    Attribute.GENERIC_ATTACK_DAMAGE.name(),
                    damage - 1,
                    AttributeModifier.Operation.MULTIPLY_SCALAR_1
            ));
        }

        stack.setItemMeta(meta);
        return stack;
    }

    public ItemBuilder lore(String lore) {
        this.lore.add(lore);
        return this;
    }
}

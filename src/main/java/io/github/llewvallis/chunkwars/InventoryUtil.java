package io.github.llewvallis.chunkwars;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

@UtilityClass
public class InventoryUtil {

    public void removeHeldItem(Player player) {
        PlayerInventory inventory = player.getInventory();

        boolean offhand = inventory.getItemInMainHand().getType() == Material.AIR;

        ItemStack stack = offhand ? inventory.getItemInOffHand() : inventory.getItemInMainHand();
        stack.setAmount(stack.getAmount() - 1);

        if (offhand) {
            inventory.setItemInOffHand(stack);
        } else {
            inventory.setItemInMainHand(stack);
        }
    }
}

package org.leralix.tan.utils.gameplay;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.gui.service.requirements.model.ItemScope;

public class InventoryUtil {

    public static boolean playerEnoughItem(Player player, ItemScope itemScope, int amount){
        return getItemsNumberInInventory(player, itemScope) >= amount;
    }

    public static int getItemsNumberInInventory(Player player, ItemScope itemScope){
        if (player == null || itemScope == null) {
            return 0;
        }

        int total = 0;

        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getType() == Material.AIR || item.getAmount() <= 0) {
                continue;
            }

            if (itemScope.isInScope(item)) {
                total += item.getAmount();
            }
        }
        return total;
    }

}

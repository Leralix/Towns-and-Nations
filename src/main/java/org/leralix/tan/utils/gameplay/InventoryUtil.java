package org.leralix.tan.utils.gameplay;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.gui.service.requirements.model.ItemScope;

public class InventoryUtil {

    private InventoryUtil(){
        // Utility class
    }

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

    public static void removeItemsFromInventory(Player player, ItemScope itemScope, int amountToRemove) {
        if (player == null || itemScope == null || amountToRemove <= 0) {
            return;
        }

        int amountRemoved = 0;

        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getType() == Material.AIR || item.getAmount() <= 0) {
                continue;
            }

            if (itemScope.isInScope(item)) {
                int itemAmount = item.getAmount();
                if (itemAmount + amountRemoved <= amountToRemove) {
                    amountRemoved += itemAmount;
                    player.getInventory().remove(item);
                } else {
                    int toRemove = amountToRemove - amountRemoved;
                    item.setAmount(itemAmount - toRemove);
                    amountRemoved += toRemove;
                }

                if (amountRemoved >= amountToRemove) {
                    break;
                }
            }
        }

    }

}

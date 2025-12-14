package org.leralix.tan.utils.gameplay;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.gui.service.requirements.model.ItemScope;

public class InventoryUtil {

    private InventoryUtil() {
        // Utility class
    }

    public static boolean playerEnoughItem(Player player, ItemScope itemScope, int amount) {
        return playerEnoughItem(player, itemScope, amount, Integer.MIN_VALUE);
    }

    public static boolean playerEnoughItem(Player player, ItemScope itemScope, int amount, Integer customModelData) {
        return getItemsNumberInInventory(player, itemScope, customModelData) >= amount;
    }

    public static int getItemsNumberInInventory(Player player, ItemScope itemScope) {
        return getItemsNumberInInventory(player, itemScope, Integer.MIN_VALUE);
    }

    public static int getItemsNumberInInventory(Player player, ItemScope itemScope, int customModelData) {
        if (player == null || itemScope == null) {
            return 0;
        }

        int total = 0;

        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getType() == Material.AIR || item.getAmount() <= 0) {
                continue;
            }

            if (isInScope(item, itemScope, customModelData)) {
                total += item.getAmount();
            }
        }
        return total;
    }

    private static boolean isInScope(ItemStack item, ItemScope itemScope, Integer customModelData) {
        if (itemScope.isInScope(item)) {
            return customModelData == Integer.MIN_VALUE ||
                    (item.hasItemMeta()
                            && item.getItemMeta().hasCustomModelData()
                            && item.getItemMeta().getCustomModelData() == customModelData);
        }
        return false;
    }

    public static void removeItemsFromInventory(Player player, ItemScope itemScope, int amountToRemove) {
        removeItemsFromInventory(player, itemScope, amountToRemove, null);
    }

    public static void removeItemsFromInventory(Player player, ItemScope itemScope, int amountToRemove, Integer customModelData) {
        if (player == null || itemScope == null || amountToRemove <= 0) {
            return;
        }

        int amountRemoved = 0;

        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getType() == Material.AIR || item.getAmount() <= 0) {
                continue;
            }

            if (isInScope(item, itemScope, customModelData)) {

                int itemAmount = item.getAmount();
                int remainingToRemove = amountToRemove - amountRemoved;

                int toRemove = Math.min(itemAmount, remainingToRemove);

                item.setAmount(itemAmount - toRemove);
                amountRemoved += toRemove;

                if (amountRemoved >= amountToRemove) {
                    break;
                }
            }
        }

    }

}

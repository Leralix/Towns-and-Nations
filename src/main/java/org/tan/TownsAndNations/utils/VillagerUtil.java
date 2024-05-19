package org.tan.TownsAndNations.utils;

import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.enums.CustomVillagerProfession;

/**
 * This class is used for villager related utilities.
 */
public class VillagerUtil {

    /***
     * This method is used to create a custom villager for selling rare items
     * @param player the player who is creating the villager. His position will be used to spawn the villager in the exact same position.
     * @param customProfession the custom profession of the villager.
     */
    public static void createCustomVillager(Player player, CustomVillagerProfession customProfession) {

        Villager villager = player.getWorld().spawn(player.getLocation(), Villager.class);

        villager.setCustomName(customProfession.getDisplayName());
        villager.setProfession(customProfession.getProfession());

        villager.setAI(false);
        villager.setCustomNameVisible(true);
        villager.setInvulnerable(true);

        villager.addScoreboardTag(customProfession.name());

        player.sendMessage(ChatUtils.getTANString() + Lang.CUSTOM_VILLAGER_CREATED_SUCCESS.get());
    }
}

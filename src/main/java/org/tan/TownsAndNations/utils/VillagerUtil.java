package org.tan.TownsAndNations.utils;

import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.enums.CustomVillagerProfession;

public class VillagerUtil {

    public static void createCustomVillager(Player player, CustomVillagerProfession customProfession) {

        Villager villager = player.getWorld().spawn(player.getLocation(), Villager.class);

        villager.setCustomName(customProfession.getDisplayName());
        villager.setProfession(customProfession.getProfession());

        villager.setAI(false);
        villager.setCustomNameVisible(true);
        villager.setInvulnerable(true);

        villager.addScoreboardTag(customProfession.name());

        player.sendMessage(ChatUtils.getTANString() + Lang.CUSTOM_VILLAGER_CREATED_SUCCESS.getTranslation());
    }
}

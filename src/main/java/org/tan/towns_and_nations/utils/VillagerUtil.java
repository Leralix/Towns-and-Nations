package org.tan.towns_and_nations.utils;

import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.metadata.FixedMetadataValue;
import org.tan.towns_and_nations.Lang.Lang;
import org.tan.towns_and_nations.TownsAndNations;
import org.tan.towns_and_nations.enums.CustomVillagerProfession;

public class VillagerUtil {

    public static void createCustomVillager(Player player, CustomVillagerProfession customProfession) {

        Villager villager = player.getWorld().spawn(player.getLocation(), Villager.class);

        villager.setCustomName(customProfession.getDisplayName());
        villager.setProfession(customProfession.getProfession());

        villager.setAI(false);
        villager.setCustomNameVisible(true);
        villager.setInvulnerable(true);

        villager.setMetadata(MetaDataKeys.PROFESSION, new FixedMetadataValue(TownsAndNations.getPlugin(), customProfession.name()));

        player.sendMessage(ChatUtils.getTANString() + Lang.CUSTOM_VILLAGER_CREATED_SUCCESS.getTranslation());
    }
}

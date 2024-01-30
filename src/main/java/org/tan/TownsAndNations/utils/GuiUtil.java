package org.tan.TownsAndNations.utils;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.DataClass.TownUpgrade;
import org.tan.TownsAndNations.Lang.Lang;

import java.util.ArrayList;
import java.util.List;

import static org.tan.TownsAndNations.GUI.GuiManager2.OpenTownLevel;
import static org.tan.TownsAndNations.utils.TownUtil.upgradeTown;

public class GuiUtil {

    public static GuiItem makeUpgradeGuiItem(Player player, TownUpgrade townUpgrade, TownData townData){

        int townLevel = townData.getTownLevel().getTownLevel();
        int townUpgradeLevel = townData.getTownLevel().getUpgradeLevel(townUpgrade.getName());

        System.out.println(townUpgrade.getMaterialCode());
        ItemStack upgradeItemStack = HeadUtils.getCustomLoreItem(
                Material.getMaterial(townUpgrade.getMaterialCode()),
                Lang.GUI_TOWN_LEVEL_UP.get());


        HeadUtils.setLore(upgradeItemStack,
                Lang.GUI_TOWN_LEVEL_UP_DESC1.get(townUpgradeLevel + "/" + townUpgrade.getMaxLevel()),
                Lang.GUI_TOWN_LEVEL_UP_DESC2.get(townData.getTownLevel().getTownLevel()+1, townUpgrade.getCost(townUpgradeLevel))
        );


        return ItemBuilder.from(upgradeItemStack).asGuiItem(event -> {
            event.setCancelled(true);
            upgradeTown(player,townUpgrade,townData);
            OpenTownLevel(player);
        });
    }


}

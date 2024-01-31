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
import java.util.Map;

import static org.tan.TownsAndNations.GUI.GuiManager2.OpenTownLevel;
import static org.tan.TownsAndNations.utils.TownUtil.upgradeTown;

public class GuiUtil {

    public static GuiItem makeUpgradeGuiItem(Player player, TownUpgrade townUpgrade, TownData townData){

        int townLevel = townData.getTownLevel().getTownLevel();
        int townUpgradeLevel = townData.getTownLevel().getUpgradeLevel(townUpgrade.getName());

        ItemStack upgradeItemStack = HeadUtils.getCustomLoreItem(
                Material.getMaterial(townUpgrade.getMaterialCode()),
                townUpgrade.getName());
                //Lang.GUI_TOWN_LEVEL_UP.get());

        HeadUtils.setLore(upgradeItemStack,
                Lang.GUI_TOWN_LEVEL_UP_UNI_DESC1.get(townUpgradeLevel + "/" + townUpgrade.getMaxLevel()),
                Lang.GUI_TOWN_LEVEL_UP_UNI_DESC2.get(townUpgradeLevel+1, townUpgrade.getCost(townUpgradeLevel)),
                Lang.GUI_TOWN_LEVEL_UP_UNI_DESC3.get(townUpgrade.getCost(townUpgradeLevel))
        );

        for(Map.Entry<String,Integer> entry : townUpgrade.getBenefits().entrySet()){
            String name = entry.getKey();
            Integer value = entry.getValue();
            String line;
            if(value > 0){
                line = Lang.GUI_TOWN_LEVEL_UP_UNI_DESC3_1.get(name, value);
            }
            else {
                line = Lang.GUI_TOWN_LEVEL_UP_UNI_DESC3_2.get(name, value);
            }
            HeadUtils.addLore(upgradeItemStack, line);
        }
        HeadUtils.addLore(upgradeItemStack, Lang.GUI_TOWN_LEVEL_UP_UNI_DESC4.get());
        for(Map.Entry<String,Integer> entry : townUpgrade.getBenefits().entrySet()){
            String name = entry.getKey();
            Integer value = entry.getValue();
            String line;
            if(value > 0){
                line = Lang.GUI_TOWN_LEVEL_UP_UNI_DESC3_1.get(name, value * townUpgradeLevel);
            }
            else {
                line = Lang.GUI_TOWN_LEVEL_UP_UNI_DESC3_2.get(name, value * townUpgradeLevel);
            }
            HeadUtils.addLore(upgradeItemStack, line);
        }


        return ItemBuilder.from(upgradeItemStack).asGuiItem(event -> {
            event.setCancelled(true);
            upgradeTown(player,townUpgrade,townData);
            OpenTownLevel(player);
        });
    }


}

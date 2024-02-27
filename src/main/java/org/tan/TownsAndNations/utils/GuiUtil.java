package org.tan.TownsAndNations.utils;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.DataClass.TownLevel;
import org.tan.TownsAndNations.DataClass.TownUpgrade;
import org.tan.TownsAndNations.Lang.DynamicLang;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.enums.SoundEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.tan.TownsAndNations.GUI.GuiManager2.OpenTownLevel;
import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;
import static org.tan.TownsAndNations.utils.TownUtil.upgradeTown;

public class GuiUtil {

    public static GuiItem makeUpgradeGuiItem(Player player, TownUpgrade townUpgrade, TownData townData){

        TownLevel townLevelClass = townData.getTownLevel();
        int townLevel = townLevelClass.getTownLevel();
        int townUpgradeLevel = townLevelClass.getUpgradeLevel(townUpgrade.getName());
        boolean requirementsMet = true;

        List <String> lore = new ArrayList<>();
        ItemStack upgradeItemStack = HeadUtils.getCustomLoreItem(
                Material.getMaterial(townUpgrade.getMaterialCode()),
                DynamicLang.get(townUpgrade.getName()));

        lore.add(Lang.GUI_TOWN_LEVEL_UP_UNI_DESC1.get(townUpgradeLevel + "/" + townUpgrade.getMaxLevel()));
        lore.add(Lang.GUI_TOWN_LEVEL_UP_UNI_DESC2.get(townUpgradeLevel+1, townUpgrade.getCost(townUpgradeLevel)));

        //Pre-requisite
        if(townUpgrade.isPrerequisitesNotEmpty()){
            lore.add(Lang.GUI_TOWN_LEVEL_UP_UNI_DESC3.get());

            for(Map.Entry<String,Integer> entry : townUpgrade.getPrerequisites().entrySet()) {
                String name = entry.getKey();
                Integer value = entry.getValue();
                if(value <= townLevelClass.getUpgradeLevel(name)){
                    lore.add(Lang.GUI_TOWN_LEVEL_UP_UNI_DESC3_1.get(DynamicLang.get(name), value));
                }
                else {
                    lore.add(Lang.GUI_TOWN_LEVEL_UP_UNI_DESC3_2.get(DynamicLang.get(name), value));
                    requirementsMet = false;
                }
            }
        }

        //Benefits
        lore.add(Lang.GUI_TOWN_LEVEL_UP_UNI_DESC4.get());
        for(Map.Entry<String,Integer> entry : townUpgrade.getBenefits().entrySet()){
            String name = entry.getKey();
            Integer value = entry.getValue();
            if(value > 0){
                lore.add(Lang.GUI_TOWN_LEVEL_UP_UNI_DESC4_1.get(DynamicLang.get(name), value));
            }
            else {
                lore.add(Lang.GUI_TOWN_LEVEL_UP_UNI_DESC4_2.get(DynamicLang.get(name), value));
            }
        }
        //Total Benefits
        lore.add(Lang.GUI_TOWN_LEVEL_UP_UNI_DESC5.get());
        for(Map.Entry<String,Integer> entry : townUpgrade.getBenefits().entrySet()){
            String name = entry.getKey();
            Integer value = entry.getValue();
            String line;
            if(value > 0){
                lore.add(Lang.GUI_TOWN_LEVEL_UP_UNI_DESC4_1.get(DynamicLang.get(name), value * townUpgradeLevel));
            }
            else {
                lore.add(Lang.GUI_TOWN_LEVEL_UP_UNI_DESC4_2.get(DynamicLang.get(name), value * townUpgradeLevel));
            }
        }
        HeadUtils.setLore(upgradeItemStack, lore);

        boolean finalRequirementsMet = requirementsMet;
        return ItemBuilder.from(upgradeItemStack).asGuiItem(event -> {
            event.setCancelled(true);
            if(finalRequirementsMet){
                upgradeTown(player,townUpgrade,townData,townUpgradeLevel);
            }
            else {
                player.sendMessage(getTANString() + Lang.GUI_TOWN_LEVEL_UP_UNI_REQ_NOT_MET.get());
                SoundUtil.playSound(player, SoundEnum.NOT_ALLOWED);
            }
            OpenTownLevel(player);
        });
    }

    public static GuiItem townUpgradeResume(TownData townData){

        ItemStack townIcon = HeadUtils.getTownIcon(townData.getID());

        List<String> lore = new ArrayList<>();
        lore.add(Lang.TOWN_LEVEL_BONUS_RECAP.get());

        Map<String,Integer> benefits = townData.getTownLevel().getTotalBenefits();

        for(Map.Entry<String,Integer> entry : benefits.entrySet()){
            String value_ID = entry.getKey();
            Integer value = entry.getValue();
            String line;
            if(value > 0){
                line = Lang.GUI_TOWN_LEVEL_UP_UNI_DESC4_1.get(DynamicLang.get(value_ID), value);
            }
            else {
                line = Lang.GUI_TOWN_LEVEL_UP_UNI_DESC4_2.get(DynamicLang.get(value_ID), value);
            }
            lore.add(line);
        }



        HeadUtils.setLore(townIcon, lore);

        return ItemBuilder.from(townIcon).asGuiItem(event -> {
            event.setCancelled(true);
        });
    }


}

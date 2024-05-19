package org.tan.TownsAndNations.utils;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.DataClass.TownLevel;
import org.tan.TownsAndNations.DataClass.TownUpgrade;
import org.tan.TownsAndNations.GUI.GuiManager2;
import org.tan.TownsAndNations.Lang.DynamicLang;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.enums.SoundEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;
import static org.tan.TownsAndNations.utils.TownUtil.upgradeTown;

public class GuiUtil {

    /**
     * Creates a {@link GuiItem} for upgrading a town's level based on the provided town upgrade and town data.
     * @param player the player who is viewing the GUI and attempting the upgrade
     * @param townUpgrade the town upgrade to be applied
     * @param townData the data of the town to be upgraded
     * @return a GUI item representing the upgrade action
     */
    public static GuiItem makeUpgradeGuiItem(final @NotNull Player player, final @NotNull  TownUpgrade townUpgrade, final @NotNull  TownData townData){

        TownLevel townLevelClass = townData.getTownLevel();
        int townUpgradeLevel = townLevelClass.getUpgradeLevel(townUpgrade.getName());

        List<String> lore = townUpgrade.getItemLore(townLevelClass, townUpgradeLevel);

        ItemStack upgradeItemStack = HeadUtils.getCustomLoreItem(
                Material.getMaterial(townUpgrade.getMaterialCode()),
                DynamicLang.get(townUpgrade.getName()),
                lore);

        return ItemBuilder.from(upgradeItemStack).asGuiItem(event -> {
            event.setCancelled(true);
            if(townUpgrade.isPrerequisiteMet(townLevelClass)){
                upgradeTown(player,townUpgrade,townData,townUpgradeLevel);
            }
            else {
                player.sendMessage(getTANString() + Lang.GUI_TOWN_LEVEL_UP_UNI_REQ_NOT_MET.get());
                SoundUtil.playSound(player, SoundEnum.NOT_ALLOWED);
            }
            GuiManager2.OpenTownLevel(player,0);
        });
    }

    /**
     * Create the town upgrade resume {@link GuiItem}. This gui is used to summarise
     * every upgrade rewards the town currently have.
     * @param townData  The town on which the upgrade should be shown
     * @return          The {@link GuiItem} displaying the town current benefices
     */
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

        return ItemBuilder.from(townIcon).asGuiItem(event -> event.setCancelled(true));
    }


}

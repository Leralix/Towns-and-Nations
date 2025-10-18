package org.leralix.tan.dataclass;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.gui.legacy.PlayerGUI;
import org.leralix.tan.lang.DynamicLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.deprecated.HeadUtils;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TownUpgrade {
    private final String name;
    private final String materialCode;
    private final int col;
    private final int row;
    private final int maxLevel;
    private final List<Integer> cost;
    private final Map<String, Integer> prerequisites;
    private final Map<String, Integer> benefits;


    public TownUpgrade(String name, int col, int row, String materialCode, int maxLevel, List<Integer> cost, Map<String, Integer> prerequisites, Map<String, Integer> benefits) {
        this.name = name;
        this.col = col;
        this.row = row;
        this.materialCode = materialCode;
        this.maxLevel = maxLevel;
        this.cost = cost;
        this.prerequisites = prerequisites;
        this.benefits = benefits;
    }


    public String getName() {
        return name;
    }

    public String getMaterialCode() {
        if (materialCode == null)
            return "BEDROCK";
        return materialCode;
    }

    public int getCost(int level) {
        if (cost.size() <= level)
            return cost.getLast();
        return cost.get(level);
    }

    public Map<String, Integer> getPrerequisites() {
        return prerequisites;
    }

    public Map<String, Integer> getBenefits() {
        return benefits;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public List<String> getItemLore(LangType lang, Level townLevelClass, int townUpgradeLevel) {
        List<String> lore = new ArrayList<>();
        boolean isMaxLevel = townUpgradeLevel >= this.getMaxLevel();


        lore.add(Lang.GUI_TOWN_LEVEL_UP_UNI_DESC1.get(lang, townUpgradeLevel + "/" + this.getMaxLevel()));
        if (isMaxLevel)
            lore.add(Lang.GUI_TOWN_LEVEL_UP_UNI_DESC2_MAX_LEVEL.get(lang));
        else
            lore.add(Lang.GUI_TOWN_LEVEL_UP_UNI_DESC2.get(lang, Integer.toString(townUpgradeLevel + 1), Integer.toString(this.getCost(townUpgradeLevel))));


        //Pre-requisite
        if (!prerequisites.isEmpty()) {
            lore.add(Lang.GUI_TOWN_LEVEL_UP_UNI_DESC3.get(lang));

            for (Map.Entry<String, Integer> entry : this.getPrerequisites().entrySet()) {
                String prerequisiteName = entry.getKey();
                int levelNeeded = entry.getValue();
                int currentLevel = townLevelClass.getUpgradeLevel(prerequisiteName);
                if (levelNeeded <= townLevelClass.getUpgradeLevel(prerequisiteName)) {
                    lore.add(Lang.GUI_TOWN_LEVEL_UP_UNI_DESC3_1.get(lang, DynamicLang.get(lang, prerequisiteName), Integer.toString(currentLevel), Integer.toString(levelNeeded)));
                } else {
                    lore.add(Lang.GUI_TOWN_LEVEL_UP_UNI_DESC3_2.get(lang, DynamicLang.get(lang, prerequisiteName), Integer.toString(currentLevel), Integer.toString(levelNeeded)));
                }
            }
        }

        //Benefits
        if (!isMaxLevel) {    //If max level do not show this part
            lore.add(Lang.GUI_TOWN_LEVEL_UP_UNI_DESC4.get(lang));
            for (Map.Entry<String, Integer> entry : this.getBenefits().entrySet()) {
                String prerequisiteName = entry.getKey();
                int value = entry.getValue();
                if (value > 0)
                    lore.add(Lang.GUI_TOWN_LEVEL_UP_UNI_DESC4_1.get(lang, DynamicLang.get(lang, prerequisiteName), Integer.toString(value)));
                else
                    lore.add(Lang.GUI_TOWN_LEVEL_UP_UNI_DESC4_2.get(lang, DynamicLang.get(lang, prerequisiteName), Integer.toString(value)));
            }
        }

        //Total Benefits
        lore.add(Lang.GUI_TOWN_LEVEL_UP_UNI_DESC5.get(lang));
        for (Map.Entry<String, Integer> entry : this.getBenefits().entrySet()) {
            String benefitName = entry.getKey();
            Integer value = entry.getValue();
            if (value > 0) {
                lore.add(Lang.GUI_TOWN_LEVEL_UP_UNI_DESC4_1.get(lang, DynamicLang.get(lang, benefitName), Integer.toString(value * townUpgradeLevel)));
            } else {
                lore.add(Lang.GUI_TOWN_LEVEL_UP_UNI_DESC4_2.get(lang, DynamicLang.get(lang, benefitName), Integer.toString(value * townUpgradeLevel)));
            }
        }
        return lore;
    }

    public boolean isPrerequisiteMet(Level townLevel) {
        if (prerequisites.isEmpty())
            return true;

        for (Map.Entry<String, Integer> entry : this.getPrerequisites().entrySet()) {
            String prerequisiteName = entry.getKey();
            Integer levelNeeded = entry.getValue();
            Integer currentLevel = townLevel.getUpgradeLevel(prerequisiteName);
            if (levelNeeded > currentLevel) {
                return false;
            }
        }

        return true;
    }

    public GuiItem createGuiItem(Player player, TownData townData, int page) {
        Level townLevel = townData.getLevel();
        int townUpgradeLevel = townLevel.getUpgradeLevel(getName());
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);
        List<String> lore = getItemLore(tanPlayer.getLang(), townLevel, townUpgradeLevel);

        ItemStack upgradeItemStack = HeadUtils.createCustomItemStack(
                Material.getMaterial(getMaterialCode()),
                DynamicLang.get(tanPlayer.getLang(), getName()),
                lore);

        return ItemBuilder.from(upgradeItemStack).asGuiItem(event -> {
            event.setCancelled(true);
            if (!isPrerequisiteMet(townLevel)) {
                TanChatUtils.message(player, Lang.GUI_TOWN_LEVEL_UP_UNI_REQ_NOT_MET.get(tanPlayer.getLang()), SoundEnum.NOT_ALLOWED);
                return;
            }
            townData.upgradeTown(player, this, townUpgradeLevel);
            PlayerGUI.openTownLevel(player, page);
        });
    }
}

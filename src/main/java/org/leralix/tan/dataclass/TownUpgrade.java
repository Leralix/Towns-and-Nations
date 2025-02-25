package org.leralix.tan.dataclass;

import dev.triumphteam.gui
.builder.item.ItemBuilder;
import dev.triumphteam.gui
.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.utils.TanChatUtils;
import org.leralix.tan.utils.HeadUtils;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.gui.PlayerGUI;
import org.leralix.tan.lang.DynamicLang;
import org.leralix.tan.lang.Lang;

import java.util.ArrayList;
import java.util.HashMap;
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
        if(materialCode == null)
            return "BEDROCK";
        return materialCode;
    }

    public int getCost(int level) {
        if(cost.size() <= level)
            return cost.get(cost.size()-1);
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

    public List<String> getItemLore(Level townLevelClass, int townUpgradeLevel ) {
        List <String> lore = new ArrayList<>();
        boolean isMaxLevel = townUpgradeLevel >= this.getMaxLevel();


        lore.add(Lang.GUI_TOWN_LEVEL_UP_UNI_DESC1.get(townUpgradeLevel + "/" + this.getMaxLevel()));
        if(isMaxLevel)
            lore.add(Lang.GUI_TOWN_LEVEL_UP_UNI_DESC2_MAX_LEVEL.get());
        else
            lore.add(Lang.GUI_TOWN_LEVEL_UP_UNI_DESC2.get(townUpgradeLevel + 1 , this.getCost(townUpgradeLevel)));


        //Pre-requisite
        if(!prerequisites.isEmpty()){
            lore.add(Lang.GUI_TOWN_LEVEL_UP_UNI_DESC3.get());

            for(Map.Entry<String,Integer> entry : this.getPrerequisites().entrySet()) {
                String prerequisiteName = entry.getKey();
                Integer levelNeeded = entry.getValue();
                Integer currentLevel = townLevelClass.getUpgradeLevel(prerequisiteName);
                if(levelNeeded <= townLevelClass.getUpgradeLevel(prerequisiteName)){
                    lore.add(Lang.GUI_TOWN_LEVEL_UP_UNI_DESC3_1.get(DynamicLang.get(prerequisiteName), currentLevel, levelNeeded));
                }
                else {
                    lore.add(Lang.GUI_TOWN_LEVEL_UP_UNI_DESC3_2.get(DynamicLang.get(prerequisiteName), currentLevel, levelNeeded));
                }
            }
        }

        //Benefits
        if(!isMaxLevel){    //If max level do not show this part
            lore.add(Lang.GUI_TOWN_LEVEL_UP_UNI_DESC4.get());
            for(Map.Entry<String,Integer> entry : this.getBenefits().entrySet()){
                String prerequisiteName = entry.getKey();
                Integer value = entry.getValue();
                if(value > 0)
                    lore.add(Lang.GUI_TOWN_LEVEL_UP_UNI_DESC4_1.get(DynamicLang.get(prerequisiteName), value));
                else
                    lore.add(Lang.GUI_TOWN_LEVEL_UP_UNI_DESC4_2.get(DynamicLang.get(prerequisiteName), value));
            }
        }

        //Total Benefits
        lore.add(Lang.GUI_TOWN_LEVEL_UP_UNI_DESC5.get());
        for(Map.Entry<String,Integer> entry : this.getBenefits().entrySet()){
            String benefitName = entry.getKey();
            Integer value = entry.getValue();
            if(value > 0){
                lore.add(Lang.GUI_TOWN_LEVEL_UP_UNI_DESC4_1.get(DynamicLang.get(benefitName), value * townUpgradeLevel));
            }
            else {
                lore.add(Lang.GUI_TOWN_LEVEL_UP_UNI_DESC4_2.get(DynamicLang.get(benefitName), value * townUpgradeLevel));
            }
        }
        return lore;
    }

    public boolean isPrerequisiteMet(Level townLevel) {
        if(prerequisites.isEmpty())
            return true;

        for(Map.Entry<String,Integer> entry : this.getPrerequisites().entrySet()) {
            String prerequisiteName = entry.getKey();
            Integer levelNeeded = entry.getValue();
            Integer currentLevel = townLevel.getUpgradeLevel(prerequisiteName);
            if(levelNeeded > currentLevel){
                return false;
            }
        }

        return true;
    }

    public GuiItem createGuiItem(Player player, TownData townData, int page) {
        Level townLevel = townData.getLevel();
        int townUpgradeLevel = townLevel.getUpgradeLevel(getName());

        List<String> lore = getItemLore(townLevel, townUpgradeLevel);

        ItemStack upgradeItemStack = HeadUtils.createCustomItemStack(
                Material.getMaterial(getMaterialCode()),
                DynamicLang.get(getName()),
                lore);

        return ItemBuilder.from(upgradeItemStack).asGuiItem(event -> {
            event.setCancelled(true);
            if(!isPrerequisiteMet(townLevel)){
                player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_LEVEL_UP_UNI_REQ_NOT_MET.get());
                SoundUtil.playSound(player, SoundEnum.NOT_ALLOWED);
                return;
            }
            townData.upgradeTown(player,this,townUpgradeLevel);
            PlayerGUI.openTownLevel(player,page);
        });
    }
}

package org.tan.TownsAndNations.DataClass;

import org.tan.TownsAndNations.Lang.DynamicLang;
import org.tan.TownsAndNations.Lang.Lang;

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
    private final HashMap<String, Integer> prerequisites;
    private final HashMap<String, Integer> benefits;



    public TownUpgrade(String name, int col, int row, String materialCode, int maxLevel, List<Integer> cost, HashMap<String, Integer> prerequisites, HashMap<String, Integer> benefits) {
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

    public Boolean isPrerequisitesNotEmpty() {
        return !prerequisites.isEmpty();
    }
    public HashMap<String, Integer> getPrerequisites() {
        return prerequisites;
    }

    public HashMap<String, Integer> getBenefits() {
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

    public List<String> getItemLore(TownLevel townLevelClass, int townUpgradeLevel ) {
        List <String> lore = new ArrayList<>();
        boolean isMaxLevel = townUpgradeLevel >= this.getMaxLevel();

        lore.add(Lang.GUI_TOWN_LEVEL_UP_UNI_DESC1.get(townUpgradeLevel + "/" + this.getMaxLevel()));
        if(isMaxLevel)
            lore.add(Lang.GUI_TOWN_LEVEL_UP_UNI_DESC2_MAX_LEVEL.get());
        else
            lore.add(Lang.GUI_TOWN_LEVEL_UP_UNI_DESC2.get(townUpgradeLevel + 1 , this.getCost(townUpgradeLevel)));


        //Pre-requisite
        if(this.isPrerequisitesNotEmpty()){
            lore.add(Lang.GUI_TOWN_LEVEL_UP_UNI_DESC3.get());

            for(Map.Entry<String,Integer> entry : this.getPrerequisites().entrySet()) {
                String name = entry.getKey();
                Integer levelNeeded = entry.getValue();
                Integer currentLevel = townLevelClass.getUpgradeLevel(name);
                if(levelNeeded <= townLevelClass.getUpgradeLevel(name)){
                    lore.add(Lang.GUI_TOWN_LEVEL_UP_UNI_DESC3_1.get(DynamicLang.get(name), currentLevel, levelNeeded));
                }
                else {
                    lore.add(Lang.GUI_TOWN_LEVEL_UP_UNI_DESC3_2.get(DynamicLang.get(name), currentLevel, levelNeeded));
                }
            }
        }

        //Benefits
        if(isMaxLevel){
            lore.add(Lang.GUI_TOWN_LEVEL_UP_UNI_DESC4.get());
            for(Map.Entry<String,Integer> entry : this.getBenefits().entrySet()){
                String name = entry.getKey();
                Integer value = entry.getValue();
                if(value > 0){
                    lore.add(Lang.GUI_TOWN_LEVEL_UP_UNI_DESC4_1.get(DynamicLang.get(name), value));
                }
                else {
                    lore.add(Lang.GUI_TOWN_LEVEL_UP_UNI_DESC4_2.get(DynamicLang.get(name), value));
                }
            }
        }
        //Total Benefits
        lore.add(Lang.GUI_TOWN_LEVEL_UP_UNI_DESC5.get());
        for(Map.Entry<String,Integer> entry : this.getBenefits().entrySet()){
            String name = entry.getKey();
            Integer value = entry.getValue();
            if(value > 0){
                lore.add(Lang.GUI_TOWN_LEVEL_UP_UNI_DESC4_1.get(DynamicLang.get(name), value * townUpgradeLevel));
            }
            else {
                lore.add(Lang.GUI_TOWN_LEVEL_UP_UNI_DESC4_2.get(DynamicLang.get(name), value * townUpgradeLevel));
            }
        }
        return lore;
    }

    public boolean isPrerequisiteMet(TownLevel townLevel) {
        boolean requirementsMet = true;
        if(this.isPrerequisitesNotEmpty()){
            for(Map.Entry<String,Integer> entry : this.getPrerequisites().entrySet()) {
                String name = entry.getKey();
                Integer levelNeeded = entry.getValue();
                Integer currentLevel = townLevel.getUpgradeLevel(name);
                if(levelNeeded > currentLevel){
                    requirementsMet = false;
                }
            }
        }
        return requirementsMet;
    }
}

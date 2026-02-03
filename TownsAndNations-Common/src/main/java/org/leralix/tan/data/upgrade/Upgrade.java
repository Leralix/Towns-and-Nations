package org.leralix.tan.data.upgrade;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.data.upgrade.rewards.IndividualStat;
import org.leralix.tan.gui.service.requirements.IndividualRequirement;
import org.leralix.tan.gui.service.requirements.upgrade.UpgradeRequirement;
import org.leralix.tan.lang.DynamicLang;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Upgrade {

    /**
     * The row position of the upgrade in the GUI.
     * Min value: 0
     */
    private final int row;
    /**
     * The column position of the upgrade in the GUI.
     * Min value: 0
     */
    private final int column;
    /**
     * The icon material representing the upgrade.
     */
    private final Material iconMaterial;
    /**
     * The name key used for localization.
     * Cannot be null
     */
    private final String nameKey;
    /**
     * The description key used for localization.
     * Can be null
     */
    private final String descriptionKey;
    private final int maxLevel;
    private final List<UpgradeRequirement> upgradeRequirements;
    private final List<IndividualStat> rewards;


    public Upgrade(
            int row,
            int column,
            String nameKey,
            String descriptionKey,
            Material iconMaterial,
            int maxLevel,
            List<UpgradeRequirement> upgradeRequirements,
            List<IndividualStat> rewards
    ){
        this.row = row;
        this.column = column;
        this.nameKey = nameKey;
        this.descriptionKey = descriptionKey;
        this.iconMaterial = iconMaterial;
        this.maxLevel = maxLevel;
        this.upgradeRequirements = upgradeRequirements;
        this.rewards = rewards;

    }

    public Material getIconMaterial() {
        return iconMaterial;
    }

    public String getID() {
        return nameKey;
    }

    public String getDescriptionKey() {
        return descriptionKey;
    }

    public String getName(LangType langType) {
        return DynamicLang.get(langType, nameKey);
    }

    public Collection<IndividualRequirement> getRequirements(TerritoryData territoryData, Player player) {

        List<IndividualRequirement> res = new ArrayList<>();
        for(UpgradeRequirement upgradeRequirement : upgradeRequirements){
            res.add(upgradeRequirement.toIndividualRequirement(this, territoryData, player));
        }
        return res;

    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public int getMaxLevel(){
        return maxLevel;
    }

    public Collection<IndividualStat> getRewards() {
        return rewards;
    }

    public List<FilledLang> getDescription(LangType langType) {
        if(descriptionKey == null){
            return List.of();
        }
        List<FilledLang> res = new ArrayList<>();
        String[] descLines = DynamicLang.get(langType, descriptionKey).split("\n");
        for (String descLine : descLines) {
            res.add(Lang.UPGRADE_DESCRIPTION.get(descLine));
        }
        return res;
    }
}

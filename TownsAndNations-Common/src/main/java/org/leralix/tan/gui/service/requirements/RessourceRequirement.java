package org.leralix.tan.gui.service.requirements;

import org.bukkit.entity.Player;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.data.upgrade.Upgrade;
import org.leralix.tan.gui.service.requirements.upgrade.ItemRequirementBuilder;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.utils.gameplay.InventoryUtil;

import java.util.List;

public class RessourceRequirement extends IndividualRequirementWithCost {

    private final ItemRequirementBuilder info;
    private final Upgrade upgrade;
    private final TerritoryData territoryData;
    private final  Player player;

    public RessourceRequirement(ItemRequirementBuilder info, Upgrade upgrade, TerritoryData territoryData, Player player){
        this.info = info;
        this.upgrade = upgrade;
        this.territoryData = territoryData;
        this.player = player;
    }

    @Override
    public String getLine(LangType langType) {
        int quantity = getRequiredQuantity();
        String name = info.getCustomName() != null ? info.getCustomName() : info.getItemScope().getName(langType);
        if(isInvalid()){
            return Lang.REQUIREMENT_RESOURCES_NEGATIVE.get(langType, Integer.toString(quantity), name);
        }
        else {
            return Lang.REQUIREMENT_RESOURCES_POSITIVE.get(langType, Integer.toString(quantity), name);
        }
    }

    private int getRequiredQuantity() {
        int level = territoryData.getNewLevel().getLevel(upgrade);
        List<Integer> quantity = info.getQuantity();
        if (quantity.size() <= level)
            return quantity.getLast();
        return quantity.get(level);
    }

    @Override
    public boolean isInvalid() {
        return !InventoryUtil.playerEnoughItem(player, info.getItemScope(), getRequiredQuantity(), info.getCustomID());
    }

    @Override
    public void actionDone() {
        InventoryUtil.removeItemsFromInventory(player, info.getItemScope(), getRequiredQuantity(), info.getCustomID());
    }
}

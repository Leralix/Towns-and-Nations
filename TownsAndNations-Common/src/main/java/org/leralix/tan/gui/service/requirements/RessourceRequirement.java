package org.leralix.tan.gui.service.requirements;

import org.bukkit.entity.Player;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.data.upgrade.Upgrade;
import org.leralix.tan.gui.service.requirements.upgrade.ItemRequirementBuilder;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.Placeholder;
import org.leralix.tan.utils.gameplay.InventoryUtil;

import java.util.List;

public class RessourceRequirement extends IndividualRequirementWithCost {

    private final ItemRequirementBuilder info;
    private final Upgrade upgrade;
    private final Territory territoryData;
    private final  Player player;

    public RessourceRequirement(ItemRequirementBuilder info, Upgrade upgrade, Territory territoryData, Player player){
        this.info = info;
        this.upgrade = upgrade;
        this.territoryData = territoryData;
        this.player = player;
    }

    @Override
    public Placeholder getLine() {
        int quantity = getRequiredQuantity();

        List<Placeholder> placeholders = List.of(
                Lang.of(Integer.toString(quantity)),
                info.getCustomName() != null ? Lang.of(info.getCustomName()) : info.getItemScope().getName()
        );
        if(isInvalid()){
            return Lang.REQUIREMENT_RESOURCES_NEGATIVE.get(placeholders);
        }
        else {
            return Lang.REQUIREMENT_RESOURCES_POSITIVE.get(placeholders);
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

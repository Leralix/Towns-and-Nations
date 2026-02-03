package org.leralix.tan.gui.service.requirements.upgrade;

import org.bukkit.entity.Player;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.data.upgrade.Upgrade;
import org.leralix.tan.gui.service.requirements.IndividualRequirement;
import org.leralix.tan.gui.service.requirements.RessourceRequirement;
import org.leralix.tan.gui.service.requirements.model.ItemScope;

import java.util.List;

public class ItemRequirementBuilder extends UpgradeRequirement {

    /**
     * The type of item required
     */
    private final ItemScope itemScope;
    /**
     * A list of the quantity required. Each level will use its corresponding slot in the list, or the last
     * one if too short
     */
    private final List<Integer> quantity;
    /**
     * The name with which the item will be shown
     */
    private final String customName;
    /**
     * THe custom id of the item required to be marked as valid.
     * If {@link Integer#MIN_VALUE} is used, all items will be allowed.
     */
    private final int customID;

    public ItemRequirementBuilder(ItemScope itemScope, List<Integer> quantity, String customName) {
        this(itemScope, quantity, customName, Integer.MIN_VALUE);
    }

    public ItemRequirementBuilder(ItemScope itemScope, List<Integer> quantity, String customName, int customID) {
        this.itemScope = itemScope;
        this.quantity = quantity;
        this.customName = customName;
        this.customID = customID;
    }

    @Override
    public IndividualRequirement toIndividualRequirement(Upgrade upgrade, TerritoryData territoryData, Player player) {
        return new RessourceRequirement(this, upgrade, territoryData , player);
    }

    public ItemScope getItemScope() {
        return itemScope;
    }

    public List<Integer> getQuantity() {
        return quantity;
    }

    public String getCustomName() {
        return customName;
    }

    public int getCustomID() {
        return customID;
    }
}

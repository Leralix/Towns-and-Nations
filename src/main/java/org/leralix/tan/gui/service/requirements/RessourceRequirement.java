package org.leralix.tan.gui.service.requirements;

import org.bukkit.entity.Player;
import org.leralix.tan.gui.service.requirements.model.ItemScope;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.utils.gameplay.InventoryUtil;

public class RessourceRequirement extends IndividualRequirement {

    private final ItemScope itemScope;
    private final Player player;
    private final int amount;

    public RessourceRequirement(ItemScope itemScope, int amount, Player player){
        this.itemScope = itemScope;
        this.player = player;
        this.amount = amount;
    }

    @Override
    public String getLine(LangType langType) {
        if(isInvalid()){
            return Lang.REQUIREMENT_RESOURCES_NEGATIVE.get(langType, itemScope.getName(langType), Integer.toString(amount), itemScope.getName(langType));
        }
        else {
            return Lang.REQUIREMENT_RESOURCES_POSITIVE.get(langType, itemScope.getName(langType), Integer.toString(amount), itemScope.getName(langType));
        }
    }

    @Override
    public boolean isInvalid() {
        return !InventoryUtil.playerEnoughItem(player, itemScope, amount);
    }
}

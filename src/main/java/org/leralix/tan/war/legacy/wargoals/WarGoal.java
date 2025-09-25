package org.leralix.tan.war.legacy.wargoals;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.lang.LangType;

import java.util.List;

public abstract class WarGoal {

    /**
     * Used for serialization
     */
    private final String type;

    protected WarGoal(){
        this.type = this.getClass().getSimpleName();
    }

    public abstract IconBuilder getIcon(LangType langType);

    public abstract String getDisplayName(LangType langType);

    public abstract void applyWarGoal(TerritoryData winner, TerritoryData loser);

    public abstract boolean isCompleted();

    protected IconBuilder buildIcon(Material material, List<String> description, LangType langType){
        ItemStack itemStack = new ItemStack(material);

        return IconManager.getInstance().get(itemStack)
                .setName(getDisplayName(langType))
                .setDescription(description);
    }

    public abstract String getCurrentDesc(LangType langType);

}

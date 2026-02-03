package org.leralix.tan.war.wargoals;

import org.bukkit.Material;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.tan.api.interfaces.war.wargoals.TanWargoal;

import java.util.List;

public abstract class WarGoal implements TanWargoal {

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

    protected IconBuilder buildIcon(Material material, List<FilledLang> description, LangType langType){
        return IconManager.getInstance().get(material)
                .setName(getDisplayName(langType))
                .setDescription(description)
                .setClickToAcceptMessage(Lang.GUI_GENERIC_RIGHT_CLICK_TO_DELETE);
    }

    public abstract String getCurrentDesc(LangType langType);

}

package org.leralix.tan.gui.user.war;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.war.War;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractWarMenu extends BasicGui {

    protected final War war;

    public AbstractWarMenu(Player player, Lang lang, int rows, War war){
        super(player, lang, rows);
        this.war = war;
    }


    protected @NotNull IconBuilder getDefendingSidePanel() {
        List<FilledLang> description = new ArrayList<>();
        description.add(Lang.GUI_DEFENDING_SIDE_ICON_DESC1.get());
        for (TerritoryData territoryData : war.getDefendingTerritories()) {
            description.add(Lang.GUI_ICON_LIST.get(territoryData.getBaseColoredName()));
        }

        return iconManager.get(IconKey.WAR_DEFENDER_SIDE_ICON)
                .setName(Lang.GUI_DEFENDING_SIDE_ICON.get(langType))
                .setDescription(description);
    }

    protected @NotNull IconBuilder getAttackingSidePanel() {
        List<FilledLang> description = new ArrayList<>();
        description.add(Lang.GUI_ATTACKING_SIDE_ICON_DESC1.get());
        for (TerritoryData territoryData : war.getAttackingTerritories()) {
            description.add(Lang.GUI_ICON_LIST.get(territoryData.getBaseColoredName()));
        }

        return iconManager.get(IconKey.WAR_ATTACKER_SIDE_ICON)
                .setName(Lang.GUI_ATTACKING_SIDE_ICON.get(langType))
                .setDescription(description);
    }

    protected @NotNull GuiItem getDefendingSideInfo() {
        return getDefendingSidePanel().asGuiItem(player, langType);
    }

    protected @NotNull GuiItem getAttackingSideInfo() {
        return getAttackingSidePanel().asGuiItem(player, langType);
    }

}

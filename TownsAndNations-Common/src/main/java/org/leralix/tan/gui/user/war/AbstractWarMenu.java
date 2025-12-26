package org.leralix.tan.gui.user.war;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.war.War;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractWarMenu extends BasicGui {

    protected final War war;

    public AbstractWarMenu(Player player, Lang lang, int rows, War war) {
        super(player, lang, rows);
        this.war = war;
    }


    protected @NotNull IconBuilder getDefendingSidePanel() {

        List<FilledLang> description = new ArrayList<>(getCapitulationProgress(war.getMainDefender()));

        description.add(Lang.GUI_DEFENDING_SIDE_ICON_DESC1.get());
        for (TerritoryData territoryData : war.getDefendingTerritories()) {
            description.add(Lang.GUI_ICON_LIST.get(territoryData.getBaseColoredName()));
        }

        return iconManager.get(IconKey.WAR_DEFENDER_SIDE_ICON)
                .setName(Lang.GUI_DEFENDING_SIDE_ICON.get(langType))
                .setDescription(description);
    }

    protected @NotNull IconBuilder getAttackingSidePanel() {

        List<FilledLang> description = new ArrayList<>(getCapitulationProgress(war.getMainAttacker()));

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

    private List<FilledLang> getCapitulationProgress(TerritoryData territoryData) {
        int totalChunk = territoryData.getNumberOfClaimedChunk();
        int occupiedChunk = territoryData.getNumberOfOccupiedChunk();

        int ratio;
        if(totalChunk != 0){
            ratio = occupiedChunk * 100 / totalChunk;
        }
        else {
            ratio = 0;
        }

        boolean capitalExist = territoryData instanceof TownData townData && townData.getCapitalLocation().isPresent();
        boolean capitalCaptured = territoryData instanceof TownData townData && townData.isTownCapitalOccupied();

        List<FilledLang> description = new ArrayList<>();

        if(capitalExist && capitalCaptured){
            ratio += Constants.getCaptureCapitalBonusPercentage();
        }

        description.add(Lang.SURRENDER_PROGRESS_DESCRIPTION.get(
                territoryData.getColoredName(),
                Integer.toString(ratio)
        ));

        if (capitalExist) {
            if (capitalCaptured) {
                description.add(Lang.SURRENDER_PROGRESS_CAPITAL_OCCUPIED.get(
                        Integer.toString(Constants.getCaptureCapitalBonusPercentage())));
            } else {
                description.add(Lang.SURRENDER_PROGRESS_CAPITAL_OWNED.get());
            }
        }
        description.add(Lang.EMPTY.get());
        return description;
    }


}

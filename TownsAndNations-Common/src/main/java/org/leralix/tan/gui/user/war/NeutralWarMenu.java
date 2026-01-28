package org.leralix.tan.gui.user.war;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.war.War;

/**
 * War menu shown to neutral territories
 */
public class NeutralWarMenu extends AbstractWarMenu {

    private final TerritoryData territoryData;

    NeutralWarMenu(Player player, TerritoryData territoryData, War war) {
        super(player, Lang.HEADER_WARS_MENU, 3, war);
        this.territoryData = territoryData;
        open();
    }

    @Override
    public void open() {
        gui.setItem(2, 4, getJoinAttackingSide());
        gui.setItem(2, 6, getJoinDefendingSide());
    }

    private @NotNull GuiItem getJoinDefendingSide() {
        return getDefendingSidePanel()
                .setClickToAcceptMessage(Lang.CLICK_TO_JOIN_SIDE)
                .setAction(action -> {
                    war.addDefender(territoryData);
                    new SecondaryWarMenu(player, territoryData, war);
                })
                .asGuiItem(player, langType);
    }

    private @NotNull GuiItem getJoinAttackingSide() {
        return getAttackingSidePanel()
                .setClickToAcceptMessage(Lang.CLICK_TO_JOIN_SIDE)
                .setAction(action -> {
                    war.addAttacker(territoryData);
                    new SecondaryWarMenu(player, territoryData, war);
                })
                .asGuiItem(player, langType);
    }
}

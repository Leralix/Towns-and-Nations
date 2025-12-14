package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.BrowseAttackScope;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.user.war.PlannedAttackMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.WarStorage;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.war.PlannedAttack;

import java.util.ArrayList;
import java.util.List;

public class AttackMenu extends IteratorGUI {

    private final TerritoryData territoryData;

    private BrowseAttackScope scope;

    public AttackMenu(Player player, TerritoryData territoryData) {
        super(player, Lang.HEADER_WARS_MENU, 6);
        this.territoryData = territoryData;
        this.scope = BrowseAttackScope.UNFINISHED_ONLY;
        open();
    }

    @Override
    public void open() {

        iterator(getWars(tanPlayer), p -> new WarsMenu(player, territoryData));

        gui.setItem(6, 6, browseAttackButton());
        gui.open(player);
    }

    private GuiItem browseAttackButton() {
        return GuiUtil.getNextScopeButton(
                iconManager,
                this,
                scope,
                nextScope -> scope = nextScope,
                langType,
                player
        );
    }


    private List<GuiItem> getWars(ITanPlayer tanPlayer) {
        ArrayList<GuiItem> guiItems = new ArrayList<>();
        for (PlannedAttack plannedAttack : WarStorage.getInstance().getAllAttacks()) {

            if(scope.allowAttack(plannedAttack)){
                guiItems.add(
                        plannedAttack.getIcon(iconManager, tanPlayer.getLang(), tanPlayer.getTimeZone(), territoryData)
                        .setAction(action -> new PlannedAttackMenu(player, territoryData, plannedAttack))
                        .asGuiItem(player, langType)
                );
            }
        }
        return guiItems;
    }
}

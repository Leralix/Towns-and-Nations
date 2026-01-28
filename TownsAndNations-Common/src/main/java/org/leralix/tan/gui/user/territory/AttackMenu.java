package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.gui.scope.BrowseAttackScope;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.WarStorage;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.war.PlannedAttack;
import org.leralix.tan.war.info.AttackResultCancelled;

import java.util.ArrayList;
import java.util.List;

import static org.leralix.lib.data.SoundEnum.MINOR_GOOD;

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

        iterator(getAttacks(tanPlayer), p -> new WarsMenu(player, territoryData));

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


    private List<GuiItem> getAttacks(ITanPlayer tanPlayer) {
        ArrayList<GuiItem> guiItems = new ArrayList<>();
        for (PlannedAttack plannedAttack : WarStorage.getInstance().getAllAttacks()) {
            if (scope.allowAttack(plannedAttack)) {

                boolean startedAttack = plannedAttack.getSideDeclaring() == plannedAttack.getWar().getTerritoryRole(territoryData);

                IconBuilder builder = plannedAttack.getIcon(iconManager, tanPlayer.getLang(), tanPlayer.getTimeZone(), territoryData);

                if (startedAttack && plannedAttack.isNotStarted() && !plannedAttack.isCancelled()) {

                    builder.setClickToAcceptMessage(Lang.GUI_GENERIC_RIGHT_CLICK_TO_CANCEL)
                            .setAction(action -> {
                                if (action.isRightClick()) {
                                    plannedAttack.end(new AttackResultCancelled());
                                    territoryData.broadcastMessageWithSound(Lang.ATTACK_SUCCESSFULLY_CANCELLED.get(plannedAttack.getWar().getMainDefender().getName()), MINOR_GOOD);
                                    open();
                                }
                            });
                }

                guiItems.add(builder.asGuiItem(player, langType));
            }
        }
        return guiItems;
    }
}

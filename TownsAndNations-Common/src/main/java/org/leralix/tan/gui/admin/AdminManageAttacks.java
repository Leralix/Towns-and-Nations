package org.leralix.tan.gui.admin;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.war.PlannedAttack;
import org.leralix.tan.war.War;
import org.leralix.tan.war.info.AttackResultCancelled;

import java.util.ArrayList;
import java.util.List;

public class AdminManageAttacks extends IteratorGUI {

    private final War war;
    private final BasicGui returnMenu;

    public AdminManageAttacks(Player player, War war, BasicGui returnMenu) {
        super(player, Lang.HEADER_ADMIN_WAR_MENU, 3);
        this.war = war;
        this.returnMenu = returnMenu;
        open();
    }


    @Override
    public void open() {
        iterator(getWars(), p -> returnMenu.open());
        gui.open(player);
    }

    private List<GuiItem> getWars() {
        List<GuiItem> res = new ArrayList<>();
        for(PlannedAttack plannedAttack : war.getPlannedAttacks()){
            res.add(plannedAttack.getAdminIcon(iconManager, langType, tanPlayer.getTimeZone())
                    .setAction(action -> {
                        if (!plannedAttack.isAdminApproved()) {
                            if (action.isLeftClick()) {
                                plannedAttack.setAdminApproved(true);
                            } else if (action.isRightClick()) {
                                plannedAttack.end(new AttackResultCancelled());
                            }
                        }
                        open();
                    })
                    .asGuiItem(player, langType)
            );
        }
        return res;
    }
}

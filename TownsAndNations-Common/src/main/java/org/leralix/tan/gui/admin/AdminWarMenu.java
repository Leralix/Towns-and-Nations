package org.leralix.tan.gui.admin;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.WarStorage;
import org.leralix.tan.war.War;
import org.leralix.tan.war.info.AttackResultCancelled;

import java.util.ArrayList;
import java.util.List;

public class AdminWarMenu extends IteratorGUI {

    public AdminWarMenu(Player player) {
        super(player, Lang.HEADER_ADMIN_WAR_MENU, 6);
        open();
    }


    @Override
    public void open() {
        iterator(getWars(), p -> new AdminMainMenu(player));
        gui.open(player);
    }

    private List<GuiItem> getWars() {
        List<GuiItem> res = new ArrayList<>();
        for (War war : WarStorage.getInstance().getAllWars()) {

            IconBuilder iconBuilder = war.getIcon();

            int nbAttackToApprove = nbAttackToApprove(war);
            if (nbAttackToApprove > 0) {
                iconBuilder.addDescription(
                        Lang.ADMIN_ATTACKS_NEED_APPROVALS.get(Integer.toString(nbAttackToApprove))
                );
            }

            res.add(
                    iconBuilder.setAction( action ->
                            new AdminManageWarMenu(player, war, this)
                    ).asGuiItem(player, langType)
            );
        }
        return res;
    }

    private int nbAttackToApprove(War war) {
        int res = 0;
        for (var attacks : war.getPlannedAttacks()) {
            if (!attacks.isAdminApproved()) {
                res++;
            }
        }
        return res;
    }
}

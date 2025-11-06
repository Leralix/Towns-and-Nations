package org.leralix.tan.gui.admin;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlannedAttackStorage;
import org.leralix.tan.war.PlannedAttack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AdminWarMenu extends IteratorGUI {

    private final Collection<PlannedAttack> plannedAttackList;

    public AdminWarMenu(Player player){
        super(player, Lang.HEADER_ADMIN_WAR_MENU, 6);
        plannedAttackList = PlannedAttackStorage.getInstance().getAll().values();
        open();
    }


    @Override
    public void open() {
        iterator(getWars(), p -> new AdminMainMenu(player));
        gui.open(player);
    }

    private List<GuiItem> getWars() {
        List<GuiItem> res = new ArrayList<>();
        for (PlannedAttack plannedAttack : plannedAttackList) {
            ItemStack icon = plannedAttack.getAdminIcon(langType);

            GuiItem item = ItemBuilder.from(icon).asGuiItem(event -> {
                event.setCancelled(true);
                if (!plannedAttack.isAdminApproved()) {
                    if (event.isLeftClick()) {
                        plannedAttack.setAdminApproved(true);
                    } else if (event.isRightClick()) {
                        plannedAttack.end();
                    }
                }
                open();
            });
            res.add(item);
        }
        return res;
    }
}

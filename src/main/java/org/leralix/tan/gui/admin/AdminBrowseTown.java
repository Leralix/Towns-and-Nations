package org.leralix.tan.gui.admin;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.deprecated.HeadUtils;

import java.util.ArrayList;
import java.util.List;

public class AdminBrowseTown extends IteratorGUI {


    public AdminBrowseTown(Player player) {
        super(player, Lang.HEADER_ADMIN_TOWN_MENU, 6);
        open();
    }

    @Override
    public void open() {
        iterator(getTowns(), p -> new AdminMainMenu(player));
        gui.open(player);
    }

    private List<GuiItem> getTowns() {
        ArrayList<GuiItem> guiItems = new ArrayList<>();
        for (TownData townData : TownDataStorage.getInstance().getAll().values()) {
            ItemStack townIcon = townData.getIconWithInformations(tanPlayer.getLang());
            HeadUtils.addLore(townIcon,
                    "",
                    Lang.ADMIN_GUI_LEFT_CLICK_TO_MANAGE_TOWN.get(langType)
            );

            GuiItem townIterationGui = ItemBuilder.from(townIcon).asGuiItem(event -> {
                event.setCancelled(true);
                new AdminManageTown(player, townData);
            });
            guiItems.add(townIterationGui);
        }
        return guiItems;
    }
}

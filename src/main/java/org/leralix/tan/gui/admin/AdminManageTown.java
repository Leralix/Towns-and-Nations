package org.leralix.tan.gui.admin;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.deprecated.HeadUtils;
import org.leralix.tan.utils.text.TanChatUtils;

public class AdminManageTown extends AdminManageTerritory {

    private final TownData townData;

    public AdminManageTown(Player player, TownData townData) {
        super(player, Lang.HEADER_ADMIN_SPECIFIC_REGION_MENU.get(player, townData.getName()), 3, townData);
        this.townData = townData;
        open();
    }

    @Override
    public void open() {

        gui.setItem(2, 2, getRenameTerritory());
        gui.setItem(2, 3, getChangeDescription());
        gui.setItem(2, 4, changeLeader());
        gui.setItem(2, 5, getChangeRegion());

        gui.setItem(2, 6, getTransactionHistory());
        gui.setItem(2, 8, getDelete());

        gui.open(player);
    }

    private @NotNull GuiItem getChangeRegion() {
        String name = townData.getOverlord()
                .map(TerritoryData::getName)
                .orElseGet(() -> Lang.NO_REGION.get(langType));

        ItemStack setRegionIcon = HeadUtils.makeSkullB64(name, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDljMTgzMmU0ZWY1YzRhZDljNTE5ZDE5NGIxOTg1MDMwZDI1NzkxNDMzNGFhZjI3NDVjOWRmZDYxMWQ2ZDYxZCJ9fX0=");
        if (townData.haveOverlord()) {
            if (townData.isCapital())
                HeadUtils.addLore(setRegionIcon, Lang.GUI_CANNOT_QUIT_IF_LEADER.get(langType));
            else
                HeadUtils.addLore(setRegionIcon, Lang.GUI_RIGHT_CLICK_TO_QUIT.get(langType));
        } else {
            HeadUtils.addLore(setRegionIcon, Lang.GUI_LEFT_CLICK_TO_SET_REGION.get(langType));
        }

        return ItemBuilder.from(setRegionIcon).asGuiItem(event -> {
            event.setCancelled(true);
            if (townData.haveOverlord()) {
                if (townData.isCapital())
                    TanChatUtils.message(player, Lang.GUI_CANNOT_QUIT_IF_LEADER.get(langType));
                else {
                    townData.removeOverlord();
                    open();
                }
            } else {
                new AdminSelectNewOverlord(player, townData);
            }
        });

    }

    private @NotNull GuiItem changeLeader() {
        //TODO : merge leader system between town and region
        return null;
    }

}

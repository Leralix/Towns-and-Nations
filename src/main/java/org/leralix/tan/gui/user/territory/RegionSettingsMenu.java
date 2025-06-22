package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.legacy.PlayerGUI;
import org.leralix.tan.gui.user.MainMenu;
import org.leralix.tan.gui.user.property.TownPropertiesMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.FileUtil;
import org.leralix.tan.utils.GuiUtil;
import org.leralix.tan.utils.TanChatUtils;

import static org.leralix.lib.data.SoundEnum.GOOD;
import static org.leralix.lib.data.SoundEnum.NOT_ALLOWED;

public class RegionSettingsMenu extends SettingsMenus {

    private final RegionData regionData;

    public RegionSettingsMenu(Player player, RegionData regionData) {
        super(player, Lang.HEADER_SETTINGS.get(player), regionData);
        this.regionData = regionData;
        open();
    }

    @Override
    public void open() {
        gui.setItem(1, 5, getTerritoryInfo());
        gui.getFiller().fillTop(GuiUtil.getUnnamedItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE));

        gui.setItem(2, 2, getRenameButton());
        gui.setItem(2, 3, getChangeDescriptionButton());
        gui.setItem(2, 4, getChangeColorButton());

        gui.setItem(2, 6, getChangeOwnershipButton());
        gui.setItem(2, 7, getDeleteButton());

        gui.setItem(3, 1, GuiUtil.createBackArrow(player, p -> new RegionMenu(player, regionData)));

        gui.open(player);
    }

    private @NotNull GuiItem getChangeOwnershipButton() {
        return iconManager.get(IconKey.REGION_CHANGE_OWNERSHIP_ICON)
                .setName(Lang.GUI_REGION_CHANGE_CAPITAL.get(tanPlayer))
                .setDescription(
                        Lang.GUI_REGION_CHANGE_CAPITAL_DESC1.get(tanPlayer, regionData.getCapital().getName()),
                        Lang.GUI_REGION_CHANGE_CAPITAL_DESC2.get(tanPlayer)
                )
                .setAction(event -> {
                    event.setCancelled(true);
                    if (!regionData.isLeader(tanPlayer)) {
                        player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_NEED_TO_BE_LEADER_OF_REGION.get(tanPlayer));
                        return;
                    }
                    PlayerGUI.openRegionChangeOwnership(player, 0);
                })
                .asGuiItem(player);
    }

    private GuiItem getDeleteButton() {
        return iconManager.get(IconKey.REGION_DELETE_REGION_ICON)
                .setName(Lang.GUI_REGION_DELETE.get(tanPlayer))
                .setDescription(
                        Lang.GUI_REGION_DELETE_DESC1.get(tanPlayer, regionData.getName()),
                        Lang.GUI_REGION_DELETE_DESC2.get(tanPlayer),
                        Lang.GUI_REGION_DELETE_DESC3.get(tanPlayer)
                )
                .setAction(event -> {
                    event.setCancelled(true);
                    if (!regionData.isLeader(tanPlayer)) {
                        player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_NEED_TO_BE_LEADER_OF_REGION.get(tanPlayer));
                        return;
                    }
                    if (regionData.isCapital()) {
                        player.sendMessage(Lang.CANNOT_DELETE_TERRITORY_IF_CAPITAL.get(tanPlayer, regionData.getOverlord().getBaseColoredName()));
                        return;
                    }

                    if (!player.hasPermission("tan.base.region.disband")) {
                        player.sendMessage(Lang.PLAYER_NO_PERMISSION.get(tanPlayer));
                        SoundUtil.playSound(player, NOT_ALLOWED);
                        return;
                    }

                    PlayerGUI.openConfirmMenu(player, Lang.GUI_CONFIRM_DELETE_REGION.get(tanPlayer, regionData.getName()), confirm -> {
                        FileUtil.addLineToHistory(Lang.REGION_DELETED_NEWSLETTER.get(tanPlayer, player.getName(), regionData.getName()));
                        regionData.delete();
                        SoundUtil.playSound(player, GOOD);
                        new MainMenu(player);
                    }, remove -> open());
                })
                .asGuiItem(player);
    }


}

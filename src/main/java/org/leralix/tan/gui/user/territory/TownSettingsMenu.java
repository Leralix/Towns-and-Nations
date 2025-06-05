package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.legacy.PlayerGUI;
import org.leralix.tan.gui.user.property.TownPropertiesMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.FileUtil;
import org.leralix.tan.utils.GuiUtil;
import org.leralix.tan.utils.TanChatUtils;

import static org.leralix.lib.data.SoundEnum.*;

public class TownSettingsMenu extends SettingsMenus {

    private final TownData townData;

    public TownSettingsMenu(Player player, TownData townData) {
        super(player, Lang.HEADER_SETTINGS.get(player), townData);
        this.townData = townData;
        open();
    }

    @Override
    public void open() {

        gui.setItem(1, 5, getTerritoryInfo());
        gui.getFiller().fillTop(GuiUtil.getUnnamedItem(Material.BLUE_STAINED_GLASS_PANE));

        gui.setItem(2, 2, getRenameButton());
        gui.setItem(2, 3, getChangeDescriptionButton());
        gui.setItem(2, 4, getChangeColorButton());

        gui.setItem(2, 5, getChangeApplicationButton());
        gui.setItem(2, 6, getChangeOwnershipButton());
        gui.setItem(2, 7, getQuitButton());
        gui.setItem(2, 8, getDeleteButton());

        gui.setItem(3, 1, GuiUtil.createBackArrow(player, p -> new TownMenu(player)));

        gui.open(player);
    }

    private GuiItem getQuitButton() {
        return iconManager.get(IconKey.TOWN_QUIT_TOWN_ICON)
                .setName(Lang.GUI_TOWN_SETTINGS_LEAVE_TOWN.get(playerData))
                .setDescription(
                        Lang.GUI_TOWN_SETTINGS_LEAVE_TOWN_DESC1.get(playerData, townData.getName()),
                        Lang.GUI_TOWN_SETTINGS_LEAVE_TOWN_DESC2.get(playerData)
                )
                .setAction(event -> {
                    event.setCancelled(true);

                    if (!player.hasPermission("tan.base.town.quit")) {
                        player.sendMessage(Lang.PLAYER_NO_PERMISSION.get());
                        SoundUtil.playSound(player, NOT_ALLOWED);
                        return;
                    }

                    if (townData.isLeader(playerData)) {
                        SoundUtil.playSound(player, NOT_ALLOWED);
                        player.sendMessage(TanChatUtils.getTANString() + Lang.CHAT_CANT_LEAVE_TOWN_IF_LEADER.get(playerData));
                        return;
                    }

                    if (townData.haveOverlord()) {
                        RegionData regionData = townData.getRegion();
                        if (regionData.isLeader(playerData)) {
                            SoundUtil.playSound(player, NOT_ALLOWED);
                            player.sendMessage(TanChatUtils.getTANString() + Lang.CHAT_CANT_LEAVE_TOWN_IF_REGION_LEADER.get(playerData));
                        }
                    }

                    PlayerGUI.openConfirmMenu(player, Lang.GUI_CONFIRM_PLAYER_LEAVE_TOWN.get(playerData, playerData.getNameStored()), confirm -> {
                        player.closeInventory();
                        townData.removePlayer(playerData);
                        player.sendMessage(TanChatUtils.getTANString() + Lang.CHAT_PLAYER_LEFT_THE_TOWN.get(playerData));
                        townData.broadcastMessageWithSound(Lang.TOWN_BROADCAST_PLAYER_LEAVE_THE_TOWN.get(playerData, playerData.getNameStored()), BAD);
                    }, remove -> open());
                })
                .asGuiItem(player);

    }

    private GuiItem getDeleteButton() {
        return iconManager.get(IconKey.TOWN_DELETE_TOWN_ICON)
                .setName(Lang.GUI_TOWN_SETTINGS_DELETE_TOWN.get(playerData))
                .setDescription(
                        Lang.GUI_TOWN_SETTINGS_DELETE_TOWN_DESC1.get(playerData, townData.getName()),
                        Lang.GUI_TOWN_SETTINGS_DELETE_TOWN_DESC2.get(playerData)
                )
                .setAction(event -> {
                    event.setCancelled(true);
                    if (!townData.isLeader(playerData)) {
                        player.sendMessage(TanChatUtils.getTANString() + Lang.CHAT_CANT_DISBAND_TOWN_IF_NOT_LEADER.get(playerData));
                        return;
                    }
                    if (townData.isCapital()) {
                        player.sendMessage(Lang.CANNOT_DELETE_TERRITORY_IF_CAPITAL.get(playerData, townData.getOverlord().getBaseColoredName()));
                        return;
                    }

                    if (!player.hasPermission("tan.base.town.disband")) {
                        player.sendMessage(Lang.PLAYER_NO_PERMISSION.get(playerData));
                        SoundUtil.playSound(player, NOT_ALLOWED);
                        return;
                    }

                    PlayerGUI.openConfirmMenu(player, Lang.GUI_CONFIRM_PLAYER_DELETE_TOWN.get(playerData, townData.getName()), confirm -> {
                        FileUtil.addLineToHistory(Lang.TOWN_DELETED_NEWSLETTER.get(playerData, player.getName(), townData.getName()));
                        townData.delete();
                        player.closeInventory();
                        SoundUtil.playSound(player, GOOD);
                    }, remove -> open());


                })
                .asGuiItem(player);
    }

    private @NotNull GuiItem getChangeApplicationButton() {
        IconKey iconKey = townData.isRecruiting() ? IconKey.TOWN_ALLOW_APPLICATION_ICON : IconKey.TOWN_DENY_APPLICATION_ICON;
        return iconManager.get(iconKey)
                .setName(Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_APPLICATION.get(playerData))
                .setDescription(
                        townData.isRecruiting() ?
                                Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_APPLICATION_ACCEPT.get(playerData) :
                                Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_APPLICATION_NOT_ACCEPT.get(playerData),
                        Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_APPLICATION_CLICK_TO_SWITCH.get(playerData)
                )
                .setAction(event -> {
                    townData.swapRecruiting();
                    open();
                })
                .asGuiItem(player);
    }

    private @NotNull GuiItem getChangeOwnershipButton() {
        return iconManager.get(IconKey.TOWN_CHANGE_OWNERSHIP_ICON)
                .setName(Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP.get(playerData))
                .setDescription(
                        Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_DESC1.get(playerData),
                        Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_DESC2.get(playerData)
                )
                .setAction(event -> {
                    if (townData.isLeader(playerData))
                        PlayerGUI.openTownChangeOwnershipPlayerSelect(player, townData, 0);
                    else
                        player.sendMessage(TanChatUtils.getTANString() + Lang.NOT_TOWN_LEADER_ERROR.get(playerData));
                })
                .asGuiItem(player);

    }


}

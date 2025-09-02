package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.events.EventManager;
import org.leralix.tan.events.events.TownDeletedInternalEvent;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.legacy.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.listeners.chat.events.ChangeTownTag;
import org.leralix.tan.listeners.interact.RightClickListener;
import org.leralix.tan.listeners.interact.events.ChangeCapital;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.file.FileUtil;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.List;

import static org.leralix.lib.data.SoundEnum.*;

public class TownSettingsMenu extends SettingsMenus {

    private final TownData townData;

    public TownSettingsMenu(Player player, TownData townData) {
        super(player, Lang.HEADER_SETTINGS.get(player), townData, 4);
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

        gui.setItem(3, 2, getChangeApplicationButton());
        gui.setItem(3, 3, getChangeCapitalChunkButton());
        if(Constants.enableTownTag()){
            gui.setItem(3, 4, getChangeTagButton());
        }


        gui.setItem(2, 6, getChangeOwnershipButton());
        gui.setItem(2, 7, getQuitButton());
        gui.setItem(2, 8, getDeleteButton());

        gui.setItem(4, 1, GuiUtil.createBackArrow(player, p -> new TownMenu(player, townData)));

        gui.open(player);
    }

    private @NotNull GuiItem getChangeCapitalChunkButton() {

        List<String> desc = new ArrayList<>();

        townData.getCapitalLocation().ifPresentOrElse(
                vector2D -> {
                    desc.add(Lang.GUI_CAPITAL_CHUNK_ACTUAL_POSITION.get(langType, Lang.DISPLAY_2D_COORDINATES.get(vector2D.getX() * 16, vector2D.getZ() * 16)));
                    desc.add(Lang.GUI_GENERIC_CLICK_TO_MODIFY.get(langType));
                },
                () -> desc.add(Lang.GUI_NO_CAPITAL_CHUNK.get(langType))
        );


        return iconManager.get(IconKey.CHANGE_TOWN_CAPITAL_ICON)
                .setName(Lang.GUI_TOWN_SETTINGS_CHANGE_CAPITAL.get(langType))
                .setDescription(desc)
                .setAction( action -> RightClickListener.register(player, new ChangeCapital(townData, p -> open())))
                .asGuiItem(player);

    }

    private @NotNull GuiItem getChangeTagButton() {
        return iconManager.get(IconKey.CHANGE_TOWN_TAG_ICON)
                .setName(Lang.GUI_TOWN_SETTINGS_CHANGE_TAG.get(langType))
                .setDescription(
                        Lang.GUI_TOWN_SETTINGS_CHANGE_TAG_DESC1.get(langType, townData.getColoredTag()),
                        Lang.GUI_GENERIC_CLICK_TO_MODIFY.get(langType)
                )
                .setAction( action -> PlayerChatListenerStorage.register(player, new ChangeTownTag(townData, p -> open())))
                .asGuiItem(player);
    }

    private GuiItem getQuitButton() {
        return iconManager.get(IconKey.TOWN_QUIT_TOWN_ICON)
                .setName(Lang.GUI_TOWN_SETTINGS_LEAVE_TOWN.get(tanPlayer))
                .setDescription(
                        Lang.GUI_TOWN_SETTINGS_LEAVE_TOWN_DESC1.get(tanPlayer, townData.getName()),
                        Lang.GUI_TOWN_SETTINGS_LEAVE_TOWN_DESC2.get(tanPlayer)
                )
                .setAction(event -> {
                    event.setCancelled(true);

                    if (!player.hasPermission("tan.base.town.quit")) {
                        player.sendMessage(Lang.PLAYER_NO_PERMISSION.get());
                        SoundUtil.playSound(player, NOT_ALLOWED);
                        return;
                    }

                    if (townData.isLeader(tanPlayer)) {
                        SoundUtil.playSound(player, NOT_ALLOWED);
                        player.sendMessage(TanChatUtils.getTANString() + Lang.CHAT_CANT_LEAVE_TOWN_IF_LEADER.get(tanPlayer));
                        return;
                    }

                    if (townData.haveOverlord()) {
                        RegionData regionData = townData.getRegion();
                        if (regionData.isLeader(tanPlayer)) {
                            SoundUtil.playSound(player, NOT_ALLOWED);
                            player.sendMessage(TanChatUtils.getTANString() + Lang.CHAT_CANT_LEAVE_TOWN_IF_REGION_LEADER.get(tanPlayer));
                        }
                    }

                    PlayerGUI.openConfirmMenu(player, Lang.GUI_CONFIRM_PLAYER_LEAVE_TOWN.get(tanPlayer, tanPlayer.getNameStored()), confirm -> {
                        player.closeInventory();
                        townData.removePlayer(tanPlayer);
                        player.sendMessage(TanChatUtils.getTANString() + Lang.CHAT_PLAYER_LEFT_THE_TOWN.get(tanPlayer));
                        townData.broadcastMessageWithSound(Lang.TOWN_BROADCAST_PLAYER_LEAVE_THE_TOWN.get(tanPlayer, tanPlayer.getNameStored()), BAD);
                    }, remove -> open());
                })
                .asGuiItem(player);

    }

    private GuiItem getDeleteButton() {
        return iconManager.get(IconKey.TOWN_DELETE_TOWN_ICON)
                .setName(Lang.GUI_TOWN_SETTINGS_DELETE_TOWN.get(tanPlayer))
                .setDescription(
                        Lang.GUI_TOWN_SETTINGS_DELETE_TOWN_DESC1.get(tanPlayer, townData.getName()),
                        Lang.GUI_TOWN_SETTINGS_DELETE_TOWN_DESC2.get(tanPlayer)
                )
                .setAction(event -> {
                    event.setCancelled(true);
                    if (!townData.isLeader(tanPlayer)) {
                        player.sendMessage(TanChatUtils.getTANString() + Lang.CHAT_CANT_DISBAND_TOWN_IF_NOT_LEADER.get(tanPlayer));
                        return;
                    }
                    if (townData.isCapital()) {
                        player.sendMessage(Lang.CANNOT_DELETE_TERRITORY_IF_CAPITAL.get(tanPlayer, townData.getOverlord().get().getBaseColoredName()));
                        return;
                    }

                    if (!player.hasPermission("tan.base.town.disband")) {
                        player.sendMessage(Lang.PLAYER_NO_PERMISSION.get(tanPlayer));
                        SoundUtil.playSound(player, NOT_ALLOWED);
                        return;
                    }

                    PlayerGUI.openConfirmMenu(player, Lang.GUI_CONFIRM_PLAYER_DELETE_TOWN.get(tanPlayer, townData.getName()), confirm -> {
                        FileUtil.addLineToHistory(Lang.TOWN_DELETED_NEWSLETTER.get(tanPlayer, player.getName(), townData.getName()));
                        EventManager.getInstance().callEvent(new TownDeletedInternalEvent(townData, tanPlayer));
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
                .setName(Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_APPLICATION.get(tanPlayer))
                .setDescription(
                        townData.isRecruiting() ?
                                Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_APPLICATION_ACCEPT.get(tanPlayer) :
                                Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_APPLICATION_NOT_ACCEPT.get(tanPlayer),
                        Lang.GUI_GENERIC_CLICK_TO_SWITCH.get(tanPlayer)
                )
                .setAction(event -> {
                    townData.swapRecruiting();
                    open();
                })
                .asGuiItem(player);
    }

    private @NotNull GuiItem getChangeOwnershipButton() {
        return iconManager.get(IconKey.TOWN_CHANGE_OWNERSHIP_ICON)
                .setName(Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP.get(tanPlayer))
                .setDescription(
                        Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_DESC1.get(tanPlayer),
                        Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_DESC2.get(tanPlayer)
                )
                .setAction(event -> {
                    if (townData.isLeader(tanPlayer)){
                        new SelectNewOwnerForTownMenu(player, townData);
                    }
                    else{
                        player.sendMessage(TanChatUtils.getTANString() + Lang.NOT_TOWN_LEADER_ERROR.get(tanPlayer));
                    }
                })
                .asGuiItem(player);

    }


}

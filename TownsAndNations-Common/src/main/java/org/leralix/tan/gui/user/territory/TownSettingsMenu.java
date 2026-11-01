package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.position.Vector2D;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.territory.Town;
import org.leralix.tan.data.territory.permission.RecruitingPolicy;
import org.leralix.tan.data.territory.rank.RolePermission;
import org.leralix.tan.events.EventManager;
import org.leralix.tan.events.events.TownDeletedInternalEvent;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.common.ConfirmMenu;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.service.requirements.LeaderRequirement;
import org.leralix.tan.gui.service.requirements.RankPermissionRequirement;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.listeners.chat.events.ChangeTownTag;
import org.leralix.tan.listeners.interact.RightClickListener;
import org.leralix.tan.listeners.interact.events.ChangeCapital;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.file.FileUtil;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.leralix.lib.data.SoundEnum.GOOD;
import static org.leralix.lib.data.SoundEnum.NOT_ALLOWED;

public class TownSettingsMenu extends SettingsMenus {

    private final Town townData;

    public TownSettingsMenu(Player player, Town townData, BasicGui returnGUI) {
        super(player, Lang.HEADER_SETTINGS, townData, 4, returnGUI);
        this.townData = townData;
        open();
    }

    @Override
    public void open() {

        gui.setItem(1, 5, getTerritoryInfo());
        gui.getFiller().fillTop(getUnnamedItem(Material.BLUE_STAINED_GLASS_PANE));

        gui.setItem(2, 2, getRenameButton());
        gui.setItem(2, 3, getChangeDescriptionButton());
        gui.setItem(2, 4, getChangeColorButton());

        gui.setItem(3, 2, getChangeApplicationRulesButton());
        gui.setItem(3, 3, getChangeCapitalChunkButton());
        if (Constants.allowShareOfTeleportation()) {
            gui.setItem(3, 4, getAuthorizedTeleportationButton());
        }

        if (Constants.enableTownTag()) {
            gui.setItem(3, 7, getChangeTagButton());
        }
        gui.setItem(3, 8, setBannerButton());


        gui.setItem(2, 7, getChangeOwnershipButton());
        gui.setItem(2, 8, getDeleteButton());

        gui.setItem(4, 1, createBackArrow(player, p -> returnGui.open(), langType));

        gui.open(player);
    }

    private @NotNull GuiItem getChangeCapitalChunkButton() {

        Optional<Vector2D> optCapitalLocation = townData.getCapitalLocation();
        List<FilledLang> desc = new ArrayList<>();

        if (optCapitalLocation.isEmpty()) {
            desc.add(Lang.GUI_NO_CAPITAL_CHUNK.get());
        } else {
            desc.add(Lang.GUI_CAPITAL_CHUNK_ACTUAL_POSITION.get(Lang.DISPLAY_2D_COORDINATES.get(
                    Integer.toString(optCapitalLocation.get().getX() * 16),
                    Integer.toString(optCapitalLocation.get().getZ() * 16))
            ));
            if (townData.isTownCapitalOccupied()) {
                desc.add(Lang.GUI_CAPITAL_CHUNK_OCCUPIED.get());
            }
        }

        return iconManager.get(IconKey.CHANGE_TOWN_CAPITAL_ICON)
                .setName(Lang.GUI_TOWN_SETTINGS_CHANGE_CAPITAL.translate(langType))
                .setRequirements(new RankPermissionRequirement(territoryData, tanPlayer, RolePermission.TOWN_ADMINISTRATOR))
                .setDescription(desc)
                .setClickToAcceptMessage(
                        territoryData.isAtWar() ?
                                Lang.CANNOT_MOVE_CAPITAL_WHILE_AT_WAR :
                                Lang.GUI_GENERIC_CLICK_TO_MODIFY
                )
                .setAction(action -> {
                    if (territoryData.isAtWar()) {
                        TanChatUtils.message(player, Lang.CANNOT_MOVE_CAPITAL_WHILE_AT_WAR.get());
                    } else {
                        TanChatUtils.message(player, Lang.RIGHT_CLICK_TO_SELECT_CAPITAL.translate(tanPlayer));
                        RightClickListener.register(player, langType, new ChangeCapital(townData, p -> open()));
                    }
                })
                .asGuiItem(player, langType);
    }

    private @NotNull GuiItem getChangeTagButton() {
        return iconManager.get(IconKey.CHANGE_TOWN_TAG_ICON)
                .setName(Lang.GUI_TOWN_SETTINGS_CHANGE_TAG.translate(langType))
                .setDescription(Lang.GUI_TOWN_SETTINGS_CHANGE_TAG_DESC1.get(townData.getFormatedTag()))
                .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_MODIFY)
                .setRequirements(new RankPermissionRequirement(territoryData, tanPlayer, RolePermission.TOWN_ADMINISTRATOR))
                .setAction(action -> {
                    TanChatUtils.message(player, Lang.ENTER_NEW_VALUE.translate(langType));
                    PlayerChatListenerStorage.register(player, langType, new ChangeTownTag(townData, p -> open()));
                })
                .asGuiItem(player, langType);
    }

    private GuiItem getDeleteButton() {
        return iconManager.get(IconKey.TOWN_DELETE_TOWN_ICON)
                .setName(Lang.GUI_TOWN_SETTINGS_DELETE_TOWN.translate(tanPlayer))
                .setDescription(
                        Lang.GUI_TOWN_SETTINGS_DELETE_TOWN_DESC1.get(townData.getName()),
                        Lang.GUI_TOWN_SETTINGS_DELETE_TOWN_DESC2.get()
                )
                .setRequirements(
                        new LeaderRequirement(territoryData, tanPlayer)
                )
                .setAction(event -> {
                    event.setCancelled(true);
                    if (townData.isCapital()) {
                        TanChatUtils.message(tanPlayer, Lang.CANNOT_DELETE_TERRITORY_IF_CAPITAL.get(townData.getOverlordInternal().get().getColoredName()));
                        return;
                    }

                    if (!TownsAndNations.getPlugin().getWarStorage().getWarsOfTerritory(territoryData).isEmpty()) {
                        TanChatUtils.message(tanPlayer, Lang.CANNOT_DELETE_TERRITORY_IF_AT_WAR, SoundEnum.NOT_ALLOWED);
                        return;
                    }

                    if (!player.hasPermission("tan.base.town.disband")) {
                        TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.translate(tanPlayer), NOT_ALLOWED);
                        return;
                    }

                    new ConfirmMenu(
                            player,
                            Lang.GUI_CONFIRM_PLAYER_DELETE_TOWN.get(townData.getName()),
                            () -> {
                                FileUtil.addLineToHistory(Lang.TOWN_DELETED_NEWSLETTER.get(player.getName(), townData.getName()));
                                EventManager.getInstance().callEvent(new TownDeletedInternalEvent(townData, tanPlayer));
                                townData.delete();
                                player.closeInventory();
                                SoundUtil.playSound(player, GOOD);
                            },
                            this::open
                    );
                })
                .asGuiItem(player, langType);
    }

    private @NotNull GuiItem getChangeApplicationRulesButton() {

        RecruitingPolicy townRecruitingPolicy = townData.getRecruitingPolicy();

        IconKey iconKey = switch (townRecruitingPolicy) {
            case APPLICATION_OPEN, AUTHORIZE_ALL -> IconKey.TOWN_ALLOW_APPLICATION_ICON;
            case CLOSED -> IconKey.TOWN_DENY_APPLICATION_ICON;
        };

        return iconManager.get(iconKey)
                .setName(Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_APPLICATION.translate(tanPlayer))
                .setDescription(
                        Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_APPLICATION_DESC.get(
                                switch (townRecruitingPolicy) {
                                    case AUTHORIZE_ALL ->
                                            Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_APPLICATION_OPEN.translate(langType);
                                    case APPLICATION_OPEN ->
                                            Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_APPLICATION_ACCEPT.translate(langType);
                                    case CLOSED ->
                                            Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_APPLICATION_NOT_ACCEPT.translate(langType);
                                }
                        )
                )
                .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_SWITCH)
                .setRequirements(new RankPermissionRequirement(territoryData, tanPlayer, RolePermission.INVITE_PLAYER))
                .setAction(event -> {
                    townData.swapRecruiting();
                    open();
                })
                .asGuiItem(player, langType);
    }

    protected @NotNull GuiItem getChangeOwnershipButton() {
        return iconManager.get(IconKey.TERRITORY_CHANGE_OWNER_ICON)
                .setName(Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP.translate(tanPlayer))
                .setDescription(
                        Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_DESC1.get(),
                        Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_DESC2.get()
                )
                .setRequirements(new LeaderRequirement(territoryData, tanPlayer))
                .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_MODIFY)
                .setAction(event -> new SelectNewOwnerForTownMenu(player, townData, this::open))
                .asGuiItem(player, langType);
    }
}

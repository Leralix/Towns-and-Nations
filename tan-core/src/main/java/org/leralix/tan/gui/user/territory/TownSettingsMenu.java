package org.leralix.tan.gui.user.territory;

import static org.leralix.lib.data.SoundEnum.*;

import dev.triumphteam.gui.guis.GuiItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.lib.position.Vector2D;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.events.EventManager;
import org.leralix.tan.events.events.TownDeletedInternalEvent;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.service.requirements.LeaderRequirement;
import org.leralix.tan.gui.service.requirements.RankPermissionRequirement;
import org.leralix.tan.gui.utils.ConfirmMenu;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.listeners.chat.events.ChangeTownTag;
import org.leralix.tan.listeners.interact.RightClickListener;
import org.leralix.tan.listeners.interact.events.ChangeCapital;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.file.FileUtil;
import org.leralix.tan.utils.text.TanChatUtils;

public class TownSettingsMenu extends SettingsMenus {

  private final TownData townData;

  private TownSettingsMenu(Player player, ITanPlayer tanPlayer, TownData townData) {
    super(player, tanPlayer, Lang.HEADER_SETTINGS.get(tanPlayer.getLang()), townData, 4);
    this.townData = townData;
  }

  public static void open(Player player, TownData townData) {
    PlayerDataStorage.getInstance()
        .get(player)
        .thenAccept(
            tanPlayer -> {
              new TownSettingsMenu(player, tanPlayer, townData).open();
            });
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
    if (Constants.enableTownTag()) {
      gui.setItem(3, 4, getChangeTagButton());
    }

    gui.setItem(2, 6, getChangeOwnershipButton());
    gui.setItem(2, 7, getQuitButton());
    gui.setItem(2, 8, getDeleteButton());

    gui.setItem(4, 1, GuiUtil.createBackArrow(player, p -> TownMenu.open(player, townData)));

    gui.open(player);
  }

  private @NotNull GuiItem getChangeCapitalChunkButton() {

    List<FilledLang> desc = new ArrayList<>();

    Optional<Vector2D> optVector2D = townData.getCapitalLocation();
    boolean capitalPresent = optVector2D.isPresent();
    optVector2D.ifPresent(
        vector2D ->
            desc.add(
                Lang.GUI_CAPITAL_CHUNK_ACTUAL_POSITION.get(
                    Lang.DISPLAY_2D_COORDINATES.get(
                        langType,
                        Integer.toString(vector2D.getX() * 16),
                        Integer.toString(vector2D.getZ() * 16)))));

    return iconManager
        .get(IconKey.CHANGE_TOWN_CAPITAL_ICON)
        .setName(Lang.GUI_TOWN_SETTINGS_CHANGE_CAPITAL.get(langType))
        .setDescription(desc)
        .setRequirements(
            new RankPermissionRequirement(
                territoryData, tanPlayer, RolePermission.TOWN_ADMINISTRATOR))
        .setClickToAcceptMessage(
            capitalPresent ? Lang.GUI_GENERIC_CLICK_TO_MODIFY : Lang.GUI_NO_CAPITAL_CHUNK)
        .setAction(
            action -> {
              RightClickListener.register(player, new ChangeCapital(townData, p -> open()));
            })
        .asGuiItem(player, langType);
  }

  private @NotNull GuiItem getChangeTagButton() {
    return iconManager
        .get(IconKey.CHANGE_TOWN_TAG_ICON)
        .setName(Lang.GUI_TOWN_SETTINGS_CHANGE_TAG.get(langType))
        .setDescription(Lang.GUI_TOWN_SETTINGS_CHANGE_TAG_DESC1.get(townData.getColoredTag()))
        .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_MODIFY)
        .setRequirements(
            new RankPermissionRequirement(
                territoryData, tanPlayer, RolePermission.TOWN_ADMINISTRATOR))
        .setAction(
            action -> {
              TanChatUtils.message(player, Lang.ENTER_NEW_VALUE.get(langType));
              PlayerChatListenerStorage.register(player, new ChangeTownTag(townData, p -> open()));
            })
        .asGuiItem(player, langType);
  }

  private GuiItem getQuitButton() {
    return iconManager
        .get(IconKey.TOWN_QUIT_TOWN_ICON)
        .setName(Lang.GUI_TOWN_SETTINGS_LEAVE_TOWN.get(tanPlayer))
        .setDescription(
            Lang.GUI_TOWN_SETTINGS_LEAVE_TOWN_DESC1.get(townData.getName()),
            Lang.GUI_TOWN_SETTINGS_LEAVE_TOWN_DESC2.get())
        .setAction(
            event -> {
              event.setCancelled(true);

              if (!player.hasPermission("tan.base.town.quit")) {
                TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(langType), NOT_ALLOWED);
                return;
              }

              if (townData.isLeader(tanPlayer)) {
                TanChatUtils.message(
                    player, Lang.CHAT_CANT_LEAVE_TOWN_IF_LEADER.get(tanPlayer), NOT_ALLOWED);
                return;
              }

              if (townData.haveOverlord()) {
                RegionData regionData = townData.getRegionSync();
                if (regionData.isLeader(tanPlayer)) {
                  TanChatUtils.message(
                      player,
                      Lang.CHAT_CANT_LEAVE_TOWN_IF_REGION_LEADER.get(tanPlayer),
                      NOT_ALLOWED);
                  return;
                }
              }

              ConfirmMenu.open(
                  player,
                  Lang.GUI_CONFIRM_PLAYER_LEAVE_TOWN.get(),
                  p -> {
                    townData.removePlayer(tanPlayer);
                    TanChatUtils.message(player, Lang.CHAT_PLAYER_LEFT_THE_TOWN.get(tanPlayer));
                    townData.broadcastMessageWithSound(
                        Lang.TOWN_BROADCAST_PLAYER_LEAVE_THE_TOWN.get(tanPlayer.getNameStored()),
                        BAD);
                  },
                  p -> open());
            })
        .asGuiItem(player, langType);
  }

  private GuiItem getDeleteButton() {
    return iconManager
        .get(IconKey.TOWN_DELETE_TOWN_ICON)
        .setName(Lang.GUI_TOWN_SETTINGS_DELETE_TOWN.get(tanPlayer))
        .setDescription(
            Lang.GUI_TOWN_SETTINGS_DELETE_TOWN_DESC1.get(townData.getName()),
            Lang.GUI_TOWN_SETTINGS_DELETE_TOWN_DESC2.get())
        .setRequirements(new LeaderRequirement(territoryData, tanPlayer))
        .setAction(
            event -> {
              event.setCancelled(true);
              if (townData.isCapital()) {
                TanChatUtils.message(
                    player,
                    Lang.CANNOT_DELETE_TERRITORY_IF_CAPITAL.get(
                        tanPlayer, townData.getOverlord().get().getBaseColoredName()));
                return;
              }

              if (!player.hasPermission("tan.base.town.disband")) {
                TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(tanPlayer), NOT_ALLOWED);
                return;
              }

              ConfirmMenu.open(
                  player,
                  Lang.GUI_CONFIRM_PLAYER_DELETE_TOWN.get(),
                  p -> {
                    FileUtil.addLineToHistory(
                        Lang.TOWN_DELETED_NEWSLETTER.get(player.getName(), townData.getName()));
                    EventManager.getInstance()
                        .callEvent(new TownDeletedInternalEvent(townData, tanPlayer));
                    townData.delete();
                    SoundUtil.playSound(player, GOOD);
                  },
                  p -> open());
            })
        .asGuiItem(player, langType);
  }

  private @NotNull GuiItem getChangeApplicationButton() {
    IconKey iconKey =
        townData.isRecruiting()
            ? IconKey.TOWN_ALLOW_APPLICATION_ICON
            : IconKey.TOWN_DENY_APPLICATION_ICON;
    return iconManager
        .get(iconKey)
        .setName(Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_APPLICATION.get(tanPlayer))
        .setDescription(
            townData.isRecruiting()
                ? Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_APPLICATION_ACCEPT.get()
                : Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_APPLICATION_NOT_ACCEPT.get())
        .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_SWITCH)
        .setRequirements(
            new RankPermissionRequirement(territoryData, tanPlayer, RolePermission.INVITE_PLAYER))
        .setAction(
            event -> {
              townData.swapRecruiting();
              open();
            })
        .asGuiItem(player, langType);
  }

  private @NotNull GuiItem getChangeOwnershipButton() {
    return iconManager
        .get(IconKey.TOWN_CHANGE_OWNERSHIP_ICON)
        .setName(Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP.get(tanPlayer))
        .setDescription(
            Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_DESC1.get(),
            Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_DESC2.get())
        .setAction(
            event -> {
              if (townData.isLeader(tanPlayer)) {
                SelectNewOwnerForTownMenu.open(player, townData);
              } else {
                TanChatUtils.message(player, Lang.NOT_TOWN_LEADER_ERROR.get(tanPlayer));
              }
            })
        .asGuiItem(player, langType);
  }
}

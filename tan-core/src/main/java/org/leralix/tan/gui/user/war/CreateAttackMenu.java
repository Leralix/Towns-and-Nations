package org.leralix.tan.gui.user.war;

import static org.leralix.lib.data.SoundEnum.REMOVE;

import dev.triumphteam.gui.guis.GuiItem;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.events.EventManager;
import org.leralix.tan.events.events.AttackDeclaredInternalEvent;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.gui.user.territory.AttackMenu;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlannedAttackStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.timezone.TimeZoneManager;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.text.DateUtil;
import org.leralix.tan.wars.War;
import org.leralix.tan.wars.legacy.CreateAttackData;
import org.leralix.tan.wars.legacy.WarRole;

public class CreateAttackMenu extends BasicGui {

  private final CreateAttackData attackData;
  private final TerritoryData territoryData;
  private final War war;
  private final WarRole warRole;

  private CreateAttackMenu(
      Player player, ITanPlayer tanPlayer, TerritoryData territoryData, War war, WarRole warRole) {
    super(
        player,
        tanPlayer,
        Lang.HEADER_CREATE_WAR_MANAGER.get(tanPlayer.getLang(), war.getMainDefender().getName()),
        3);
    this.territoryData = territoryData;
    this.war = war;
    this.warRole = warRole;
    this.attackData = new CreateAttackData(war, warRole);
  }

  public static void open(Player player, TerritoryData territoryData, War war, WarRole warRole) {
    PlayerDataStorage.getInstance()
        .get(player)
        .thenAccept(
            tanPlayer -> {
              new CreateAttackMenu(player, tanPlayer, territoryData, war, warRole).open();
            });
  }

  @Override
  public void open() {
    gui.setItem(2, 2, getRemoveTimeButton());
    gui.setItem(2, 3, getTimeIcon());
    gui.setItem(2, 4, getAddTimeButton());

    gui.setItem(2, 8, getConfirmButton());
    gui.setItem(
        3, 1, GuiUtil.createBackArrow(player, e -> WarMenu.open(player, territoryData, war)));

    gui.open(player);
  }

  private @NotNull GuiItem getConfirmButton() {

    boolean isOutsideOfSlots = isIsOutsideOfSlots();

    IconKey iconKey =
        isOutsideOfSlots
            ? IconKey.CONFIRM_WAR_START_ICON
            : IconKey.CONFIRM_WAR_START_IMPOSSIBLE_ICON;

    return iconManager
        .get(iconKey)
        .setName(Lang.GUI_CONFIRM_ATTACK.get(tanPlayer))
        .setClickToAcceptMessage(
            isOutsideOfSlots
                ? Lang.GUI_GENERIC_CLICK_TO_PROCEED
                : Lang.GUI_WARGOAL_OUTSIDE_AUTHORIZED_SLOTS)
        .setAction(
            event -> {
              event.setCancelled(true);

              if (!isOutsideOfSlots) {
                SoundUtil.playSound(player, REMOVE);
                return;
              }

              EventManager.getInstance()
                  .callEvent(
                      new AttackDeclaredInternalEvent(
                          war.getTerritory(warRole.opposite()), war.getTerritory(warRole)));

              PlannedAttackStorage.getInstance().newAttack(attackData);
              AttackMenu.open(player, war.getTerritory(warRole));
            })
        .asGuiItem(player, langType);
  }

  private boolean isIsOutsideOfSlots() {
    Instant warStart = Instant.now().plusSeconds(attackData.getSelectedTime() * 60L);
    return !Constants.getWarTimeSlot().canWarBeDeclared(warStart);
  }

  private @NotNull GuiItem getAddTimeButton() {
    return iconManager
        .get(IconKey.ADD_WAR_START_TIME_ICON)
        .setName(Lang.GUI_ATTACK_ADD_TIME.get(tanPlayer))
        .setDescription(
            Lang.GUI_LEFT_CLICK_FOR_1_MINUTE.get(), Lang.GUI_SHIFT_CLICK_FOR_1_HOUR.get())
        .setAction(
            event -> {
              event.setCancelled(true);
              SoundUtil.playSound(player, REMOVE);

              if (event.isShiftClick()) {
                attackData.addDeltaDateTime(60);
              } else if (event.isLeftClick()) {
                attackData.addDeltaDateTime(1);
              }
              open();
            })
        .asGuiItem(player, langType);
  }

  private @NotNull GuiItem getTimeIcon() {

    Instant startTime = Instant.now().plusSeconds(attackData.getSelectedTime() * 60L);

    List<FilledLang> availableTimeSlots = new ArrayList<>();
    availableTimeSlots.add(TimeZoneManager.getInstance().formatDateForPlayer(tanPlayer, startTime));
    availableTimeSlots.add(Lang.AUTHORIZED_ATTACK_TIME_SLOT_TITLE.get());
    availableTimeSlots.addAll(Constants.getWarTimeSlot().getPrintedTimeSlots());

    return IconManager.getInstance()
        .get(IconKey.WAR_START_TIME_ICON)
        .setName(
            Lang.GUI_ATTACK_SET_TO_START_IN.get(
                tanPlayer, DateUtil.getDateStringFromTicks(attackData.getSelectedTime())))
        .setDescription(availableTimeSlots)
        .asGuiItem(player, langType);
  }

  private @NotNull GuiItem getRemoveTimeButton() {
    return iconManager
        .get(IconKey.REMOVE_WAR_START_TIME_ICON)
        .setName(Lang.GUI_ATTACK_REMOVE_TIME.get(tanPlayer))
        .setDescription(
            Lang.GUI_LEFT_CLICK_FOR_1_MINUTE.get(), Lang.GUI_SHIFT_CLICK_FOR_1_HOUR.get())
        .setAction(
            event -> {
              event.setCancelled(true);
              SoundUtil.playSound(player, REMOVE);

              if (event.isShiftClick()) {
                attackData.addDeltaDateTime(-60);
              } else if (event.isLeftClick()) {
                attackData.addDeltaDateTime(-1);
              }
              open();
            })
        .asGuiItem(player, langType);
  }
}

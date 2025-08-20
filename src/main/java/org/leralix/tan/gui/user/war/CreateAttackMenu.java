package org.leralix.tan.gui.user.war;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.events.EventManager;
import org.leralix.tan.events.events.AttackDeclaredInternalEvent;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.gui.user.territory.AttackMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlannedAttackStorage;
import org.leralix.tan.timezone.TimeZoneManager;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.text.DateUtil;
import org.leralix.tan.war.War;
import org.leralix.tan.war.WarTimeSlot;
import org.leralix.tan.war.legacy.CreateAttackData;
import org.leralix.tan.war.legacy.WarRole;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.leralix.lib.data.SoundEnum.REMOVE;

public class CreateAttackMenu extends BasicGui {

    private final CreateAttackData attackData;
    private final TerritoryData territoryData;
    private final War war;
    private final WarRole warRole;

    public CreateAttackMenu(Player player, TerritoryData territoryData, War war, WarRole warRole) {
        super(player, Lang.HEADER_CREATE_WAR_MANAGER.get(player, war.getMainDefender().getName()), 3);
        this.territoryData = territoryData;
        this.war = war;
        this.warRole = warRole;
        this.attackData = new CreateAttackData(war, warRole);
        open();
    }

    @Override
    public void open() {
        gui.setItem(2, 2, getRemoveTimeButton());
        gui.setItem(2, 3, getTimeIcon());
        gui.setItem(2, 4, getAddTimeButton());

        gui.setItem(2, 8, getConfirmButton());
        gui.setItem(3, 1, GuiUtil.createBackArrow(player, e -> new WarMenu(player, territoryData, war)));

        gui.open(player);
    }

    private @NotNull GuiItem getConfirmButton() {

        List<String> errorMessages = new ArrayList<>();
        boolean isValid = isValid(errorMessages);

        IconKey iconKey = isValid ? IconKey.CONFIRM_WAR_START_ICON : IconKey.CONFIRM_WAR_START_IMPOSSIBLE_ICON;

        return iconManager.get(iconKey)
                .setName(Lang.GUI_CONFIRM_ATTACK.get(tanPlayer))
                .setDescription(
                        errorMessages.isEmpty() ?
                                Collections.singleton(Lang.GUI_GENERIC_CLICK_TO_PROCEED.get(tanPlayer))
                                : errorMessages
                )
                .setAction(event -> {
                    event.setCancelled(true);

                    if (!isValid) {
                        SoundUtil.playSound(player, REMOVE);
                        return;
                    }

                    EventManager.getInstance().callEvent(new AttackDeclaredInternalEvent(war.getTerritory(warRole.opposite()), war.getTerritory(warRole)));

                    PlannedAttackStorage.getInstance().newAttack(attackData);
                    new AttackMenu(player, war.getTerritory(warRole));
                })
                .asGuiItem(player);

    }

    private boolean isValid(List<String> errorMessages) {
        boolean isValid = true;

        Instant warStart = Instant.now().plusSeconds(attackData.getSelectedTime() / 20);
        if (!WarTimeSlot.getInstance().canWarBeDeclared(warStart)) {
            errorMessages.add(Lang.GUI_WARGOAL_OUTSIDE_AUTHORIZED_SLOTS.get(tanPlayer));
            isValid = false;
        }
        return isValid;
    }


    private @NotNull GuiItem getAddTimeButton() {
        return iconManager.get(IconKey.ADD_WAR_START_TIME_ICON)
                .setName(Lang.GUI_ATTACK_ADD_TIME.get(tanPlayer))
                .setDescription(
                        Lang.GUI_LEFT_CLICK_FOR_1_MINUTE.get(tanPlayer),
                        Lang.GUI_SHIFT_CLICK_FOR_1_HOUR.get(tanPlayer)
                )
                .setAction(event -> {
                    event.setCancelled(true);
                    SoundUtil.playSound(player, REMOVE);

                    if (event.isShiftClick()) {
                        attackData.addDeltaDateTime(60 * 1200L);
                    } else if (event.isLeftClick()) {
                        attackData.addDeltaDateTime(1200L);
                    }
                    open();
                })
                .asGuiItem(player);
    }

    private @NotNull GuiItem getTimeIcon() {

        Instant startTime = Instant.now().plusSeconds(attackData.getSelectedTime() / 20);

        List<String> availableTimeSlots = new ArrayList<>();
        availableTimeSlots.add(TimeZoneManager.getInstance().formatDateForPlayer(tanPlayer, startTime));
        availableTimeSlots.add(Lang.AUTHORIZED_ATTACK_TIME_SLOT_TITLE.get());
        availableTimeSlots.addAll(WarTimeSlot.getInstance().getPrintedTimeSlots(tanPlayer.getLang()));

        return IconManager.getInstance().get(IconKey.WAR_START_TIME_ICON)
                .setName(Lang.GUI_ATTACK_SET_TO_START_IN.get(tanPlayer, DateUtil.getDateStringFromTicks(attackData.getSelectedTime())))
                .setDescription(availableTimeSlots)
                .asGuiItem(player);
    }

    private @NotNull GuiItem getRemoveTimeButton() {
        return iconManager.get(IconKey.REMOVE_WAR_START_TIME_ICON)
                .setName(Lang.GUI_ATTACK_REMOVE_TIME.get(tanPlayer))
                .setDescription(
                        Lang.GUI_LEFT_CLICK_FOR_1_MINUTE.get(tanPlayer),
                        Lang.GUI_SHIFT_CLICK_FOR_1_HOUR.get(tanPlayer)
                )
                .setAction(event -> {
                    event.setCancelled(true);
                    SoundUtil.playSound(player, REMOVE);

                    if (event.isShiftClick()) {
                        attackData.addDeltaDateTime(-60 * 1200L);
                    } else if (event.isLeftClick()) {
                        attackData.addDeltaDateTime(-1200);
                    }
                    open();
                })
                .asGuiItem(player);
    }
}

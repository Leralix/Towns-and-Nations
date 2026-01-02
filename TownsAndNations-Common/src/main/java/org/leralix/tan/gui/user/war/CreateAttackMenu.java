package org.leralix.tan.gui.user.war;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.events.EventManager;
import org.leralix.tan.events.events.AttackDeclaredInternalEvent;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.gui.user.territory.AttackMenu;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.timezone.TimeZoneManager;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.text.DateUtil;
import org.leralix.tan.war.PlannedAttack;
import org.leralix.tan.war.War;
import org.leralix.tan.war.WarTimeSlot;
import org.leralix.tan.war.legacy.CreateAttackData;
import org.leralix.tan.war.legacy.WarRole;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.leralix.lib.data.SoundEnum.REMOVE;

public class CreateAttackMenu extends BasicGui {

    private final CreateAttackData attackData;
    private final War war;
    private final WarRole warRole;
    private final WarTimeSlot warTimeSlot;
    private final BasicGui returnGui;

    public CreateAttackMenu(Player player, War war, WarRole warRole, BasicGui returnGui) {
        super(player, Lang.HEADER_CREATE_WAR_MANAGER.get(war.getMainDefender().getName()), 3);
        this.war = war;
        this.warRole = warRole;
        this.attackData = new CreateAttackData(warRole);
        this.warTimeSlot = Constants.getWarTimeSlot();
        this.returnGui = returnGui;
        open();
    }

    @Override
    public void open() {
        gui.setItem(2, 2, getRemoveTimeButton());
        gui.setItem(2, 3, getTimeIcon());
        gui.setItem(2, 4, getAddTimeButton());

        gui.setItem(2, 8, getConfirmButton());
        gui.setItem(3, 1, GuiUtil.createBackArrow(player, e -> returnGui.open()));

        gui.open(player);
    }

    private @NotNull GuiItem getConfirmButton() {

        boolean isAuthorized = isStartDateAuthorized();
        boolean isOverridingOtherAttack = isOverridingOtherAttack();

        IconKey iconKey = isAuthorized ? IconKey.CONFIRM_WAR_START_ICON : IconKey.CONFIRM_WAR_START_IMPOSSIBLE_ICON;

        return iconManager.get(iconKey)
                .setName(Lang.GUI_CONFIRM_ATTACK.get(tanPlayer))
                .setClickToAcceptMessage(
                        isOverridingOtherAttack ?
                                Lang.GUI_ATTACK_ALREADY_PLANNED_IN_TIME :
                                isAuthorized ?
                                        Lang.GUI_GENERIC_CLICK_TO_PROCEED :
                                        Lang.GUI_WARGOAL_OUTSIDE_AUTHORIZED_SLOTS
                )
                .setAction(event -> {
                    event.setCancelled(true);

                    if (!isAuthorized || isOverridingOtherAttack) {
                        SoundUtil.playSound(player, REMOVE);
                        return;
                    }

                    EventManager.getInstance().callEvent(new AttackDeclaredInternalEvent(war.getTerritory(warRole.opposite()), war.getTerritory(warRole)));

                    war.createPlannedAttack(
                            attackData.getAttackingSide(),
                            attackData.getSelectedTime(),
                            Constants.getAttackDuration()
                    );
                    new AttackMenu(player, war.getTerritory(warRole));
                })
                .asGuiItem(player, langType);

    }

    private boolean isOverridingOtherAttack() {
        Instant warStart = Instant.now().plusSeconds(attackData.getSelectedTime() * 60L);
        Instant warEnd = warStart.plusSeconds(Constants.getAttackDuration() * 60L);
        for (PlannedAttack plannedAttack : war.getPlannedAttacks()) {
            if (plannedAttack.isInstantInAttack(warStart.toEpochMilli()) ||
                    plannedAttack.isInstantInAttack(warEnd.toEpochMilli())) {
                return true;
            }
        }
        return false;
    }

    private boolean isStartDateAuthorized() {
        Instant warStart = Instant.now().plusSeconds(attackData.getSelectedTime() * 60L);
        return warTimeSlot.canWarBeDeclared(warStart);
    }

    private @NotNull GuiItem getAddTimeButton() {
        return iconManager.get(IconKey.ADD_WAR_START_TIME_ICON)
                .setName(Lang.GUI_ATTACK_ADD_TIME.get(tanPlayer))
                .setDescription(
                        Lang.GUI_LEFT_CLICK_FOR_1_MINUTE.get(),
                        Lang.GUI_SHIFT_CLICK_FOR_1_HOUR.get()
                )
                .setAction(event -> {
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
        availableTimeSlots.addAll(warTimeSlot.getPrintedTimeSlots());
        availableTimeSlots.addAll(warTimeSlot.getPrintedDaysOfTheWeek(langType));

        return IconManager.getInstance().get(IconKey.WAR_START_TIME_ICON)
                .setName(Lang.GUI_ATTACK_SET_TO_START_IN.get(tanPlayer, DateUtil.getDateStringFromMinutes(attackData.getSelectedTime())))
                .setDescription(availableTimeSlots)
                .asGuiItem(player, langType);
    }

    private @NotNull GuiItem getRemoveTimeButton() {
        return iconManager.get(IconKey.REMOVE_WAR_START_TIME_ICON)
                .setName(Lang.GUI_ATTACK_REMOVE_TIME.get(tanPlayer))
                .setDescription(
                        Lang.GUI_LEFT_CLICK_FOR_1_MINUTE.get(),
                        Lang.GUI_SHIFT_CLICK_FOR_1_HOUR.get()
                )
                .setAction(event -> {
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

package org.leralix.tan.gui.user.war;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.wars.CreateAttackData;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.events.EventManager;
import org.leralix.tan.events.events.AttackDeclaredInternalEvent;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.gui.legacy.PlayerGUI;
import org.leralix.tan.gui.user.territory.WarMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlannedAttackStorage;
import org.leralix.tan.timezone.TimeZoneManager;
import org.leralix.tan.utils.DateUtil;
import org.leralix.tan.utils.GuiUtil;
import org.leralix.tan.war.WarTimeSlot;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.leralix.lib.data.SoundEnum.REMOVE;
import static org.leralix.lib.data.SoundEnum.WAR;

public class CreateWarMenu extends BasicGui {

    private final CreateAttackData attackData;
    private final TerritoryData attackingTerritory;
    private final TerritoryData attackedTerritory;

    public CreateWarMenu(Player player, TerritoryData attackingTerritory, TerritoryData attackedTerritory) {
        super(player, Lang.HEADER_CREATE_WAR_MANAGER.get(player, attackedTerritory.getName()), 3);
        this.attackData = new CreateAttackData(attackingTerritory, attackedTerritory);
        this.attackingTerritory = attackingTerritory;
        this.attackedTerritory = attackedTerritory;
        open();
    }

    public CreateWarMenu(Player player, CreateAttackData attackData) {
        super(player, Lang.HEADER_CREATE_WAR_MANAGER.get(player, attackData.getMainDefender().getName()), 3);
        this.attackData = attackData;
        this.attackingTerritory = attackData.getMainAttacker();
        this.attackedTerritory = attackData.getMainDefender();
        open();
    }

    @Override
    public void open() {


        gui.setItem(2, 2, getRemoveTimeButton());
        gui.setItem(2, 3, getTimeIcon());
        gui.setItem(2, 4, getAddTimeButton());

        gui.setItem(2, 6, getWargoalButton());

        gui.setItem(2, 8, getConfirmButton());
        gui.setItem(3, 1, GuiUtil.createBackArrow(player, e -> PlayerGUI.openSingleRelation(player, attackingTerritory, TownRelation.WAR, 0)));

        attackData.getWargoal().addExtraOptions(gui, player, attackData);

        gui.open(player);

    }

    private @NotNull GuiItem getWargoalButton() {

        ItemStack wargoalIcon = attackData.getWargoal().getIcon();
        return ItemBuilder.from(wargoalIcon).asGuiItem(event -> {
            PlayerGUI.openSelectWarGoalMenu(player, attackData);
            event.setCancelled(true);
        });
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

                    EventManager.getInstance().callEvent(new AttackDeclaredInternalEvent(attackedTerritory, attackingTerritory));


                    //TODO : delete when event system is fully implemented
                    String message = Lang.GUI_TOWN_ATTACK_TOWN_INFO.get(tanPlayer, attackingTerritory.getName(), attackedTerritory.getName());
                    attackingTerritory.broadcastMessageWithSound(message, WAR);
                    attackedTerritory.broadcastMessageWithSound(message, WAR);

                    PlannedAttackStorage.newWar(attackData);
                    new WarMenu(player, attackingTerritory);



                })
                .asGuiItem(player);

    }

    private boolean isValid(List<String> errorMessages) {
        boolean isValid = true;

        Instant warStart = Instant.now().plusSeconds(attackData.getDeltaDateTime() / 20);
        if (!WarTimeSlot.getInstance().canWarBeDeclared(warStart)) {
            errorMessages.add(Lang.GUI_WARGOAL_OUTSIDE_AUTHORIZED_SLOTS.get(tanPlayer));
            isValid = false;
        }

        if (!attackData.getWargoal().isCompleted()) {
            errorMessages.add(Lang.GUI_WARGOAL_NOT_COMPLETED.get(tanPlayer));
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

        Instant startTime = Instant.now().plusSeconds(attackData.getDeltaDateTime() / 20);

        List<String> availableTimeSlots = new ArrayList<>();
        availableTimeSlots.add(TimeZoneManager.getInstance().formatDateForPlayer(tanPlayer, startTime));
        availableTimeSlots.add(Lang.AUTHORIZED_ATTACK_TIME_SLOT_TITLE.get());
        availableTimeSlots.addAll(WarTimeSlot.getInstance().getPrintedTimeSlots(tanPlayer.getLang()));

        return IconManager.getInstance().get(IconKey.WAR_START_TIME_ICON)
                .setName(Lang.GUI_ATTACK_SET_TO_START_IN.get(tanPlayer, DateUtil.getDateStringFromTicks(attackData.getDeltaDateTime())))
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

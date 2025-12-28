package org.leralix.tan.gui.user.war;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.user.territory.SelectWarGoals;
import org.leralix.tan.gui.user.territory.WarsMenu;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.listeners.chat.events.ChangeWarName;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.text.StringUtil;
import org.leralix.tan.utils.text.TanChatUtils;
import org.leralix.tan.war.War;
import org.leralix.tan.war.legacy.WarRole;
import org.leralix.tan.war.legacy.wargoals.WarGoal;

import java.util.ArrayList;
import java.util.List;

public class WarMenu extends AbstractWarMenu {

    private final TerritoryData territoryData;
    private final WarRole warRole;

    public WarMenu(Player player, TerritoryData territoryData, War war) {
        super(player, Lang.HEADER_WARS_MENU, 3, war);
        this.territoryData = territoryData;
        this.warRole = war.isMainAttacker(territoryData) ? WarRole.MAIN_ATTACKER : WarRole.MAIN_DEFENDER;
        open();
    }


    @Override
    public void open() {
        gui.setItem(1, 5, getWarIcon());

        gui.setItem(2, 2, getAttackingSideInfo());
        gui.setItem(2, 3, getDefendingSideInfo());
        gui.setItem(2, 4, getAttackButton());
        gui.setItem(2, 5, getRenameWarButton());
        gui.setItem(2, 6, getWargoalsButton());
        gui.setItem(2, 7, getEnemyWargoalsIcon());
        gui.setItem(2, 8, getSurrenderButton());

        gui.setItem(3, 1, GuiUtil.createBackArrow(player, p -> new WarsMenu(player, territoryData)));
        gui.open(player);
    }

    private GuiItem getRenameWarButton() {
        return iconManager.get(IconKey.RENAME_WAR_ICON)
                .setName(Lang.GUI_RENAME_WAR.get(tanPlayer))
                .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_RENAME)
                .setAction(action -> {
                    TanChatUtils.message(player, Lang.ENTER_NEW_VALUE.get(tanPlayer));
                    PlayerChatListenerStorage.register(player, new ChangeWarName(war, p -> open()));
                })
                .asGuiItem(player, langType);
    }


    private GuiItem getWarIcon() {
        return war.getIcon().asGuiItem(player, langType);
    }

    private @NotNull GuiItem getWargoalsButton() {
        List<FilledLang> description = new ArrayList<>();
        description.add(Lang.WAR_GOAL_LIST_BUTTON_DESC1.get());
        for(WarGoal goal : war.getGoals(warRole)) {
            description.add(Lang.WAR_GOAL_LIST_BUTTON_LIST.get(goal.getCurrentDesc(langType)));
        }

        // If no goals are set, add a message
        if(description.size() == 1) {
            description.add(Lang.WAR_GOAL_LIST_BUTTON_LIST_NO_WAR_GOAL_SET.get());
        }


        return iconManager.get(IconKey.WAR_GOAL_LIST_ICON)
                .setName(Lang.WAR_GOAL_LIST_BUTTON.get(langType))
                .setDescription(description)
                .setAction(action -> new SelectWarGoals(player, territoryData, war, warRole))
                .asGuiItem(player, langType);
    }

    private @NotNull GuiItem getEnemyWargoalsIcon() {
        List<FilledLang> description = new ArrayList<>();
        description.add(Lang.WAR_ENEMY_GOAL_LIST_DESC1.get());
        for(WarGoal goal : war.getGoals(warRole.opposite())) {
            description.add(Lang.WAR_GOAL_LIST_BUTTON_LIST.get(goal.getCurrentDesc(langType)));
        }

        // If no goals are set, add a message
        if(description.size() == 1) {
            description.add(Lang.WAR_GOAL_LIST_BUTTON_LIST_NO_WAR_GOAL_SET.get());
        }


        return iconManager.get(IconKey.WAR_ENEMY_GOAL_LIST_ICON)
                .setName(Lang.WAR_ENEMY_GOAL_LIST.get(langType))
                .setDescription(description)
                .asGuiItem(player, langType);
    }

    private @NotNull GuiItem getAttackButton() {
        return iconManager.get(IconKey.WAR_CREATE_ATTACK_ICON)
                .setName(Lang.WAR_CREATE_ATTACK.get(langType))
                .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_PROCEED)
                .setAction(action -> new CreateAttackMenu(player, territoryData, war, warRole, this))
                .asGuiItem(player, langType);
    }

    private @NotNull GuiItem getSurrenderButton() {

        List<FilledLang> description = new ArrayList<>();
        description.add(Lang.WAR_SURRENDER_DESC1.get());
        description.add(Lang.WAR_SURRENDER_DESC2.get());

        description.addAll(war.generateWarGoalsDesciption(warRole, langType));

        return iconManager.get(IconKey.WAR_SURRENDER_ICON)
                .setName(Lang.WAR_SURRENDER.get(langType))
                .setDescription(description)
                .setAction( action -> {
                    war.territorySurrender(warRole);
                    new WarsMenu(player, territoryData);
                })
                .asGuiItem(player, langType);
    }
}


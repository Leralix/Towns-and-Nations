package org.leralix.tan.gui.user.war;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.user.territory.SelectWarGoals;
import org.leralix.tan.gui.user.territory.WarsMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.war.War;
import org.leralix.tan.war.legacy.WarRole;
import org.leralix.tan.war.legacy.wargoals.WarGoal;

import java.util.ArrayList;
import java.util.List;

public class WarMenu extends BasicGui {

    private final TerritoryData territoryData;
    private final War war;
    private final WarRole warRole;

    public WarMenu(Player player, TerritoryData territoryData, War war) {
        super(player, "War Menu", 3);
        this.territoryData = territoryData;
        this.war = war;
        this.warRole = war.isMainAttacker(territoryData) ? WarRole.MAIN_ATTACKER : WarRole.MAIN_DEFENDER;
        open();
    }


    @Override
    public void open() {
        gui.setItem(1, 5, getWarIcon());
        gui.setItem(2, 3, getWargoalsButton());
        gui.setItem(2, 4, getAttackButton());
        gui.setItem(2, 6, getEnemyWargoalsIcon());
        gui.setItem(2, 7, getSurrenderButton());
        gui.setItem(3, 1, GuiUtil.createBackArrow(player, p -> territoryData.openMainMenu(player)));
        gui.open(player);
    }



    private GuiItem getWarIcon() {

        return iconManager.get(war.getIcon())
                .setName(war.getName())
                .setDescription(
                        Lang.ATTACK_ICON_DESC_1.get(langType, war.getMainAttacker().getColoredName()),
                        Lang.ATTACK_ICON_DESC_2.get(langType, war.getMainDefender().getColoredName())
                )
                .asGuiItem(player);

    }

    private @NotNull GuiItem getWargoalsButton() {


        List<String> description = new ArrayList<>();
        description.add(Lang.WAR_GOAL_LIST_BUTTON_DESC1.get(langType));
        for(WarGoal goal : war.getGoals(warRole)) {
            description.add(Lang.WAR_GOAL_LIST_BUTTON_LIST.get(langType, goal.getCurrentDesc(langType)));
        }

        // If no goals are set, add a message
        if(description.size() == 1) {
            description.add(Lang.WAR_GOAL_LIST_BUTTON_LIST_NO_WAR_GOAL_SET.get(langType));
        }


        return iconManager.get(IconKey.WAR_GOAL_LIST_ICON)
                .setName(Lang.WAR_GOAL_LIST_BUTTON.get(langType))
                .setDescription(description)
                .setAction(action -> new SelectWarGoals(player, territoryData, war, warRole))
                .asGuiItem(player);
    }

    private @NotNull GuiItem getAttackButton() {
        return iconManager.get(IconKey.WAR_CREATE_ATTACK_ICON)
                .setName(Lang.WAR_CREATE_ATTACK.get(langType))
                .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_PROCEED)
                .setAction(action -> new CreateAttackMenu(player, territoryData, war, warRole))
                .asGuiItem(player);
    }

    private @NotNull GuiItem getEnemyWargoalsIcon() {


        List<String> description = new ArrayList<>();
        description.add(Lang.WAR_ENEMY_GOAL_LIST_DESC1.get(langType));
        for(WarGoal goal : war.getGoals(warRole.opposite())) {
            description.add(Lang.WAR_GOAL_LIST_BUTTON_LIST.get(langType, goal.getCurrentDesc(langType)));
        }

        // If no goals are set, add a message
        if(description.size() == 1) {
            description.add(Lang.WAR_GOAL_LIST_BUTTON_LIST_NO_WAR_GOAL_SET.get(langType));
        }


        return iconManager.get(IconKey.WAR_ENEMY_GOAL_LIST_ICON)
                .setName(Lang.WAR_ENEMY_GOAL_LIST.get(langType))
                .setDescription(description)
                .asGuiItem(player);
    }

    private @NotNull GuiItem getSurrenderButton() {

        List<String> description = new ArrayList<>();
        description.add(Lang.WAR_SURRENDER_DESC1.get(langType));
        description.add(Lang.WAR_SURRENDER_DESC2.get(langType));

        description.addAll(war.generateWarGoalsDesciption(warRole, langType));

        return iconManager.get(IconKey.WAR_SURRENDER_ICON)
                .setName(Lang.WAR_SURRENDER.get(langType))
                .setDescription(description)
                .setAction( action -> {
                    war.territorySurrender(warRole);
                    new WarsMenu(player, territoryData);
                })
                .asGuiItem(player);
    }
}


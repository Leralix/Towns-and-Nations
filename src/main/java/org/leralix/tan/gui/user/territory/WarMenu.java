package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.user.war.CreateAttackMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.GuiUtil;
import org.leralix.tan.war.War;
import org.leralix.tan.war.legacy.wargoals.WarGoal;

import java.util.ArrayList;
import java.util.List;

public class WarMenu extends BasicGui {

    private final TerritoryData territoryData;
    private final War war;

    public WarMenu(Player player, TerritoryData territoryData, War war) {
        super(player, "War Menu", 3);
        this.territoryData = territoryData;
        this.war = war;
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
                .asGuiItem(player);

    }

    private @NotNull GuiItem getWargoalsButton() {

        List<String> description = new ArrayList<>();
        description.add(Lang.WAR_GOAL_LIST_BUTTON_DESC1.get(langType));
        for(WarGoal goal : war.getAttackGoals()) {
            description.add(Lang.WAR_GOAL_LIST_BUTTON_LIST.get(langType, goal.getCurrentDesc()));
        }


        return iconManager.get(IconKey.WAR_GOAL_LIST_ICON)
                .setName(Lang.WAR_GOAL_LIST_BUTTON.get(langType))
                .setDescription(description)
                .asGuiItem(player);
    }

    private @NotNull GuiItem getAttackButton() {
        return iconManager.get(IconKey.WAR_CREATE_ATTACK_ICON)
                .setName(Lang.WAR_CREATE_ATTACK.get(langType))
                .setDescription(Lang.GUI_GENERIC_CLICK_TO_PROCEED.get(langType))
                .setAction(action -> new CreateAttackMenu(player, territoryData, war.getMainDefender()))
                .asGuiItem(player);
    }

    private @NotNull GuiItem getEnemyWargoalsIcon() {
        return iconManager.get(IconKey.WAR_ENEMY_GOAL_LIST_ICON)
                .setName(Lang.WAR_ENEMY_GOAL_LIST.get(langType))
                .setDescription(Lang.WAR_ENEMY_GOAL_LIST_DESC1.get(langType))
                .asGuiItem(player);
    }

    private @NotNull GuiItem getSurrenderButton() {
        return iconManager.get(IconKey.WAR_SURRENDER_ICON)
                .setName(Lang.WAR_SURRENDER.get(langType))
                .setDescription(
                        Lang.WAR_SURRENDER_DESC1.get(langType),
                        Lang.WAR_SURRENDER_DESC2.get(langType)
                )
                .setAction( action -> war.territorySurrender(territoryData))
                .asGuiItem(player);
    }
}


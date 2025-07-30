package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.war.War;
import org.leralix.tan.war.legacy.WarRole;
import org.leralix.tan.war.legacy.wargoals.ConquerWarGoal;
import org.leralix.tan.war.legacy.wargoals.LiberateWarGoal;
import org.leralix.tan.war.legacy.wargoals.SubjugateWarGoal;

public class ChooseWarGoal extends BasicGui {

    private final TerritoryData territoryData;
    private final War war;
    private final WarRole warRole;

    public ChooseWarGoal(Player player, TerritoryData territoryData, War war, WarRole warRole) {
        super(player, Lang.HEADER_SELECT_WARGOAL, 3);
        this.territoryData = territoryData;
        this.war = war;
        this.warRole = warRole;
        open();
    }


    @Override
    public void open() {
        gui.setItem(2, 2, getConquerButton());
        gui.setItem(2, 3, getcaptureLandmarkButton());
        gui.setItem(2, 4, getCaptureFortButton());

        gui.setItem(2, 7, getSubjugateButton());
        gui.setItem(2, 8, getLiberateButton());

        gui.open(player);
    }

    private @NotNull GuiItem getConquerButton() {
        return iconManager.get(IconKey.WAR_GOAL_CONQUER_ICON)
                .setName(Lang.CONQUER_WAR_GOAL.get(langType))
                .setDescription(
                        Lang.CONQUER_WAR_GOAL_DESC.get(tanPlayer),
                        Lang.GUI_GENERIC_CLICK_TO_SELECT.get(tanPlayer)
                )
                .setAction(
                        action -> {
                            war.addGoal(warRole, new ConquerWarGoal(1));
                            new SelectWarGoals(player, territoryData, war, warRole);
                        }
                )
                .asGuiItem(player);
    }

    private @NotNull GuiItem getcaptureLandmarkButton() {
        return iconManager.get(IconKey.WAR_GOAL_CAPTURE_LANDMARK_ICON)
                .setName(Lang.CAPTURE_LANDMARK_WAR_GOAL.get(langType))
                .setDescription(
                        Lang.CAPTURE_LANDMARK_WAR_GOAL_DESC.get(tanPlayer),
                        Lang.GUI_GENERIC_CLICK_TO_SELECT.get(tanPlayer)
                )
                .setAction(
                        action -> {
                            new SelectLandmarkForCapture(player, territoryData, war, warRole);
                        }
                )
                .asGuiItem(player);
    }

    private @NotNull GuiItem getCaptureFortButton() {
        return iconManager.get(IconKey.WAR_GOAL_CAPTURE_FORT_ICON)
                .setName(Lang.CAPTURE_FORT_WAR_GOAL.get(langType))
                .setDescription(
                        Lang.CAPTURE_FORT_WAR_GOAL_DESC.get(tanPlayer),
                        Lang.GUI_GENERIC_CLICK_TO_SELECT.get(tanPlayer)
                )
                .setAction(
                        action -> {
                            //war.addGoal(warRole, new CaptureLandmarkWarGoal());
                            new SelectWarGoals(player, territoryData, war, warRole);
                        }
                )
                .asGuiItem(player);
    }

    private @NotNull GuiItem getSubjugateButton() {
        return iconManager.get(IconKey.WAR_GOAL_SUBJUGATE_ICON)
                .setName(Lang.SUBJUGATE_WAR_GOAL.get(langType))
                .setDescription(
                        Lang.SUBJUGATE_WAR_GOAL_DESC.get(tanPlayer),
                        Lang.GUI_GENERIC_CLICK_TO_SELECT.get(tanPlayer)
                )
                .setAction(
                        action -> {
                            war.addGoal(warRole, new SubjugateWarGoal());
                            new SelectWarGoals(player, territoryData, war, warRole);
                        }
                )
                .asGuiItem(player);
    }

    private @NotNull GuiItem getLiberateButton() {
        return iconManager.get(IconKey.WAR_GOAL_LIBERATE_ICON)
                .setName(Lang.LIBERATE_SUBJECT_WAR_GOAL.get(langType))
                .setDescription(
                        Lang.LIBERATE_SUBJECT_WAR_GOAL.get(langType),
                        Lang.LIBERATE_SUBJECT_WAR_GOAL_DESC.get()
                )
                .setAction(
                        action -> {
                            war.addGoal(warRole, new LiberateWarGoal());
                            new SelectWarGoals(player, territoryData, war, warRole);
                        }
                )
                .asGuiItem(player);

    }

}

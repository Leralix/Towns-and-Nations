package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.listeners.chat.events.SelectNbChunksForConquer;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.war.War;
import org.leralix.tan.war.legacy.WarRole;
import org.leralix.tan.war.legacy.wargoals.ConquerWarGoal;
import org.leralix.tan.war.legacy.wargoals.SubjugateWarGoal;

import java.util.ArrayList;
import java.util.List;

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

        gui.setItem(3, 1, GuiUtil.createBackArrow(player, p -> new SelectWarGoals(player, territoryData, war, warRole)));

        gui.open(player);
    }

    private @NotNull GuiItem getConquerButton() {

        boolean conquerAlreadyUsed = war.getGoals(warRole).stream()
                .anyMatch(warGoal -> warGoal instanceof ConquerWarGoal);

        List<String> description = new ArrayList<>();
        description.add(Lang.CONQUER_WAR_GOAL_DESC.get(tanPlayer));
        if (conquerAlreadyUsed){
            description.add(Lang.GUI_ONLY_ONE_CONQUER_WAR_GOAL.get(tanPlayer));
        }
        else {
            description.add(Lang.GUI_GENERIC_CLICK_TO_SELECT.get(tanPlayer));
        }

        return iconManager.get(IconKey.WAR_GOAL_CONQUER_ICON)
                .setName(Lang.CONQUER_WAR_GOAL.get(langType))
                .setDescription(description)
                .setAction(
                        action -> {

                            if(conquerAlreadyUsed){
                                SoundUtil.playSound(player, SoundEnum.NOT_ALLOWED);
                                player.sendMessage(Lang.GUI_ONLY_ONE_CONQUER_WAR_GOAL.get(tanPlayer));
                                return;
                            }
                            player.sendMessage(Lang.ENTER_NEW_VALUE.get(langType));
                            PlayerChatListenerStorage.register(player, new SelectNbChunksForConquer(war, warRole, new SelectWarGoals(player, territoryData, war, warRole)));
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
                            new SelectFortForCapture(player, territoryData, war, warRole);
                        }
                )
                .asGuiItem(player);
    }

    private @NotNull GuiItem getSubjugateButton() {

        boolean canBeSubjugated = warRole == WarRole.MAIN_ATTACKER
                ? war.getMainDefender().getHierarchyRank() < territoryData.getHierarchyRank()
                : war.getMainAttacker().getHierarchyRank() < territoryData.getHierarchyRank();

        List<String> description = new ArrayList<>();
        description.add(Lang.SUBJUGATE_WAR_GOAL_DESC.get(langType));
        description.add(Lang.GUI_GENERIC_CLICK_TO_SELECT.get(langType));
        if(!canBeSubjugated){
            description.add(Lang.GUI_WARGOAL_SUBJUGATE_CANNOT_BE_USED.get(langType));
        }



        return iconManager.get(IconKey.WAR_GOAL_SUBJUGATE_ICON)
                .setName(Lang.SUBJUGATE_WAR_GOAL.get(langType))
                .setDescription(description)
                .setAction(
                        action -> {

                            if(!canBeSubjugated) {
                                player.sendMessage(Lang.GUI_WARGOAL_SUBJUGATE_CANNOT_BE_USED.get(langType));
                                return;
                            }

                            war.addGoal(warRole, new SubjugateWarGoal());
                            new SelectWarGoals(player, territoryData, war, warRole);
                        }
                )
                .asGuiItem(player);
    }

    private @NotNull GuiItem getLiberateButton() {

        boolean doesEnemyHaveSubjects = warRole == WarRole.MAIN_ATTACKER
                ? !war.getMainDefender().getVassals().isEmpty()
                : !war.getMainAttacker().getVassals().isEmpty();

        List<String> description = new ArrayList<>();
        description.add(Lang.LIBERATE_SUBJECT_WAR_GOAL_DESC.get(langType));
        description.add(Lang.GUI_GENERIC_CLICK_TO_SELECT.get(langType));
        if(!doesEnemyHaveSubjects){
            description.add(Lang.GUI_WARGOAL_LIBERATE_CANNOT_BE_USED.get(langType));
        }

        return iconManager.get(IconKey.WAR_GOAL_LIBERATE_ICON)
                .setName(Lang.LIBERATE_SUBJECT_WAR_GOAL.get(langType))
                .setDescription(
                        description
                )
                .setAction(
                        action -> {
                            if(!doesEnemyHaveSubjects){
                                player.sendMessage(Lang.GUI_WARGOAL_LIBERATE_CANNOT_BE_USED.get(langType));
                                return;
                            }

                            new SelectTerritoryForLIberation(player, territoryData, war, warRole);
                        }
                )
                .asGuiItem(player);

    }

}

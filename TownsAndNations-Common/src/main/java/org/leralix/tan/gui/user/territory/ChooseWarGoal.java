package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.listeners.chat.events.SelectNbChunksForConquer;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.text.TanChatUtils;
import org.leralix.tan.war.War;
import org.leralix.tan.war.legacy.WarRole;
import org.leralix.tan.war.legacy.wargoals.ConquerWarGoal;
import org.leralix.tan.war.legacy.wargoals.SubjugateWarGoal;

public class ChooseWarGoal extends BasicGui {

    private final TerritoryData territoryData;
    private final War war;
    private final WarRole warRole;
    private final BasicGui returnGui;

    public ChooseWarGoal(Player player, TerritoryData territoryData, War war, WarRole warRole, BasicGui returnGui) {
        super(player, Lang.HEADER_SELECT_WARGOAL, 3);
        this.territoryData = territoryData;
        this.war = war;
        this.warRole = warRole;
        this.returnGui = returnGui;
        open();
    }


    @Override
    public void open() {
        gui.setItem(2, 2, getConquerButton());
        gui.setItem(2, 3, getcaptureLandmarkButton());
        gui.setItem(2, 4, getCaptureFortButton());

        gui.setItem(2, 7, getSubjugateButton());
        gui.setItem(2, 8, getLiberateButton());

        gui.setItem(3, 1, GuiUtil.createBackArrow(player, p -> returnGui.open()));

        gui.open(player);
    }

    private @NotNull GuiItem getConquerButton() {

        boolean conquerAlreadyUsed = war.getGoals(warRole).stream()
                .anyMatch(ConquerWarGoal.class::isInstance);


        return iconManager.get(IconKey.WAR_GOAL_CONQUER_ICON)
                .setName(Lang.CONQUER_WAR_GOAL.get(langType))
                .setDescription(Lang.CONQUER_WAR_GOAL_DESC.get())
                .setClickToAcceptMessage(conquerAlreadyUsed ?
                        Lang.GUI_ONLY_ONE_CONQUER_WAR_GOAL :
                        Lang.GUI_GENERIC_CLICK_TO_SELECT
                )
                .setAction(
                        action -> {

                            if(conquerAlreadyUsed){
                                TanChatUtils.message(player, Lang.GUI_ONLY_ONE_CONQUER_WAR_GOAL.get(tanPlayer), SoundEnum.NOT_ALLOWED);
                                return;
                            }
                            TanChatUtils.message(player, Lang.ENTER_NEW_VALUE.get(langType));
                            PlayerChatListenerStorage.register(player, new SelectNbChunksForConquer(war, warRole, returnGui));
                        }
                )
                .asGuiItem(player, langType);
    }

    private @NotNull GuiItem getcaptureLandmarkButton() {

        boolean isTown = territoryData instanceof TownData;

        return iconManager.get(IconKey.WAR_GOAL_CAPTURE_LANDMARK_ICON)
                .setName(Lang.CAPTURE_LANDMARK_WAR_GOAL.get(langType))
                .setDescription(Lang.CAPTURE_LANDMARK_WAR_GOAL_DESC.get())
                .setClickToAcceptMessage(
                        isTown ?
                                Lang.GUI_GENERIC_CLICK_TO_SELECT :
                                Lang.GUI_WARGOAL_CAPTURE_LANDMARK_CANNOT_BE_USED
                )
                .setAction(
                        action -> {
                            if(!isTown){
                                TanChatUtils.message(player, Lang.GUI_WARGOAL_CAPTURE_LANDMARK_CANNOT_BE_USED.get(langType));
                                return;
                            }
                            new SelectLandmarkForCapture(player, territoryData, war, warRole, returnGui);
                        }
                )
                .asGuiItem(player, langType);
    }

    private @NotNull GuiItem getCaptureFortButton() {
        return iconManager.get(IconKey.WAR_GOAL_CAPTURE_FORT_ICON)
                .setName(Lang.CAPTURE_FORT_WAR_GOAL.get(langType))
                .setDescription(Lang.CAPTURE_FORT_WAR_GOAL_DESC.get())
                .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_SELECT)
                .setAction(
                        action -> new SelectFortForCapture(player, territoryData, war, warRole, returnGui)
                )
                .asGuiItem(player, langType);
    }

    private @NotNull GuiItem getSubjugateButton() {

        boolean canBeSubjugated = warRole == WarRole.MAIN_ATTACKER
                ? war.getMainDefender().getHierarchyRank() < territoryData.getHierarchyRank()
                : war.getMainAttacker().getHierarchyRank() < territoryData.getHierarchyRank();

        return iconManager.get(IconKey.WAR_GOAL_SUBJUGATE_ICON)
                .setName(Lang.SUBJUGATE_WAR_GOAL.get(langType))
                .setDescription(Lang.SUBJUGATE_WAR_GOAL_DESC.get())
                .setClickToAcceptMessage(
                        canBeSubjugated ?
                                Lang.GUI_GENERIC_CLICK_TO_SELECT :
                                Lang.GUI_WARGOAL_SUBJUGATE_CANNOT_BE_USED
                )
                .setAction(
                        action -> {

                            if(!canBeSubjugated) {
                                TanChatUtils.message(player, Lang.GUI_WARGOAL_SUBJUGATE_CANNOT_BE_USED.get(langType));
                                return;
                            }

                            war.addGoal(warRole, new SubjugateWarGoal());
                            returnGui.open();
                        }
                )
                .asGuiItem(player, langType);
    }

    private @NotNull GuiItem getLiberateButton() {

        boolean doesEnemyHaveSubjects = warRole == WarRole.MAIN_ATTACKER
                ? !war.getMainDefender().getVassalsInternal().isEmpty()
                : !war.getMainAttacker().getVassalsInternal().isEmpty();

        return iconManager.get(IconKey.WAR_GOAL_LIBERATE_ICON)
                .setName(Lang.LIBERATE_SUBJECT_WAR_GOAL.get(langType))
                .setDescription(Lang.LIBERATE_SUBJECT_WAR_GOAL_DESC.get())
                .setClickToAcceptMessage(
                        doesEnemyHaveSubjects ?
                                Lang.GUI_GENERIC_CLICK_TO_SELECT :
                                Lang.GUI_WARGOAL_LIBERATE_CANNOT_BE_USED
                )
                .setAction(
                        action -> {
                            if(!doesEnemyHaveSubjects){
                                TanChatUtils.message(player, Lang.GUI_WARGOAL_LIBERATE_CANNOT_BE_USED.get(langType));
                                return;
                            }

                            new SelectTerritoryForLIberation(player, territoryData, war, warRole, returnGui);
                        }
                )
                .asGuiItem(player, langType);

    }

}

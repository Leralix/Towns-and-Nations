package org.leralix.tan.dataclass.wars.wargoals;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.Landmark;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.dataclass.wars.CreateAttackData;
import org.leralix.tan.gui.legacy.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.HeadUtils;
import org.leralix.tan.utils.TerritoryUtil;

public class CaptureLandmarkWarGoal extends WarGoal {

    Landmark landmarkToCapture;

    final String attackingTerritoryID;
    final String defendingTerritoryID;


    public CaptureLandmarkWarGoal(String attackingTerritoryID, String defendingTerritoryID){
        this.attackingTerritoryID = attackingTerritoryID;
        this.defendingTerritoryID = defendingTerritoryID;
    }


    @Override
    public void addExtraOptions(Gui gui, Player player, CreateAttackData createAttackData) {

        GuiItem selectedTerritoryGui;
        if(landmarkToCapture == null){
            ItemStack selectTerritory = HeadUtils.makeSkullB64(Lang.GUI_SELECT_LANDMARK_TO_CAPTURE.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjMyZmZmMTYzZTIzNTYzMmY0MDQ3ZjQ4NDE1OTJkNDZmODVjYmJmZGU4OWZjM2RmNjg3NzFiZmY2OWE2NjIifX19",
                    Lang.LEFT_CLICK_TO_MODIFY.get());

            selectedTerritoryGui = ItemBuilder.from(selectTerritory).asGuiItem(event -> {
                PlayerGUI.openSelecteLandmarkToCapture(player, createAttackData,this, 0);
                event.setCancelled(true);
            });
        }
        else{
            ItemStack selectedTerritory = HeadUtils.createCustomItemStack(landmarkToCapture.getIcon() , Lang.GUI_SELECT_LANDMARK_TO_CAPTURE.get(),
                    Lang.GUI_SELECTED_LANDMARK_TO_CAPTURE.get(landmarkToCapture.getName()),
                    Lang.LEFT_CLICK_TO_MODIFY.get());
            selectedTerritoryGui = ItemBuilder.from(selectedTerritory).asGuiItem(event -> {
                PlayerGUI.openSelecteLandmarkToCapture(player, createAttackData,this, 0);
                event.setCancelled(true);
            });
        }

        gui.setItem(3, 6, selectedTerritoryGui);

    }

    @Override
    public ItemStack getIcon() {
        return buildIcon(Material.DIAMOND, Lang.CAPTURE_LANDMARK_WAR_GOAL_DESC.get());
    }

    @Override
    public String getDisplayName() {
        return Lang.CONQUER_WAR_GOAL.get();
    }

    @Override
    public void applyWarGoal() {
        TownData defendingTerritory = (TownData) TerritoryUtil.getTerritory(defendingTerritoryID);
        if(defendingTerritory != null)
            defendingTerritory.removeLandmark(landmarkToCapture);
        TownData attackingTerritory = (TownData) TerritoryUtil.getTerritory(attackingTerritoryID);
        if(attackingTerritory != null)
            attackingTerritory.addLandmark(landmarkToCapture);

    }

    @Override
    public boolean isCompleted() {
        return true;
    }

    @Override
    public String getCurrentDesc() {
        return Lang.GUI_CAPTURE_LANDMARK_CURRENT_DESC.get(landmarkToCapture.getName());
    }

    @Override
    public void sendAttackSuccessToAttackers(Player player) {
        super.sendAttackSuccessToAttackers(player);
        TerritoryData attackingTerritory = TerritoryUtil.getTerritory(attackingTerritoryID);
        TerritoryData defendingTerritory = TerritoryUtil.getTerritory(defendingTerritoryID);
        if (attackingTerritory == null || defendingTerritory == null)
            return;

        player.sendMessage(Lang.WARGOAL_CAPTURE_LANDMARK_SUCCESS_WINNING_SIDE.get(landmarkToCapture.getName()));
    }

    @Override
    public void sendAttackSuccessToDefenders(Player player) {
        super.sendAttackSuccessToDefenders(player);
        TerritoryData attackingTerritory = TerritoryUtil.getTerritory(attackingTerritoryID);
        TerritoryData defendingTerritory = TerritoryUtil.getTerritory(defendingTerritoryID);
        if (attackingTerritory == null || defendingTerritory == null)
            return;
        player.sendMessage(Lang.WARGOAL_CAPTURE_LANDMARK_SUCCESS_LOOSING_SIDE.get(attackingTerritory.getBaseColoredName(), landmarkToCapture.getName()));
    }

    public void setLandmarkToCapture(Landmark ownedLandmark) {
        this.landmarkToCapture = ownedLandmark;
    }
}

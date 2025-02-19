package org.leralix.tan.dataclass.wars.wargoals;

import dev.triumphteam.gui
.builder.item.ItemBuilder;
import dev.triumphteam.gui
.guis.Gui;
import dev.triumphteam.gui
.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.wars.CreateAttackData;
import org.leralix.tan.utils.HeadUtils;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.PlayerGUI;
import org.leralix.tan.lang.Lang;

import java.util.function.Consumer;

public class LiberateWarGoal extends WarGoal {

    TerritoryData territoryToLiberate;
    @Override
    public ItemStack getIcon() {
        return buildIcon(Material.LANTERN, Lang.LIBERATE_SUBJECT_WAR_GOAL_DESC.get());
    }

    @Override
    public String getDisplayName() {
        return Lang.LIBERATE_SUBJECT_WAR_GOAL.get();
    }

    @Override
    public void addExtraOptions(Gui gui, Player player, CreateAttackData createAttackData) {

        GuiItem selectedTerritoryGui;
        if(territoryToLiberate == null){
            ItemStack selectTerritory = HeadUtils.makeSkullB64(Lang.GUI_SELECT_TERRITORY_TO_LIBERATE.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjMyZmZmMTYzZTIzNTYzMmY0MDQ3ZjQ4NDE1OTJkNDZmODVjYmJmZGU4OWZjM2RmNjg3NzFiZmY2OWE2NjIifX19",
                    Lang.LEFT_CLICK_TO_SELECT.get());

            selectedTerritoryGui = ItemBuilder.from(selectTerritory).asGuiItem(event -> {
                PlayerGUI.openSelecteTerritoryToLiberate(player, createAttackData,this, exit);
                event.setCancelled(true);
            });
        }
        else{
            ItemStack selectedTerritory = HeadUtils.createCustomItemStack(territoryToLiberate.getIcon() , Lang.GUI_SELECT_TERRITORY_TO_LIBERATE.get(territoryToLiberate.getName()),
                    Lang.GUI_SELECTED_TERRITORY_TO_LIBERATE.get(territoryToLiberate.getName()),
                    Lang.LEFT_CLICK_TO_SELECT.get());
            selectedTerritoryGui = ItemBuilder.from(selectedTerritory).asGuiItem(event -> {
                PlayerGUI.openSelecteTerritoryToLiberate(player, createAttackData,this, exit);
                event.setCancelled(true);
            });
        }

        gui.setItem(3, 6, selectedTerritoryGui);


    }

    @Override
    public void applyWarGoal() {
        if(!territoryToLiberate.haveOverlord())
            return;
        territoryToLiberate.getOverlord().removeVassal(territoryToLiberate);
        territoryToLiberate.removeOverlord();
    }

    @Override
    public boolean isCompleted() {
        return territoryToLiberate != null;
    }

    @Override
    public String getCurrentDesc() {
        if(territoryToLiberate == null)
            return null;
        return Lang.GUI_WARGOAL_LIBERATE_WAR_GOAL_RESULT.get();
    }

    public void setTerritoryToLiberate(TerritoryData territoryToLiberate) {
        this.territoryToLiberate = territoryToLiberate;
    }

    @Override
    public void sendAttackSuccessToAttackers(Player player) {
        super.sendAttackSuccessToAttackers(player);
        TerritoryData loosingTerritory = territoryToLiberate.getOverlord();
        if(loosingTerritory == null)
            return;
        player.sendMessage(Lang.WARGOAL_LIBERATE_SUCCESS.get(territoryToLiberate.getColoredName(), loosingTerritory.getColoredName()));
    }

    @Override
    public void sendAttackSuccessToDefenders(Player player) {
        sendAttackSuccessToAttackers(player);
    }
}

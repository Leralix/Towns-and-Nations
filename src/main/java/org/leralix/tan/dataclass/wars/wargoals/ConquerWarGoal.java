package org.leralix.tan.dataclass.wars.wargoals;

import org.leralix.lib.gui
.builder.item.ItemBuilder;
import org.leralix.lib.gui
.guis.Gui;
import org.leralix.lib.gui
.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.wars.CreateAttackData;
import org.leralix.tan.utils.HeadUtils;
import org.leralix.tan.utils.TerritoryUtil;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.lang.Lang;

import java.util.function.Consumer;

import static org.leralix.tan.gui.PlayerGUI.openStartWarSettings;

public class ConquerWarGoal extends WarGoal {

    final String attackingTerritoryID;
    final String defendingTerritoryID;

    int numberOfChunks;

    public ConquerWarGoal(String attackingTerritoryID, String defendingTerritoryID){
        numberOfChunks = 1;
        this.attackingTerritoryID = attackingTerritoryID;
        this.defendingTerritoryID = defendingTerritoryID;
    }


    @Override
    public void addExtraOptions(Gui gui, Player player, CreateAttackData createAttackData, Consumer<Player> exit) {

        ItemStack removeChunk = HeadUtils.makeSkullB64(Lang.GUI_CONQUER_REMOVE_CHUNK.get(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGU0YjhiOGQyMzYyYzg2NGUwNjIzMDE0ODdkOTRkMzI3MmE2YjU3MGFmYmY4MGMyYzViMTQ4Yzk1NDU3OWQ0NiJ9fX0=",
                Lang.GUI_DECREASE_1_DESC.get(),
                Lang.GUI_DECREASE_10_DESC.get());
        ItemStack addChunk = HeadUtils.makeSkullB64(Lang.GUI_CONQUER_ADD_CHUNK.get(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19",
                Lang.GUI_INCREASE_1_DESC.get(),
                Lang.GUI_INCREASE_10_DESC.get());

        ItemStack chunkInfo = HeadUtils.makeSkullB64(Lang.GUI_CONQUER_CHUNK_INFO.get(numberOfChunks), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTc5ODBiOTQwYWY4NThmOTEwOTQzNDY0ZWUwMDM1OTI4N2NiMGI1ODEwNjgwYjYwYjg5YmU0MjEwZGRhMGVkMSJ9fX0=");

        GuiItem addChunkGui = ItemBuilder.from(addChunk).asGuiItem(event -> {
            event.setCancelled(true);
            SoundUtil.playSound(player, SoundEnum.ADD);
            if(event.isShiftClick()){
                numberOfChunks += 10;
            } else if(event.isLeftClick()){
                numberOfChunks += 1;
            }
            openStartWarSettings(player, exit, createAttackData);
        });

        GuiItem chunkInfoGui = ItemBuilder.from(chunkInfo).asGuiItem(event -> event.setCancelled(true));

        GuiItem removeChunkGui = ItemBuilder.from(removeChunk).asGuiItem(event -> {
            event.setCancelled(true);
            SoundUtil.playSound(player, SoundEnum.REMOVE);
            if(event.isShiftClick()){
                numberOfChunks -= 10;
            } else if(event.isLeftClick()){
                numberOfChunks -= 1;
            }
            if(numberOfChunks < 1){
                numberOfChunks = 1;
            }
            openStartWarSettings(player, exit, createAttackData);
        });

        gui.setItem(3, 5, removeChunkGui);
        gui.setItem(3, 6, chunkInfoGui);
        gui.setItem(3, 7, addChunkGui);

    }

    @Override
    public ItemStack getIcon() {
        return buildIcon(Material.IRON_SWORD, Lang.CONQUER_WAR_GOAL_DESC.get());
    }

    @Override
    public String getDisplayName() {
        return Lang.CONQUER_WAR_GOAL.get();
    }

    @Override
    public void applyWarGoal() {
        TerritoryData attackingTerritory = TerritoryUtil.getTerritory(attackingTerritoryID);
        if (attackingTerritory == null)
            return;
        attackingTerritory.addAvailableClaims(defendingTerritoryID, numberOfChunks);
    }

    @Override
    public boolean isCompleted() {
        return true;
    }

    @Override
    public String getCurrentDesc() {
        return Lang.GUI_CONQUER_CHUNK_CURRENT_DESC.get(numberOfChunks);
    }

    @Override
    public void sendAttackSuccessToAttackers(Player player) {
        super.sendAttackSuccessToAttackers(player);
        TerritoryData attackingTerritory = TerritoryUtil.getTerritory(attackingTerritoryID);
        TerritoryData defendingTerritory = TerritoryUtil.getTerritory(defendingTerritoryID);
        if (attackingTerritory == null || defendingTerritory == null)
            return;

        player.sendMessage(Lang.WARGOAL_CONQUER_SUCCESS_WINNING_SIDE.get(numberOfChunks, defendingTerritory.getColoredName()));
    }

    @Override
    public void sendAttackSuccessToDefenders(Player player) {
        super.sendAttackSuccessToDefenders(player);
        TerritoryData attackingTerritory = TerritoryUtil.getTerritory(attackingTerritoryID);
        TerritoryData defendingTerritory = TerritoryUtil.getTerritory(defendingTerritoryID);
        if (attackingTerritory == null || defendingTerritory == null)
            return;
        player.sendMessage(Lang.WARGOAL_CONQUER_SUCCESS_LOOSING_SIDE.get(attackingTerritory.getColoredName(), numberOfChunks, defendingTerritory.getColoredName()));
    }

}

package org.tan.TownsAndNations.DataClass.wars.wargoals;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.tan.TownsAndNations.DataClass.wars.CreateAttackData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.utils.HeadUtils;
import org.tan.TownsAndNations.utils.SoundUtil;

import java.util.function.Consumer;

import static org.tan.TownsAndNations.GUI.GuiManager.OpenStartWarSettings;
import static org.tan.TownsAndNations.enums.SoundEnum.ADD;
import static org.tan.TownsAndNations.enums.SoundEnum.REMOVE;

public class ConquerWarGoal extends WarGoal {

    int numberOfChunks;

    public ConquerWarGoal(){
        numberOfChunks = 1;
    }


    @Override
    public void addExtraOptions(Gui gui, Player player, CreateAttackData createAttackData, Consumer<Player> exit) {

        ItemStack removeChunk = HeadUtils.makeSkull(Lang.GUI_CONQUER_REMOVE_CHUNK.get(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGU0YjhiOGQyMzYyYzg2NGUwNjIzMDE0ODdkOTRkMzI3MmE2YjU3MGFmYmY4MGMyYzViMTQ4Yzk1NDU3OWQ0NiJ9fX0=",
                Lang.GUI_DECREASE_1_DESC.get(),
                Lang.GUI_DECREASE_10_DESC.get());
        ItemStack addChunk = HeadUtils.makeSkull(Lang.GUI_CONQUER_ADD_CHUNK.get(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19",
                Lang.GUI_INCREASE_1_DESC.get(),
                Lang.GUI_INCREASE_10_DESC.get());

        ItemStack chunkInfo = HeadUtils.makeSkull(Lang.GUI_CONQUER_CHUNK_INFO.get(numberOfChunks), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTc5ODBiOTQwYWY4NThmOTEwOTQzNDY0ZWUwMDM1OTI4N2NiMGI1ODEwNjgwYjYwYjg5YmU0MjEwZGRhMGVkMSJ9fX0=");

        GuiItem _addChunk = ItemBuilder.from(addChunk).asGuiItem(event -> {
            event.setCancelled(true);
            SoundUtil.playSound(player, ADD);
            if(event.isShiftClick()){
                numberOfChunks += 10;
            } else if(event.isLeftClick()){
                numberOfChunks += 1;
            }
            OpenStartWarSettings(player, exit, createAttackData);
        });

        GuiItem _chunkInfo = ItemBuilder.from(chunkInfo).asGuiItem(event -> {
            event.setCancelled(true);
        });

        GuiItem _removeChunk = ItemBuilder.from(removeChunk).asGuiItem(event -> {
            event.setCancelled(true);
            SoundUtil.playSound(player, REMOVE);
            if(event.isShiftClick()){
                numberOfChunks -= 10;
            } else if(event.isLeftClick()){
                numberOfChunks -= 1;
            }
            if(numberOfChunks < 1){
                numberOfChunks = 1;
            }
            OpenStartWarSettings(player, exit, createAttackData);
        });

        gui.setItem(3, 5, _removeChunk);
        gui.setItem(3, 6, _chunkInfo);
        gui.setItem(3, 7, _addChunk);

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
        System.out.println("Conquer war goal applied, number of chunks : " + numberOfChunks);
    }

    @Override
    public boolean isCompleted() {
        return true;
    }

    @Override
    public String getCurrentDesc() {
        return Lang.GUI_CONQUER_CHUNK_CURRENT_DESC.get(numberOfChunks);
    }


}

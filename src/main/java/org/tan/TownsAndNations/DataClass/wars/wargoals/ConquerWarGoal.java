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
        ItemStack addChunk = HeadUtils.makeSkull(Lang.GUI_CONQUER_ADD_CHUNK.get(), "texture_url_for_add_chunk",
                Lang.GUI_INCREASE_1_DESC.get(),
                Lang.GUI_INCREASE_10_DESC.get());
        ItemStack removeChunk = HeadUtils.makeSkull(Lang.GUI_CONQUER_REMOVE_CHUNK.get(), "texture_url_for_remove_chunk",
                Lang.GUI_DECREASE_1_DESC.get(),
                Lang.GUI_DECREASE_10_DESC.get());

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

        GuiItem _removeChunk = ItemBuilder.from(removeChunk).asGuiItem(event -> {
            event.setCancelled(true);
            SoundUtil.playSound(player, REMOVE);
            if(event.isShiftClick()){
                numberOfChunks -= 10;
            } else if(event.isLeftClick()){
                numberOfChunks -= 1;
            }
            OpenStartWarSettings(player, exit, createAttackData);
        });

        gui.setItem(2, 5, _addChunk);
        gui.setItem(2, 6, _removeChunk);
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

    }
}

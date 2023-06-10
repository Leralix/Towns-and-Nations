package org.tan.towns_and_nations.GUI;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.tan.towns_and_nations.utils.HeadUtils;

import java.util.Dictionary;
import java.util.LinkedHashMap;
import java.util.Map;

public class GuiClass {

    String name;
    int nRows;
    LinkedHashMap<Integer,GuiItem> items;



    public GuiClass(String name, int nRows,LinkedHashMap<Integer,GuiItem> Items){
        this.name = name;
        this.nRows = nRows;
        this.items = Items;
    }

    public void buildGUI(Player player){
        Inventory inv = Bukkit.createInventory(player, nRows * 9, "Inventaire Custom");


        for (Map.Entry<Integer, GuiItem> entry : items.entrySet()) {
            Integer position = entry.getKey();
            GuiItem item = entry.getValue();

            if(item.skin == null)
                inv.setItem(position, HeadUtils.getPlayerHead(player));
            else
                inv.setItem(position,item.getItemStack());
        }
        player.openInventory(inv);




    }
}

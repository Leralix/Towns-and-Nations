package org.tan.towns_and_nations.GUI;

import org.bukkit.inventory.ItemStack;

public class GuiItem {

    String name;
    String[] lore;
    ItemStack skin;



    public GuiItem(String _name, String[] _lore, ItemStack _skin) {

        this.name = _name;
        this.lore = _lore;
        this.skin = _skin;

    }

    public ItemStack getItemStack(){

        ItemStack res = skin;

        //.....

        return res;
    }

}

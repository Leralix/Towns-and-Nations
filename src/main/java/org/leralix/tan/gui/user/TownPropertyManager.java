package org.leralix.tan.gui.user;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.PropertyData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.GuiUtil;

public class TownPropertyManager extends PropertyMenus{

    public TownPropertyManager(Player player, PropertyData propertyData) {
        super(player, Lang.HEADER_TOWN_SPECIFIC_PROPERTY, 3, propertyData);

        open();
    }

    @Override
    public void open() {
        gui.getFiller().fillTop(GuiUtil.getUnnamedItem(Material.BROWN_STAINED_GLASS_PANE));

        gui.setItem(1, 5, getPropertyIcon());

        gui.setItem(2, 5, getBoundariesButton());
        gui.setItem(3, 6, getDeleteButton());
        if(propertyData.isRented())
            gui.setItem(3, 7, getKickRenterButton());


        gui.open(player);
    }
}

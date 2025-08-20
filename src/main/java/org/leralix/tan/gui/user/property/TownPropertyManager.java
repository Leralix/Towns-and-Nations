package org.leralix.tan.gui.user.property;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.PropertyData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.deprecated.GuiUtil;

public class TownPropertyManager extends PropertyMenus {

    private final TownData townData;
    public TownPropertyManager(Player player, PropertyData propertyData, TownData townData) {
        super(player, Lang.HEADER_PLAYER_SPECIFIC_PROPERTY.get(player, propertyData.getName()), 3, propertyData);
        this.townData = townData;
        open();
    }

    @Override
    public void open() {
        gui.getFiller().fillTop(GuiUtil.getUnnamedItem(Material.BROWN_STAINED_GLASS_PANE));

        gui.setItem(1, 5, getPropertyIcon());

        gui.setItem(2, 5, getBoundariesButton());
        gui.setItem(2, 6, getDeleteButton());
        if(propertyData.isRented())
            gui.setItem(2, 7, getKickRenterButton());

        gui.setItem(3, 1, GuiUtil.createBackArrow(player, p -> new TownPropertiesMenu(player, townData)));

        gui.open(player);
    }
}

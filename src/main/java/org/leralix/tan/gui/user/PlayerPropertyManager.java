package org.leralix.tan.gui.user;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.PropertyData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.GuiUtil;

import java.util.function.Consumer;

public class PlayerPropertyManager extends PropertyMenus{

    Consumer<Player> onClose;

    public PlayerPropertyManager(Player player, PropertyData propertyData, Consumer<Player> onClose) {
        super(player, Lang.HEADER_PLAYER_SPECIFIC_PROPERTY, 3, propertyData);
        this.onClose = onClose;
        open();
    }

    @Override
    public void open() {
        gui.getFiller().fillTop(GuiUtil.getUnnamedItem(Material.BROWN_STAINED_GLASS_PANE));

        gui.setItem(1, 5, getPropertyIcon());

        gui.setItem(2, 2, getRenameButton());
        gui.setItem(2, 3, getDescriptionButton());
        gui.setItem(2, 4, getAuthorizedPlayersButton());
        gui.setItem(2, 5, getBoundariesButton());
        gui.setItem(2, 6, forRentButton());
        if(propertyData.isRented()){
            gui.setItem(2, 7, getKickRenterButton());
        }
        else {
            gui.setItem(2, 7, forSaleButton());
        }
        gui.setItem(2, 8, getDeleteButton());


        gui.setItem(3, 1, GuiUtil.createBackArrow(player, onClose));

        gui.open(player);
    }
}

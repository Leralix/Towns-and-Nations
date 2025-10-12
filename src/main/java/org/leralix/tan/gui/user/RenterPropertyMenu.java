package org.leralix.tan.gui.user;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.PropertyData;
import org.leralix.tan.dataclass.property.AbstractOwner;
import org.leralix.tan.dataclass.property.PlayerOwned;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.user.property.PropertyMenus;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.UUID;

import static org.leralix.lib.data.SoundEnum.MINOR_BAD;
import static org.leralix.lib.data.SoundEnum.MINOR_GOOD;

public class RenterPropertyMenu extends PropertyMenus {

    public RenterPropertyMenu(Player player, PropertyData propertyData){
        super(player, Lang.HEADER_PLAYER_SPECIFIC_PROPERTY.get(player, propertyData.getName()), 3, propertyData);
        open();
    }


    @Override
    public void open() {
        gui.setItem(1,5, getPropertyIcon());
        gui.getFiller().fillTop(GuiUtil.getUnnamedItem(Material.BROWN_STAINED_GLASS_PANE));

        gui.setItem(2, 4, getAuthorizedPlayersButton());
        gui.setItem(2, 6, getStopRentPropertyButton());

        gui.setItem(3, 1, GuiUtil.createBackArrow(player, HumanEntity::closeInventory));
        gui.open(player);
    }

    private GuiItem getStopRentPropertyButton() {
        return iconManager.get(IconKey.STOP_RENTING_PROPERTY_ICON)
                .setName(Lang.GUI_PROPERTY_STOP_RENTING_PROPERTY.get(tanPlayer))
                .setDescription(Lang.GUI_PROPERTY_STOP_RENTING_PROPERTY_DESC1.get(tanPlayer))
                .setAction(action -> {
                    propertyData.expelRenter(true);

                    TanChatUtils.message(player, Lang.PROPERTY_RENTER_LEAVE_RENTER_SIDE.get(tanPlayer, propertyData.getName()), MINOR_GOOD);

                    AbstractOwner owner = propertyData.getOwner();
                    if(owner instanceof PlayerOwned playerOwned){
                        Player playerOwn = Bukkit.getPlayer(UUID.fromString(playerOwned.getPlayerID()));
                        TanChatUtils.message(playerOwn, Lang.PROPERTY_RENTER_LEAVE_OWNER_SIDE.get(playerOwn, player.getName(), propertyData.getName()), MINOR_BAD);
                    }

                    player.closeInventory();
                })
                .asGuiItem(player);
    }


}

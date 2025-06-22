package org.leralix.tan.gui.user.property;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.PropertyData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.user.player.PlayerMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.PlayerSelectPropertyPositionStorage;
import org.leralix.tan.utils.GuiUtil;
import org.leralix.tan.utils.TanChatUtils;

import java.util.ArrayList;
import java.util.List;

import static org.leralix.lib.data.SoundEnum.NOT_ALLOWED;

public class PlayerPropertiesMenu extends IteratorGUI {

    public PlayerPropertiesMenu(Player player){
        super(player, Lang.HEADER_PLAYER_PROPERTIES, 3);
    }


    @Override
    public void open() {

        GuiUtil.createIterator(gui, getProperties(), page, player,
                p -> new PlayerMenu(player).open(),
                p -> nextPage(),
                p -> previousPage());

        gui.setItem(3, 5, getNewPropertyButton());

        gui.open(player);
    }

    private GuiItem getNewPropertyButton() {

        List<String> description = new ArrayList<>();
        if(tanPlayer.hasTown()){
            TownData townData = tanPlayer.getTown();
            int nbProperties = townData.getProperties().size();
            int maxNbProperties = townData.getLevel().getPropertyCap();
            if(nbProperties >= maxNbProperties){
                description.add(Lang.GUI_PROPERTY_CAP_FULL.get(tanPlayer, nbProperties, maxNbProperties));
            }
            else {
                description.add(Lang.GUI_PROPERTY_CAP.get(tanPlayer, nbProperties, maxNbProperties));
            }
        }
        else {
            description.add(Lang.PLAYER_NO_TOWN.get());
        }


        return iconManager.get(IconKey.CREATE_NEW_PROPERTY_ICON)
                .setName(Lang.GUI_PLAYER_NEW_PROPERTY.get(tanPlayer))
                .setDescription(description)
                .setAction(event -> {
                    TownData playerTown = tanPlayer.getTown();
                    if(!playerTown.doesPlayerHavePermission(tanPlayer, RolePermission.CREATE_PROPERTY)){
                        player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get(tanPlayer));
                        SoundUtil.playSound(player, NOT_ALLOWED);
                        return;
                    }

                    if(playerTown.getPropertyDataMap().size() >= playerTown.getLevel().getPropertyCap()){
                        player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_PROPERTY_CAP_REACHED.get(tanPlayer));
                        return;
                    }

                    if(PlayerSelectPropertyPositionStorage.contains(tanPlayer)){
                        player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_ALREADY_IN_SCOPE.get(tanPlayer));
                        return;
                    }
                    player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_RIGHT_CLICK_2_POINTS_TO_CREATE_PROPERTY.get(tanPlayer));
                    player.sendMessage(TanChatUtils.getTANString() + Lang.WRITE_CANCEL_TO_CANCEL.get(tanPlayer, Lang.CANCEL_WORD.get(tanPlayer)));
                    PlayerSelectPropertyPositionStorage.addPlayer(tanPlayer);
                    player.closeInventory();
                })
                .asGuiItem(player);
    }

    private List<GuiItem> getProperties() {
        List<GuiItem> guiItems = new ArrayList<>();
        for (PropertyData propertyData : tanPlayer.getProperties()){

            List<String> desc = propertyData.getBasicDescription(tanPlayer.getLang());
            desc.add(Lang.GUI_GENERIC_CLICK_TO_OPEN.get(tanPlayer));


            guiItems.add(iconManager.get(propertyData.getIcon())
                    .setName(propertyData.getName())
                    .setDescription(desc)
                    .setAction(event -> new PlayerPropertyManager(player, propertyData, p -> open()))
                    .asGuiItem(player));
        }
        return guiItems;
    }
}

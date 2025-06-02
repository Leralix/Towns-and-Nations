package org.leralix.tan.gui.user;

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
import org.leralix.tan.gui.legacy.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.PlayerSelectPropertyPositionStorage;
import org.leralix.tan.utils.GuiUtil;
import org.leralix.tan.utils.TanChatUtils;

import java.util.ArrayList;
import java.util.List;

import static org.leralix.lib.data.SoundEnum.NOT_ALLOWED;

public class PlayerPropertyMenu extends IteratorGUI {

    public PlayerPropertyMenu(Player player){
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
        if(playerData.hasTown()){
            TownData townData = playerData.getTown();
            int nbProperties = townData.getProperties().size();
            int maxNbProperties = townData.getLevel().getPropertyCap();
            if(nbProperties >= maxNbProperties){
                description.add(Lang.GUI_PROPERTY_CAP_FULL.get(playerData, nbProperties, maxNbProperties));
            }
            else {
                description.add(Lang.GUI_PROPERTY_CAP.get(playerData, nbProperties, maxNbProperties));
            }
        }
        else {
            description.add(Lang.PLAYER_NO_TOWN.get());
        }


        return iconManager.get(IconKey.PLAYER_PROPERTY_ICON)
                .setName(Lang.GUI_PLAYER_NEW_PROPERTY.get(playerData))
                .setDescription(description)
                .setAction(event -> {
                    TownData playerTown = playerData.getTown();
                    if(!playerTown.doesPlayerHavePermission(playerData, RolePermission.CREATE_PROPERTY)){
                        player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get(playerData));
                        SoundUtil.playSound(player, NOT_ALLOWED);
                        return;
                    }

                    if(playerTown.getPropertyDataMap().size() >= playerTown.getLevel().getPropertyCap()){
                        player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_PROPERTY_CAP_REACHED.get(playerData));
                        return;
                    }

                    if(PlayerSelectPropertyPositionStorage.contains(playerData)){
                        player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_ALREADY_IN_SCOPE.get(playerData));
                        return;
                    }
                    player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_RIGHT_CLICK_2_POINTS_TO_CREATE_PROPERTY.get(playerData));
                    player.sendMessage(TanChatUtils.getTANString() + Lang.WRITE_CANCEL_TO_CANCEL.get(playerData, Lang.CANCEL_WORD.get(playerData)));
                    PlayerSelectPropertyPositionStorage.addPlayer(playerData);
                    player.closeInventory();
                })
                .asGuiItem(player);
    }

    private List<GuiItem> getProperties() {
        List<GuiItem> guiItems = new ArrayList<>();
        for (PropertyData propertyData : playerData.getProperties()){
            ItemStack property = propertyData.getIcon(playerData.getLang());
            GuiItem propertyGui = ItemBuilder.from(property).asGuiItem(event -> new PlayerPropertyManager(player, propertyData, p -> open()));
            guiItems.add(propertyGui);
        }
        return guiItems;
    }
}

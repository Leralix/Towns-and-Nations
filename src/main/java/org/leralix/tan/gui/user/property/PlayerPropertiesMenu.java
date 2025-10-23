package org.leralix.tan.gui.user.property;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.dataclass.PropertyData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.service.requirements.IndividualRequirement;
import org.leralix.tan.gui.service.requirements.RankPermissionRequirement;
import org.leralix.tan.gui.user.player.PlayerMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.interact.RightClickListener;
import org.leralix.tan.listeners.interact.events.property.CreatePlayerPropertyEvent;
import org.leralix.tan.upgrade.rewards.numeric.PropertyCap;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.List;

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
        List<IndividualRequirement> requirements = new ArrayList<>();
        if(tanPlayer.hasTown()){
            TownData townData = tanPlayer.getTown();

            double costPerBlock = townData.getTaxOnCreatingProperty();

            if(costPerBlock > 0) {
                description.add(Lang.GUI_PROPERTY_COST_PER_BLOCK.get(langType, Double.toString(costPerBlock)));
            }
            requirements.add(townData.getNewLevel().getStat(PropertyCap.class).getRequirement(townData));
            requirements.add(new RankPermissionRequirement(townData, tanPlayer, RolePermission.CREATE_PROPERTY));
        }
        else {
            description.add(Lang.PLAYER_NO_TOWN.get(langType));
        }



        return iconManager.get(IconKey.CREATE_NEW_PROPERTY_ICON)
                .setName(Lang.GUI_PLAYER_NEW_PROPERTY.get(tanPlayer))
                .setDescription(description)
                .setRequirements(requirements)
                .setAction(event -> {
                    if(!tanPlayer.hasTown()){
                        TanChatUtils.message(player, Lang.PLAYER_NO_TOWN.get(tanPlayer), SoundEnum.NOT_ALLOWED);
                        return;
                    }

                    TanChatUtils.message(player, Lang.PLAYER_RIGHT_CLICK_2_POINTS_TO_CREATE_PROPERTY.get(tanPlayer));
                    RightClickListener.register(player, new CreatePlayerPropertyEvent(player));
                    player.closeInventory();
                })
                .asGuiItem(player);
    }

    private List<GuiItem> getProperties() {
        List<GuiItem> guiItems = new ArrayList<>();
        for (PropertyData propertyData : tanPlayer.getProperties()){


            guiItems.add(
                    iconManager.get(propertyData.getIcon())
                            .setName(propertyData.getName())
                            .setDescription(propertyData.getBasicDescription(tanPlayer.getLang()))
                            .setAction(event -> new PlayerPropertyManager(player, propertyData, p -> open()))
                            .asGuiItem(player)
            );
        }
        return guiItems;
    }
}

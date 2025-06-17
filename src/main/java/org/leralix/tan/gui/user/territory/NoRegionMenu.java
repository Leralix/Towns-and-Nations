package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.enums.BrowseScope;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.user.MainMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.listeners.chat.events.CreateRegion;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.GuiUtil;
import org.leralix.tan.utils.TanChatUtils;

import static org.leralix.lib.data.SoundEnum.NOT_ALLOWED;

public class NoRegionMenu extends BasicGui {


    public NoRegionMenu(Player player){
        super(player, Lang.HEADER_NO_REGION, 3);
        open();
    }

    @Override
    public void open() {

        gui.setItem(2, 3, getCreateRegionButton());
        gui.setItem(2, 7, getBrowseRegionsButton());
        gui.setItem(3, 1, GuiUtil.createBackArrow(player, p -> new MainMenu(player).open()));

        gui.open(player);
    }

    private GuiItem getCreateRegionButton() {

        int regionCost = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("regionCost");

        return iconManager.get(IconKey.CREATE_REGION_ICON)
                .setName(Lang.GUI_REGION_CREATE.get(playerData))
                .setDescription(
                        Lang.GUI_REGION_CREATE_DESC1.get(playerData, regionCost),
                        Lang.GUI_REGION_CREATE_DESC2.get(playerData)
                )
                .setAction(action -> {
                    if(!player.hasPermission("tan.base.region.create")){
                        player.sendMessage(Lang.PLAYER_NO_PERMISSION.get(playerData));
                        SoundUtil.playSound(player, NOT_ALLOWED);
                        return;
                    }

                    if(!playerData.hasTown()){
                        player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_TOWN.get(playerData));
                        return;
                    }
                    double townMoney = TownDataStorage.getInstance().get(player).getBalance();
                    if (townMoney < regionCost) {
                        player.sendMessage(TanChatUtils.getTANString() + Lang.TOWN_NOT_ENOUGH_MONEY_EXTENDED.get(playerData, regionCost - townMoney));
                    }
                    else {
                        player.sendMessage(TanChatUtils.getTANString() + Lang.WRITE_IN_CHAT_NEW_REGION_NAME.get(playerData));
                        player.closeInventory();
                        int cost = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("regionCost");
                        PlayerChatListenerStorage.register(player, new CreateRegion(cost));
                    }
                })
                .asGuiItem(player);
    }

    private GuiItem getBrowseRegionsButton() {
        return iconManager.get(IconKey.BROWSE_REGION_ICON)
                .setName(Lang.GUI_REGION_BROWSE.get(playerData))
                .setDescription(
                        Lang.GUI_REGION_BROWSE_DESC1.get(playerData, RegionDataStorage.getInstance().getNumberOfRegion()),
                        Lang.GUI_REGION_BROWSE_DESC2.get(playerData)
                )
                .setAction(action -> {
                    new BrowseTerritoryMenu(player, null, BrowseScope.REGIONS, p -> open());
                })
                .asGuiItem(player);

    }


}

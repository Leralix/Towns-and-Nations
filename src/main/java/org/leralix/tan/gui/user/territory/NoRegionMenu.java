package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.enums.BrowseScope;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.user.MainMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.listeners.chat.events.CreateRegion;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.deprecated.GuiUtil;

import static org.leralix.lib.data.SoundEnum.NOT_ALLOWED;

public class NoRegionMenu extends BasicGui {


    public NoRegionMenu(Player player) {
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

        int regionCost = Constants.getRegionCost();

        return iconManager.get(IconKey.CREATE_REGION_ICON).setName(Lang.GUI_REGION_CREATE.get(tanPlayer)).setDescription(Lang.GUI_REGION_CREATE_DESC1.get(tanPlayer, Integer.toString(regionCost)), Lang.GUI_REGION_CREATE_DESC2.get(tanPlayer)).setAction(action -> {
            if (!player.hasPermission("tan.base.region.create")) {
                player.sendMessage(Lang.PLAYER_NO_PERMISSION.get(tanPlayer));
                SoundUtil.playSound(player, NOT_ALLOWED);
                return;
            }

            if (!tanPlayer.hasTown()) {
                player.sendMessage(Lang.PLAYER_NO_TOWN.get(tanPlayer));
                return;
            }
            TownData townData = TownDataStorage.getInstance().get(player);
            double townMoney = townData.getBalance();
            if (townMoney < regionCost) {
                player.sendMessage(Lang.TERRITORY_NOT_ENOUGH_MONEY.get(tanPlayer, townData.getColoredName(), Double.toString(regionCost - townMoney)));
            } else {
                player.sendMessage(Lang.WRITE_IN_CHAT_NEW_REGION_NAME.get(tanPlayer));
                PlayerChatListenerStorage.register(player, new CreateRegion(regionCost));
            }
        }).asGuiItem(player);
    }

    private GuiItem getBrowseRegionsButton() {
        return iconManager.get(IconKey.BROWSE_REGION_ICON).setName(Lang.GUI_REGION_BROWSE.get(tanPlayer)).setDescription(Lang.GUI_REGION_BROWSE_DESC1.get(tanPlayer, Integer.toString(RegionDataStorage.getInstance().getAll().size())), Lang.GUI_REGION_BROWSE_DESC2.get(tanPlayer)).setAction(action -> {
            new BrowseTerritoryMenu(player, null, BrowseScope.REGIONS, p -> open());
        }).asGuiItem(player);

    }


}

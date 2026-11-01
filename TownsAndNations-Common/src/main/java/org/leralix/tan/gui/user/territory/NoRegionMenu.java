package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.territory.Town;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.scope.BrowseScope;
import org.leralix.tan.gui.user.MainMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.listeners.chat.events.CreateRegion;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.text.TanChatUtils;

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
        gui.setItem(3, 1, createBackArrow(player, p -> new MainMenu(player), langType));

        gui.open(player);
    }

    private GuiItem getCreateRegionButton() {

        int regionCost = Constants.getRegionCost();

        return iconManager.get(IconKey.CREATE_REGION_ICON)
                .setName(Lang.GUI_REGION_CREATE.translate(tanPlayer))
                .setDescription(
                        Lang.GUI_REGION_CREATE_DESC1.get(Integer.toString(regionCost)),
                        Lang.GUI_REGION_CREATE_DESC2.get())
                .setAction(action -> {
                    if (!player.hasPermission("tan.base.region.create")) {
                        TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.translate(tanPlayer), NOT_ALLOWED);
                        return;
                    }

                    if (!tanPlayer.hasTown()) {
                        TanChatUtils.message(player, Lang.PLAYER_NO_TOWN.translate(tanPlayer), NOT_ALLOWED);
                        return;
                    }
                    Town townData = tanPlayer.getTown();
                    double townMoney = townData.getBalance();
                    if (townMoney < regionCost) {
                        TanChatUtils.message(tanPlayer, Lang.TERRITORY_NOT_ENOUGH_MONEY.get(townData.getColoredName(), Double.toString(regionCost - townMoney)));
                    } else {
                        TanChatUtils.message(player, Lang.WRITE_IN_CHAT_NEW_REGION_NAME.translate(tanPlayer));
                        PlayerChatListenerStorage.register(player, langType, new CreateRegion(regionCost));
                    }
                }).asGuiItem(player, langType);
    }

    private GuiItem getBrowseRegionsButton() {
        return iconManager.get(IconKey.BROWSE_REGION_ICON)
                .setName(Lang.GUI_REGION_BROWSE.translate(tanPlayer))
                .setDescription(
                        Lang.GUI_REGION_BROWSE_DESC1.get(Integer.toString(TownsAndNations.getPlugin().getRegionStorage().getAll().size())),
                        Lang.GUI_REGION_BROWSE_DESC2.get())
                .setAction(action -> new BrowseTerritoryMenu(player, null, BrowseScope.REGIONS, p -> open()))
                .asGuiItem(player, langType);

    }


}

package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.data.territory.RegionData;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.scope.BrowseScope;
import org.leralix.tan.gui.user.MainMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.listeners.chat.events.CreateNation;
import org.leralix.tan.storage.stored.NationDataStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.text.TanChatUtils;

import static org.leralix.lib.data.SoundEnum.NOT_ALLOWED;

public class NoNationMenu extends BasicGui {

    public NoNationMenu(Player player) {
        super(player, Lang.HEADER_NO_NATION, 3);
        open();
    }

    @Override
    public void open() {
        gui.setItem(2, 3, getCreateNationButton());
        gui.setItem(2, 7, getBrowseNationsButton());
        gui.setItem(3, 1, GuiUtil.createBackArrow(player, p -> new MainMenu(player).open()));
        gui.open(player);
    }

    private GuiItem getCreateNationButton() {
        int nationCost = Constants.getNationCost();

        return iconManager.get(IconKey.CREATE_NATION_ICON)
                .setName(Lang.GUI_NATION_CREATE.get(tanPlayer))
                .setDescription(
                        Lang.GUI_NATION_CREATE_DESC1.get(Integer.toString(nationCost)),
                        Lang.GUI_NATION_CREATE_DESC2.get()
                )
                .setAction(action -> {
                    if (!player.hasPermission("tan.base.nation.create")) {
                        TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(tanPlayer), NOT_ALLOWED);
                        return;
                    }

                    if (!tanPlayer.hasRegion()) {
                        TanChatUtils.message(player, Lang.PLAYER_NO_REGION.get(tanPlayer), NOT_ALLOWED);
                        return;
                    }

                    RegionData regionData = tanPlayer.getRegion();
                    if (!regionData.isLeader(tanPlayer)) {
                        TanChatUtils.message(player, Lang.PLAYER_ONLY_LEADER_CAN_PERFORM_ACTION.get(tanPlayer), NOT_ALLOWED);
                        return;
                    }

                    if (regionData.haveOverlord()) {
                        TanChatUtils.message(player, Lang.TOWN_ALREADY_HAVE_OVERLORD.get(tanPlayer), NOT_ALLOWED);
                        return;
                    }

                    double regionMoney = regionData.getBalance();
                    if (regionMoney < nationCost) {
                        TanChatUtils.message(player, Lang.TERRITORY_NOT_ENOUGH_MONEY.get(tanPlayer, regionData.getColoredName(), Double.toString(nationCost - regionMoney)));
                        return;
                    }

                    TanChatUtils.message(player, Lang.WRITE_IN_CHAT_NEW_NATION_NAME.get(tanPlayer));
                    PlayerChatListenerStorage.register(player, new CreateNation(nationCost));
                })
                .asGuiItem(player, langType);
    }

    private GuiItem getBrowseNationsButton() {
        return iconManager.get(IconKey.BROWSE_NATION_ICON)
                .setName(Lang.GUI_NATION_BROWSE.get(tanPlayer))
                .setDescription(
                        Lang.GUI_NATION_BROWSE_DESC1.get(Integer.toString(NationDataStorage.getInstance().getAll().size())),
                        Lang.GUI_NATION_BROWSE_DESC2.get()
                )
                .setAction(action -> new BrowseTerritoryMenu(player, null, BrowseScope.NATIONS, p -> open()))
                .asGuiItem(player, langType);
    }
}

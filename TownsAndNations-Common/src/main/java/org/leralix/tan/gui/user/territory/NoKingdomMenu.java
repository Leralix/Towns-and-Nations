package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.enums.BrowseScope;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.user.MainMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.listeners.chat.events.CreateKingdom;
import org.leralix.tan.storage.stored.KingdomDataStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.text.TanChatUtils;

import static org.leralix.lib.data.SoundEnum.NOT_ALLOWED;

public class NoKingdomMenu extends BasicGui {

    public NoKingdomMenu(Player player) {
        super(player, Lang.HEADER_NO_KINGDOM, 3);
        open();
    }

    @Override
    public void open() {
        gui.setItem(2, 3, getCreateKingdomButton());
        gui.setItem(2, 7, getBrowseKingdomsButton());
        gui.setItem(3, 1, GuiUtil.createBackArrow(player, p -> new MainMenu(player).open()));
        gui.open(player);
    }

    private GuiItem getCreateKingdomButton() {
        int kingdomCost = Constants.getKingdomCost();

        return iconManager.get(IconKey.CREATE_KINGDOM_ICON)
                .setName(Lang.GUI_KINGDOM_CREATE.get(tanPlayer))
                .setDescription(
                        Lang.GUI_KINGDOM_CREATE_DESC1.get(Integer.toString(kingdomCost)),
                        Lang.GUI_KINGDOM_CREATE_DESC2.get()
                )
                .setAction(action -> {
                    if (!player.hasPermission("tan.base.kingdom.create")) {
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
                    if (regionMoney < kingdomCost) {
                        TanChatUtils.message(player, Lang.TERRITORY_NOT_ENOUGH_MONEY.get(tanPlayer, regionData.getColoredName(), Double.toString(kingdomCost - regionMoney)));
                        return;
                    }

                    TanChatUtils.message(player, Lang.WRITE_IN_CHAT_NEW_KINGDOM_NAME.get(tanPlayer));
                    PlayerChatListenerStorage.register(player, new CreateKingdom(kingdomCost));
                })
                .asGuiItem(player, langType);
    }

    private GuiItem getBrowseKingdomsButton() {
        return iconManager.get(IconKey.BROWSE_KINGDOM_ICON)
                .setName(Lang.GUI_KINGDOM_BROWSE.get(tanPlayer))
                .setDescription(
                        Lang.GUI_KINGDOM_BROWSE_DESC1.get(Integer.toString(KingdomDataStorage.getInstance().getAll().size())),
                        Lang.GUI_KINGDOM_BROWSE_DESC2.get()
                )
                .setAction(action -> new BrowseTerritoryMenu(player, null, BrowseScope.KINGDOMS, p -> open()))
                .asGuiItem(player, langType);
    }
}

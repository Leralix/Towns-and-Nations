package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.NationData;
import org.leralix.tan.data.territory.RegionData;
import org.leralix.tan.events.EventManager;
import org.leralix.tan.events.events.NationCreatedInternalEvent;
import org.leralix.tan.gui.common.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.ChatListenerEvent;
import org.leralix.tan.storage.stored.NationDataStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.file.FileUtil;
import org.leralix.tan.utils.text.NameFilter;
import org.leralix.tan.utils.text.TanChatUtils;

public class CreateNation extends ChatListenerEvent {

    private final int cost;

    public CreateNation(int cost) {
        super();
        this.cost = cost;
    }

    @Override
    public boolean execute(Player player, ITanPlayer tanPlayer, String message) {

        if (!tanPlayer.hasRegion()) {
            TanChatUtils.message(player, Lang.PLAYER_NO_REGION.get(tanPlayer));
            return false;
        }

        RegionData regionData = tanPlayer.getRegion();

        if (!regionData.isLeader(tanPlayer)) {
            TanChatUtils.message(player, Lang.PLAYER_ONLY_LEADER_CAN_PERFORM_ACTION.get(tanPlayer));
            return false;
        }

        if (regionData.getBalance() < cost) {
            TanChatUtils.message(player, Lang.TERRITORY_NOT_ENOUGH_MONEY.get(tanPlayer, regionData.getColoredName(), Double.toString(cost - regionData.getBalance())));
            return false;
        }

        String nationName = message == null ? "" : message.trim();

        if (!NameFilter.validateOrWarn(player, nationName, NameFilter.Scope.NATION)) {
            return false;
        }

        int maxSize = Constants.getNationMaxNameSize();
        if (nationName.length() > maxSize) {
            TanChatUtils.message(player, Lang.MESSAGE_TOO_LONG.get(tanPlayer, Integer.toString(maxSize)));
            return false;
        }

        if (NationDataStorage.getInstance().isNameUsed(nationName)) {
            TanChatUtils.message(player, Lang.NAME_ALREADY_USED.get(tanPlayer));
            return false;
        }

        createNation(player, tanPlayer, nationName, regionData);
        return true;
    }

    private void createNation(Player player, ITanPlayer playerData, String nationName, RegionData capital) {
        capital.removeFromBalance(cost);
        NationData nation = NationDataStorage.getInstance().createNewNation(nationName, capital);

        EventManager.getInstance().callEvent(new NationCreatedInternalEvent(nation, playerData));
        FileUtil.addLineToHistory(Lang.NATION_CREATED_NEWSLETTER.get(player.getName(), nation.getName()));

        openGui(p -> PlayerGUI.dispatchPlayerNation(player, playerData), player);
    }
}

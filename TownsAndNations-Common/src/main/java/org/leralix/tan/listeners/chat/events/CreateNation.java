package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.gui.legacy.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.ChatListenerEvent;
import org.leralix.tan.storage.stored.NationDataStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.text.TanChatUtils;

public class CreateNation extends ChatListenerEvent {

    private final int cost;

    public CreateNation() {
        super();
        this.cost = Constants.getNationCost();
    }

    @Override
    public boolean execute(Player player, String message) {
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);

        if (!tanPlayer.hasRegion()) {
            TanChatUtils.message(player, Lang.PLAYER_NO_REGION.get(tanPlayer), SoundEnum.NOT_ALLOWED);
            return false;
        }

        RegionData regionData = tanPlayer.getRegion();
        if (regionData == null) {
            TanChatUtils.message(player, Lang.PLAYER_NO_REGION.get(tanPlayer), SoundEnum.NOT_ALLOWED);
            return false;
        }

        if (!regionData.isLeader(tanPlayer.getID())) {
            TanChatUtils.message(player, Lang.GUI_NEED_TO_BE_LEADER_OF_REGION.get(tanPlayer), SoundEnum.NOT_ALLOWED);
            return false;
        }

        if (regionData.haveOverlord()) {
            TanChatUtils.message(player, Lang.TOWN_ALREADY_HAVE_OVERLORD.get(tanPlayer), SoundEnum.NOT_ALLOWED);
            return false;
        }

        if (NationDataStorage.getInstance().isNameUsed(message)) {
            TanChatUtils.message(player, Lang.NAME_ALREADY_USED.get(tanPlayer), SoundEnum.NOT_ALLOWED);
            return false;
        }

        if (message.length() > Constants.getNationMaxNameSize()) {
            TanChatUtils.message(player, Lang.MESSAGE_TOO_LONG.get(tanPlayer, Integer.toString(Constants.getNationMaxNameSize())), SoundEnum.NOT_ALLOWED);
            return false;
        }

        if (regionData.getBalance() < cost) {
            TanChatUtils.message(player, Lang.TERRITORY_NOT_ENOUGH_MONEY.get(tanPlayer, regionData.getColoredName(), Double.toString(cost - regionData.getBalance())));
            return false;
        }

        NationDataStorage.getInstance().createNewNation(message, regionData);

        TanChatUtils.message(player, Lang.REGION_CREATED_NEWSLETTER.get(message), SoundEnum.GOOD);
        openGui(p -> PlayerGUI.dispatchPlayerNation(player), player);
        return true;
    }
}

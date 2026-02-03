package org.leralix.tan.gui.admin;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.text.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class AdminBrowsePlayers extends IteratorGUI {
    
    public AdminBrowsePlayers(Player player){
        super(player, Lang.HEADER_ADMIN_PLAYER_MENU, 6);
        open();
    }
    
    @Override
    public void open() {
        iterator(getPlayers(), p -> new AdminMainMenu(player));
        gui.open(player);
    }

    private List<GuiItem> getPlayers() {
        ArrayList<GuiItem> guiItems = new ArrayList<>();
        for (ITanPlayer tanPlayer : PlayerDataStorage.getInstance().getAll().values()) {

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(tanPlayer.getID());

            guiItems.add(iconManager.get(offlinePlayer)
                    .setName(offlinePlayer.getName())
                    .setDescription(Lang.GUI_YOUR_BALANCE_DESC1.get(StringUtil.formatMoney(EconomyUtil.getBalance(offlinePlayer))))
                    .setAction(action -> new AdminManagePlayer(player, tanPlayer))
                    .asGuiItem(player, langType)
            );
        }
        return guiItems;
    }
}

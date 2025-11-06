package org.leralix.tan.gui.admin;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.deprecated.HeadUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(tanPlayer.getID()));
            ItemStack playerHead = HeadUtils.getPlayerHeadInformation(offlinePlayer);

            GuiItem playerHeadGui = ItemBuilder.from(playerHead).asGuiItem(event -> {
                event.setCancelled(true);
                new AdminManagePlayer(player, tanPlayer);
            });
            guiItems.add(playerHeadGui);
        }
        return guiItems;
    }
}

package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.legacy.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.deprecated.HeadUtils;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SelectNewOwnerForTownMenu extends IteratorGUI {

    private final TownData townData;

    public SelectNewOwnerForTownMenu(Player player, TownData townData) {
        super(player, Lang.HEADER_CHANGE_OWNERSHIP.get(player), 3);
        this.townData = townData;
        open();
    }

    @Override
    public void open() {
        iterator(getCandidates(), p -> new TownSettingsMenu(player, townData));
        gui.open(player);
    }

    private List<GuiItem> getCandidates() {
        List<GuiItem> guiItems = new ArrayList<>();
        for (String playerUUID : townData.getPlayerIDList()) {

            OfflinePlayer townPlayer = Bukkit.getServer().getOfflinePlayer(UUID.fromString(playerUUID));

            ItemStack playerHead = HeadUtils.getPlayerHead(townPlayer.getName(), townPlayer,
                    Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_TO_SPECIFIC_PLAYER_DESC1.get(tanPlayer, player.getName()),
                    Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_TO_SPECIFIC_PLAYER_DESC2.get(tanPlayer));


            GuiItem playerHeadIcon = ItemBuilder.from(playerHead).asGuiItem(event -> {
                event.setCancelled(true);

                PlayerGUI.openConfirmMenu(player, Lang.GUI_CONFIRM_CHANGE_TOWN_LEADER.get(tanPlayer, townPlayer.getName()), confirm -> {

                    townData.setLeaderID(townPlayer.getUniqueId().toString());
                    player.sendMessage(Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_TO_SPECIFIC_PLAYER_SUCCESS.get(tanPlayer, townPlayer.getName()));
                    PlayerGUI.dispatchPlayerTown(player);

                    player.closeInventory();

                }, remove -> new TownSettingsMenu(player, townData));

            });
            guiItems.add(playerHeadIcon);
        }
       return guiItems;

    }
}

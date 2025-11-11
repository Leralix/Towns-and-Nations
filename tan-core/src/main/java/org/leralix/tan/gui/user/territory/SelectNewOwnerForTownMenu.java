package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.deprecated.HeadUtils;
import org.leralix.tan.utils.text.TanChatUtils;

public class SelectNewOwnerForTownMenu extends IteratorGUI {

  private final TownData townData;

  private SelectNewOwnerForTownMenu(Player player, ITanPlayer tanPlayer, TownData townData) {
    super(player, tanPlayer, Lang.HEADER_CHANGE_OWNERSHIP.get(tanPlayer.getLang()), 3);
    this.townData = townData;
  }

  public static void open(Player player, TownData townData) {
    PlayerDataStorage.getInstance()
        .get(player)
        .thenAccept(
            tanPlayer -> {
              new SelectNewOwnerForTownMenu(player, tanPlayer, townData).open();
            });
  }

  @Override
  public void open() {
    iterator(getCandidates(), p -> TownSettingsMenu.open(p, townData));
    gui.open(player);
  }

  private List<GuiItem> getCandidates() {
    List<GuiItem> guiItems = new ArrayList<>();
    for (String playerUUID : townData.getPlayerIDList()) {

      OfflinePlayer townPlayer = Bukkit.getServer().getOfflinePlayer(UUID.fromString(playerUUID));

      ItemStack playerHead =
          HeadUtils.getPlayerHead(
              townPlayer.getName(),
              townPlayer,
              Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_TO_SPECIFIC_PLAYER_DESC1.get(
                  tanPlayer, player.getName()),
              Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_TO_SPECIFIC_PLAYER_DESC2.get(tanPlayer));

      GuiItem playerHeadIcon =
          ItemBuilder.from(playerHead)
              .asGuiItem(
                  event -> {
                    event.setCancelled(true);

                    // TODO: Restore confirmation dialog after PlayerGUI migration
                    // Original: PlayerGUI.openConfirmMenu(player, confirmMsg, confirmAction,
                    // cancelAction)
                    // Temporary: Direct ownership transfer without confirmation
                    townData.setLeaderID(townPlayer.getUniqueId().toString());
                    TanChatUtils.message(
                        player,
                        Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_TO_SPECIFIC_PLAYER_SUCCESS.get(
                            tanPlayer, townPlayer.getName()));

                    // TODO: Replace with proper town menu navigation after PlayerGUI migration
                    // Original: PlayerGUI.dispatchPlayerTown(player);
                    player.closeInventory();
                  });
      guiItems.add(playerHeadIcon);
    }
    return guiItems;
  }
}

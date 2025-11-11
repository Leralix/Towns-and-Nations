package org.leralix.tan.gui.admin;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.user.player.PlayerMenu;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.deprecated.GuiUtil;

public class AdminPlayerMenu extends IteratorGUI {

  private AdminPlayerMenu(Player player, ITanPlayer tanPlayer) {
    super(player, tanPlayer, "Admin - Players List", 6);
  }

  public static void open(Player player) {
    PlayerDataStorage.getInstance()
        .get(player)
        .thenAccept(
            tanPlayer -> {
              new AdminPlayerMenu(player, tanPlayer).open();
            });
  }

  @Override
  public void open() {
    GuiUtil.createIterator(
        gui,
        getAllPlayers(),
        page,
        player,
        p -> AdminMainMenu.open(player),
        p -> nextPage(),
        p -> previousPage());

    gui.open(player);
  }

  private List<GuiItem> getAllPlayers() {
    List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());

    List<GuiItem> guiItems = new ArrayList<>();
    for (Player targetPlayer : onlinePlayers) {
      ITanPlayer tanPlayerData = PlayerDataStorage.getInstance().get(targetPlayer).join();

      String townInfo =
          tanPlayerData.hasTown()
              ? tanPlayerData.getTownSync().getColoredName()
              : "No Town";

      String regionInfo =
          tanPlayerData.hasRegion()
              ? tanPlayerData.getRegionSync().getColoredName()
              : "No Region";

      guiItems.add(
          ItemBuilder.from(Material.PLAYER_HEAD)
              .name(Component.text(targetPlayer.getName()))
              .lore(Component.text("Town: " + townInfo), Component.text("Region: " + regionInfo))
              .asGuiItem(
                  event -> {
                    PlayerMenu.open(targetPlayer);
                  }));
    }

    return guiItems;
  }
}

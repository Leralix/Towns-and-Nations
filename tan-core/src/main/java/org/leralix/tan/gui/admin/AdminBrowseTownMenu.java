package org.leralix.tan.gui.admin;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.deprecated.GuiUtil;

public class AdminBrowseTownMenu extends IteratorGUI {

  private AdminBrowseTownMenu(Player player, ITanPlayer tanPlayer) {
    super(player, tanPlayer, "Admin - Towns List", 6);
  }

  public static void open(Player player) {
    PlayerDataStorage.getInstance()
        .get(player)
        .thenAccept(
            tanPlayer -> {
              new AdminBrowseTownMenu(player, tanPlayer).open();
            });
  }

  @Override
  public void open() {
    GuiUtil.createIterator(
        gui,
        getTowns(),
        page,
        player,
        p -> AdminMainMenu.open(player),
        p -> nextPage(),
        p -> previousPage());

    gui.open(player);
  }

  private List<GuiItem> getTowns() {
    List<TownData> townList = new ArrayList<>(TownDataStorage.getInstance().getAllSync().values());

    ArrayList<GuiItem> townGuiItems = new ArrayList<>();

    for (TownData townData : townList) {
      ItemStack townIcon = townData.getIconWithInformationAndRelation(null, tanPlayer.getLang());
      GuiItem townGUI =
          ItemBuilder.from(townIcon)
              .asGuiItem(
                  event -> {
                    townData.openMainMenu(player);
                  });

      townGuiItems.add(townGUI);
    }
    return townGuiItems;
  }
}

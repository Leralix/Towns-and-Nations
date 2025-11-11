package org.leralix.tan.gui.admin;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.utils.deprecated.GuiUtil;

public class AdminBrowseRegionMenu extends IteratorGUI {

  private AdminBrowseRegionMenu(Player player, ITanPlayer tanPlayer) {
    super(player, tanPlayer, "Admin - Regions List", 6);
  }

  public static void open(Player player) {
    PlayerDataStorage.getInstance()
        .get(player)
        .thenAccept(
            tanPlayer -> {
              new AdminBrowseRegionMenu(player, tanPlayer).open();
            });
  }

  @Override
  public void open() {
    GuiUtil.createIterator(
        gui,
        getRegions(),
        page,
        player,
        p -> AdminMainMenu.open(player),
        p -> nextPage(),
        p -> previousPage());

    gui.open(player);
  }

  private List<GuiItem> getRegions() {
    List<RegionData> regionList =
        new ArrayList<>(RegionDataStorage.getInstance().getAllSync().values());

    ArrayList<GuiItem> regionGuiItems = new ArrayList<>();

    for (RegionData regionData : regionList) {
      ItemStack regionIcon =
          regionData.getIconWithInformationAndRelation(null, tanPlayer.getLang());
      GuiItem regionGUI =
          ItemBuilder.from(regionIcon)
              .asGuiItem(
                  event -> {
                    regionData.openMainMenu(player);
                  });

      regionGuiItems.add(regionGUI);
    }
    return regionGuiItems;
  }
}

package org.leralix.tan.utils.gui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import java.util.List;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.item.HeadUtils;

public class GuiUtil {

  private GuiUtil() {
    throw new IllegalStateException("Utility class");
  }

  public static Gui createChestGui(String name, int nRow) {
    return Gui.gui().title(Component.text(name)).type(GuiType.CHEST).rows(nRow).create();
  }

  public static GuiItem createBackArrow(Player player, Consumer<Player> openMenuAction) {
    ItemStack getBackArrow =
        HeadUtils.createCustomItemStack(Material.ARROW, Lang.GUI_BACK_ARROW.get(player));
    return ItemBuilder.from(getBackArrow)
        .asGuiItem(
            event -> {
              event.setCancelled(true);
              openMenuAction.accept(player);
            });
  }

  public static GuiItem getUnnamedItem(Material material) {
    ItemStack item = new ItemStack(material);
    ItemMeta itemMeta = item.getItemMeta();
    itemMeta.setDisplayName(" ");
    item.setItemMeta(itemMeta);
    return ItemBuilder.from(item).asGuiItem(event -> event.setCancelled(true));
  }

  public static void createIterator(
      Gui gui,
      List<GuiItem> guItems,
      int page,
      Player player,
      Consumer<Player> backArrowAction,
      Consumer<Player> nextPageAction,
      Consumer<Player> previousPageAction) {

    createIterator(
        gui,
        guItems,
        page,
        player,
        backArrowAction,
        nextPageAction,
        previousPageAction,
        Material.GRAY_STAINED_GLASS_PANE);
  }

  public static void createIterator(
      Gui gui,
      List<GuiItem> guItems,
      int page,
      Player player,
      Consumer<Player> backArrowAction,
      Consumer<Player> nextPageAction,
      Consumer<Player> previousPageAction,
      Material decorativeMaterial) {

    ItemStack decorativeGlassPane = new ItemStack(decorativeMaterial);
    ItemMeta itemMeta = decorativeGlassPane.getItemMeta();
    itemMeta.setDisplayName("");
    decorativeGlassPane.setItemMeta(itemMeta);
    createIterator(
        gui,
        guItems,
        page,
        player,
        backArrowAction,
        nextPageAction,
        previousPageAction,
        decorativeGlassPane);
  }

  public static void createIterator(
      Gui gui,
      List<GuiItem> guItems,
      int page,
      Player player,
      Consumer<Player> backArrowAction,
      Consumer<Player> nextPageAction,
      Consumer<Player> previousPageAction,
      ItemStack decorativeGlassPane) {

    int pageSize = (gui.getRows() - 1) * 9;
    int startIndex = page * pageSize;
    boolean lastPage;
    int totalSize = guItems.size();

    int endIndex;
    if (startIndex + pageSize > totalSize) {
      endIndex = totalSize;
      lastPage = true;
    } else {
      lastPage = false;
      endIndex = startIndex + pageSize;
    }

    for (int i = 0; i < pageSize; i++) {
      gui.removeItem(i);
    }

    int slot = 0;

    for (int i = startIndex; i < endIndex; i++) {
      gui.setItem(slot, guItems.get(i));
      slot++;
    }
    GuiItem panel =
        ItemBuilder.from(decorativeGlassPane).asGuiItem(event -> event.setCancelled(true));
    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(player);
    ItemStack previousPageButton =
        HeadUtils.makeSkullB64(
            Lang.GUI_PREVIOUS_PAGE.get(tanPlayer),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTQyZmRlOGI4MmU4YzFiOGMyMmIyMjY3OTk4M2ZlMzVjYjc2YTc5Nzc4NDI5YmRhZGFiYzM5N2ZkMTUwNjEifX19");
    ItemStack nextPageButton =
        HeadUtils.makeSkullB64(
            Lang.GUI_NEXT_PAGE.get(tanPlayer),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDA2MjYyYWYxZDVmNDE0YzU5NzA1NWMyMmUzOWNjZTE0OGU1ZWRiZWM0NTU1OWEyZDZiODhjOGQ2N2I5MmVhNiJ9fX0=");

    GuiItem previousButton =
        ItemBuilder.from(previousPageButton)
            .asGuiItem(
                event -> {
                  event.setCancelled(true);
                  if (page == 0) {
                    return;
                  }
                  previousPageAction.accept(player);
                });

    GuiItem nextButton =
        ItemBuilder.from(nextPageButton)
            .asGuiItem(
                event -> {
                  event.setCancelled(true);
                  if (lastPage) {
                    return;
                  }
                  nextPageAction.accept(player);
                });

    int lastRow = gui.getRows();

    gui.getFiller().fillBottom(panel);
    gui.setItem(lastRow, 1, GuiUtil.createBackArrow(player, backArrowAction));
    gui.setItem(lastRow, 7, previousButton);
    gui.setItem(lastRow, 8, nextButton);
  }
}

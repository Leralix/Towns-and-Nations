package org.leralix.tan.utils.gui;

import dev.triumphteam.gui.guis.GuiItem;
import java.util.List;
import java.util.function.Consumer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Bridge between legacy GuiUtil and modern AsyncGuiHelper.
 *
 * <p>This class provides wrapper methods that gradually transition code to async patterns.
 *
 * <p><b>DEPRECATION TIMELINE:</b>
 *
 * <ul>
 *   <li>Phase 1 (Now): Provide legacy API for backward compatibility
 *   <li>Phase 2 (Weeks 1-2): Encourage async patterns via documentation
 *   <li>Phase 3 (Weeks 2-3): Remove GuiUtil.java entirely, keep bridge
 *   <li>Phase 4 (Month 2): Deprecate GuiHelperBridge and remove
 * </ul>
 *
 * <p>See {@link MIGRATION_GUIUTIL_ASYNCGUIHELPER.md} for full migration plan.
 *
 * @deprecated Use {@link AsyncGuiHelper} for new code. This bridge is temporary for gradual
 *     migration of the 98+ usages of GuiUtil.
 * @since 0.16.0
 */
@Deprecated(since = "0.16.0", forRemoval = true)
public class GuiHelperBridge {

  private GuiHelperBridge() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Create a back arrow button.
   *
   * <p><b>Migration Status:</b> SAFE TO MIGRATE (no async needed)
   *
   * <p><b>Usage (OLD):</b>
   *
   * <pre>
   * GuiUtil.createBackArrow(player, p -> openMenu(p))
   * </pre>
   *
   * <p><b>Usage (NEW - SAME):</b>
   *
   * <pre>
   * GuiHelperBridge.createBackArrow(player, p -> openMenu(p))
   * </pre>
   *
   * @param player The player viewing the GUI
   * @param openMenuAction Consumer called when back arrow is clicked
   * @return GuiItem representing the back arrow button
   */
  public static GuiItem createBackArrow(Player player, Consumer<Player> openMenuAction) {
    // Delegate to legacy implementation for backward compatibility
    // TODO: Create modern replacement in AsyncGuiHelper that doesn't use deprecated GuiUtil
    return org.leralix.tan.utils.deprecated.GuiUtil.createBackArrow(player, openMenuAction);
  }

  /**
   * Create an unnamed item (usually for decoration).
   *
   * <p><b>Migration Status:</b> SAFE TO MIGRATE (no async needed)
   *
   * <p><b>Usage (OLD):</b>
   *
   * <pre>
   * GuiUtil.getUnnamedItem(Material.GRAY_STAINED_GLASS_PANE)
   * </pre>
   *
   * <p><b>Usage (NEW - SAME):</b>
   *
   * <pre>
   * GuiHelperBridge.getUnnamedItem(Material.GRAY_STAINED_GLASS_PANE)
   * </pre>
   *
   * @param material The material type for the item
   * @return GuiItem representing an unnamed item
   */
  public static GuiItem getUnnamedItem(Material material) {
    // Delegate to legacy implementation for backward compatibility
    return org.leralix.tan.utils.deprecated.GuiUtil.getUnnamedItem(material);
  }

  /**
   * Get an unnamed ItemStack (no display name).
   *
   * <p><b>Migration Status:</b> NEW HELPER - use this for internal inventory operations
   *
   * <p>Creates an ItemStack with an empty display name. Useful for decorative items in GUIs.
   *
   * @param material The material type for the item
   * @return ItemStack with empty display name
   */
  public static ItemStack getUnnamedItemStack(Material material) {
    ItemStack item = new ItemStack(material);
    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      meta.displayName(net.kyori.adventure.text.Component.text(""));
      item.setItemMeta(meta);
    }
    return item;
  }

  /**
   * Get a decorative glass pane ItemStack.
   *
   * <p>Common helper for creating separator items in GUIs.
   *
   * @param material The glass pane material (e.g., GRAY_STAINED_GLASS_PANE)
   * @return ItemStack representing the glass pane
   */
  public static ItemStack getDecorativePane(Material material) {
    return getUnnamedItemStack(material);
  }

  /**
   * Create pagination for GUI with items.
   *
   * <p><b>Migration Status:</b> COMPLEX - requires async refactoring
   *
   * <p>This method is more complex to migrate because it calls {@code PlayerDataStorage.getSync()}.
   * When migrating, consider using {@link AsyncGuiHelper#loadAsync(Player, Supplier, Consumer)} to
   * load player data asynchronously.
   *
   * <p>See {@link MIGRATION_GUIUTIL_ASYNCGUIHELPER.md} Phase 2 for migration example.
   *
   * @param gui The GUI to add pagination to
   * @param guItems The items to paginate
   * @param page The current page number
   * @param player The player viewing the GUI
   * @param backArrowAction Consumer called when back arrow is clicked
   * @param nextPageAction Consumer called when next page button is clicked
   * @param previousPageAction Consumer called when previous page button is clicked
   * @deprecated This method calls blocking getSync(). Use {@link AsyncGuiHelper#loadAsync(Player,
   *     Supplier, Consumer)} instead.
   */
  @Deprecated(since = "0.16.0", forRemoval = true)
  public static void createIterator(
      dev.triumphteam.gui.guis.Gui gui,
      List<GuiItem> guItems,
      int page,
      Player player,
      Consumer<Player> backArrowAction,
      Consumer<Player> nextPageAction,
      Consumer<Player> previousPageAction) {
    // Delegate to legacy implementation
    org.leralix.tan.utils.deprecated.GuiUtil.createIterator(
        gui, guItems, page, player, backArrowAction, nextPageAction, previousPageAction);
  }

  /**
   * Create pagination for GUI with items (with custom decoration material).
   *
   * @param gui The GUI to add pagination to
   * @param guItems The items to paginate
   * @param page The current page number
   * @param player The player viewing the GUI
   * @param backArrowAction Consumer called when back arrow is clicked
   * @param nextPageAction Consumer called when next page button is clicked
   * @param previousPageAction Consumer called when previous page button is clicked
   * @param decorativeMaterial The material to use for decoration
   * @deprecated This method calls blocking getSync(). Use {@link AsyncGuiHelper#loadAsync(Player,
   *     Supplier, Consumer)} instead.
   */
  @Deprecated(since = "0.16.0", forRemoval = true)
  public static void createIterator(
      dev.triumphteam.gui.guis.Gui gui,
      List<GuiItem> guItems,
      int page,
      Player player,
      Consumer<Player> backArrowAction,
      Consumer<Player> nextPageAction,
      Consumer<Player> previousPageAction,
      Material decorativeMaterial) {
    // Delegate to legacy implementation
    org.leralix.tan.utils.deprecated.GuiUtil.createIterator(
        gui,
        guItems,
        page,
        player,
        backArrowAction,
        nextPageAction,
        previousPageAction,
        decorativeMaterial);
  }

  /**
   * Create pagination for GUI with items (with custom decoration ItemStack).
   *
   * @param gui The GUI to add pagination to
   * @param guItems The items to paginate
   * @param page The current page number
   * @param player The player viewing the GUI
   * @param backArrowAction Consumer called when back arrow is clicked
   * @param nextPageAction Consumer called when next page button is clicked
   * @param previousPageAction Consumer called when previous page button is clicked
   * @param decorativeGlassPane The custom decoration ItemStack
   * @deprecated This method calls blocking getSync(). Use {@link AsyncGuiHelper#loadAsync(Player,
   *     Supplier, Consumer)} instead.
   */
  @Deprecated(since = "0.16.0", forRemoval = true)
  public static void createIterator(
      dev.triumphteam.gui.guis.Gui gui,
      List<GuiItem> guItems,
      int page,
      Player player,
      Consumer<Player> backArrowAction,
      Consumer<Player> nextPageAction,
      Consumer<Player> previousPageAction,
      ItemStack decorativeGlassPane) {
    // Delegate to legacy implementation
    org.leralix.tan.utils.deprecated.GuiUtil.createIterator(
        gui,
        guItems,
        page,
        player,
        backArrowAction,
        nextPageAction,
        previousPageAction,
        decorativeGlassPane);
  }
}

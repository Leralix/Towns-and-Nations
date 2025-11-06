package org.leralix.tan.utils.text;

import java.util.List;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.inventory.meta.ItemMeta;

/** Utility class for working with Adventure Components and legacy text. */
public class ComponentUtil {

  private static final LegacyComponentSerializer SECTION_SERIALIZER =
      LegacyComponentSerializer.legacySection();

  /**
   * Converts a legacy formatted string (with § or & color codes) to a Component.
   *
   * @param legacyText The legacy text with color codes
   * @return A Component with the colors parsed
   */
  public static Component fromLegacy(String legacyText) {
    if (legacyText == null) {
      return Component.empty();
    }
    return SECTION_SERIALIZER.deserialize(legacyText);
  }

  /**
   * Converts a list of legacy formatted strings to Components.
   *
   * @param legacyTexts The legacy texts with color codes
   * @return A list of Components
   */
  public static List<Component> fromLegacy(List<String> legacyTexts) {
    if (legacyTexts == null) {
      return List.of();
    }
    return legacyTexts.stream().map(ComponentUtil::fromLegacy).collect(Collectors.toList());
  }

  /**
   * Converts a Component back to legacy format.
   *
   * @param component The component to convert
   * @return A legacy formatted string
   */
  public static String toLegacy(Component component) {
    if (component == null) {
      return "";
    }
    return SECTION_SERIALIZER.serialize(component);
  }

  /**
   * Sets the display name of an ItemMeta using a legacy formatted string. This method properly
   * converts the legacy string to a Component.
   *
   * @param meta The ItemMeta to modify
   * @param legacyDisplayName The display name with legacy color codes
   */
  public static void setDisplayName(ItemMeta meta, String legacyDisplayName) {
    if (meta != null && legacyDisplayName != null) {
      meta.displayName(fromLegacy(legacyDisplayName));
    }
  }

  /**
   * Sets the lore of an ItemMeta using legacy formatted strings. This method properly converts the
   * legacy strings to Components.
   *
   * @param meta The ItemMeta to modify
   * @param legacyLore The lore lines with legacy color codes
   */
  public static void setLore(ItemMeta meta, List<String> legacyLore) {
    if (meta != null && legacyLore != null) {
      meta.lore(fromLegacy(legacyLore));
    }
  }

  /**
   * Converts a TextColor to legacy ChatColor. This is useful for APIs that still require ChatColor
   * (like scoreboard teams). Returns the closest ChatColor match.
   *
   * @param textColor The TextColor to convert
   * @return The corresponding ChatColor, or WHITE if no match found
   */
  @SuppressWarnings("deprecation")
  public static ChatColor toLegacyChatColor(TextColor textColor) {
    if (textColor == null) {
      return ChatColor.WHITE;
    }

    // Handle NamedTextColor cases
    if (textColor.equals(NamedTextColor.BLACK)) return ChatColor.BLACK;
    if (textColor.equals(NamedTextColor.DARK_BLUE)) return ChatColor.DARK_BLUE;
    if (textColor.equals(NamedTextColor.DARK_GREEN)) return ChatColor.DARK_GREEN;
    if (textColor.equals(NamedTextColor.DARK_AQUA)) return ChatColor.DARK_AQUA;
    if (textColor.equals(NamedTextColor.DARK_RED)) return ChatColor.DARK_RED;
    if (textColor.equals(NamedTextColor.DARK_PURPLE)) return ChatColor.DARK_PURPLE;
    if (textColor.equals(NamedTextColor.GOLD)) return ChatColor.GOLD;
    if (textColor.equals(NamedTextColor.GRAY)) return ChatColor.GRAY;
    if (textColor.equals(NamedTextColor.DARK_GRAY)) return ChatColor.DARK_GRAY;
    if (textColor.equals(NamedTextColor.BLUE)) return ChatColor.BLUE;
    if (textColor.equals(NamedTextColor.GREEN)) return ChatColor.GREEN;
    if (textColor.equals(NamedTextColor.AQUA)) return ChatColor.AQUA;
    if (textColor.equals(NamedTextColor.RED)) return ChatColor.RED;
    if (textColor.equals(NamedTextColor.LIGHT_PURPLE)) return ChatColor.LIGHT_PURPLE;
    if (textColor.equals(NamedTextColor.YELLOW)) return ChatColor.YELLOW;
    if (textColor.equals(NamedTextColor.WHITE)) return ChatColor.WHITE;

    // Default to white if no match
    return ChatColor.WHITE;
  }
}

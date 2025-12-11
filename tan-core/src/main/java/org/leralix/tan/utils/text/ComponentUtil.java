package org.leralix.tan.utils.text;

import java.util.List;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.inventory.meta.ItemMeta;

public class ComponentUtil {

  private static final LegacyComponentSerializer SECTION_SERIALIZER =
      LegacyComponentSerializer.legacySection();

  public static Component fromLegacy(String legacyText) {
    if (legacyText == null) {
      return Component.empty();
    }
    return SECTION_SERIALIZER.deserialize(legacyText);
  }

  public static List<Component> fromLegacy(List<String> legacyTexts) {
    if (legacyTexts == null) {
      return List.of();
    }
    return legacyTexts.stream().map(ComponentUtil::fromLegacy).collect(Collectors.toList());
  }

  public static String toLegacy(Component component) {
    if (component == null) {
      return "";
    }
    return SECTION_SERIALIZER.serialize(component);
  }

  public static void setDisplayName(ItemMeta meta, String legacyDisplayName) {
    if (meta != null && legacyDisplayName != null) {
      meta.displayName(fromLegacy(legacyDisplayName));
    }
  }

  public static void setLore(ItemMeta meta, List<String> legacyLore) {
    if (meta != null && legacyLore != null) {
      meta.lore(fromLegacy(legacyLore));
    }
  }

  @SuppressWarnings("deprecation")
  public static ChatColor toLegacyChatColor(TextColor textColor) {
    if (textColor == null) {
      return ChatColor.WHITE;
    }

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

    return ChatColor.WHITE;
  }
}

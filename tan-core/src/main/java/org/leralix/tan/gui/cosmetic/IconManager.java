package org.leralix.tan.gui.cosmetic;

import java.io.File;
import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.gui.cosmetic.type.*;

public class IconManager {

  private static IconManager instance;

  Map<IconKey, IconType> iconMap;

  private IconManager() {
    this.iconMap = new EnumMap<>(IconKey.class);

    Plugin plugin = TownsAndNations.getPlugin();

    ConfigUtil.saveAndUpdateResource(plugin, "menu/icons.yml");
    YamlConfiguration config =
        YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "menu/icons.yml"));

    for (String key : config.getKeys(false)) {
      try {
        IconKey iconKey = IconKey.valueOf(key);
        String value = config.getString(key);

        IconType menuIcon = chooseIconBuilderType(value);

        iconMap.put(iconKey, menuIcon);
      } catch (IllegalArgumentException e) {
        plugin.getLogger().warning("Unkown key : " + key);
      }
    }
  }

  public static IconManager getInstance() {
    if (instance == null) {
      instance = new IconManager();
    }
    return instance;
  }

  IconType chooseIconBuilderType(String value) {

    if (value.startsWith("http")) {
      return new UrlHeadIconType(value);
    }
    if (value.startsWith("minecraft:")) {

      String[] args = value.split(":");

      if (args.length <= 1) {
        TownsAndNations.getPlugin()
            .getLogger()
            .log(Level.WARNING, "Invalid name for item : {0}", value);
        return new ItemIconBuilder(Material.BARRIER);
      }

      Material iconMaterial;

      try {
        iconMaterial = Material.valueOf(args[1]);
      } catch (IllegalArgumentException e) {
        TownsAndNations.getPlugin()
            .getLogger()
            .log(Level.WARNING, "Invalid material in config : {0}", args[1]);
        iconMaterial = Material.BARRIER;
      }

      if (args.length >= 3) {
        int customModelData;
        try {
          customModelData = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
          TownsAndNations.getPlugin()
              .getLogger()
              .log(Level.WARNING, "Invalid custom model data for menu icon : {0}", args[2]);
          customModelData = 0;
        }
        return new CustomMaterialIcon(iconMaterial, customModelData);
      } else {
        return new ItemIconBuilder(iconMaterial);
      }
    }
    return switch (value) {
      case "PLAYER_HEAD" -> new PlayerHeadIconType();
      case "TOWN_HEAD" -> new TownIconType();
      case "PLAYER_LANGUAGE_HEAD" -> new PlayerLanguageIconType();
      case "NO_ICON" -> new NoIconType();
      default -> new UrlHeadIconType("");
    };
  }

  public IconBuilder get(IconKey key) {
    return new IconBuilder(iconMap.get(key));
  }

  public IconBuilder get(ItemStack icon) {
    return new IconBuilder(new ItemIconType(icon));
  }

  public IconBuilder get(Material material) {
    return get(new ItemStack(material));
  }
}

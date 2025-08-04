package org.leralix.tan.gui.cosmetic;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.gui.cosmetic.type.*;

import java.io.File;
import java.util.EnumMap;
import java.util.Map;

public class IconManager {

    private static IconManager instance;

    Map<IconKey, IconType> iconMap;

    private IconManager(){
        this.iconMap = new EnumMap<>(IconKey.class);

        Plugin plugin = TownsAndNations.getPlugin();

        ConfigUtil.saveAndUpdateResource(plugin, "menu/icons.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "menu/icons.yml"));


        for (String key : config.getKeys(false)) {
            try {
                IconKey iconKey = IconKey.valueOf(key);
                String value = config.getString(key);

                IconType iconType = chooseIconBuilderType(value);

                iconMap.put(iconKey, iconType);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Unknown key : " + key);
            }
        }
    }

    public static IconManager getInstance(){
        if(instance == null){
            instance = new IconManager();
        }
        return instance;
    }


    private IconType chooseIconBuilderType(String value) {

        if(value.startsWith("http")){
            return new UrlHeadIconType(value);
        }
        if(value.startsWith("minecraft:")){
            try {
                return new ItemIconBuillder(Material.valueOf(value.replace("minecraft:", "")));
            } catch (IllegalArgumentException e) {
                TownsAndNations.getPlugin().getLogger().warning("Invalid material in config: " + value);
                return new ItemIconBuillder(Material.BARRIER); // Default fallback material
            }
        }
        return switch (value) {
            case "PLAYER_HEAD" -> new PlayerHeadIconType();
            case "TOWN_HEAD" -> new TownIconType();
            case "PLAYER_LANGUAGE_HEAD" -> new PlayerLanguageIconType();
            default -> new UrlHeadIconType("");
        };
    }


    public IconBuilder get(IconKey key){
        return new IconBuilder(iconMap.get(key));
    }

    public IconBuilder get(ItemStack icon){
        return new IconBuilder(new ItemIconType(icon));
    }

}

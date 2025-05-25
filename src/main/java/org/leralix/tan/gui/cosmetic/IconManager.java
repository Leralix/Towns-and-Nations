package org.leralix.tan.gui.cosmetic;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.gui.cosmetic.type.*;

import java.io.File;
import java.util.EnumMap;
import java.util.Map;

public class IconManager {

    private static IconManager instance;

    Map<IconKey, IconBuilder> iconMap;

    private IconManager(){
        this.iconMap = new EnumMap<>(IconKey.class);

        Plugin plugin = TownsAndNations.getPlugin();

        ConfigUtil.saveAndUpdateResource(plugin, "menu/icons.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "menu/icons.yml"));


        for (String key : config.getKeys(false)) {
            try {
                IconKey iconKey = IconKey.valueOf(key);
                String value = config.getString(key);

                IconBuilder iconBuilder = chooseIconBuilderType(value);

                iconMap.put(iconKey, iconBuilder);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Clé inconnue dans le fichier config.yml : " + key);
            }
        }
    }

    public static IconManager getInstance(){
        if(instance == null){
            instance = new IconManager();
        }
        return instance;
    }


    private IconBuilder chooseIconBuilderType(String value) {

        if(value.startsWith("http")){
            return new UrlHeadIconBuilder(value);
        }
        if(value.equals("PLAYER_HEAD")){
            return new PlayerHeadIconBuilder();
        }
        if(value.startsWith("minecraft:")){
            try {
                return new ItemIconBuillder(Material.valueOf(value.replace("minecraft:", "")));
            } catch (IllegalArgumentException e) {
                TownsAndNations.getPlugin().getLogger().warning("Invalid material in config: " + value);
                return new ItemIconBuillder(Material.BARRIER); // Default fallback material
            }
        }
        if(value.equals("PLAYER_LANGUAGE_HEAD")){
            return new PlayerLanguageIconBuilder();
        }
        return new UrlHeadIconBuilder(""); //Malformed url will display default head
    }


    public IconBuilder get(IconKey key){
        return iconMap.get(key);
    }

}

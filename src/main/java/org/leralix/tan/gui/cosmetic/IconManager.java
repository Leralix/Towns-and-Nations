package org.leralix.tan.gui.cosmetic;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.gui.cosmetic.type.PlayerHeadIconBuilder;
import org.leralix.tan.gui.cosmetic.type.UrlHeadIconBuilder;

import java.io.File;
import java.util.EnumMap;
import java.util.Map;

public class IconManager {

    private static IconManager instance;

    Map<IconKey, IconBuilder> iconMap;


    private IconManager(){
        this.iconMap = new EnumMap<>(IconKey.class);

        Plugin plugin = TownsAndNations.getPlugin();

        ConfigUtil.saveAndUpdateResource(plugin, "heads.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "heads.yml"));


        for (String key : config.getKeys(false)) {
            try {
                IconKey iconKey = IconKey.valueOf(key);
                String value = config.getString(key);

                IconBuilder iconBuilder = chooseIconBuilderType(value);

                iconMap.put(iconKey, value);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Clé inconnue dans le fichier config.yml : " + key);
            }
        }

    }

    private IconBuilder chooseIconBuilderType(String value) {

        if(value.startsWith("http")){
            return new UrlHeadIconBuilder(value);
        }
        if(value.equals("PLAYER_HEAD")){
            return new PlayerHeadIconBuilder();
        }
        return new UrlHeadIconBuilder(""); //Malformed url will display default head
    }


    public static IconManager getInstance(){
        if(instance == null){
            instance = new IconManager();
        }
        return instance;
    }

}

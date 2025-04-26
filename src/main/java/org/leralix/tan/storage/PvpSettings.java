package org.leralix.tan.storage;

import org.bukkit.configuration.ConfigurationSection;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.enums.TownRelation;

import java.util.EnumMap;
import java.util.Map;

public class PvpSettings {


    private static Map<TownRelation, Boolean> authorisationList;

    public static void init() {
        authorisationList = new EnumMap<>(TownRelation.class);
        ConfigurationSection section = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getConfigurationSection("disablePvpWhenRelationIs");

        for(TownRelation relation : TownRelation.values()){
            Boolean authorized = section.getBoolean(relation.name());
            authorisationList.put(relation, authorized);
        }
    }

    public static boolean canPvpHappenWithRelation(TownRelation relation){
        return authorisationList.getOrDefault(relation, true);
    }
}

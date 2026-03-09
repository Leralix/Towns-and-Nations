package org.leralix.tan.utils.constants;

import org.bukkit.configuration.ConfigurationSection;
import org.leralix.tan.data.territory.permission.GeneralChunkSetting;

import java.util.HashMap;
import java.util.Map;

public class GeneralChunkSettings {


    private final Map<GeneralChunkSetting, InteractionStatus> allowAction;

    private static final String DEFAULT_VALUE = "PLAYER_CHOICE_AND_WAR";

    public GeneralChunkSettings(ConfigurationSection configurationSection){
        allowAction = new HashMap<>();

        allowAction.put(GeneralChunkSetting.FIRE_GRIEF, InteractionStatus.valueOf(configurationSection.getString("fire", DEFAULT_VALUE)));
        allowAction.put(GeneralChunkSetting.TNT_GRIEF, InteractionStatus.valueOf(configurationSection.getString("explosion", DEFAULT_VALUE)));
        allowAction.put(GeneralChunkSetting.ENABLE_PVP, InteractionStatus.valueOf(configurationSection.getString("pvp", DEFAULT_VALUE)));
        allowAction.put(GeneralChunkSetting.HOSTILE_MOB_GRIEF, InteractionStatus.valueOf(configurationSection.getString("hostile_mob", DEFAULT_VALUE)));
        allowAction.put(GeneralChunkSetting.PASSIVE_MOB_GRIEF, InteractionStatus.valueOf(configurationSection.getString("passive_mob", DEFAULT_VALUE)));
        allowAction.put(GeneralChunkSetting.VILLAGER_GRIEF, InteractionStatus.valueOf(configurationSection.getString("villager", DEFAULT_VALUE)));

    }

    public InteractionStatus getAction(GeneralChunkSetting key) {
        return allowAction.get(key);
    }
}

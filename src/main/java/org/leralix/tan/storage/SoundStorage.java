package org.leralix.tan.storage;

import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.leralix.tan.dataclass.SoundData;
import org.leralix.tan.enums.SoundEnum;
import org.leralix.tan.utils.config.ConfigTag;
import org.leralix.tan.utils.config.ConfigUtil;

import java.util.HashMap;
import java.util.List;

public class SoundStorage {

    public static final HashMap<SoundEnum, SoundData> soundMap = new HashMap<>();

    public static void init(){
        ConfigurationSection soundsSection = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getConfigurationSection("sounds");
        if (soundsSection != null) {
            for (String key : soundsSection.getKeys(false)) {
                List<String> soundValues = soundsSection.getStringList(key);

                Sound soundName = Sound.valueOf(soundValues.get(0));
                int volume = Integer.parseInt(soundValues.get(1));
                float pitch = Float.parseFloat(soundValues.get(2));

                soundMap.put(SoundEnum.valueOf(key), new SoundData(soundName, volume, pitch));

            }
        }
    }

    public static SoundData getSoundData(SoundEnum soundName){
        return soundMap.get(soundName);
    }
}

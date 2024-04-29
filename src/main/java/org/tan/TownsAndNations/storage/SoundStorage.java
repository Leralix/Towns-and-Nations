package org.tan.TownsAndNations.storage;

import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.tan.TownsAndNations.DataClass.SoundData;
import org.tan.TownsAndNations.utils.ConfigUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class SoundStorage {

    public static final HashMap<String, SoundData> soundMap = new HashMap<>();

    public static void init(){
        ConfigurationSection soundsSection = ConfigUtil.getCustomConfig("config.yml").getConfigurationSection("sounds");
        if (soundsSection != null) {
            for (String key : soundsSection.getKeys(false)) {
                List<String> soundValues = soundsSection.getStringList(key);



                Sound soundName = Sound.valueOf(soundValues.get(0));
                int volume = Integer.parseInt(soundValues.get(1));
                float pitch = Float.parseFloat(soundValues.get(2));

                soundMap.put(key, new SoundData(soundName, volume, pitch));

            }
        }
    }

    public static SoundData getSoundData(String soundName){
        return soundMap.get(soundName);
    }
}

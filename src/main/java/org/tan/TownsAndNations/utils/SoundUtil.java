package org.tan.TownsAndNations.utils;

import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.SoundData;
import org.tan.TownsAndNations.enums.SoundEnum;
import org.tan.TownsAndNations.storage.SoundStorage;

/**
 * Utility class for playing sounds
 */
public class SoundUtil {
    /**
     * Play a predefined sound to the player
     * @param player    The player to play the sound to
     * @param soundEnum The sound to play
     */
    public static void playSound(Player player, SoundEnum soundEnum){
        if(ConfigUtil.getCustomConfig("config.yml").getBoolean("enableSounds",true)){
            SoundStorage.getSoundData(soundEnum).playSound(player);
        }
    }
}

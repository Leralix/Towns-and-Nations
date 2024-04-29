package org.tan.TownsAndNations.utils;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.SoundData;
import org.tan.TownsAndNations.enums.SoundEnum;
import org.tan.TownsAndNations.storage.SoundStorage;

public class SoundUtil {

    public static void playSound(Player player, SoundEnum soundEnum){
        if(ConfigUtil.getCustomConfig("config.yml").getBoolean("enableSounds",true)){


            SoundData soundData = SoundStorage.getSoundData(soundEnum.toString());

            player.playSound(player.getLocation(), soundData.getSound(), soundData.getVolume(), soundData.getPitch());

        }

    }
}

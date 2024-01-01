package org.tan.TownsAndNations.utils;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.enums.SoundEnum;

public class SoundUtil {


    public static void playSound(Player player, Sound sound, float volume, float pitch){
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    public static void playSound(Player player, SoundEnum soundEnum){
        player.playSound(player.getLocation(), soundEnum.getSound(), soundEnum.getVolume(), soundEnum.getPitch());
    }




}

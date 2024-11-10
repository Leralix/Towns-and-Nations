package org.leralix.tan.dataclass;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundData {

    private final Sound soundName;
    private final int volume;
    private final float pitch;

    public SoundData(Sound soundName, int volume, float pitch) {
        this.soundName = soundName;
        this.volume = volume;
        this.pitch = pitch;
    }

    public Sound getSound() {
        return soundName;
    }
    public void playSound(Player player){
        player.playSound(player.getLocation(), soundName, volume, pitch);
    }
}

package org.tan.TownsAndNations.enums;

import org.bukkit.Sound;

import static org.bukkit.Sound.*;

public enum SoundEnum {

    LEVEL_UP(ENTITY_PLAYER_LEVELUP, 1, 1),
    MINOR_LEVEL_UP(ENTITY_PLAYER_LEVELUP, 1, 6),
    ADD(BLOCK_NOTE_BLOCK_HAT, 1, 8),
    REMOVE(BLOCK_NOTE_BLOCK_HAT, 1, 6),
    NOT_ALLOWED(BLOCK_NOTE_BLOCK_HAT, 1, 2),
    WAR(ITEM_GOAT_HORN_SOUND_0, 1, 1),
    GOOD(ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1.7F),
    MINOR_GOOD(BLOCK_AMETHYST_BLOCK_BREAK, 1, 1),
    BAD(BLOCK_BELL_USE, 1, 1.5F),
    MINOR_BAD(ENTITY_SLIME_ATTACK, 1, 6);



    private final Sound sound;
    private final float volume;
    private final float pitch;

    SoundEnum(Sound sound, float volume, float pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    public Sound getSound() {
        return sound;
    }

    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
    }





}

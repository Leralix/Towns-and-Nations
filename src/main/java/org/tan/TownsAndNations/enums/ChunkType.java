package org.tan.TownsAndNations.enums;

import org.bukkit.Sound;

public enum ChunkType {

    TOWN("town"),
    REGION("region");

    private final String name;
    ChunkType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}

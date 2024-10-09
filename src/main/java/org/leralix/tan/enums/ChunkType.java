package org.leralix.tan.enums;

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

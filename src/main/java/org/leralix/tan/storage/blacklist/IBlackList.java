package org.leralix.tan.storage.blacklist;

import org.bukkit.Chunk;

public interface IBlackList {

    public boolean isChunkInArea(Chunk chunk);
}

package org.leralix.tan.storage.blacklist;

import org.bukkit.Chunk;

public interface IBlackList {

    boolean isChunkInArea(Chunk chunk);
}

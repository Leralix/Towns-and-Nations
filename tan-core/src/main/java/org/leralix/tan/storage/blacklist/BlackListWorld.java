package org.leralix.tan.storage.blacklist;

import org.bukkit.Chunk;

public class BlackListWorld implements IBlackList {
  String worldName;

  public BlackListWorld(String name) {
    worldName = name;
  }

  public boolean isChunkInArea(Chunk chunk) {
    return chunk.getWorld().getName().equals(worldName);
  }
}

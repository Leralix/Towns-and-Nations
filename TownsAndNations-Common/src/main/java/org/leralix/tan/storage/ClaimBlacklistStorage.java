package org.leralix.tan.storage;

import org.bukkit.Chunk;
import org.bukkit.configuration.file.FileConfiguration;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.data.chunk.ClaimedChunk;
import org.leralix.tan.storage.blacklist.BlackListWorld;
import org.leralix.tan.storage.blacklist.BlackListZone;
import org.leralix.tan.storage.blacklist.IBlackList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ClaimBlacklistStorage {

    private ClaimBlacklistStorage() {
        throw new IllegalStateException("Utility class");
    }

    private static List<IBlackList> blacklist;

    public static void init() {
        blacklist = new ArrayList<>();
        FileConfiguration config = ConfigUtil.getCustomConfig(ConfigTag.MAIN);
        for (Object item : config.getList("claimBlacklist", Collections.emptyList())){
            if (item instanceof Map<?, ?> map) {
                String name = (String) map.get("name");
                List<Integer> coordinates = (List<Integer>) map.get("coordinate");

                if(name == null) {
                    continue;
                }

                if (coordinates == null || coordinates.isEmpty()) {
                    blacklist.add(new BlackListWorld(name));
                    continue;
                }

                if (coordinates.size() == 4) {
                    blacklist.add(new BlackListZone(name, coordinates));
                }
            }
        }
    }

    public static boolean cannotBeClaimed(Chunk chunk) {
        for(IBlackList blacklist : blacklist) {
            if(blacklist.isChunkInArea(chunk)) {
                return true;
            }
        }
        return false;
    }

    public static boolean cannotBeClaimed(ClaimedChunk claimedChunk) {
        return cannotBeClaimed(claimedChunk.getChunk());
    }

}

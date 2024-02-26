package org.tan.TownsAndNations.DataClass.newChunkData;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.RegionData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.enums.ChunkPermissionType;
import org.tan.TownsAndNations.storage.DataStorage.RegionDataStorage;

import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;

public class RegionClaimedChunk extends ClaimedChunk2{


    public RegionClaimedChunk(Chunk chunk, String owner) {
        super(chunk, owner);
    }
    public RegionClaimedChunk(int x, int z, String worldUUID, String ownerID) {
        super(x,z,worldUUID,ownerID);
    }

    public String getName(){
        return RegionDataStorage.get(getID()).getName();
    }

    @Override
    public boolean canPlayerDo(Player player, ChunkPermissionType permissionType) {
        System.out.println("CLAIM DE REGION !");
        return false;
    }

    public RegionData getRegion() {
        return RegionDataStorage.get(getID());
    }

    public void unclaimChunk(Player player, Chunk chunk){
        System.out.println("UNCLAIM DE REGION !");
    }

    public void playerEnterClaimedArea(Player player){
        RegionData region = getRegion();
        player.sendMessage( getTANString() + Lang.CHUNK_ENTER_REGION.get(region.getName()));
    }
}

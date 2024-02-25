package org.tan.TownsAndNations.DataClass.newChunkData;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.enums.ChunkPermissionType;
import org.tan.TownsAndNations.enums.TownChunkPermission;
import org.tan.TownsAndNations.enums.TownRelation;
import org.tan.TownsAndNations.storage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.TownDataStorage;
import org.tan.TownsAndNations.storage.WarTaggedPlayer;

import static org.tan.TownsAndNations.enums.TownChunkPermission.ALLIANCE;
import static org.tan.TownsAndNations.enums.TownChunkPermission.FOREIGN;

public class TownClaimedChunk extends ClaimedChunk2{
    public TownClaimedChunk(Chunk chunk, String owner) {
        super(chunk, owner);
    }

    public TownClaimedChunk(int x, int z, String worldUUID, String ownerID) {
        super(x,z,worldUUID,ownerID);
    }

    @Override
    public boolean canPlayerDo(Player player, ChunkPermissionType permissionType) {
        TownData playerTown = TownDataStorage.get(player);
        PlayerData playerData = PlayerDataStorage.get(player);

        //Chunk is claimed yet player have no town
        if(!playerData.haveTown()){
            playerCantPerformAction(player);
            return false;
        }
        TownData chunkTown = TownDataStorage.get(ownerID);

        //Same town, can interact
        if(ownerID.equals(playerData.getTown().getID()))
            return true;

        TownChunkPermission townPermission = chunkTown.getPermission(permissionType);

        //Same alliance + alliance accepted permission
        if(townPermission == ALLIANCE && chunkTown.getTownRelationWithCurrent(TownRelation.ALLIANCE,playerTown.getID()))
            return true;

        //permission is on foreign
        if(townPermission == FOREIGN)
            return true;

        //war has been declared
        if(WarTaggedPlayer.isPlayerInWarWithTown(player,chunkTown))
            return true;

        playerCantPerformAction(player);
        return false;
    }
}

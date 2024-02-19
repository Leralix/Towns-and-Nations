package org.tan.TownsAndNations.DataClass;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.enums.TownChunkPermission;
import org.tan.TownsAndNations.enums.TownChunkPermissionType;
import org.tan.TownsAndNations.enums.TownRelation;
import org.tan.TownsAndNations.storage.ClaimedChunkStorage;
import org.tan.TownsAndNations.storage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.TownDataStorage;
import org.tan.TownsAndNations.storage.WarTaggedPlayer;

import java.util.Objects;

import static org.tan.TownsAndNations.enums.TownChunkPermission.ALLIANCE;
import static org.tan.TownsAndNations.enums.TownChunkPermission.FOREIGN;
import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;

public class ClaimedChunk {
    private final int x, z;
    private final String worldUUID, townUUID;

    public ClaimedChunk(Chunk chunk, String owner) {
        this.x = chunk.getX();
        this.z = chunk.getZ();
        this.worldUUID = chunk.getWorld().getUID().toString();
        this.townUUID = owner;
    }
    public ClaimedChunk(int x, int z, String worldUUID , String owner) {
        this.x = x;
        this.z = z;
        this.worldUUID = worldUUID;
        this.townUUID = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClaimedChunk that)) return false;
        return x == that.x && z == that.z && worldUUID.equals(that.worldUUID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, z, worldUUID);
    }

    public String getID() {
        return this.townUUID;
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }

    public String getWorldUUID() {
        return this.worldUUID;
    }

    public boolean canPlayerDo(Player player, TownChunkPermissionType permissionType) {

        TownData playerTown = TownDataStorage.get(player);
        PlayerData playerData = PlayerDataStorage.get(player);

        //Chunk is claimed yet player have no town
        if(!playerData.haveTown()){
            playerCantPerformAction(player);
            return false;
        }
        //Chunk is claimed by a town
        if(townUUID.startsWith("T")){

            //Means it is a town chunk
            TownData chunkTown = TownDataStorage.get(townUUID);
            //Same town, can interact
            if(townUUID.equals(playerData.getTown().getID()))
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
        else if(townUUID.startsWith("R")){

        }
        return false;
    }

    private void playerCantPerformAction(Player player){
        player.sendMessage(getTANString() + Lang.PLAYER_ACTION_NO_PERMISSION.get());
        player.sendMessage(getTANString() + Lang.CHUNK_BELONGS_TO.get(TownDataStorage.get(getID()).getName()));
    }

}
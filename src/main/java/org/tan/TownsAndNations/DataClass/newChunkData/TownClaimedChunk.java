package org.tan.TownsAndNations.DataClass.newChunkData;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.enums.ChunkPermissionType;
import org.tan.TownsAndNations.enums.TownChunkPermission;
import org.tan.TownsAndNations.enums.TownRelation;
import org.tan.TownsAndNations.enums.TownRolePermission;
import org.tan.TownsAndNations.storage.*;
import org.tan.TownsAndNations.storage.DataStorage.NewClaimedChunkStorage;
import org.tan.TownsAndNations.storage.DataStorage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;
import org.tan.TownsAndNations.utils.ChatUtils;
import org.tan.TownsAndNations.utils.SoundUtil;

import static org.tan.TownsAndNations.enums.SoundEnum.BAD;
import static org.tan.TownsAndNations.enums.TownChunkPermission.ALLIANCE;
import static org.tan.TownsAndNations.enums.TownChunkPermission.FOREIGN;
import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;

public class TownClaimedChunk extends ClaimedChunk2{
    public TownClaimedChunk(Chunk chunk, String owner) {
        super(chunk, owner);
    }

    public TownClaimedChunk(int x, int z, String worldUUID, String ownerID) {
        super(x,z,worldUUID,ownerID);
    }

    public String getName(){
        return TownDataStorage.get(getID()).getName();
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

    public TownData getTown(){
        return TownDataStorage.get(ownerID);
    }

    public void unclaimChunk(Player player, Chunk chunk){
        PlayerData playerStat = PlayerDataStorage.get(player.getUniqueId().toString());
        if(!playerStat.haveTown()){
            player.sendMessage(getTANString() + Lang.PLAYER_NO_TOWN.get());
            return;
        }

        if(!playerStat.hasPermission(TownRolePermission.UNCLAIM_CHUNK)){
            player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());
            return;
        }

        TownData townStat = playerStat.getTown();
        if(!townStat.getLeaderID().equals(playerStat.getID())){
            player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());
        }



        if(NewClaimedChunkStorage.isChunkClaimed(chunk)){

            if(NewClaimedChunkStorage.isOwner(chunk, townStat.getID())) {
                NewClaimedChunkStorage.unclaimChunk(player.getLocation().getChunk());

                townStat.addNumberOfClaimChunk(-1);

                player.sendMessage(getTANString() + Lang.UNCLAIMED_CHUNK_SUCCESS.get(townStat.getNumberOfClaimedChunk(),townStat.getTownLevel().getChunkCap()));

                return;
            }
            TownData otherTown = TownDataStorage.get(NewClaimedChunkStorage.getChunkOwnerID(chunk));
            player.sendMessage(getTANString() + Lang.UNCLAIMED_CHUNK_NOT_RIGHT_TOWN.get(otherTown.getName()));

        }
    }

    public void playerEnterClaimedArea(Player player){
        TownData townTo = getTown();
        player.sendMessage(ChatUtils.getTANString() + Lang.CHUNK_ENTER_TOWN.get(townTo.getName()));

        TownData playerTown = TownDataStorage.get(player);
        if(playerTown == null){
            return;
        }
        TownRelation relation = TownDataStorage.get(player).getRelationWith(townTo);

        if(relation == TownRelation.WAR){
            SoundUtil.playSound(player, BAD);
            player.sendMessage(Lang.CHUNK_ENTER_TOWN_AT_WAR.get());

            townTo.broadCastMessageWithSound(Lang.CHUNK_INTRUSION_ALERT.get(TownDataStorage.get(player).getName(),player.getName()), BAD);
        }


    }
}

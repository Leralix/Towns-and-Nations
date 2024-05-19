package org.tan.TownsAndNations.DataClass.newChunkData;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.DataClass.PropertyData;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.enums.ChunkPermissionType;
import org.tan.TownsAndNations.enums.TownChunkPermission;
import org.tan.TownsAndNations.enums.TownRelation;
import org.tan.TownsAndNations.enums.TownRolePermission;
import org.tan.TownsAndNations.storage.DataStorage.NewClaimedChunkStorage;
import org.tan.TownsAndNations.storage.DataStorage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;
import org.tan.TownsAndNations.storage.WarTaggedPlayer;
import org.tan.TownsAndNations.utils.SoundUtil;

import static org.tan.TownsAndNations.enums.SoundEnum.BAD;
import static org.tan.TownsAndNations.enums.SoundEnum.NOT_ALLOWED;
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
        return TownDataStorage.get(getOwnerID()).getName();
    }
    public TownData getTown(){
        return TownDataStorage.get(ownerID);
    }

    @Override
    public boolean canPlayerDo(Player player, ChunkPermissionType permissionType, Location location) {
        TownData playerTown = TownDataStorage.get(player);
        PlayerData playerData = PlayerDataStorage.get(player);

        //player is at war with the town. He can interact with everything
        if(WarTaggedPlayer.isPlayerInWarWithTown(player, this.getTown()))
            return true;

        //Location is in a property and players owns or rent it
        TownData ownerTown = getTown();
        PropertyData property = ownerTown.getProperty(location);
        if(property != null){
            if(property.isAllowed(playerData)){
                return true;
            }
            else {
                player.sendMessage(property.getDenyMessage());
                return false;
            }
        }

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

        playerCantPerformAction(player);
        return false;
    }


    public void unclaimChunk(Player player, Chunk chunk){
        PlayerData playerStat = PlayerDataStorage.get(player.getUniqueId().toString());
        if(!playerStat.haveTown()){
            player.sendMessage(getTANString() + Lang.PLAYER_NO_TOWN.get());
            return;
        }

        if(!playerStat.hasPermission(TownRolePermission.UNCLAIM_CHUNK)){
            player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());
            SoundUtil.playSound(player, NOT_ALLOWED);
            return;
        }

        TownData townStat = playerStat.getTown();
        if(!townStat.getLeaderID().equals(playerStat.getID())){
            player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());
        }



        if(NewClaimedChunkStorage.isChunkClaimed(chunk)){
            if(NewClaimedChunkStorage.isOwner(chunk, townStat.getID())) {
                NewClaimedChunkStorage.unclaimChunk(player.getLocation().getChunk());
                player.sendMessage(getTANString() + Lang.UNCLAIMED_CHUNK_SUCCESS_TOWN.get(townStat.getNumberOfClaimedChunk(),townStat.getTownLevel().getChunkCap()));
                return;
            }
            TownData otherTown = TownDataStorage.get(NewClaimedChunkStorage.getChunkOwnerID(chunk));
            player.sendMessage(getTANString() + Lang.UNCLAIMED_CHUNK_NOT_RIGHT_TOWN.get(otherTown.getName()));

        }
    }

    public void playerEnterClaimedArea(Player player){
        TownData townTo = getTown();
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Lang.CHUNK_ENTER_TOWN.get(townTo.getName())));

        TownData playerTown = TownDataStorage.get(player);
        if(playerTown == null){
            return;
        }
        TownRelation relation = TownDataStorage.get(player).getRelationWith(townTo);

        if(relation == TownRelation.WAR){
            SoundUtil.playSound(player, BAD);
            player.sendMessage(getTANString() + Lang.CHUNK_ENTER_TOWN_AT_WAR.get());
            townTo.broadCastMessageWithSound(getTANString() + Lang.CHUNK_INTRUSION_ALERT.get(TownDataStorage.get(player).getName(),player.getName()), BAD);
        }
    }

    @Override
    public boolean canEntitySpawn(EntityType entityType) {
       return getTown().getChunkSettings().getSpawnControl(entityType.toString()).canSpawn();
    }
}

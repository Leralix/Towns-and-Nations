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

public class LandmarkClaimedChunk extends ClaimedChunk2{
    public LandmarkClaimedChunk(Chunk chunk, String owner) {
        super(chunk, owner);
    }

    public LandmarkClaimedChunk(int x, int z, String worldUUID, String ownerID) {
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
        player.sendMessage(getTANString() + Lang.CANNOT_DO_IN_LANDMARK.get());
        return false;
    }


    public void unclaimChunk(Player player, Chunk chunk){
        player.sendMessage(getTANString() + Lang.CANNOT_UNCLAIM_LANDMARK_CHUNK.get());
        return;
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
       return true;
    }
}

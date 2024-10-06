package org.tan.TownsAndNations.DataClass.newChunkData;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.DataClass.PropertyData;
import org.tan.TownsAndNations.DataClass.territoryData.ITerritoryData;
import org.tan.TownsAndNations.DataClass.territoryData.TownData;
import org.tan.TownsAndNations.DataClass.wars.CurrentAttacks;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.enums.ChunkPermissionType;
import org.tan.TownsAndNations.enums.TownChunkPermission;
import org.tan.TownsAndNations.enums.TownRelation;
import org.tan.TownsAndNations.enums.TownRolePermission;
import org.tan.TownsAndNations.storage.DataStorage.NewClaimedChunkStorage;
import org.tan.TownsAndNations.storage.DataStorage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;
import org.tan.TownsAndNations.utils.SoundUtil;
import org.tan.TownsAndNations.utils.config.ConfigTag;
import org.tan.TownsAndNations.utils.config.ConfigUtil;

import static org.tan.TownsAndNations.enums.SoundEnum.BAD;
import static org.tan.TownsAndNations.enums.SoundEnum.NOT_ALLOWED;
import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;

public class TownClaimedChunk extends ClaimedChunk2{
    public TownClaimedChunk(Chunk chunk, String owner) {
        super(chunk, owner);
    }

    public TownClaimedChunk(int x, int z, String worldUUID, String ownerID) {
        super(x,z,worldUUID,ownerID);
    }
    public String getName(){
        return getTown().getName();
    }
    public TownData getTown(){
        return TownDataStorage.get(ownerID);
    }

    @Override
    public boolean canPlayerDo(Player player, ChunkPermissionType permissionType, Location location) {
        PlayerData playerData = PlayerDataStorage.get(player);

        //Location is in a property and players owns or rent it
        TownData ownerTown = getTown();
        PropertyData property = ownerTown.getProperty(location);
        if(property != null){
            if(property.isPlayerAllowed(playerData)){
                return true;
            }
            else {
                player.sendMessage(getTANString() + property.getDenyMessage());
                return false;
            }
        }

        //Chunk is claimed yet player have no town
        if(!playerData.haveTown()){
            playerCantPerformAction(player);
            return false;
        }

        TownData playerTown = TownDataStorage.get(player);
        //player is a part of a war with this town.
        for(CurrentAttacks currentAttacks : playerTown.getCurrentAttacks())
            if(currentAttacks.containsPlayer(playerData))
                return true;


        if(getTown().havePlayer(playerData)){
            return true;
        }

        TownChunkPermission townPermission = ownerTown.getPermission(permissionType);
        if(townPermission.isAllowed(ownerTown, playerData))
            return true;

        playerCantPerformAction(player);
        return false;
    }


    public void unclaimChunk(Player player){
        PlayerData playerStat = PlayerDataStorage.get(player);
        if(!playerStat.haveTown()){
            player.sendMessage(getTANString() + Lang.PLAYER_NO_TOWN.get());
            return;
        }

        if(!playerStat.hasPermission(TownRolePermission.UNCLAIM_CHUNK)){
            player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.get());
            SoundUtil.playSound(player, NOT_ALLOWED);
            return;
        }

        TownData playerTown = playerStat.getTown();

        if(!getOwner().equals(playerTown)){
            player.sendMessage(getTANString() + Lang.UNCLAIMED_CHUNK_NOT_RIGHT_TOWN.get(getOwner().getName()));
            return;
        }

        for(PropertyData propertyData : getTown().getPropertyDataList()){
            if(propertyData.isInChunk(this)){
                player.sendMessage(getTANString() + Lang.PROPERTY_IN_CHUNK.get(propertyData.getName()));
                return;
            }
        }

        player.sendMessage(getTANString() + Lang.UNCLAIMED_CHUNK_SUCCESS_TOWN.get(playerTown.getNumberOfClaimedChunk(),playerTown.getTownLevel().getChunkCap()));
        NewClaimedChunkStorage.unclaimChunk(player.getLocation().getChunk());
    }

    public void playerEnterClaimedArea(Player player){
        TownData townTo = getTown();
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Lang.PLAYER_ENTER_TOWN_CHUNK.get(townTo.getName())));

        TownData playerTown = TownDataStorage.get(player);
        if(playerTown == null){
            return;
        }
        TownRelation relation = playerTown.getRelationWith(townTo);

        if(relation == TownRelation.WAR && ConfigUtil.getCustomConfig(ConfigTag.MAIN).getBoolean("notifyEnemyEnterTown",true)){
            SoundUtil.playSound(player, BAD);
            player.sendMessage(getTANString() + Lang.CHUNK_ENTER_TOWN_AT_WAR.get());
            townTo.broadCastMessageWithSound(Lang.CHUNK_INTRUSION_ALERT.get(TownDataStorage.get(player).getName(),player.getName()), BAD);
        }
    }

    @Override
    public boolean canEntitySpawn(EntityType entityType) {
       return getTown().getChunkSettings().getSpawnControl(entityType.toString()).canSpawn();
    }

    @Override
    public TextComponent getMapIcon(PlayerData playerData) {

        TextComponent textComponent = new TextComponent("⬛");
        textComponent.setColor(getTown().getChunkColor());
        textComponent.setHoverEvent(new HoverEvent(
            HoverEvent.Action.SHOW_TEXT,
            new Text("x : " + super.getX() + " z : " + super.getZ() + "\n" +
                    getTown().getColoredName() + "\n" +
                    Lang.LEFT_CLICK_TO_CLAIM.get())));
        return textComponent;


    }

    @Override
    public boolean canPlayerClaim(Player player,ITerritoryData territoryData) {

        if(territoryData.canConquerChunk(this))
            return true;

        player.sendMessage(getTANString() + Lang.CHUNK_ALREADY_CLAIMED_WARNING.get(getOwner().getColoredName()));
        return false;
    }

    @Override
    public boolean isClaimed() {
        return true;
    }

    @Override
    public boolean canBeOverClaimed(ITerritoryData territoryData) {
        return false;
    }
}

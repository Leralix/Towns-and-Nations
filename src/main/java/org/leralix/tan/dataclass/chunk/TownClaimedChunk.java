package org.leralix.tan.dataclass.chunk;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.PropertyData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.dataclass.wars.CurrentAttack;
import org.leralix.tan.dataclass.wars.GriefAllowed;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.enums.permissions.GeneralChunkSetting;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.TanChatUtils;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.dataclass.territory.permission.ChunkPermission;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.enums.permissions.ChunkPermissionType;

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
        return TownDataStorage.getInstance().get(ownerID);
    }

    @Override
    public boolean canPlayerDo(Player player, ChunkPermissionType permissionType, Location location) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);

        //Location is in a property and players owns or rent it
        TownData ownerTown = getTown();
        PropertyData property = ownerTown.getProperty(location);
        if(property != null){
            if(property.isPlayerAllowed(playerData)){
                return true;
            }
            else {
                player.sendMessage(TanChatUtils.getTANString() + property.getDenyMessage());
                return false;
            }
        }

        //Chunk is claimed yet player have no town
        if(!playerData.hasTown()){
            playerCantPerformAction(player);
            return false;
        }

        //Player is at war with the town
        for(CurrentAttack currentAttacks : ownerTown.getCurrentAttacks()) {
            if (currentAttacks.containsPlayer(playerData))
                return true;
        }

        ChunkPermission chunkPermission = ownerTown.getPermission(permissionType);
        if(chunkPermission.isAllowed(ownerTown, playerData))
            return true;

        playerCantPerformAction(player);
        return false;
    }


    public void unclaimChunk(Player player){
        PlayerData playerStat = PlayerDataStorage.getInstance().get(player);
        TownData playerTown = playerStat.getTown();

        if(playerTown == null){
            player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_TOWN.get());
            return;
        }

        if(!playerTown.doesPlayerHavePermission(playerStat, RolePermission.UNCLAIM_CHUNK)){
            player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get());
            SoundUtil.playSound(player, SoundEnum.NOT_ALLOWED);
            return;
        }


        if(!getOwner().equals(playerTown)){
            player.sendMessage(TanChatUtils.getTANString() + Lang.UNCLAIMED_CHUNK_NOT_RIGHT_TOWN.get(getOwner().getName()));
            return;
        }

        for(PropertyData propertyData : getTown().getPropertyDataList()){
            if(propertyData.isInChunk(this)){
                player.sendMessage(TanChatUtils.getTANString() + Lang.PROPERTY_IN_CHUNK.get(propertyData.getName()));
                return;
            }
        }

        player.sendMessage(TanChatUtils.getTANString() + Lang.UNCLAIMED_CHUNK_SUCCESS_TOWN.get(playerTown.getNumberOfClaimedChunk(),playerTown.getLevel().getChunkCap()));
        NewClaimedChunkStorage.getInstance().unclaimChunk(this);
    }

    public void playerEnterClaimedArea(Player player){
        TownData townTo = getTown();
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Lang.PLAYER_ENTER_TOWN_CHUNK.get(townTo.getName())));

        TownData playerTown = TownDataStorage.getInstance().get(player);
        if(playerTown == null){
            return;
        }
        TownRelation relation = playerTown.getRelationWith(townTo);

        if(relation == TownRelation.WAR && ConfigUtil.getCustomConfig(ConfigTag.MAIN).getBoolean("notifyEnemyEnterTown",true)){
            SoundUtil.playSound(player, SoundEnum.BAD);
            player.sendMessage(TanChatUtils.getTANString() + Lang.CHUNK_ENTER_TOWN_AT_WAR.get());
            townTo.broadCastMessageWithSound(Lang.CHUNK_INTRUSION_ALERT.get(TownDataStorage.getInstance().get(player).getName(),player.getName()), SoundEnum.BAD);
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
    public boolean canTerritoryClaim(Player player, TerritoryData territoryData) {
        if(territoryData.canConquerChunk(this))
            return true;

        player.sendMessage(TanChatUtils.getTANString() + Lang.CHUNK_ALREADY_CLAIMED_WARNING.get(getOwner().getColoredName()));
        return false;
    }

    @Override
    public boolean isClaimed() {
        return true;
    }

    @Override
    public boolean canExplosionGrief() {
        String fireGrief = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getString("explosionGrief", "ALWAYS");
        GriefAllowed griefAllowed =  GriefAllowed.valueOf(fireGrief);
        return griefAllowed.canGrief(getTown(), GeneralChunkSetting.TNT_GRIEF);
    }

    @Override
    public boolean canFireGrief() {
        String fireGrief = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getString("fireGrief", "ALWAYS");
        GriefAllowed griefAllowed = GriefAllowed.valueOf(fireGrief);
        return griefAllowed.canGrief(getTown(), GeneralChunkSetting.FIRE_GRIEF);
    }

    @Override
    public boolean canPVPHappen() {
        String pvpEnabled = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getString("pvpEnabledInClaimedChunks", "ALWAYS");
        GriefAllowed griefAllowed = GriefAllowed.valueOf(pvpEnabled);
        return griefAllowed.canGrief(getTown(), GeneralChunkSetting.ENABLE_PVP);
    }


}

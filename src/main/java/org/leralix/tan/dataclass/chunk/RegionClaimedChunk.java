package org.leralix.tan.dataclass.chunk;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.wars.CurrentAttack;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.enums.permissions.GeneralChunkSetting;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.utils.TanChatUtils;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.wars.GriefAllowed;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.permission.ChunkPermission;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.enums.permissions.ChunkPermissionType;

public class RegionClaimedChunk extends ClaimedChunk2{


    public RegionClaimedChunk(Chunk chunk, String owner) {
        super(chunk, owner);
    }
    public RegionClaimedChunk(int x, int z, String worldUUID, String ownerID) {
        super(x,z,worldUUID,ownerID);
    }

    public String getName(){
        return getOwner().getName();
    }
    @Override
    public boolean canPlayerDo(Player player, ChunkPermissionType permissionType, Location location) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);

        //Location is in a property and players owns or rent it
        RegionData ownerRegion = getRegion();
        //Chunk is claimed yet player have no town
        if(!playerData.hasTown()){
            playerCantPerformAction(player);
            return false;
        }

        //Player is at war with the region
        for(CurrentAttack currentAttacks : ownerRegion.getCurrentAttacks()) {
            if (currentAttacks.containsPlayer(playerData))
                return true;
        }

        //Player have the right to do the action
        ChunkPermission chunkPermission = ownerRegion.getPermission(permissionType);
        if(chunkPermission.isAllowed(ownerRegion, playerData))
            return true;

        playerCantPerformAction(player);
        return false;
    }

    public RegionData getRegion() {
        return RegionDataStorage.get(getOwnerID());
    }

    public void unclaimChunk(Player player){
        PlayerData playerStat = PlayerDataStorage.getInstance().get(player.getUniqueId().toString());
        if(!playerStat.hasTown()){
            player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_TOWN.get());
            return;
        }

        if(!playerStat.hasRegion()){
            player.sendMessage(TanChatUtils.getTANString() + Lang.TOWN_NO_REGION.get());
            return;
        }

        RegionData regionData = playerStat.getRegion();

        if(!regionData.equals(getRegion())){
            player.sendMessage(TanChatUtils.getTANString() + Lang.UNCLAIMED_CHUNK_NOT_RIGHT_REGION.get(getRegion().getName()));
            return;
        }

        if(!regionData.doesPlayerHavePermission(playerStat, RolePermission.UNCLAIM_CHUNK)){
            player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NOT_LEADER_OF_REGION.get());
            return;
        }
        NewClaimedChunkStorage.getInstance().unclaimChunk(this);
        player.sendMessage(TanChatUtils.getTANString() + Lang.UNCLAIMED_CHUNK_SUCCESS_REGION.get(regionData.getNumberOfClaimedChunk()));
    }

    public void playerEnterClaimedArea(Player player){
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Lang.PLAYER_ENTER_REGION_CHUNK.get(getRegion().getName())));
    }

    @Override
    public boolean canEntitySpawn(EntityType entityType) {
        return true;
    }

    @Override
    public TextComponent getMapIcon(PlayerData playerData) {
        TextComponent textComponent = new TextComponent("⬛");
        textComponent.setColor(getRegion().getChunkColor());
        textComponent.setHoverEvent(new HoverEvent(
            HoverEvent.Action.SHOW_TEXT,
            new Text("x : " + super.getX() + " z : " + super.getZ() + "\n" +
                    getRegion().getColoredName() + "\n" +
                    Lang.LEFT_CLICK_TO_CLAIM.get())));
        return textComponent;
    }

    @Override
    public boolean canTerritoryClaim(Player player, TerritoryData territoryData) {
        if(territoryData.canConquerChunk(this))
            return true;

        if(getRegion().getSubjects().contains(territoryData)){
            return true; // if the town is part of this specific region they can claim
        }

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
        return griefAllowed.canGrief(getRegion(), GeneralChunkSetting.TNT_GRIEF);
    }

    @Override
    public boolean canFireGrief() {
        String fireGrief = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getString("fireGrief", "ALWAYS");
        GriefAllowed griefAllowed =  GriefAllowed.valueOf(fireGrief);
        return griefAllowed.canGrief(getRegion(), GeneralChunkSetting.FIRE_GRIEF);
    }

    @Override
    public boolean canPVPHappen() {
        String pvpEnabled = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getString("pvpEnabledInClaimedChunks", "ALWAYS");
        GriefAllowed griefAllowed = GriefAllowed.valueOf(pvpEnabled);
        return griefAllowed.canGrief(getRegion(), GeneralChunkSetting.ENABLE_PVP);
    }

}

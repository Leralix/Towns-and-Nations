package org.leralix.tan.dataclass.chunk;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.permission.ChunkPermission;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.enums.permissions.ChunkPermissionType;
import org.leralix.tan.enums.permissions.GeneralChunkSetting;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.utils.TanChatUtils;
import org.leralix.tan.war.legacy.CurrentAttack;
import org.leralix.tan.war.legacy.GriefAllowed;

public class RegionClaimedChunk extends TerritoryChunk {


    public RegionClaimedChunk(Chunk chunk, String owner) {
        super(chunk, owner);
    }

    public RegionClaimedChunk(int x, int z, String worldUUID, String ownerID) {
        super(x, z, worldUUID, ownerID);
    }

    public String getName() {
        return getOwner().getName();
    }

    @Override
    protected boolean canPlayerDoInternal(Player player, ChunkPermissionType permissionType, Location location) {
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);

        RegionData ownerRegion = getRegion();
        //Chunk is claimed yet player has no town
        if (!tanPlayer.hasTown()) {
            playerCantPerformAction(player);
            return false;
        }

        //Player is at war with the region
        for (CurrentAttack currentAttacks : ownerRegion.getCurrentAttacks()) {
            if (currentAttacks.containsPlayer(tanPlayer))
                return true;
        }

        //Player have the right to do the action
        ChunkPermission chunkPermission = ownerRegion.getPermission(permissionType);
        if (chunkPermission.isAllowed(ownerRegion, tanPlayer))
            return true;

        playerCantPerformAction(player);
        return false;
    }

    public RegionData getRegion() {
        return RegionDataStorage.getInstance().get(getOwnerID());
    }

    public void unclaimChunk(Player player) {
        ITanPlayer playerStat = PlayerDataStorage.getInstance().get(player.getUniqueId().toString());
        if (!playerStat.hasTown()) {
            player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_TOWN.get());
            return;
        }

        if (!playerStat.hasRegion()) {
            player.sendMessage(TanChatUtils.getTANString() + Lang.TOWN_NO_REGION.get());
            return;
        }

        RegionData regionData = playerStat.getRegion();

        if (!regionData.equals(getRegion())) {
            player.sendMessage(TanChatUtils.getTANString() + Lang.UNCLAIMED_CHUNK_NOT_RIGHT_REGION.get(getRegion().getName()));
            return;
        }

        if (!regionData.doesPlayerHavePermission(playerStat, RolePermission.UNCLAIM_CHUNK)) {
            player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NOT_LEADER_OF_REGION.get());
            return;
        }
        NewClaimedChunkStorage.getInstance().unclaimChunk(this);
        player.sendMessage(TanChatUtils.getTANString() + Lang.UNCLAIMED_CHUNK_SUCCESS_REGION.get(regionData.getNumberOfClaimedChunk()));
    }

    public void playerEnterClaimedArea(Player player, boolean displayTerritoryColor) {
        RegionData regionData = getRegion();

        TextComponent name = displayTerritoryColor ? regionData.getCustomColoredName() : new TextComponent(regionData.getBaseColoredName());
        String message = Lang.PLAYER_ENTER_TERRITORY_CHUNK.get(name.toLegacyText());
        player.sendTitle("", message, 5, 40, 20);

        TextComponent textComponent = new TextComponent(regionData.getDescription());
        textComponent.setColor(ChatColor.GRAY);
        textComponent.setItalic(true);

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, textComponent);
    }

    @Override
    public boolean canEntitySpawn(EntityType entityType) {
        return true;
    }

    @Override
    public boolean canTerritoryClaim(TerritoryData territoryData) {
        if (territoryData.canConquerChunk(this))
            return true;

        // if the town is part of this specific region, they can claim
        return getRegion().getSubjects().contains(territoryData);
    }

    @Override
    public boolean isClaimed() {
        return true;
    }

    @Override
    public boolean canExplosionGrief() {
        String fireGrief = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getString("explosionGrief", "ALWAYS");
        GriefAllowed griefAllowed = GriefAllowed.valueOf(fireGrief);
        return griefAllowed.canGrief(getRegion(), GeneralChunkSetting.TNT_GRIEF);
    }

    @Override
    public boolean canFireGrief() {
        String fireGrief = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getString("fireGrief", "ALWAYS");
        GriefAllowed griefAllowed = GriefAllowed.valueOf(fireGrief);
        return griefAllowed.canGrief(getRegion(), GeneralChunkSetting.FIRE_GRIEF);
    }

    @Override
    public boolean canPVPHappen() {
        String pvpEnabled = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getString("pvpEnabledInClaimedChunks", "ALWAYS");
        GriefAllowed griefAllowed = GriefAllowed.valueOf(pvpEnabled);
        return griefAllowed.canGrief(getRegion(), GeneralChunkSetting.ENABLE_PVP);
    }

    @Override
    public ChunkType getType() {
        return ChunkType.REGION;
    }

}

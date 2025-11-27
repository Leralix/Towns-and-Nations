package org.leralix.tan.dataclass.chunk;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.permission.ChunkPermission;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.enums.permissions.ChunkPermissionType;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.upgrade.rewards.numeric.ChunkCap;
import org.leralix.tan.utils.text.TanChatUtils;
import org.leralix.tan.war.legacy.CurrentAttack;

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

        //Player is at war with the region
        for (CurrentAttack currentAttacks : ownerRegion.getCurrentAttacks()) {
            if (currentAttacks.containsPlayer(tanPlayer))
                return true;
        }

        //Player have the right to do the action
        ChunkPermission chunkPermission = ownerRegion.getChunkSettings().getPermission(permissionType);
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
            TanChatUtils.message(player, Lang.PLAYER_NO_TOWN.get(player));
            return;
        }

        if (!playerStat.hasRegion()) {
            TanChatUtils.message(player, Lang.TOWN_NO_REGION.get(player));
            return;
        }

        RegionData regionData = playerStat.getRegion();

        if (!regionData.equals(getRegion())) {
            TanChatUtils.message(player, Lang.UNCLAIMED_CHUNK_NOT_RIGHT_REGION.get(player, getRegion().getName()));
            return;
        }

        if (!regionData.doesPlayerHavePermission(playerStat, RolePermission.UNCLAIM_CHUNK)) {
            TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(player));
            return;
        }
        NewClaimedChunkStorage.getInstance().unclaimChunkAndUpdate(this);

        ChunkCap chunkCap = regionData.getNewLevel().getStat(ChunkCap.class);
        if(chunkCap.isUnlimited()){
            Lang.CHUNK_UNCLAIMED_SUCCESS_UNLIMITED.get(player, regionData.getColoredName());
        }
        else {
            String currentChunks = Integer.toString(regionData.getNumberOfClaimedChunk());
            String maxChunks = Integer.toString(chunkCap.getMaxAmount());
            Lang.CHUNK_UNCLAIMED_SUCCESS_LIMITED.get(player, regionData.getColoredName(), currentChunks, maxChunks);
        }
    }

    public void playerEnterClaimedArea(Player player, boolean displayTerritoryColor) {
        RegionData regionData = getRegion();

        TextComponent name = displayTerritoryColor ? regionData.getCustomColoredName() : new TextComponent(regionData.getBaseColoredName());
        String message = Lang.PLAYER_ENTER_TERRITORY_CHUNK.get(player, name.toLegacyText());
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
    public ChunkType getType() {
        return ChunkType.REGION;
    }

    @Override
    public void notifyUpdate() {
        //TODO : Unclaim chunks if no longer linked to fort
    }

}

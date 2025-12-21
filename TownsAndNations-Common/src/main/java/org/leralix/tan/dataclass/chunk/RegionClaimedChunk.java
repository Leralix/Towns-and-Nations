package org.leralix.tan.dataclass.chunk;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.enums.permissions.ChunkPermissionType;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.upgrade.rewards.numeric.ChunkCap;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.territory.ChunkUtil;
import org.leralix.tan.utils.text.TanChatUtils;

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
        return commonTerritoryCanPlayerDo(player, permissionType, tanPlayer);
    }

    public RegionData getRegion() {
        return RegionDataStorage.getInstance().get(getOwnerID());
    }

    public void unclaimChunk(Player player) {
        ITanPlayer playerStat = PlayerDataStorage.getInstance().get(player);
        LangType langType = playerStat.getLang();

        // Special case: one of the player's territories can conquer chunks due to a past war.
        for(TerritoryData territoryData : playerStat.getAllTerritoriesPlayerIsIn()){
            if(territoryData.canConquerChunk(this)){
                NewClaimedChunkStorage.getInstance().unclaimChunkAndUpdate(this);
                TanChatUtils.message(player, Lang.CHUNK_UNCLAIMED_SUCCESS_UNLIMITED.get(langType, getRegion().getColoredName()), SoundEnum.MINOR_GOOD);
                return;
            }
        }

        if (!playerStat.hasTown()) {
            TanChatUtils.message(player, Lang.PLAYER_NO_TOWN.get(langType));
            return;
        }

        if (!playerStat.hasRegion()) {
            TanChatUtils.message(player, Lang.TOWN_NO_REGION.get(langType));
            return;
        }

        RegionData regionData = playerStat.getRegion();

        if (!regionData.equals(getRegion())) {
            TanChatUtils.message(player, Lang.UNCLAIMED_CHUNK_NOT_RIGHT_REGION.get(langType, getRegion().getName()));
            return;
        }

        if (!regionData.doesPlayerHavePermission(playerStat, RolePermission.UNCLAIM_CHUNK)) {
            TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(langType));
            return;
        }

        if(isOccupied()){
            TanChatUtils.message(player, Lang.CHUNK_OCCUPIED_CANT_UNCLAIM.get(langType));
            return;
        }

        NewClaimedChunkStorage.getInstance().unclaimChunkAndUpdate(this);

        ChunkCap chunkCap = regionData.getNewLevel().getStat(ChunkCap.class);
        if(chunkCap.isUnlimited()){
            TanChatUtils.message(player, Lang.CHUNK_UNCLAIMED_SUCCESS_UNLIMITED.get(player, regionData.getColoredName()));
        }
        else {
            String currentChunks = Integer.toString(regionData.getNumberOfClaimedChunk());
            String maxChunks = Integer.toString(chunkCap.getMaxAmount());
            TanChatUtils.message(player, Lang.CHUNK_UNCLAIMED_SUCCESS_LIMITED.get(player, regionData.getColoredName(), currentChunks, maxChunks));
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
        if (!Constants.allowNonAdjacentChunksForRegion()) {
            ChunkUtil.unclaimIfNoLongerSupplied(this);
        }
    }

}

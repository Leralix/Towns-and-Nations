package org.leralix.tan.data.chunk;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.data.territory.permission.ChunkPermissionType;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.ClaimBlacklistStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.text.TanChatUtils;

public class WildernessChunkData extends ChunkData implements WildernessChunk {

    public WildernessChunkData(int x, int z, String worldUUID) {
        super(x, z, worldUUID);
    }

    public WildernessChunkData(Chunk chunk) {
        super(chunk);
    }

    @Override
    protected boolean canPlayerDoInternal(Player player, ITanPlayer tanPlayer, ChunkPermissionType permissionType, Location location) {

        World world = location.getWorld();

        if(Constants.getWildernessRules().canPlayerDoInWilderness(world, permissionType)){
            return true;
        }
        TanChatUtils.message(player, Lang.WILDERNESS_NO_PERMISSION.getDefault());
        return false;
    }

    @Override
    protected void playerCantPerformAction(Player player, LangType langType) {
        TanChatUtils.message(player, Lang.WILDERNESS_NO_PERMISSION.getDefault());
    }

    @Override
    public void playerEnterClaimedArea(Player player, ITanPlayer tanPlayer, boolean displayTerritoryColor) {
        player.sendActionBar(Component.text(Lang.WILDERNESS.get(tanPlayer)));
    }

    @Override
    public String getName() {
        return Lang.WILDERNESS.getDefault();
    }

    @Override
    public boolean canEntitySpawn(EntityType entityType) {
        return true;
    }

    @Override
    public Component getMapIcon(LangType langType, boolean isMiddleOfMap) {

        if (ClaimBlacklistStorage.cannotBeClaimed(this)) {

            String text = chunkCoordinateString() +
                    Lang.WILDERNESS.get(langType) + "\n" +
                    Lang.CHUNK_IS_BLACKLISTED.get(langType);

            return Component.text("✖")
                    .color(TextColor.color(0xFF5555))
                    .hoverEvent(HoverEvent.showText(Component.text(text)));
        }

        String text = chunkCoordinateString() +
                Lang.WILDERNESS.get(langType) + "\n" +
                Lang.LEFT_CLICK_TO_CLAIM.get(langType);

        return Component.text(isMiddleOfMap ? "🌕" : "⬜")
                .color(TextColor.color(0xFFFFFF))
                .hoverEvent(HoverEvent.showText(Component.text(text)));
    }

    @Override
    public boolean canTerritoryClaim(Player player, Territory territoryData, LangType langType) {
        return true;
    }

    @Override
    public boolean canTerritoryClaim(Territory territoryData) {
        return true;
    }

    @Override
    public boolean isClaimed() {
        return false; // Wilderness chunks are never claimed
    }

    @Override
    public boolean canBeGriefByExplosion() {
        return true;
    }

    @Override
    public boolean canBeGriefByFire() {
        return true;
    }

    @Override
    public boolean canPvpHappen() {
        return true;
    }

    @Override
    public boolean canHostileGrief() {
        return true;
    }

    @Override
    public boolean canVillagerGrief() {
        return true;
    }

    @Override
    public boolean canPassiveGrief() {
        return true;
    }

    @Override
    public boolean canUnauthorizedPlayerUseMounts() {
        return true;
    }

    @Override
    public ChunkType getType() {
        return ChunkType.WILDERNESS;
    }

    @Override
    public void notifyUpdate() {
        // Wilderness chunks do not need to notify updates
    }
}

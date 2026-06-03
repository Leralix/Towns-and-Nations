package org.leralix.tan.listeners;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Tag;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.data.chunk.IClaimedChunk;
import org.leralix.tan.data.chunk.TerritoryChunk;
import org.leralix.tan.data.chunk.WildernessChunkData;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.data.territory.permission.ChunkPermissionType;
import org.leralix.tan.data.territory.relation.TownRelation;
import org.leralix.tan.gui.scope.ClaimType;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.PlayerAutoClaimStorage;
import org.leralix.tan.storage.stored.ClaimStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.text.TanChatUtils;

public class PlayerEnterChunkListener implements Listener {

    private final boolean displayTerritoryNameWithColor;
    private final ClaimStorage claimStorage;
    private final PlayerDataStorage playerDataStorage;

    public PlayerEnterChunkListener(PlayerDataStorage playerDataStorage, ClaimStorage claimStorage) {
        this.displayTerritoryNameWithColor = Constants.displayTerritoryColor();
        this.claimStorage = claimStorage;
        this.playerDataStorage = playerDataStorage;
    }

    @EventHandler
    public void onVehicleMove(VehicleMoveEvent event) {
        if (!(event.getVehicle() instanceof Minecart minecart)) {
            return;
        }

        minecart.getPassengers().forEach(passenger -> {
            if (passenger instanceof Player player) {
                playerEnterChunk(event.getFrom(), event.getTo(), player);
            }
        });
    }

    @EventHandler
    public void playerMoveEvent(final @NotNull PlayerMoveEvent event) {

        Player player = event.getPlayer();

        boolean accepted = playerEnterChunk(event.getFrom(), event.getTo(), player);
        if(!accepted){
            event.setCancelled(true);
        }
    }

    private boolean playerEnterChunk(Location currentLocation, Location nextLocation, Player player) {

        Chunk currentChunk = currentLocation.getChunk();
        Chunk nextChunk = nextLocation.getChunk();

        if (currentChunk.equals(nextChunk)) {
            return true;
        }

        // If both chunks are not claimed, no need to display anything
        if (!claimStorage.isChunkClaimed(currentChunk) &&
                !claimStorage.isChunkClaimed(nextChunk)) {

            if (PlayerAutoClaimStorage.containsPlayer(player)) {
                autoClaimChunk(nextChunk, player);
            }
            return true;
        }

        IClaimedChunk currentClaimedChunk = claimStorage.get(currentChunk);
        IClaimedChunk nextClaimedChunk = claimStorage.get(nextChunk);

        // Both chunks have the same owner, no need to change
        if (sameOwner(currentClaimedChunk, nextClaimedChunk)) {
            return true;
        }
        // If territory denies access to players with a certain relation.
        ITanPlayer tanPlayer = playerDataStorage.get(player);

        if (nextClaimedChunk instanceof TerritoryChunk territoryChunk) {
            Territory territory = territoryChunk.getOwner();
            TownRelation worstRelation = territoryChunk.getOwnerInternal().getWorstRelationWith(tanPlayer);
            if (!Constants.getRelationConstants(worstRelation).canAccessTerritory()) {
                LangType lang = tanPlayer.getLang();
                TanChatUtils.message(player, Lang.PLAYER_CANNOT_ENTER_CHUNK_WITH_RELATION.get(lang,
                        territory.getColoredName(), worstRelation.getColoredName(lang)));
                return false;
            }

            if(player.isInsideVehicle() && territoryChunk.canUnauthorizedPlayerUseMounts()){
                Entity entity = player.getVehicle();
                if(entity != null){
                    if(entity instanceof Minecart && !territoryChunk.canPlayerDo(player, tanPlayer, ChunkPermissionType.INTERACT_MINECART, nextLocation)){
                        player.leaveVehicle();
                    }
                    else if(Tag.ENTITY_TYPES_BOAT.isTagged(entity.getType()) && !territoryChunk.canPlayerDo(player, tanPlayer, ChunkPermissionType.INTERACT_BOAT, nextLocation)){
                        player.leaveVehicle();
                    }
                }
            }
        }

        nextClaimedChunk.playerEnterClaimedArea(player, tanPlayer, displayTerritoryNameWithColor);

        if (nextClaimedChunk instanceof WildernessChunkData && PlayerAutoClaimStorage.containsPlayer(player)) {
            autoClaimChunk(nextChunk, player);
        }
        return true;
    }

    private void autoClaimChunk(
            final @NotNull Chunk nextChunk,
            final @NotNull Player player
    ) {
        ClaimType chunkType = PlayerAutoClaimStorage.getChunkType(player);
        ITanPlayer playerStat = playerDataStorage.get(player.getUniqueId().toString());

        if (chunkType == ClaimType.TOWN) {
            if (!playerStat.hasTown()) {
                TanChatUtils.message(player, Lang.PLAYER_NO_TOWN.get(playerStat));
                return;
            }
            playerStat.getTown().claimChunk(player, playerStat, nextChunk);
        }
        if (chunkType == ClaimType.REGION) {
            if (!playerStat.hasRegion()) {
                TanChatUtils.message(player, Lang.PLAYER_NO_REGION.get(playerStat));
                return;
            }
            playerStat.getRegion().claimChunk(player, playerStat, nextChunk);
        }
        if (chunkType == ClaimType.NATION) {
            if (!playerStat.hasNation()) {
                TanChatUtils.message(player, Lang.PLAYER_NO_NATION.get(playerStat));
                return;
            }
            playerStat.getNation().claimChunk(player, playerStat, nextChunk);
        }
    }

    /**
     * Defines what it means for two claimed chunks to be considered owned by the
     * same owner.
     * <ul>
     * <li>Both chunks are the same</li>
     * <li>Both chunks are wilderness chunks</li>
     * <li>Both chunks are territory chunks owned by the same territory</li>
     * </ul>
     * 
     * @param firstClaim  the first claimed chunk to compare
     * @param secondClaim the second claimed chunk to compare
     * @return true if both claimed chunks are considered owned by the same owner,
     *         false otherwise
     */
    public static boolean sameOwner(
            IClaimedChunk firstClaim,
            IClaimedChunk secondClaim
    ) {
        if (firstClaim == secondClaim)
            return true;
        if (firstClaim instanceof WildernessChunkData && secondClaim instanceof WildernessChunkData)
            return true;
        if (firstClaim instanceof TerritoryChunk firstTerritoryChunk
                && secondClaim instanceof TerritoryChunk secondTerritoryChunk) {
            return firstTerritoryChunk.getOwnerID().equals(secondTerritoryChunk.getOwnerID());
        }
        return false;
    }

}

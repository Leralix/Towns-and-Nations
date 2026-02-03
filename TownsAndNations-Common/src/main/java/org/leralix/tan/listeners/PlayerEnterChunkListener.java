package org.leralix.tan.listeners;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.data.chunk.ClaimedChunk;
import org.leralix.tan.data.chunk.TerritoryChunk;
import org.leralix.tan.data.chunk.WildernessChunk;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.relation.TownRelation;
import org.leralix.tan.gui.scope.ClaimType;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.PlayerAutoClaimStorage;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.text.TanChatUtils;

public class PlayerEnterChunkListener implements Listener {

    private final boolean displayTerritoryNamewithColor;
    private final NewClaimedChunkStorage newClaimedChunkStorage;
    private final PlayerDataStorage playerDataStorage;

    public PlayerEnterChunkListener(PlayerDataStorage playerDataStorage) {
        this.displayTerritoryNamewithColor = Constants.displayTerritoryColor();
        this.newClaimedChunkStorage = NewClaimedChunkStorage.getInstance();
        this.playerDataStorage = playerDataStorage;
    }

    @EventHandler
    public void playerMoveEvent(final @NotNull PlayerMoveEvent event) {

        Chunk currentChunk = event.getFrom().getChunk();
        Chunk nextChunk = event.getTo().getChunk();

        if (currentChunk.equals(nextChunk)) {
            return;
        }

        Player player = event.getPlayer();

        // If both chunks are not claimed, no need to display anything
        if (!newClaimedChunkStorage.isChunkClaimed(currentChunk) &&
                !newClaimedChunkStorage.isChunkClaimed(nextChunk)) {

            if (PlayerAutoClaimStorage.containsPlayer(event.getPlayer())) {
                autoClaimChunk(event, nextChunk, player);
            }
            return;
        }

        ClaimedChunk currentClaimedChunk = newClaimedChunkStorage.get(currentChunk);
        ClaimedChunk nextClaimedChunk = newClaimedChunkStorage.get(nextChunk);

        // Both chunks have the same owner, no need to change
        if (sameOwner(currentClaimedChunk, nextClaimedChunk)) {
            return;
        }
        // If territory denies access to players with a certain relation.
        ITanPlayer tanPlayer = playerDataStorage.get(player);

        if (nextClaimedChunk instanceof TerritoryChunk territoryChunk) {
            TownRelation worstRelation = territoryChunk.getOwnerInternal().getWorstRelationWith(tanPlayer);
            if (!Constants.getRelationConstants(worstRelation).canAccessTerritory()) {
                event.setCancelled(true);
                LangType lang = tanPlayer.getLang();
                TanChatUtils.message(player, Lang.PLAYER_CANNOT_ENTER_CHUNK_WITH_RELATION.get(lang,
                        territoryChunk.getOwner().getColoredName(), worstRelation.getColoredName(lang)));
                return;
            }
        }

        nextClaimedChunk.playerEnterClaimedArea(player, tanPlayer, displayTerritoryNamewithColor);

        if (nextClaimedChunk instanceof WildernessChunk &&
                PlayerAutoClaimStorage.containsPlayer(event.getPlayer())) {
            autoClaimChunk(event, nextChunk, player);
        }
    }

    private void autoClaimChunk(final @NotNull PlayerMoveEvent e, final @NotNull Chunk nextChunk,
            final @NotNull Player player) {
        ClaimType chunkType = PlayerAutoClaimStorage.getChunkType(e.getPlayer());
        ITanPlayer playerStat = playerDataStorage.get(player.getUniqueId().toString());

        if (chunkType == ClaimType.TOWN) {
            if (!playerStat.hasTown()) {
                TanChatUtils.message(player, Lang.PLAYER_NO_TOWN.get(playerStat));
                return;
            }
            playerStat.getTown().claimChunk(player, nextChunk);
        }
        if (chunkType == ClaimType.REGION) {
            if (!playerStat.hasRegion()) {
                TanChatUtils.message(player, Lang.PLAYER_NO_REGION.get(playerStat));
                return;
            }
            playerStat.getRegion().claimChunk(player, nextChunk);
        }
        if (chunkType == ClaimType.NATION) {
            if (!playerStat.hasNation()) {
                TanChatUtils.message(player, Lang.PLAYER_NO_NATION.get(playerStat));
                return;
            }
            playerStat.getNation().claimChunk(player, nextChunk);
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
            ClaimedChunk firstClaim,
            ClaimedChunk secondClaim) {
        if (firstClaim == secondClaim)
            return true;
        if (firstClaim instanceof WildernessChunk && secondClaim instanceof WildernessChunk)
            return true;
        if (firstClaim instanceof TerritoryChunk firstTerritoryChunk
                && secondClaim instanceof TerritoryChunk secondTerritoryChunk) {
            return firstTerritoryChunk.getOwnerID().equals(secondTerritoryChunk.getOwnerID());
        }
        return false;
    }

}

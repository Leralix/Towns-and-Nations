package org.leralix.tan.war.capture;

import org.bukkit.entity.Player;
import org.leralix.lib.position.Vector3D;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.chunk.TerritoryChunk;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.storage.CurrentAttacksStorage;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.war.War;
import org.leralix.tan.war.fort.Fort;
import org.leralix.tan.war.legacy.CurrentAttack;
import org.leralix.tan.war.legacy.WarRole;

import java.util.*;
import java.util.function.BiConsumer;

public class CaptureManager {

    private final Map<TerritoryChunk, CaptureChunk> captures = new HashMap<>();
    private final Map<String, CaptureFort> forts = new HashMap<>();

    private static CaptureManager instance;

    public static CaptureManager getInstance() {
        if(instance == null) {
            instance = new CaptureManager();
        }
        return instance;
    }

    public void updateCapture(){

        for(CaptureChunk captureChunk : captures.values()){
            captureChunk.resetPlayers();
        }

        for (CurrentAttack currentAttack : CurrentAttacksStorage.getAll()) {
            handleFortCapture(currentAttack);
            handleChunkCapture(currentAttack);
        }

        // TODO : how to delete CaptureChunk when they are done ?
        for (CaptureChunk captureChunk : new ArrayList<>(captures.values())) {
            captureChunk.update();
            if(captureChunk.isFinished()){
                captures.remove(captureChunk.getTerritoryChunk());
            }
        }
    }

    private void handleFortCapture(CurrentAttack currentAttack) {

        var attackData = currentAttack.getAttackData();

        for(Fort fortAtWar : attackData.getWar().getMainDefender().getOwnedForts()){
            forts.putIfAbsent(fortAtWar.getID(), new CaptureFort(fortAtWar, currentAttack));
        }

        registerPlayer(attackData.getAttackersPlayers(), forts, CaptureFort::addAttacker);
        registerPlayer(attackData.getDefendingPlayers(), forts, CaptureFort::addDefender);

        for(CaptureFort captureFort : forts.values()){
            captureFort.update();
        }

        for(CaptureFort captureFort : forts.values()){
            captureFort.clearPlayers();
        }
    }

    private void registerPlayer(
            Collection<ITanPlayer> players,
            Map<String, CaptureFort> forts,
            BiConsumer<CaptureFort, Player> consumer
    ) {
        for (ITanPlayer tanPlayer : players) {
            Player player = tanPlayer.getPlayer();
            if (player == null || !player.isOnline()) continue;

            Vector3D playerPosition = new Vector3D(player.getLocation());

            for (CaptureFort captureFort : forts.values()) {
                if (captureFort.getFort().getPosition().getDistance(playerPosition) < Constants.getFortCaptureRadius()) {
                    consumer.accept(captureFort, player);
                }
            }
        }
    }

    /**
     * For one attack currently ongoing, update capture progress of chunks where players are
     * @param currentAttack the attack to update
     */
    private void handleChunkCapture(CurrentAttack currentAttack) {

        var attackData = currentAttack.getAttackData();

        War warRelatedToAttack = attackData.getWar();
        for(Player player : attackData.getAllOnlinePlayers()){
            var claimedChunk = NewClaimedChunkStorage.getInstance().get(player);

            if(claimedChunk instanceof TerritoryChunk territoryChunk){
                //If chunk is surrounded by allied chunks, cannot be captured.
                if(NewClaimedChunkStorage.getInstance().isAllAdjacentChunksClaimedBySameTerritory(territoryChunk.getChunk(), territoryChunk.getOccupierID())){
                    continue;
                }

                ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);

                var occupier = territoryChunk.getOccupier();
                WarRole occupierRole = warRelatedToAttack.getTerritoryRole(occupier);
                // If territory is neutral, then do not capture it
                if(occupierRole == WarRole.NEUTRAL){
                    continue;
                }

                WarRole playerRole = warRelatedToAttack.getPlayerRole(tanPlayer);

                addPlayer(territoryChunk, player, occupierRole, playerRole, currentAttack);
            }
        }
    }

    /**
     * Register the current capture status of a territory chunk
     * @param territoryChunk    The territory chunk being captured
     * @param player            The player on the chunk
     * @param occupierRole      The war role of the chunk occupier
     * @param playerRole        The war role of the player
     */
    private void addPlayer(
            TerritoryChunk territoryChunk,
            Player player,
            WarRole occupierRole,
            WarRole playerRole,
            CurrentAttack currentAttack
    ) {
        boolean isPlayerEnemyOfOccupier = occupierRole.isOpposite(playerRole);

        if(!captures.containsKey(territoryChunk)){
            // If no captures are currently happening,
            // no need to start listening while player and occupier are on the same side
            if(!isPlayerEnemyOfOccupier){
                return;
            }
            captures.put(territoryChunk, new CaptureChunk(territoryChunk, currentAttack));
        }
        CaptureChunk captureChunk = captures.get(territoryChunk);
        if(isPlayerEnemyOfOccupier){
            captureChunk.addAttacker(player);
        }
        else {
            captureChunk.addDefender(player);
        }
    }

    /**
     * Method used to liberate all chunks and forts captured in a specific war
     * @param warEnded the finished war
     */
    public void removeCapture(War warEnded){
        String warID = warEnded.getID();

        Iterator<CaptureChunk> captureChunkIterator = captures.values().iterator();

        while (captureChunkIterator.hasNext()){
            CaptureChunk captureChunk = captureChunkIterator.next();
            if(captureChunk.getWarID().equals(warID)){
                captureChunk.warOver();
                captureChunkIterator.remove();
            }
        }

        Iterator<CaptureFort> captureFortIterator = forts.values().iterator();
        while (captureFortIterator.hasNext()){
            CaptureFort captureFort = captureFortIterator.next();
            if(captureFort.getWarID().equals(warID)){
                captureFort.warOver();
                captureFortIterator.remove();
            }
        }

        TerritoryData mainAttacker = warEnded.getMainAttacker();
        TerritoryData mainDefender = warEnded.getMainDefender();

        for(TerritoryChunk territoryChunk : NewClaimedChunkStorage.getInstance().getAllChunkFrom(mainAttacker)){
            if(territoryChunk.isOccupied() && territoryChunk.getOccupierID().equals(mainDefender.getID())){
                territoryChunk.liberate();
            }
        }

        for(TerritoryChunk territoryChunk : NewClaimedChunkStorage.getInstance().getAllChunkFrom(mainDefender)){
            if(territoryChunk.isOccupied() && territoryChunk.getOccupierID().equals(mainAttacker.getID())){
                territoryChunk.liberate();
            }
        }




    }
}

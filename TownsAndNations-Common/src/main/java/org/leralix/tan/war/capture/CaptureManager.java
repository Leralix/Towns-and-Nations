package org.leralix.tan.war.capture;

import org.bukkit.entity.Player;
import org.leralix.lib.position.Vector3D;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.chunk.TerritoryChunk;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.war.War;
import org.leralix.tan.war.fort.Fort;
import org.leralix.tan.war.legacy.CurrentAttack;

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

    public void updateCapture(CurrentAttack currentAttack){
        handleFortCapture(currentAttack);
        handleChunkCapture(currentAttack);
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

    private void handleChunkCapture(CurrentAttack currentAttack) {

        var attackData = currentAttack.getAttackData();

        for(CaptureChunk captureChunk : captures.values()){
            captureChunk.resetPlayers();
        }


        Collection<ITanPlayer> attackers = attackData.getAttackersPlayers();
        Collection<ITanPlayer> defenders = attackData.getDefendingPlayers();

        for(ITanPlayer attacker : attackers) {
            Player player = attacker.getPlayer();
            if (player == null || !player.isOnline()) {
                continue;
            }

            ClaimedChunk2 claimedChunk = NewClaimedChunkStorage.getInstance().get(player.getLocation().getChunk());

            if(claimedChunk instanceof TerritoryChunk territoryChunk){
                if(!canBeCaptured(territoryChunk,
                        attackData.getWar().getMainAttacker(),
                        attackData.getWar().getMainDefender())){
                    continue;
                }

                if(!captures.containsKey(territoryChunk)){
                    captures.putIfAbsent(territoryChunk, new CaptureChunk(0, territoryChunk, currentAttack));
                }
                captures.get(territoryChunk).addAttacker(attacker.getPlayer());
            }
        }

        for(ITanPlayer defender : defenders) {
            Player player = defender.getPlayer();
            if (player == null || !player.isOnline()) {
                continue;
            }
            ClaimedChunk2 claimedChunk = NewClaimedChunkStorage.getInstance().get(player.getLocation().getChunk());
            if(claimedChunk instanceof TerritoryChunk territoryChunk){

                if(!canBeCaptured(territoryChunk,
                        attackData.getWar().getMainAttacker(),
                        attackData.getWar().getMainDefender())  ){
                    continue;
                }

                if(!captures.containsKey(territoryChunk)){
                    captures.putIfAbsent(territoryChunk, new CaptureChunk(100, territoryChunk, currentAttack));
                }
                captures.get(territoryChunk).addDefender(defender.getPlayer());
            }
        }

        for (CaptureChunk captureChunk : new ArrayList<>(captures.values())) {
            captureChunk.update();
        }
    }

    /**
     * Check if the claimed chunk can be claimed by the main defender of the current attack.
     * A claim can be captured only if the claimed chunk is owned by the main defender
     * and is adjacent to another claimed chunk of the same territory.
     * @param territoryChunk  The claimed chunk to check
     * @param mainDefender  The main defender of the current attack
     * @return              True if the claimed chunk can be captured, false otherwise
     */
    private boolean canBeCaptured(TerritoryChunk territoryChunk, TerritoryData mainAttacker, TerritoryData mainDefender) {
        String ownerID = territoryChunk.getOwnerID();
        String occupierID = territoryChunk.getOccupierID();

        String defenderID = mainDefender.getID();
        String attackerID = mainAttacker.getID();

        if(ownerID.equals(attackerID) && occupierID.equals(defenderID)){
            return true;
        }

        if (!ownerID.equals(defenderID)) {
            return false;
        }

        boolean surroundedBySame = NewClaimedChunkStorage.getInstance()
                .isAllAdjacentChunksClaimedBySameTerritory(territoryChunk.getChunk(), defenderID);

        return !surroundedBySame;
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

package org.leralix.tan.war.capture;

import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.chunk.TerritoryChunk;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.wars.CurrentAttack;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CaptureManager {

    private final Map<TerritoryChunk, CaptureStatus> captures = new HashMap<>();


    public void updateCapture(CurrentAttack currentAttack){

        for(CaptureStatus captureStatus : captures.values()){
            captureStatus.resetPlayers();
        }
        
        Collection<ITanPlayer> attackers = currentAttack.getAttackData().getAttackersPlayers();
        Collection<ITanPlayer> defenders = currentAttack.getAttackData().getDefendingPlayers();
        TerritoryData mainAttacker = currentAttack.getAttackData().getMainAttacker();

        for(ITanPlayer attacker : attackers) {
            Player player = attacker.getPlayer();
            if (player == null || !player.isOnline()) {
                continue;
            }

            ClaimedChunk2 claimedChunk = NewClaimedChunkStorage.getInstance().get(player.getLocation().getChunk());

            if(claimedChunk instanceof TerritoryChunk territoryChunk){
                if(!canBeCaptured(territoryChunk,
                        currentAttack.getAttackData().getMainAttacker(),
                        currentAttack.getAttackData().getMainDefender())){
                    continue;
                }

                if(!captures.containsKey(territoryChunk)){
                    captures.putIfAbsent(territoryChunk, new CaptureStatus(0, territoryChunk, mainAttacker));
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
                        currentAttack.getAttackData().getMainAttacker(),
                        currentAttack.getAttackData().getMainDefender())){
                    continue;
                }

                if(!captures.containsKey(territoryChunk)){
                    captures.putIfAbsent(territoryChunk, new CaptureStatus(0, territoryChunk, mainAttacker));
                }
                captures.get(territoryChunk).addDefender(defender.getPlayer());
            }
        }

        for(CaptureStatus captureStatus : captures.values()){
            captureStatus.update();
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
}

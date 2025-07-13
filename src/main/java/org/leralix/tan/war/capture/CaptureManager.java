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


        Collection<ITanPlayer> attackers = currentAttack.getAttackData().getAttackersPlayers(); //TODO : Check if method only returns online players
        Collection<ITanPlayer> defenders = currentAttack.getAttackData().getDefendingPlayers(); //TODO : Check if method only returns online players

        for(ITanPlayer attacker : attackers) {
            Player player = attacker.getPlayer();
            if (player == null || !player.isOnline()) {
                continue;
            }

            ClaimedChunk2 claimedChunk = NewClaimedChunkStorage.getInstance().get(player.getLocation().getChunk());

            if(claimedChunk instanceof TerritoryChunk territoryChunk){
                if(!canBeCaptured(claimedChunk, currentAttack.getAttackData().getMainDefender())){
                    continue;
                }

                if(!captures.containsKey(territoryChunk)){
                    if(territoryChunk.isOccupied()){
                        captures.putIfAbsent(territoryChunk, new CaptureStatus(100, territoryChunk));
                    }
                    captures.putIfAbsent(territoryChunk, new CaptureStatus(0, territoryChunk));
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

                if(!canBeCaptured(claimedChunk, currentAttack.getAttackData().getMainDefender())){
                    continue;
                }

                if(!captures.containsKey(territoryChunk)){
                    if(territoryChunk.isOccupied()){
                        captures.putIfAbsent(territoryChunk, new CaptureStatus(100, territoryChunk));
                    }
                    captures.putIfAbsent(territoryChunk, new CaptureStatus(0, territoryChunk));
                }
                captures.get(territoryChunk).addDefender(defender.getPlayer());
            }
        }


        for(Map.Entry <TerritoryChunk,CaptureStatus> entry : captures.entrySet()){
            TerritoryChunk claimedChunk = entry.getKey();
            CaptureStatus captureStatus = entry.getValue();
            captureStatus.update();
            if(captureStatus.isCaptured()){
                claimedChunk.setOccupierID(currentAttack.getAttackData().getMainAttacker());
            } else if(captureStatus.isLiberated()){
                claimedChunk.liberate();
            }
        }
    }

    /**
     * Check if the claimed chunk can be claimed by the main defender of the current attack.
     * A claim can be captured only if the claimed chunk is owned by the main defender
     * and is adjacent to another claimed chunk of the same territory.
     * @param claimedChunk  The claimed chunk to check
     * @param mainDefender  The main defender of the current attack
     * @return              True if the claimed chunk can be captured, false otherwise
     */
    private boolean canBeCaptured(ClaimedChunk2 claimedChunk, TerritoryData mainDefender) {
        String ownerID = claimedChunk.getOwner().getID();
        String defenderID = mainDefender.getID();

        if (!ownerID.equals(defenderID)) {
            return false;
        }

        boolean surroundedBySame = NewClaimedChunkStorage.getInstance()
                .isAllAdjacentChunksClaimedBySameTerritory(claimedChunk.getChunk(), defenderID);

        return !surroundedBySame;
    }
}

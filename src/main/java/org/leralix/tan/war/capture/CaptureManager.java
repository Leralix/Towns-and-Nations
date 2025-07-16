package org.leralix.tan.war.capture;

import org.bukkit.entity.Player;
import org.leralix.lib.position.Vector3D;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.chunk.TerritoryChunk;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.wars.CurrentAttack;
import org.leralix.tan.dataclass.wars.PlannedAttack;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.utils.Constants;
import org.leralix.tan.war.fort.Fort;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class CaptureManager {

    private final Map<TerritoryChunk, CaptureChunk> captures = new HashMap<>();
    private final Map<String, CaptureFort> forts = new HashMap<>();

    public void updateCapture(CurrentAttack currentAttack){
        handleFortCapture(currentAttack);
        handleChunkCapture(currentAttack);
    }

    private void handleFortCapture(CurrentAttack currentAttack) {
        PlannedAttack attackData = currentAttack.getAttackData();
        for(Fort fortAtWar : attackData.getMainDefender().getOwnedForts()){
            forts.putIfAbsent(fortAtWar.getID(), new CaptureFort(fortAtWar, currentAttack.getAttackData().getMainAttacker()));
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

    private void registerPlayer(Collection<ITanPlayer> players,
                                Map<String, CaptureFort> forts,
                                BiConsumer<CaptureFort, Player> consumer) {
        for (ITanPlayer tanPlayer : players) {
            Player player = tanPlayer.getPlayer();
            if (player == null || !player.isOnline()) continue;

            Vector3D playerPosition = new Vector3D(player.getLocation());

            for (CaptureFort captureFort : forts.values()) {
                if (captureFort.getFort().getFlagPosition().getDistance(playerPosition) < Constants.getFortCaptureRadius()) {
                    consumer.accept(captureFort, player);
                }
            }
        }
    }

    private void handleChunkCapture(CurrentAttack currentAttack) {
        for(CaptureChunk captureChunk : captures.values()){
            captureChunk.resetPlayers();
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
                    captures.putIfAbsent(territoryChunk, new CaptureChunk(0, territoryChunk, mainAttacker));
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
                    captures.putIfAbsent(territoryChunk, new CaptureChunk(100, territoryChunk, mainAttacker));
                }
                captures.get(territoryChunk).addDefender(defender.getPlayer());
            }
        }

        for(CaptureChunk captureChunk : captures.values()){
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
}

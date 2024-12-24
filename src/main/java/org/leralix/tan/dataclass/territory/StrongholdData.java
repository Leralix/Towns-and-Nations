package org.leralix.tan.dataclass.territory;

import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.ChunkCoordinates;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.wars.AttackSide;
import org.leralix.tan.dataclass.wars.CurrentAttack;
import org.leralix.tan.storage.stored.PlayerDataStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StrongholdData {

    private ChunkCoordinates claimedChunk;
    private AttackSide controlledBy;
    private int controlLevel;

    public StrongholdData(ClaimedChunk2 claimedChunk) {
        this.claimedChunk = new ChunkCoordinates(claimedChunk.getChunk());
        this.controlledBy = AttackSide.DEFENDER;
        this.controlLevel = 0;
    }

    public Chunk getClaimedChunk() {
        return claimedChunk.getChunk();
    }


    public void updateControl(CurrentAttack currentAttack){
        Collection<PlayerData> players = getPlayersInChunk();
        int nbAttackers = 0;
        int nbDefenders = 0;
        for(PlayerData playerData : players){
            AttackSide playerSide = currentAttack.getSideOfPlayer(playerData);
            if(playerSide == AttackSide.ATTACKER){
               nbAttackers++;
            }else if(playerSide == AttackSide.DEFENDER) {
                nbDefenders++;
            }
        }
        System.out.println("Attackers: " + nbAttackers + " Defenders: " + nbDefenders);
        if(nbDefenders > nbAttackers) {
            controlLevel--;
        }
        else if(nbAttackers > nbDefenders){
            controlLevel++;
        }
        controlLevel = Math.max(0,Math.min(10,controlLevel));
        switch (controlLevel){
            case 0:
                controlledBy = AttackSide.DEFENDER;
                break;
            case 10:
                controlledBy = AttackSide.ATTACKER;
                break;
            default:
                controlledBy = AttackSide.CONTESTED;
        }
    }


    public AttackSide getHolderSide() {
        return controlledBy;
    }
    public void setHolderSide(AttackSide side){
        this.controlledBy = side;
    }

    private Collection<PlayerData> getPlayersInChunk() {

        List<PlayerData> players = new ArrayList<>();
        for(Entity entity : getClaimedChunk().getEntities()){
            if(entity instanceof Player player){
                players.add(PlayerDataStorage.get(player));
            }
        }
        return players;
    }

    public void setPosition(Chunk chunk) {
        this.claimedChunk = new ChunkCoordinates(chunk);
    }
}

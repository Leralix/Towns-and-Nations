package org.leralix.tan.dataclass.territory;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.ChunkCoordinates;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.wars.AttackSide;
import org.leralix.tan.dataclass.wars.CurrentAttack;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.ProgressBar;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.lang.Lang;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StrongholdData {

    private ChunkCoordinates claimedChunk;
    private AttackSide controlledBy;
    private int controlLevel;
    private int nbAttackers;
    private int nbDefenders;

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
        nbAttackers = 0;
        nbDefenders = 0;
        for(PlayerData playerData : players){
            AttackSide playerSide = currentAttack.getSideOfPlayer(playerData);
            if(playerSide == AttackSide.ATTACKER){
               nbAttackers++;
            }else if(playerSide == AttackSide.DEFENDER) {
                nbDefenders++;
            }
        }

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

    public int getControlLevel() {
        return controlLevel;
    }

    public void broadcastControl() {


        String part1 = Lang.ACTION_BAR_NUMBER_ATTACKER_ON_STRONGHOLD.get(nbAttackers);
        String part2 = ProgressBar.createProgressBar(controlLevel, 10, 20, ChatColor.RED, ChatColor.GREEN);
        String part3 = Lang.ACTION_BAR_NUMBER_DEFENDER_ON_STRONGHOLD.get(nbDefenders);
        String message;
        if(nbAttackers > nbDefenders){
            message = part1 + ChatColor.RED + ">>" + part2 + part3;
        }
        else if (nbDefenders > nbAttackers){
            message = part1 + part2 + ChatColor.GREEN + "<<" + part3;
        }
        else{
            message = part1 + part2 + part3;
        }

        for(PlayerData playerData : getPlayersInChunk()){
            playerData.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent(message));
        }
    }

    public void setControlLevel(int controlLevel) {
        this.controlLevel = controlLevel;
    }
}

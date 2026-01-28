package org.leralix.tan.war.capture;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.leralix.tan.data.building.fort.Fort;
import org.leralix.tan.data.chunk.TerritoryChunk;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.text.NumberUtil;
import org.leralix.tan.war.War;
import org.leralix.tan.war.attack.CurrentAttack;
import org.leralix.tan.war.info.WarRole;

import java.util.*;

public class CaptureChunk {

    /**
     * The territoryChunk related to the capture process.
     * The occupier of this chunk is used to defines if player are enemies or allies
     */
    private final TerritoryChunk territoryChunk;
    /**
     * The current attack related to the capture
     */
    private final CurrentAttack currentAttack;

    /**
     * The actual capture score of the chunk
     * <ul>
     *     <li>0: held by occupier</li>
     *     <li>{@link Constants#getChunkCaptureTime()} : control switch side</li>
     * </ul>
     */
    private int score;

    /**
     * Defines if the attack on the chunk is completed : The attackers manage to capture the chunk, and it should
     * be deleted from the attack storage.
     */
    private boolean isFinished;

    private final Set<Player> attackers;
    private final Set<Player> defenders;

    public CaptureChunk(TerritoryChunk territoryChunk, CurrentAttack currentAttack) {
        this.score = 0;
        this.territoryChunk = territoryChunk;
        this.currentAttack = currentAttack;
        this.isFinished = false;
        this.attackers = new HashSet<>();
        this.defenders = new HashSet<>();
    }

    public void addAttacker(Player player) {
        this.attackers.add(player);
    }

    public void addDefender(Player player) {
        this.defenders.add(player);
    }

    public void update() {


        Optional<Fort> fortProtectingChunk = territoryChunk.getFortProtecting();

        if (fortProtectingChunk.isEmpty()) {
            updateScore();
        }

        FilledLang message = generateMessage(fortProtectingChunk);
        List<Player> allPlayers = new ArrayList<>(attackers);
        allPlayers.addAll(defenders);

        for (Player player : allPlayers) {
            if (player == null || !player.isOnline() || player.getPlayer() == null) {
                continue;
            }
            player.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message.get(player)));
        }
    }

    public String getWarID() {
        return currentAttack.getAttackData().getWar().getID();
    }

    private FilledLang generateMessage(Optional<Fort> fortProtectingChunk) {

        String nbDefenders = Integer.toString(defenders.size());
        String nbAttackers = Integer.toString(attackers.size());

        if (fortProtectingChunk.isPresent()) {
            return Lang.WAR_INFO_CHUNK_PROTECTED.get(
                    fortProtectingChunk.get().getPosition().toString(),
                    nbAttackers,
                    nbDefenders
            );
        } else if (score > 0) {
            return Lang.WAR_INFO_CONTESTED.get(
                    getAttackingTerritory().getColoredName(),
                    NumberUtil.getPercentage(score, Constants.getChunkCaptureTime()),
                    nbAttackers,
                    nbDefenders)
                    ;
        } else {
            return Lang.WAR_INFO_CHUNK_OWNED.get(
                    territoryChunk.getOccupier().getColoredName(),
                    nbAttackers,
                    nbDefenders
            );
        }
    }

    private void updateScore() {
        if (attackers.size() > defenders.size()) {
            score += 1;
        } else if (defenders.size() > attackers.size()) {
            score -= 1;
        }

        //Clamp the score between 0 and the chunk capture time
        score = Math.clamp(score, 0, Constants.getChunkCaptureTime());

        // If capture score is full, reverse the occupation of the chunk
        if (score >= Constants.getChunkCaptureTime() && !isFinished) {
            if (territoryChunk.isOccupied()) {
                territoryChunk.liberate();
            } else {
                territoryChunk.setOccupier(getAttackingTerritory());
            }
            isFinished = true;
        }
    }

    public void resetPlayers() {
        this.attackers.clear();
        this.defenders.clear();
    }

    /**
     * If the war is over, restitute the chunk to its original owner.
     */
    public void warOver() {
        resetPlayers();
        territoryChunk.liberate();
    }

    public boolean isFinished() {
        return isFinished;
    }

    public TerritoryChunk getTerritoryChunk() {
        return territoryChunk;
    }

    /**
     * Get the attack territory trying to capture the chunk.
     * Depending on who owns the chunk, it will be the main defender or main attacker.
     *
     * @return The attacking territory
     */
    public TerritoryData getAttackingTerritory() {

        War war = currentAttack.getAttackData().getWar();

        WarRole warRole = war.getTerritoryRole(territoryChunk.getOccupierInternal());
        return switch (warRole) {
            case MAIN_ATTACKER, OTHER_ATTACKER -> war.getMainDefender();
            case MAIN_DEFENDER, OTHER_DEFENDER, NEUTRAL -> war.getMainAttacker();
        };
    }
}

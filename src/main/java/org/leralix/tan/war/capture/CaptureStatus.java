package org.leralix.tan.war.capture;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.chunk.TerritoryChunk;
import org.leralix.tan.lang.Lang;

import java.util.ArrayList;
import java.util.List;

public class CaptureStatus {

    private final TerritoryChunk territoryChunk;
    private int score;
    private final int maxScore = 100;
    private final List<Player> attackers;
    private final List<Player> defenders;

    public CaptureStatus(int initialScore, TerritoryChunk territoryChunk) {
        this.score = initialScore;
        this.attackers = new ArrayList<>();
        this.defenders = new ArrayList<>();
        this.territoryChunk = territoryChunk;
    }

    public boolean isCaptured() {
        return score >= maxScore;
    }

    public boolean isLiberated() {
        return score <= 0;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void addAttacker(Player player) {
        this.attackers.add(player);
    }

    public void addDefender(Player player) {
        this.defenders.add(player);
    }

    public void update() {

        updateScore();

        String message = generateMessage();

        List<Player> allPlayers = new ArrayList<>(attackers);
        allPlayers.addAll(defenders);

        for (Player player : allPlayers) {
            if (player == null || !player.isOnline() || player.getPlayer() == null) {
                continue;
            }
            player.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
        }

    }

    private String generateMessage() {

        String message;
        int nbDefenders = defenders.size();
        int nbAttackers = attackers.size();

        if(isCaptured()){
            message = Lang.WAR_INFO_CHUNK_CAPTURED.get(territoryChunk.getOccupier().getColoredName(), nbAttackers, nbDefenders);
        }
        else if (isLiberated()){
            message = Lang.WAR_INFO_CHUNK_OWNED.get(score, nbAttackers, nbDefenders);
        }
        else {
            message = Lang.WAR_INFO_CONTESTED.get(score, nbAttackers, nbDefenders);
        }

        return message;
    }

    private void updateScore() {
        if (attackers.size() > defenders.size()) {
            score += 10;
        } else if (defenders.size() > attackers.size()) {
            score -= 10;
        }

        if (score < 0) {
            score = 0;
        } else if (score > maxScore) {
            score = maxScore;
        }
    }


    public void resetPlayers() {
        this.attackers.clear();
        this.defenders.clear();
    }
}

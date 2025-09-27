package org.leralix.tan.war.capture;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.chunk.TerritoryChunk;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.war.fort.Fort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CaptureChunk {

    /**
     * The ID of the war related to this capture.
     */
    private final String warID;
    private final TerritoryChunk territoryChunk;
    private int score;
    private final int maxScore = 100;
    private final TerritoryData mainAttacker;
    private final List<Player> attackers;
    private final List<Player> defenders;

    public CaptureChunk(int initialScore, TerritoryChunk territoryChunk, TerritoryData mainAttacker, String id) {
        this.score = initialScore;
        this.attackers = new ArrayList<>();
        this.defenders = new ArrayList<>();
        this.territoryChunk = territoryChunk;
        this.mainAttacker = mainAttacker;
        this.warID = id;
    }

    public boolean isCaptured() {
        return score >= maxScore;
    }

    public boolean isLiberated() {
        return score <= 0;
    }

    public void addAttacker(Player player) {
        this.attackers.add(player);
    }

    public void addDefender(Player player) {
        this.defenders.add(player);
    }

    public void update() {

        Optional<Fort> fortProtectingChunk = getFortProtecting();

        if (fortProtectingChunk.isEmpty()) {
            updateScore();
        }

        String message = generateMessage(fortProtectingChunk);
        List<Player> allPlayers = new ArrayList<>(attackers);
        allPlayers.addAll(defenders);

        for (Player player : allPlayers) {
            if (player == null || !player.isOnline() || player.getPlayer() == null) {
                continue;
            }
            player.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
        }
    }

    public String getWarID() {
        return warID;
    }

    private Optional<Fort> getFortProtecting() {
        for (Fort fort : territoryChunk.getOccupier().getAllControlledFort()) {
            if (fort.getPosition().getDistance(territoryChunk.getMiddleVector2D()) <= Constants.getFortProtectionRadius()) {
                return Optional.of(fort);
            }
        }
        return Optional.empty();
    }

    private String generateMessage(Optional<Fort> fortProtectingChunk) {

        String message;
        String nbDefenders = Integer.toString(defenders.size());
        String nbAttackers = Integer.toString(attackers.size());

        if (fortProtectingChunk.isPresent()) {
            message = Lang.WAR_INFO_CHUNK_PROTECTED.get(Lang.getServerLang(), fortProtectingChunk.get().getPosition().toString(), nbAttackers, nbDefenders);
        } else if (nbAttackers == nbDefenders) {
            message = Lang.WAR_INFO_CONTESTED.get(Lang.getServerLang(), Integer.toString(score), nbAttackers, nbDefenders);
        } else if (isCaptured()) {
            message = Lang.WAR_INFO_CHUNK_CAPTURED.get(Lang.getServerLang(), territoryChunk.getOccupier().getColoredName(), nbAttackers, nbDefenders);
        } else if (isLiberated()) {
            message = Lang.WAR_INFO_CHUNK_OWNED.get(Lang.getServerLang(), nbAttackers, nbDefenders);
        } else {
            message = Lang.WAR_INFO_CONTESTED.get(Lang.getServerLang(), Integer.toString(score), nbAttackers, nbDefenders);
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
            territoryChunk.liberate();
        } else if (score > maxScore) {
            score = maxScore;
            territoryChunk.setOccupier(mainAttacker);
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
}

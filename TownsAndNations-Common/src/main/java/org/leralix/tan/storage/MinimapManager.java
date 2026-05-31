package org.leralix.tan.storage;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.utils.text.ChatChunkMapRenderer;

import java.util.*;

public class MinimapManager {

    public Set<UUID> subscribedPlayers;

    public MinimapManager() {
        this.subscribedPlayers = new HashSet<>();
    }

    public void addPlayer(UUID playerID) {
        this.subscribedPlayers.add(playerID);
    }

    public void removePlayer(UUID playerID) {
        this.subscribedPlayers.remove(playerID);
        Player player = Bukkit.getPlayer(playerID);
        if (player != null) {
            Scoreboard scoreboard = player.getScoreboard();
            Objective objective = scoreboard.getObjective("tan minimap");
            if (objective != null) {
                objective.unregister();
            }
            scoreboard.clearSlot(DisplaySlot.SIDEBAR);
        }
    }

    public void displayMap() {
        for (UUID uuid : new ArrayList<>(subscribedPlayers)) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                removePlayer(uuid);
                continue;
            }
            displayMap(player);
        }
    }

    public void displayMap(Player player) {

        Scoreboard scoreboard = player.getScoreboard();

        Objective objective = scoreboard.getObjective("minimap");

        if (objective == null) {
            objective = scoreboard.registerNewObjective(
                    "minimap",
                    Criteria.DUMMY,
                    Component.text("Towns and Nations")
            );
        }

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        for (String entry : scoreboard.getEntries()) {
            scoreboard.resetScores(entry);
        }

        List<Component> map = ChatChunkMapRenderer.getMapLines(
                player,
                4,
                LangType.ENGLISH,
                (chunkX, chunkZ) -> "/tan ",
                new HashMap<>()
        );

        int i = 9;

        for (Component component : map) {

            String text = LegacyComponentSerializer.legacySection().serialize(component);
            String entry = text + i;
            objective.getScore(entry).setScore(i);
            i--;
        }
    }

    public boolean contains(@NotNull UUID uniqueId) {
        return subscribedPlayers.contains(uniqueId);
    }
}

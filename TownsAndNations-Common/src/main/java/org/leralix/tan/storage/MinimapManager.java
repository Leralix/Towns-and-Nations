package org.leralix.tan.storage;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.text.ChatChunkMapRenderer;

import java.util.*;

public class MinimapManager {

    private final PlayerDataStorage playerDataStorage;

    private final Set<UUID> subscribedPlayers;

    public MinimapManager(PlayerDataStorage playerDataStorage) {
        this.subscribedPlayers = new HashSet<>();
        this.playerDataStorage = playerDataStorage;
    }

    public void addPlayer(UUID playerID) {
        this.subscribedPlayers.add(playerID);
    }

    public void removePlayer(UUID playerID) {
        this.subscribedPlayers.remove(playerID);
        Player player = Bukkit.getPlayer(playerID);
        if (player != null) {
            Scoreboard scoreboard = player.getScoreboard();
            Objective objective = scoreboard.getObjective("TAN_MINIMAP");
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
            displayMinimap(player);
        }
    }

    public void displayMinimapIfSubscribed(Player player) {
        if(subscribedPlayers.contains(player.getUniqueId())){
            displayMinimap(player);
        }
    }

    private void displayMinimap(Player player) {

        Scoreboard scoreboard = player.getScoreboard();

        Objective objective = scoreboard.getObjective("TAN_MINIMAP");

        LangType langType = playerDataStorage.get(player).getLang();
        TextComponent title = Component
                .text(Lang.MINIMAP_TITLE.get(langType))
                .decorate(TextDecoration.BOLD);

        if (objective == null) {
            objective = scoreboard.registerNewObjective(
                    "TAN_MINIMAP",
                    Criteria.DUMMY,
                    title
            );
        }

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        for (String entry : scoreboard.getEntries()) {
            scoreboard.resetScores(entry);
        }

        List<Component> map = ChatChunkMapRenderer.getMapLines(
                player,
                4,
                langType,
                (chunkX, chunkZ) -> "/tan ",
                new HashMap<>()
        );

        int i = 9;

        for (Component component : map) {

            String text = LegacyComponentSerializer.legacySection().serialize(component);
            String entry = text + "§" + Integer.toHexString(i);
            objective.getScore(entry).setScore(i);
            i--;
        }
    }

    public boolean contains(@NotNull UUID uniqueId) {
        return subscribedPlayers.contains(uniqueId);
    }
}

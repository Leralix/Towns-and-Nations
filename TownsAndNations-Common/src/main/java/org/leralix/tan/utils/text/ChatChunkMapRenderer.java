package org.leralix.tan.utils.text;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.chunk.TerritoryChunk;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;

import java.util.Map;
import java.util.function.BiFunction;

public final class ChatChunkMapRenderer {

    private ChatChunkMapRenderer() {
        throw new IllegalStateException("Utility class");
    }

    public static void sendChunkMap(
            Player player,
            int radius,
            LangType langType,
            BiFunction<Integer, Integer, String> clickCommand,
            Map<Integer, TextComponent> extraByDz
    ) {
        Chunk currentChunk = player.getLocation().getChunk();

        // Envoi de l'en-tête
        player.sendMessage("╭─────────⟢⟐⟣─────────╮");
        for (int dz = -radius; dz <= radius; dz++) {
            TextComponent newLine = new TextComponent();
            newLine.addExtra("   ");
            for (int dx = -radius; dx <= radius; dx++) {
                int chunkX = currentChunk.getX() + dx;
                int chunkZ = currentChunk.getZ() + dz;

                ClaimedChunk2 claimedChunk = NewClaimedChunkStorage.getInstance().get(chunkX, chunkZ, player.getWorld().getUID().toString());
                TextComponent icon = claimedChunk.getMapIcon(langType);

                if (dx == 0 && dz == 0) {
                    if (claimedChunk instanceof TerritoryChunk territoryChunk && territoryChunk.isOccupied()) {
                        icon.setText("🟠"); //Hashed orange square emoji
                    } else {
                        icon.setText("🌑"); // For some reason, the only round emoji with the same size as ⬛ is this emoji
                    }
                }

                icon.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, clickCommand.apply(chunkX, chunkZ)));
                newLine.addExtra(icon);
            }

            TextComponent extra = extraByDz.get(dz);
            if (extra != null) {
                newLine.addExtra(extra);
            }

            player.spigot().sendMessage(newLine);
        }
        player.sendMessage("╰─────────⟢⟐⟣─────────╯");
    }
}

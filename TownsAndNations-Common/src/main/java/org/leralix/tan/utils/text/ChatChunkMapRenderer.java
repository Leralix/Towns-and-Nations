package org.leralix.tan.utils.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.chunk.IClaimedChunk;
import org.leralix.tan.lang.LangType;

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
            Component newLine = Component.text("   ");
            for (int dx = -radius; dx <= radius; dx++) {
                int chunkX = currentChunk.getX() + dx;
                int chunkZ = currentChunk.getZ() + dz;

                IClaimedChunk claimedChunk = TownsAndNations.getPlugin().getClaimStorage().get(chunkX, chunkZ, player.getWorld().getUID().toString());

                boolean ifMiddleOfMap = dx == 0 && dz == 0;

                newLine = newLine.append(
                        claimedChunk.getMapIcon(langType, ifMiddleOfMap)
                        .clickEvent(
                                net.kyori.adventure.text.event.ClickEvent.clickEvent(
                                        net.kyori.adventure.text.event.ClickEvent.Action.RUN_COMMAND,
                                        ClickEvent.Payload.string(clickCommand.apply(chunkX, chunkZ))
                                )
                        )
                );
            }

//            TextComponent extra = extraByDz.get(dz);
//            if (extra != null) {
//                newLine.addExtra(extra);
//            }

            player.sendMessage(newLine);
        }
        player.sendMessage("╰─────────⟢⟐⟣─────────╯");
    }
}

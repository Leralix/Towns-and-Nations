package org.leralix.tan.data.chunk;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.lang.Lang;

public final class TerritoryEnterMessageUtil {

    private TerritoryEnterMessageUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static void sendEnterTerritoryMessage(Player player, TerritoryData territoryData, boolean displayTerritoryColor) {
        TextComponent name = displayTerritoryColor
                ? territoryData.getCustomColoredName()
                : new TextComponent(territoryData.getColoredName());

        String message = Lang.PLAYER_ENTER_TERRITORY_CHUNK.get(player, name.toLegacyText());
        player.sendTitle("", message, 5, 40, 20);

        TextComponent textComponent = new TextComponent(territoryData.getDescription());
        textComponent.setColor(ChatColor.GRAY);
        textComponent.setItalic(true);

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, textComponent);
    }
}

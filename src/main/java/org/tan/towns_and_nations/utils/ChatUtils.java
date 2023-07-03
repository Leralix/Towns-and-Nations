package org.tan.towns_and_nations.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ChatUtils {


    public static void sendClickableCommand(Player player, String message, String command) {
        // Make a new component (Bungee API).
        TextComponent component = new TextComponent(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', message)));
        // Add a click event to the component.
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + command));

        // Send it!
        player.spigot().sendMessage(component);
    }

    @Contract(pure = true)
    public static @NotNull String getTANString(){
        return org.bukkit.ChatColor.GOLD + "[TAN]" + org.bukkit.ChatColor.WHITE;
    }

    public static @NotNull String getTANDebugString(){
        return org.bukkit.ChatColor.RED + "[TAN - DEBUG]" + org.bukkit.ChatColor.WHITE;
    }

}

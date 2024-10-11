package org.leralix.tan.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.lang.Lang;

/**
 * This class is used for chat related utilities.
 */
public class ChatUtils {

    /**
     * This method is used to send a clickable message to a player.
     * @param player    The player to send the message to.
     * @param message   The message to send.
     * @param command   The command to run when the message is clicked without the "/" (exemple : tp 0 0 0)
     */
    public static void sendClickableCommand(final @NotNull Player player, final @NotNull String message, final @NotNull String command) {
        TextComponent component = new TextComponent(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', message)));
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + command));
        player.spigot().sendMessage(component);
    }

    /**
     * This method send the prefix for every chat message in the plugin.
     * @return the prefix for every chat message in the plugin.
     */
    public static String getTANString(){
        return Lang.PLUGIN_STRING.get();
    }

    /**
     * This method send the prefix for every debug or admin chat message in the plugin.
     * @return the prefix for every debug or admin chat message in the plugin.
     */
    public static String getTANDebugString(){
        return Lang.PLUGIN_DEBUG_STRING.get();
    }

}

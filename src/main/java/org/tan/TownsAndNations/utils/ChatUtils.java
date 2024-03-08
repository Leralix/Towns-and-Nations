package org.tan.TownsAndNations.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.tan.TownsAndNations.Lang.Lang;

public class ChatUtils {


    public static void sendClickableCommand(Player player, String message, String command) {
        TextComponent component = new TextComponent(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', message)));
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + command));
        player.spigot().sendMessage(component);
    }

    @Contract(pure = true)
    public static @NotNull String getTANString(){
        return "" + Lang.PLUGIN_STRING.get();
    }

    public static @NotNull String getTANDebugString(){
        return "" + Lang.PLUGIN_DEBUG_STRING.get();
    }

}

package org.leralix.tan.utils.text;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;

/**
 * This class is used for chat related utilities.
 */
public class TanChatUtils {

    private TanChatUtils() {
        throw new IllegalStateException("Utility class");
    }


    public static void message(CommandSender commandSender, Lang lang) {
        message(commandSender, lang, null);
    }

    public static void message(CommandSender commandSender, Lang message, SoundEnum soundEnum) {

        if (commandSender == null) {
            return;
        }

        if (commandSender instanceof Player player) {
            ITanPlayer playerData = PlayerDataStorage.getInstance().get(player);
            message(player, message.get(playerData), soundEnum);
        }
        commandSender.sendMessage(message.getDefault());
    }

    public static void message(CommandSender commandSender, FilledLang message) {
        message(commandSender, message, null);
    }

    public static void message(CommandSender commandSender, FilledLang message, SoundEnum soundEnum) {
        if (commandSender instanceof Player player) {
            message(player, message.get(player), soundEnum);
        }
        else if(commandSender != null){
            commandSender.sendMessage(message.getDefault());
        }
    }

    public static void message(Player player, String message) {
        if (player == null) {
            return;
        }
        player.sendMessage(Lang.PLUGIN_STRING.getDefault() + message);
    }

    public static void message(Player player, String message, SoundEnum soundEnum) {
        TanChatUtils.message(player, message);
        if (soundEnum != null && player != null) {
            SoundUtil.playSound(player, soundEnum);
        }
    }

}

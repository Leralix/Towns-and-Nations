package org.leralix.tan.utils.text;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.lang.Placeholder;
import org.leralix.tan.storage.stored.PlayerDataStorage;

/**
 * This class is used for chat related utilities.
 */
public class TanChatUtils {

    private static PlayerDataStorage playerDataStorage;

    private TanChatUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static void init(PlayerDataStorage playerDataStorageInstance){
        playerDataStorage = playerDataStorageInstance;
    }

    public static void message(CommandSender commandSender, Placeholder message) {
        message(commandSender, message, null);
    }

    public static void message(ITanPlayer playerData, Placeholder message, SoundEnum soundEnum) {
        message(playerData.getPlayer(), message.translate(playerData.getLang()), soundEnum);
    }

    public static void message(ITanPlayer playerData, Placeholder message) {
        message(playerData, message, null);
    }

    public static void message(CommandSender commandSender, Placeholder message, SoundEnum soundEnum) {
        if (commandSender instanceof Player player) {
            LangType langType = playerDataStorage.get(player).getLang();
            message(player, message.translate(langType), soundEnum);
        }
        else if(commandSender != null){
            commandSender.sendMessage(message.translateDefault());
        }
    }

    public static void message(Player player, String message) {
        message(player, message, null);
    }

    public static void message(Player player, String message, @Nullable SoundEnum soundEnum) {
        if (player == null) {
            return;
        }
        player.sendMessage(Lang.PLUGIN_STRING.getDefault() + message);

        if (soundEnum != null) {
            SoundUtil.playSound(player, soundEnum);
        }
    }

}

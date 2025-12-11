package org.leralix.tan.storage;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.file.FileUtil;
import org.leralix.tan.utils.text.TanChatUtils;

public class SudoPlayerStorage {

  private static final List<String> sudoPlayersID = new ArrayList<>();

  public static void addSudoPlayer(Player player) {
    addSudoPlayer(player.getUniqueId().toString());
  }

  public static void addSudoPlayer(String playerID) {
    sudoPlayersID.add(playerID);
  }

  public static void removeSudoPlayer(Player player) {
    removeSudoPlayer(player.getUniqueId().toString());
  }

  public static void removeSudoPlayer(String playerID) {
    sudoPlayersID.remove(playerID);
  }

  public static boolean isSudoPlayer(Player player) {
    return isSudoPlayer(player.getUniqueId().toString());
  }

  public static boolean isSudoPlayer(String playerID) {
    return sudoPlayersID.contains(playerID);
  }

  public static void swap(Player player) {
    if (sudoPlayersID.contains(player.getUniqueId().toString())) {
      removeSudoPlayer(player);
      TanChatUtils.message(
          player, Lang.SUDO_PLAYER_REMOVED.get(player, player.getName()), SoundEnum.MINOR_GOOD);
      FileUtil.addLineToHistory(
          Lang.HISTORY_SUDO_MODE_REMOVED.get(player.getName(), player.getName()));
    } else {
      addSudoPlayer(player);
      TanChatUtils.message(
          player, Lang.SUDO_PLAYER_ADDED.get(player, player.getName()), SoundEnum.MINOR_GOOD);
      FileUtil.addLineToHistory(Lang.HISTORY_SUDO_MODE.get(player.getName(), player.getName()));
    }
  }

  public static void swap(CommandSender commandSender, Player target) {
    if (sudoPlayersID.contains(target.getUniqueId().toString())) {
      removeSudoPlayer(target);
      TanChatUtils.message(
          commandSender, Lang.SUDO_PLAYER_REMOVED.get(target.getName()), SoundEnum.MINOR_GOOD);
      TanChatUtils.message(
          target, Lang.SUDO_PLAYER_REMOVED.get(target, target.getName()), SoundEnum.MINOR_GOOD);
      FileUtil.addLineToHistory(
          Lang.HISTORY_SUDO_MODE_REMOVED.get(commandSender.getName(), target.getName()));
    } else {
      addSudoPlayer(target);
      TanChatUtils.message(
          commandSender, Lang.SUDO_PLAYER_ADDED.get(target.getName()), SoundEnum.MINOR_GOOD);
      TanChatUtils.message(
          target, Lang.SUDO_PLAYER_ADDED.get(target, target.getName()), SoundEnum.MINOR_GOOD);
      FileUtil.addLineToHistory(
          Lang.HISTORY_SUDO_MODE.get(commandSender.getName(), target.getName()));
    }
  }
}

package org.tan.core.commands.admin;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.leralix.lib.commands.SubCommand;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.SudoPlayerStorage;
import org.leralix.tan.utils.text.TanChatUtils;

public class SudoPlayer extends SubCommand {

  @Override
  public String getName() {
    return "sudo";
  }

  @Override
  public String getDescription() {
    return Lang.ADMIN_SUDO_COMMAND.getDefault();
  }

  @Override
  public int getArguments() {
    return 2;
  }

  @Override
  public String getSyntax() {
    return "/tanadmin sudo <optional - player> ";
  }

  public List<String> getTabCompleteSuggestions(
      CommandSender player, String lowerCase, String[] args) {

    List<String> suggestions = new ArrayList<>();
    if (args.length == 2) {
      for (Player p : Bukkit.getOnlinePlayers()) {
        suggestions.add(p.getName());
      }
    }
    return suggestions;
  }

  @Override
  public void perform(CommandSender commandSender, String[] args) {

    if (args.length == 1) {
      if (commandSender instanceof Player player) {
        SudoPlayerStorage.swap(player);
      }
    } else if (args.length == 2) {
      Player target = Bukkit.getPlayer(args[1]);
      if (target == null) {
        TanChatUtils.message(commandSender, Lang.PLAYER_NOT_FOUND, SoundEnum.NOT_ALLOWED);
        return;
      }
      if (commandSender instanceof Player player
          && target.getUniqueId().equals(player.getUniqueId())) {
        SudoPlayerStorage.swap(player);
        return;
      }
      SudoPlayerStorage.swap(commandSender, target);

    } else {
      TanChatUtils.message(commandSender, Lang.NOT_ENOUGH_ARGS_ERROR, SoundEnum.NOT_ALLOWED);
      TanChatUtils.message(commandSender, Lang.CORRECT_SYNTAX_INFO);
    }
  }
}

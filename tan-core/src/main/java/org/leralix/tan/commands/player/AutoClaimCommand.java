package org.leralix.tan.commands.player;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.enums.ChunkType;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.PlayerAutoClaimStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.commands.CommandExceptionHandler;
import org.leralix.tan.utils.text.TanChatUtils;

public class AutoClaimCommand extends PlayerSubCommand {
  @Override
  public String getName() {
    return "autoclaim";
  }

  @Override
  public String getDescription() {
    return Lang.TOWN_AUTO_CLAIM_DESC.getDefault();
  }

  public int getArguments() {
    return 1;
  }

  @Override
  public String getSyntax() {
    return "/tan autoclaim <chunk type>";
  }

  @Override
  public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args) {
    List<String> suggestions = new ArrayList<>();
    if (args.length == 2) {
      for (ChunkType chunkType : ChunkType.values()) {
        suggestions.add(chunkType.getName());
      }
      suggestions.add("stop");
    }
    return suggestions;
  }

  @Override
  public void perform(Player player, String[] args) {
    // Validate argument count
    if (!CommandExceptionHandler.validateArgCount((CommandSender) player, args, 2, getSyntax())) {
      return;
    }

    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(player);
    LangType langType = tanPlayer.getLang();

    String message = args[1];

    switch (message) {
      case "town" -> {
        PlayerAutoClaimStorage.addPlayer(player, ChunkType.TOWN);
        TanChatUtils.message(
            player, Lang.AUTO_CLAIM_ON_FOR.get(langType, ChunkType.TOWN.getName()));
      }
      case "region" -> {
        PlayerAutoClaimStorage.addPlayer(player, ChunkType.REGION);
        TanChatUtils.message(
            player, Lang.AUTO_CLAIM_ON_FOR.get(langType, ChunkType.REGION.getName()));
      }
      case "stop" -> {
        PlayerAutoClaimStorage.removePlayer(player);
        TanChatUtils.message(player, Lang.AUTO_CLAIM_OFF.get(langType));
      }
      default -> TanChatUtils.message(player, Lang.CORRECT_SYNTAX_INFO.get(langType, getSyntax()));
    }
  }
}

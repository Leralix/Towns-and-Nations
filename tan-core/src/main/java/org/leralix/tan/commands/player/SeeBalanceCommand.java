package org.leralix.tan.commands.player;

import java.util.Collections;
import java.util.List;
import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.text.TanChatUtils;

public class SeeBalanceCommand extends PlayerSubCommand {
  @Override
  public String getName() {
    return "balance";
  }

  @Override
  public String getDescription() {
    return Lang.BAL_COMMAND_DESC.getDefault();
  }

  public int getArguments() {
    return 1;
  }

  @Override
  public String getSyntax() {
    return "/tan balance";
  }

  public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args) {
    return Collections.emptyList();
  }

  @Override
  public void perform(Player player, String[] args) {
    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(player);
    LangType langType = tanPlayer.getLang();
    if (args.length == 1) {
      TanChatUtils.message(
          player,
          Lang.BAL_AMOUNT.get(langType, Double.toString(EconomyUtil.getBalance(tanPlayer))));
    } else if (args.length > 1) {
      TanChatUtils.message(player, Lang.TOO_MANY_ARGS_ERROR.get(langType));
      TanChatUtils.message(player, Lang.CORRECT_SYNTAX_INFO.get(langType, getSyntax()));
    }
  }
}

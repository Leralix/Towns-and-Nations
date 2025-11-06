package org.leralix.tan.commands.admin;

import java.util.Collections;
import java.util.List;
import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.tan.gui.admin.AdminMainMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.utils.text.TanChatUtils;

public class OpenAdminGUI extends PlayerSubCommand {
  @Override
  public String getName() {
    return "gui";
  }

  @Override
  public String getDescription() {
    return Lang.ADMIN_OPEN_GUI.getDefault();
  }

  public int getArguments() {
    return 2;
  }

  @Override
  public String getSyntax() {
    return "/tanadmin gui";
  }

  @Override
  public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args) {
    return Collections.emptyList();
  }

  @Override
  public void perform(Player player, String[] args) {
    if (args.length == 1) {
      new AdminMainMenu(player);
    } else if (args.length > 1) {
      LangType langType = LangType.of(player);
      TanChatUtils.message(player, Lang.TOO_MANY_ARGS_ERROR.get(langType));
      TanChatUtils.message(player, Lang.CORRECT_SYNTAX_INFO.get(langType, getSyntax()));
    }
  }
}

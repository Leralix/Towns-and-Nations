package org.leralix.tan.commands.player;

import java.util.Collections;
import java.util.List;
import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.gui.user.player.NewsletterMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.text.TanChatUtils;

public class OpenNewsletterCommand extends PlayerSubCommand {

  @Override
  public String getName() {
    return "newsletter";
  }

  @Override
  public String getDescription() {
    return Lang.OPEN_NEWSLETTER_DESC.getDefault();
  }

  public int getArguments() {
    return 1;
  }

  @Override
  public String getSyntax() {
    return "/tan newsletter";
  }

  @Override
  public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args) {
    return Collections.emptyList();
  }

  @Override
  public void perform(Player player, String[] args) {

    if (args.length != 1) {
      ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(player);
      LangType langType = tanPlayer.getLang();
      TanChatUtils.message(player, Lang.CORRECT_SYNTAX_INFO.get(langType, getSyntax()));
      return;
    }
    NewsletterMenu.open(player);
  }
}

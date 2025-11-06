package org.leralix.tan.commands.admin;

import java.io.File;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.leralix.lib.commands.SubCommand;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.lang.DynamicLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.ClaimBlacklistStorage;
import org.leralix.tan.storage.MobChunkSpawnStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.constants.EnabledPermissions;
import org.leralix.tan.utils.text.NumberUtil;
import org.leralix.tan.utils.text.TanChatUtils;

public class ReloadCommand extends SubCommand {
  @Override
  public String getName() {
    return "reload";
  }

  @Override
  public String getDescription() {
    return Lang.ADMIN_RELOAD_COMMAND.getDefault();
  }

  public int getArguments() {
    return 1;
  }

  @Override
  public String getSyntax() {
    return "/tanadmin reload";
  }

  @Override
  public List<String> getTabCompleteSuggestions(
      CommandSender player, String lowerCase, String[] args) {
    return Collections.emptyList();
  }

  @Override
  public void perform(CommandSender commandSender, String[] args) {
    if (args.length == 1) {
      Plugin plugin = TownsAndNations.getPlugin();
      ConfigUtil.addCustomConfig(plugin, "config.yml", ConfigTag.MAIN);
      ConfigUtil.addCustomConfig(plugin, "upgrades.yml", ConfigTag.UPGRADE);
      ConfigUtil.addCustomConfig(plugin, "lang.yml", ConfigTag.LANG);

      String lang = ConfigUtil.getCustomConfig(ConfigTag.LANG).getString("language");
      File langFolder = new File(TownsAndNations.getPlugin().getDataFolder(), "lang");

      Lang.loadTranslations(langFolder, lang);
      DynamicLang.loadTranslations(langFolder, lang);

      Constants.init(ConfigUtil.getCustomConfig(ConfigTag.MAIN));

      MobChunkSpawnStorage.init();
      ClaimBlacklistStorage.init();
      NumberUtil.init();
      EnabledPermissions.getInstance().init();

      TanChatUtils.message(commandSender, Lang.RELOAD_SUCCESS);
      TanChatUtils.message(commandSender, Lang.LANGUAGE_SUCCESSFULLY_LOADED);
    } else {
      TanChatUtils.message(commandSender, Lang.TOO_MANY_ARGS_ERROR);
      TanChatUtils.message(commandSender, Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
    }
  }
}

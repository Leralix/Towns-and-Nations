package org.leralix.tan.commands.admin;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.leralix.lib.commands.SubCommand;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.lang.DynamicLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.ClaimBlacklistStorage;
import org.leralix.tan.storage.impl.FortDataStorage;
import org.leralix.tan.storage.stored.FortStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.file.FileUtil;
import org.leralix.tan.utils.text.NameFilter;
import org.leralix.tan.utils.text.NumberUtil;
import org.leralix.tan.utils.text.TanChatUtils;

import java.io.File;
import java.util.Collections;
import java.util.List;

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
    public List<String> getTabCompleteSuggestions(CommandSender player, String lowerCase, String[] args) {
        return Collections.emptyList();
    }

    @Override
    public void perform(CommandSender commandSender, String[] args) {
        if (args.length == 1) {
            Plugin plugin = TownsAndNations.getPlugin();

            YamlConfiguration langConfig = ConfigUtil.saveAndUpdateResource(TownsAndNations.getPlugin(), "lang.yml", Collections.emptyList());
            String lang = langConfig.getString("language", "en");
            File langFolder = new File(TownsAndNations.getPlugin().getDataFolder(), "lang");
            Lang.loadTranslations(langFolder, lang);
            DynamicLang.loadTranslations(langFolder, lang);

            List<String> mainBlackList = List.of(
                    "claimBlacklist",
                    "wildernessRules",
                    "townPermissions",
                    "regionPermissions",
                    "propertyPermissions"
            );
            YamlConfiguration mainConfig = ConfigUtil.saveAndUpdateResource(plugin, "config.yml", mainBlackList);
            YamlConfiguration upgradesConfig =  ConfigUtil.saveAndUpdateResource(plugin, "upgrades.yml", Collections.singletonList("upgrades"));

            Constants.init(mainConfig, upgradesConfig);
            NameFilter.reload(mainConfig);
            ClaimBlacklistStorage.init(mainConfig);
            IconManager.getInstance();
            NumberUtil.init();
            FortStorage.init(new FortDataStorage());
            FileUtil.setEnable(mainConfig.getBoolean("archiveHistory", false));

            TanChatUtils.message(commandSender, Lang.RELOAD_SUCCESS);
            TanChatUtils.message(commandSender, Lang.LANGUAGE_SUCCESSFULLY_LOADED);
        } else {
            TanChatUtils.message(commandSender, Lang.TOO_MANY_ARGS_ERROR);
            TanChatUtils.message(commandSender, Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
        }
    }

}

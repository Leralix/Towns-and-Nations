package org.leralix.tan.commands.debug;

import java.util.Collections;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.leralix.lib.commands.SubCommand;
import org.leralix.tan.utils.LogUtil;

public class LogLevelCommand extends SubCommand {

  @Override
  public String getName() {
    return "loglevel";
  }

  @Override
  public String getDescription() {
    return "Sets the log level";
  }

  @Override
  public int getArguments() {
    return 1;
  }

  @Override
  public String getSyntax() {
    return "/tandebug loglevel <level>";
  }

  public List<String> getTabCompleteSuggestions(
      CommandSender commandSender, String lowerCase, String[] args) {
    return Collections.emptyList();
  }

  @Override
  public void perform(CommandSender commandSender, String[] args) {
    if (args.length != 2) {
      commandSender.sendMessage(getSyntax());
      return;
    }

    if (!commandSender.hasPermission("tan.debug.loglevel")) {
      commandSender.sendMessage("You don't have permission to do this.");
      return;
    }

    LogUtil.setLogLevel(args[1]);
    commandSender.sendMessage("Log level set to " + args[1]);
  }
}

package org.leralix.tan.commands.server;

import org.leralix.lib.commands.CommandManager;

public class ServerCommandManager extends CommandManager {

  public ServerCommandManager() {
    super("tan.server");
    addSubCommand(new CreateTownServer());
    addSubCommand(new ApplyTownServer());
    addSubCommand(new QuitTownServer());
    addSubCommand(new DisbandTownServer());
    addSubCommand(new OpenGuiServer());
    addSubCommand(new LandmarkUpdateServer());
    addSubCommand(new LandmarkSetStoredLimitServer());
  }

  @Override
  public String getName() {
    return "coconationserver";
  }
}

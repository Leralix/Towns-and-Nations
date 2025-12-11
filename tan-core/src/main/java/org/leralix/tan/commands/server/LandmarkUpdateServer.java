package org.leralix.tan.commands.server;

import java.util.Collections;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.leralix.lib.commands.SubCommand;
import org.leralix.tan.dataclass.Landmark;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.LandmarkStorage;
import org.leralix.tan.utils.text.TanChatUtils;

public class LandmarkUpdateServer extends SubCommand {
  @Override
  public String getName() {
    return "landmarkUpdate";
  }

  @Override
  public String getDescription() {
    return Lang.LANDMARK_UPDATE_SERVER_DESC.getDefault();
  }

  @Override
  public int getArguments() {
    return 2;
  }

  @Override
  public String getSyntax() {
    return "/coconationserver landmarkUpdate <id?>";
  }

  @Override
  public List<String> getTabCompleteSuggestions(
      CommandSender commandSender, String s, String[] args) {
    if (args.length == 2) {
      return LandmarkStorage.getInstance().getAllSync().values().stream()
          .map(Landmark::getID)
          .toList();
    }
    return Collections.emptyList();
  }

  @Override
  public void perform(CommandSender commandSender, String[] args) {
    LandmarkStorage instance = LandmarkStorage.getInstance();
    if (args.length < 2) {
      instance.generateAllResources();
      TanChatUtils.message(commandSender, Lang.ALL_LANDMARK_UPDATED);
    } else {
      instance
          .get(args[1])
          .thenAccept(
              landmark -> {
                if (landmark == null) {
                  TanChatUtils.message(commandSender, Lang.LANDMARK_NOT_FOUND);
                  return;
                }
                landmark.generateResources();
                TanChatUtils.message(
                    commandSender, Lang.LANDMARK_UPDATED.get(landmark.getName(), landmark.getID()));
              });
    }
  }
}

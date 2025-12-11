package org.leralix.tan.commands.server;

import java.util.Collections;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.leralix.lib.commands.SubCommand;
import org.leralix.tan.dataclass.Landmark;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.LandmarkStorage;
import org.leralix.tan.utils.text.TanChatUtils;

public class LandmarkSetStoredLimitServer extends SubCommand {
  @Override
  public String getName() {
    return "landmarkSetStoredLimit";
  }

  @Override
  public String getDescription() {
    return Lang.LANDMARK_UPDATE_SERVER_DESC.getDefault();
  }

  @Override
  public int getArguments() {
    return 3;
  }

  @Override
  public String getSyntax() {
    return "/coconationserver landmarkSetStoredLimit <id> <value>";
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
    if (args.length < 3) {
      TanChatUtils.message(commandSender, Lang.INVALID_ARGUMENTS);
    } else {
      LandmarkStorage.getInstance()
          .get(args[1])
          .thenAccept(
              landmark -> {
                if (landmark == null) {
                  TanChatUtils.message(commandSender, Lang.LANDMARK_NOT_FOUND);
                  return;
                }
                String value = args[2];
                if (value == null) {
                  TanChatUtils.message(commandSender, Lang.INVALID_ARGUMENTS);
                  return;
                }

                landmark.setStoredLimit(Integer.parseInt(value));
                TanChatUtils.message(
                    commandSender,
                    Lang.LANDMARK_STORED_UPDATED.get(landmark.getName(), landmark.getID(), value));
              });
    }
  }
}

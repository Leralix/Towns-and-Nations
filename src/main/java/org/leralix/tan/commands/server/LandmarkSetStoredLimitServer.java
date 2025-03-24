package org.leralix.tan.commands.server;

import org.bukkit.command.CommandSender;
import org.leralix.lib.commands.SubCommand;
import org.leralix.tan.dataclass.Landmark;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.LandmarkStorage;

import java.util.Collections;
import java.util.List;

public class LandmarkSetStoredLimitServer extends SubCommand {
    @Override
    public String getName() {
        return "landmarkSetStoredLimit";
    }

    @Override
    public String getDescription() {
        return Lang.LANDMARK_UPDATE_SERVER_DESC.get();
    }

    @Override
    public int getArguments() {
        return 3;
    }

    @Override
    public String getSyntax() {
        return "/tanserver landmarkSetStoredLimit <id> <value>";
    }

    @Override
    public List<String> getTabCompleteSuggestions(CommandSender commandSender, String s, String[] args) {
        if (args.length == 2) {
            return LandmarkStorage.getInstance().getAll().stream().map(Landmark::getID).toList();
        }
        return Collections.emptyList();
    }

    @Override
    public void perform(CommandSender commandSender, String[] args) {
        if (args.length < 3) {
            commandSender.sendMessage(Lang.INVALID_ARGUMENTS.get());
        } else {
            Landmark landmark = LandmarkStorage.getInstance().get(args[1]);
            if(landmark == null){
                commandSender.sendMessage(Lang.LANDMARK_NOT_FOUND.get());
                return;
            }
            String value = args[2];
            if(value == null){
                commandSender.sendMessage(Lang.INVALID_ARGUMENTS.get());
                return;
            }

            landmark.setStoredLimit(Integer.parseInt(value));
            commandSender.sendMessage(Lang.LANDMARK_STORED_UPDATED.get(landmark.getName(), landmark.getID(), value));
        }
    }
}

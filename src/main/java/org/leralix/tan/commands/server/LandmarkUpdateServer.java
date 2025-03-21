package org.leralix.tan.commands.server;

import org.bukkit.command.CommandSender;
import org.leralix.lib.commands.SubCommand;
import org.leralix.tan.dataclass.Landmark;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.LandmarkStorage;

import java.util.Collections;
import java.util.List;

public class LandmarkUpdateServer extends SubCommand {
    @Override
    public String getName() {
        return "landmarkUpdate";
    }

    @Override
    public String getDescription() {
        return Lang.LANDMARK_UPDATE_SERVER_DESC.get();
    }

    @Override
    public int getArguments() {
        return 2;
    }

    @Override
    public String getSyntax() {
        return "/tanserver landmarkUpdate <id?>";
    }

    @Override
    public List<String> getTabCompleteSuggestions(CommandSender commandSender, String s, String[] args) {
        if (args.length == 2) {
            return LandmarkStorage.getList().stream().map(Landmark::getID).toList();
        }
        return Collections.emptyList();
    }

    @Override
    public void perform(CommandSender commandSender, String[] args) {
        if (args.length < 2) {
            LandmarkStorage.generateAllResources();
            commandSender.sendMessage(Lang.ALL_LANDMARK_UPDATED.get());
        } else {
            Landmark landmark = LandmarkStorage.get(args[1]);
            landmark.generateResources();
            commandSender.sendMessage(Lang.LANDMARK_UPDATED.get(landmark.getName(), landmark.getID()));
        }
    }
}

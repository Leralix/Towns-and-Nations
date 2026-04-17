package org.leralix.tan.commands.server;

import org.bukkit.command.CommandSender;
import org.leralix.lib.commands.SubCommand;
import org.leralix.tan.data.building.landmark.Landmark;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.LandmarkStorage;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.Collections;
import java.util.List;

public class LandmarkUpdateServer extends SubCommand {

    private final LandmarkStorage landmarkStorage;

    public LandmarkUpdateServer(LandmarkStorage landmarkStorage){
        this.landmarkStorage = landmarkStorage;
    }

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
        return "/tanserver landmarkUpdate <id?>";
    }

    @Override
    public List<String> getTabCompleteSuggestions(CommandSender commandSender, String s, String[] args) {
        if (args.length == 2) {
            return landmarkStorage.getAll().values().stream().map(Landmark::getID).toList();
        }
        return Collections.emptyList();
    }

    @Override
    public void perform(CommandSender commandSender, String[] args) {
        if (args.length < 2) {
            landmarkStorage.generateAllResources();
            TanChatUtils.message(commandSender, Lang.ALL_LANDMARK_UPDATED);
        } else {
            Landmark landmark = landmarkStorage.get(args[1]);
            landmark.generateResources();
            TanChatUtils.message(commandSender, Lang.LANDMARK_UPDATED.get(landmark.getName(), landmark.getID()));
        }
    }
}

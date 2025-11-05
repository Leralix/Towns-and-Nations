package org.leralix.tan.commands.server;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.leralix.lib.commands.SubCommand;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.Collections;
import java.util.List;

public class ApplyTownServer extends SubCommand {

    @Override
    public String getName() {
        return "applytown";
    }

    @Override
    public String getDescription() {
        return Lang.APPLY_TOWN_SERVER_DESC.getDefault();
    }

    @Override
    public int getArguments() {
        return 3;
    }

    @Override
    public String getSyntax() {
        return "/tanserver apply <town ID> <player username>";
    }

    @Override
    public List<String> getTabCompleteSuggestions(CommandSender commandSender, String currentMessage, String[] args) {
        return Collections.emptyList();
    }

    @Override
    public void perform(CommandSender commandSender, String[] args) {
        if(args.length < 3){
            TanChatUtils.message(commandSender, Lang.INVALID_ARGUMENTS);
            return;
        }
        String townID = args[1];
        String playerName = args[2];
        Player p = commandSender.getServer().getPlayer(playerName);
        if(p == null){
            TanChatUtils.message(commandSender, Lang.PLAYER_NOT_FOUND);
            return;
        }
        if(townID == null){
            TanChatUtils.message(commandSender, Lang.TOWN_NOT_FOUND);
            return;
        }
        PlayerDataStorage.getInstance().get(p.getUniqueId().toString()).thenAccept(tanPlayer -> {
            if(tanPlayer.hasTown()){
                TanChatUtils.message(commandSender, Lang.PLAYER_ALREADY_HAVE_TOWN);
                return;
            }
            TownDataStorage.getInstance().get(townID).thenAccept(townData -> {
                if (townData == null) {
                    TanChatUtils.message(commandSender, Lang.TOWN_NOT_FOUND); // Assuming a new Lang entry for town not found
                    return;
                }
                townData.addPlayerJoinRequest(p);
            });
        });
    }
}

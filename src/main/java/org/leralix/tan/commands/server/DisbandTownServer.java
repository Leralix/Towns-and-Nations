package org.leralix.tan.commands.server;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.leralix.lib.commands.SubCommand;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.Collections;
import java.util.List;

class DisbandTownServer extends SubCommand {


    @Override
    public String getName() {
        return "disbandtown";
    }

    @Override
    public String getDescription() {
            return Lang.DISBAND_TOWN_SERVER_DESC.getDefault();
    }

    @Override
    public int getArguments() {
        return 2;
    }

    @Override
    public String getSyntax() {
        return "/tanserver disbandtown <player_username>";
    }

    @Override
    public List<String> getTabCompleteSuggestions(CommandSender player, String currentMessage, String[] args) {
        return Collections.emptyList();
    }

    @Override
    public void perform(CommandSender commandSender, String[] args) {
        if(args.length < 2){
            TanChatUtils.message(commandSender, Lang.INVALID_ARGUMENTS);
            return;
        }

        Player p = commandSender.getServer().getPlayer(args[1]);
        if(p == null){
            TanChatUtils.message(commandSender, Lang.PLAYER_NOT_FOUND);
            return;
        }
        PlayerDataStorage.getInstance().get(p).thenAccept(tanPlayer -> {
            tanPlayer.getTown().thenAccept(townData -> {
                if(townData == null){
                    TanChatUtils.message(commandSender, Lang.PLAYER_NO_TOWN);
                    return;
                }
                if(townData.isCapital()){
                    TanChatUtils.message(commandSender, Lang.CANNOT_DELETE_TERRITORY_IF_CAPITAL);
                    return;
                }
                townData.delete();
            });
        });
    }
}

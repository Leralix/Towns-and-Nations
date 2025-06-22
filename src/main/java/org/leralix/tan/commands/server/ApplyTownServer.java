package org.leralix.tan.commands.server;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.leralix.lib.commands.SubCommand;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;

import java.util.Collections;
import java.util.List;

public class ApplyTownServer extends SubCommand {

    @Override
    public String getName() {
        return "applytown";
    }

    @Override
    public String getDescription() {
        return Lang.APPLY_TOWN_SERVER_DESC.get();
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
            commandSender.sendMessage(Lang.INVALID_ARGUMENTS.get());
            return;
        }
        String townID = args[1];
        String playerName = args[2];
        Player p = commandSender.getServer().getPlayer(playerName);
        if(p == null){
            commandSender.sendMessage(Lang.PLAYER_NOT_FOUND.get());
            return;
        }
        if(townID == null){
            commandSender.sendMessage(Lang.TOWN_NOT_FOUND.get());
            return;
        }
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(p.getUniqueId().toString());
        if(tanPlayer.hasTown()){
            commandSender.sendMessage(Lang.PLAYER_ALREADY_HAVE_TOWN.get());
            return;
        }
        TownData townData = TownDataStorage.getInstance().get(townID);
        townData.addPlayerJoinRequest(p);
    }
}

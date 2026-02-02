package org.leralix.tan.commands.server;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.leralix.lib.commands.SubCommand;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.events.CreateTown;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.text.NameFilter;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.Collections;
import java.util.List;

class CreateTownServer extends SubCommand {

    private final PlayerDataStorage playerDataStorage;

    public CreateTownServer(PlayerDataStorage playerDataStorage) {
        this.playerDataStorage = playerDataStorage;
    }

    @Override
    public String getName() {
        return "createtown";
    }

    @Override
    public String getDescription() {
        return Lang.CREATE_TOWN_SERVER_DESC.getDefault();
    }

    @Override
    public int getArguments() {
        return 2;
    }

    @Override
    public String getSyntax() {
        return "/tanserver createtown <player_username> <town name>";
    }

    @Override
    public List<String> getTabCompleteSuggestions(CommandSender player, String currentMessage, String[] args) {
        return Collections.emptyList();
    }

    @Override
    public void perform(CommandSender commandSender, String[] args) {
        if(args.length < 3){
            TanChatUtils.message(commandSender, Lang.INVALID_ARGUMENTS);
            return;
        }

        StringBuilder townNameBuilder = new StringBuilder();
        for(int i = 2; i < args.length; i++){
            townNameBuilder.append(args[i]).append(" ");
        }
        String townName = townNameBuilder.toString().trim();

        if (!NameFilter.validateOrWarn(commandSender, townName, NameFilter.Scope.TOWN)) {
            return;
        }

        Player leader = commandSender.getServer().getPlayer(args[1]);
        if(leader == null){
            TanChatUtils.message(commandSender, Lang.PLAYER_NOT_FOUND);
            return;
        }
        if(TownDataStorage.getInstance().isNameUsed(townName)){
            TanChatUtils.message(commandSender, Lang.NAME_ALREADY_USED);
            return;
        }
        new CreateTown(0).createTown(leader, playerDataStorage.get(leader), townName);

    }
}

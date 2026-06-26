package org.leralix.tan.commands.server;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.leralix.lib.commands.SubCommand;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.data.territory.Town;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownStorage;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.utils.text.StringUtil;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class AddPlayerCommand extends SubCommand {

    private final TownStorage townStorage;
    private final PlayerDataStorage playerDataStorage;

    public AddPlayerCommand(TownStorage townStorage, PlayerDataStorage playerDataStorage){
        this.townStorage = townStorage;
        this.playerDataStorage = playerDataStorage;
    }

    @Override
    public String getName() {
        return "addPlayer";
    }

    @Override
    public String getDescription() {
        return Lang.ADD_PLAYER_SERVER_DESC.getDefault();
    }

    @Override
    public int getArguments() {
        return 2;
    }

    @Override
    public String getSyntax() {
        return "/tanserver addPlayer <PlayerName> <TownName>";
    }

    @Override
    public List<String> getTabCompleteSuggestions(CommandSender player, String currentMessage, String[] args) {
        return switch (args.length){
            case 2 -> Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
            case 3 -> townStorage.getAll().values().stream().map(t -> StringUtil.removeSpaceChar(t.getName())).toList();
            default -> Collections.emptyList();
        };
    }

    @Override
    public void perform(CommandSender commandSender, String[] args) {
        if(args.length != 3){
            TanChatUtils.message(commandSender, Lang.SYNTAX_ERROR);
            return;
        }
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
        ITanPlayer playerData = playerDataStorage.get(offlinePlayer);

        Optional<Territory> optTerritory = TerritoryUtil.getTerritoryByName(args[2]);
        if(optTerritory.isEmpty() || !(optTerritory.get() instanceof Town town)){
            TanChatUtils.message(commandSender, Lang.TOWN_NOT_FOUND);
            return;
        }

        if(playerData.hasTown()){
            if(playerData.isTownOverlord()){
                TanChatUtils.message(commandSender, Lang.CHAT_CANT_LEAVE_TOWN_IF_LEADER);
                return;
            }
            playerData.getTown().removePlayer(playerData);
        }

        town.addPlayer(playerData);
    }
}

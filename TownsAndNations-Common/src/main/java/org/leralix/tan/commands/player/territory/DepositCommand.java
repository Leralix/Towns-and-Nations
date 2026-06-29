package org.leralix.tan.commands.player.territory;

import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.events.DonateToTerritory;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class DepositCommand extends PlayerSubCommand {

    private final PlayerDataStorage playerDataStorage;

    public DepositCommand(PlayerDataStorage playerDataStorage){
        this.playerDataStorage = playerDataStorage;
    }

    @Override
    public String getName() {
        return "deposit";
    }

    @Override
    public String getDescription() {
        return Lang.DEPOSIT_COMMAND_DESC.getDefault();
    }

    @Override
    public int getArguments() {
        return 0;
    }

    @Override
    public String getSyntax() {
        return "/tan deposit <town/region/nation> <amount>";
    }

    @Override
    public List<String> getTabCompleteSuggestions(Player player, String currentMessage, String[] args) {
        int nbArg = args.length;
        return switch (nbArg) {
            case 1 -> List.of("deposit");
            case 2 -> List.of("town", "region", "nation");
            case 3 -> List.of("<amount>");
            default -> Collections.emptyList();
        };
    }

    @Override
    public void perform(Player player, String[] args) {
        if(args.length != 3) {
            TanChatUtils.message(player, Lang.SYNTAX_ERROR);
            return;
        }
        ITanPlayer tanPlayer = playerDataStorage.get(player);
        Optional<Territory> optTerritory = TerritoryUtil.getTerritoryFromArgs(tanPlayer, args[1]);
        if(optTerritory.isEmpty()){
            TanChatUtils.message(player, Lang.TERRITORY_NOT_FOUND);
            return;
        }
        Territory territory = optTerritory.get();
        new DonateToTerritory(territory).execute(player, tanPlayer, args[2]);
    }
}

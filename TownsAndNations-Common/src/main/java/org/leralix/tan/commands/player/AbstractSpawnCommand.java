package org.leralix.tan.commands.player;

import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.NationDataStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.Optional;

public abstract class AbstractSpawnCommand extends PlayerSubCommand {

    protected final PlayerDataStorage playerDataStorage;
    protected final TownDataStorage townDataStorage;

    protected AbstractSpawnCommand(PlayerDataStorage playerDataStorage, TownDataStorage townDataStorage) {
        this.playerDataStorage = playerDataStorage;
        this.townDataStorage = townDataStorage;
    }

    /**
     * Get the territory data from the arguments. If the player does not have the specified territory,
     * a message will be sent to the player and an empty Optional will be returned.
     * @param player        the player executing the command
     * @param playerData    the player's data
     * @param args          the command arguments, where args[0] is the command name and args[1] (optional) is the territory type (town, region, nation)
     * @return  An Optional containing the TerritoryData if found, or an empty Optional if the player is not part of this type of territory.
     */
    protected Optional<TerritoryData> getTerritoryDataFromArgs(Player player, ITanPlayer playerData, String[] args) {
        String territoryType = args.length == 1 ? "town" : args[1].toLowerCase();

        return Optional.ofNullable(
                switch (territoryType) {
                    case "town" -> {
                        var territoryData = townDataStorage.get(playerData);
                        if (territoryData == null) {
                            TanChatUtils.message(player, Lang.PLAYER_NO_TOWN.get(playerData.getLang()));
                        }
                        yield territoryData;
                    }
                    case "region" -> {
                        var territoryData = RegionDataStorage.getInstance().get(playerData);
                        if (territoryData == null) {
                            TanChatUtils.message(player, Lang.PLAYER_NO_REGION.get(playerData.getLang()));
                        }
                        yield territoryData;
                    }
                    case "nation" -> {
                        var territoryData = NationDataStorage.getInstance().get(playerData);
                        if (territoryData == null) {
                            TanChatUtils.message(player, Lang.PLAYER_NO_NATION.get(playerData.getLang()));
                        }
                        yield territoryData;
                    }
                    default -> null;
                }
        );
    }

}

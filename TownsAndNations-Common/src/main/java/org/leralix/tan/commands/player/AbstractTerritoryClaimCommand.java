package org.leralix.tan.commands.player;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTerritoryClaimCommand extends PlayerSubCommand {

    protected final PlayerDataStorage playerDataStorage;

    protected AbstractTerritoryClaimCommand(PlayerDataStorage playerDataStorage){
        this.playerDataStorage = playerDataStorage;
    }

    @Override
    public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args) {
        if (args.length == 2) {
            ITanPlayer tanPlayer = playerDataStorage.get(player);
            return TerritoryCommandUtil.getTerritoryTypeSuggestions(tanPlayer);
        }
        return new ArrayList<>();
    }

    @Override
    public void perform(Player player, String[] args) {
        ITanPlayer tanPlayer = playerDataStorage.get(player);
        LangType langType = tanPlayer.getLang();

        int size = args.length;
        if (size != 2 && size != 4) {
            TanChatUtils.message(player, Lang.SYNTAX_ERROR.get(langType));
            TanChatUtils.message(player, Lang.CORRECT_SYNTAX_INFO.get(langType, getSyntax()));
            return;
        }

        String territoryArg = args[1];
        TerritoryData territoryData = TerritoryCommandUtil.resolveTerritory(player, tanPlayer, territoryArg, getSyntax());
        if (territoryData == null) {
            return;
        }

        if (size == 2) {
            onNoCoordinates(player, territoryData, langType, territoryArg, args);
            onEnd(player, territoryData, langType, territoryArg, args);
            return;
        }

        Chunk chunk = TerritoryCommandUtil.parseChunkFromArgs(player, args, 2, 3, langType, getSyntax());
        if (chunk == null) {
            return;
        }

        onCoordinates(player, territoryData, chunk, langType, territoryArg, args);
        onEnd(player, territoryData, langType, territoryArg, args);
    }

    protected abstract void onNoCoordinates(Player player, TerritoryData territoryData, LangType langType, String territoryArg, String[] args);

    protected abstract void onCoordinates(Player player, TerritoryData territoryData, Chunk chunk, LangType langType, String territoryArg, String[] args);

    protected abstract void onEnd(Player player, TerritoryData territoryData, LangType langType, String territoryArg, String[] args);

}

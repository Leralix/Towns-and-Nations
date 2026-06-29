package org.leralix.tan.commands.player.claim;

import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.tan.commands.player.TerritoryCommandUtil;
import org.leralix.tan.data.chunk.IClaimedChunk;
import org.leralix.tan.data.chunk.WildernessChunk;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.data.upgrade.rewards.numeric.ChunkCap;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.ClaimStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.utils.territory.ChunkUtil;
import org.leralix.tan.utils.territory.WildernessPolygon;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ClaimAreaCommand extends PlayerSubCommand {

    private final PlayerDataStorage playerDataStorage;

    private final ClaimStorage claimStorage;

    public ClaimAreaCommand(PlayerDataStorage playerDataStorage, ClaimStorage claimStorage){
        this.playerDataStorage = playerDataStorage;
        this.claimStorage = claimStorage;
    }

    @Override
    public String getName() {
        return "claimArea";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public String getSyntax() {
        return "/tan claimarea <town/region/nation>";
    }

    @Override
    public List<String> getTabCompleteSuggestions(Player player, String currentMessage, String[] args) {
        if (args.length == 2) {
            ITanPlayer tanPlayer = playerDataStorage.get(player);
            return TerritoryCommandUtil.getTerritoryTypeSuggestions(tanPlayer);
        }
        return Collections.emptyList();
    }

    @Override
    public void perform(Player player, String[] args) {
        ITanPlayer tanPlayer = playerDataStorage.get(player);

        if(args.length != 2 ){
            TanChatUtils.message(player, Lang.SYNTAX_ERROR);
            return;
        }

        IClaimedChunk chunk = claimStorage.get(player);
        Optional<Territory> optionalTerritory = TerritoryUtil.getTerritoryFromArgs(tanPlayer, args[1]);
        if(optionalTerritory.isEmpty()){
            TanChatUtils.message(player, Lang.SYNTAX_ERROR);
            return;
        }
        Territory territory = optionalTerritory.get();

        if(!(chunk instanceof WildernessChunk wildernessChunk)){
            TanChatUtils.message(player, Lang.CLAIM_AREA_NOT_WILDERNESS_CHUNK.get(tanPlayer));
            return;
        }

        ChunkCap chunkCap = territory.getNewLevel().getStat(ChunkCap.class);

        int maxIteration = Constants.getClaimAreaChunkLimit();
        if(!chunkCap.isUnlimited()){
            maxIteration = Math.min(chunkCap.getMaxAmount(), maxIteration);
        }

        Optional<WildernessPolygon> optPolygon = ChunkUtil.getPolygon(wildernessChunk, territory, maxIteration);

        if(optPolygon.isEmpty()){
            TanChatUtils.message(player, Lang.CLAIM_AREA_TOO_BIG.get(tanPlayer));
            return;
        }

        WildernessPolygon polygon = optPolygon.get();

        if(!chunkCap.canDoAction(polygon.size())){
            TanChatUtils.message(player, Lang.TERRITORY_NOT_ENOUGH_MONEY.get(
                    tanPlayer,
                    territory.getColoredName(),
                    Double.toString(territory.getBalance() - (polygon.size() * territory.getClaimCost()))
                    )
            );
            return;
        }

        for(WildernessChunk wildernessChunkIterated : polygon.getChunks()){
            territory.claimChunk(player, tanPlayer, wildernessChunkIterated.getChunk(), false);
        }
    }
}

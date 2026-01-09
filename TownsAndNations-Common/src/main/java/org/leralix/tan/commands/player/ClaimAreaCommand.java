package org.leralix.tan.commands.player;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.text.ChatChunkMapRenderer;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClaimAreaCommand extends PlayerSubCommand {

    @Override
    public String getName() {
        return "claimarea";
    }

    @Override
    public String getDescription() {
        return Lang.CLAIM_CHUNK_COMMAND_DESC.getDefault();
    }

    public int getArguments() {
        return 1;
    }

    @Override
    public String getSyntax() {
        return "/tan claimarea <town/region/kingdom>";
    }

    @Override
    public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args) {
        if (args.length == 2) {
            ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);
            return TerritoryCommandUtil.getTerritoryTypeSuggestions(tanPlayer);
        }
        return new ArrayList<>();
    }

    @Override
    public void perform(Player player, String[] args) {

        LangType langType = LangType.of(player);

        if (!(args.length == 2 || args.length == 4)) {
            TanChatUtils.message(player, Lang.SYNTAX_ERROR.get(langType));
            TanChatUtils.message(player, Lang.CORRECT_SYNTAX_INFO.get(langType, getSyntax()));
            return;
        }

        String territoryArg = args[1];

        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);

        TerritoryData territoryData = TerritoryCommandUtil.resolveTerritory(player, tanPlayer, territoryArg, getSyntax());
        if (territoryData == null) {
            return;
        }

        if (args.length == 4) {
            Chunk chunk = TerritoryCommandUtil.parseChunkFromArgs(player, args, 2, 3, langType, getSyntax());
            if (chunk == null) {
                return;
            }
            territoryData.claimChunk(player, chunk);
        }

        openClaimAreaMap(player, territoryArg);
    }

    public static void openClaimAreaMap(Player player, String territoryArg) {
        LangType langType = PlayerDataStorage.getInstance().get(player).getLang();
        int radius = 4;

        ChatChunkMapRenderer.sendChunkMap(
                player,
                radius,
                langType,
                (chunkX, chunkZ) -> "/tan claimarea " + territoryArg + " " + chunkX + " " + chunkZ,
                Collections.emptyMap()
        );
        TanChatUtils.message(player, Lang.CORRECT_SYNTAX_INFO.get(langType, "/tan claimarea " + territoryArg), SoundEnum.MINOR_GOOD);
    }
}

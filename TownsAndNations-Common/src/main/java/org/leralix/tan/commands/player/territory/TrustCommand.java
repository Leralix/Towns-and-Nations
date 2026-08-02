package org.leralix.tan.commands.player.territory;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.data.territory.permission.ChunkPermissionType;
import org.leralix.tan.data.territory.rank.RolePermission;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.PermissionManager;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.leralix.lib.data.SoundEnum.MINOR_GOOD;
import static org.leralix.lib.data.SoundEnum.NOT_ALLOWED;

public class TrustCommand extends PlayerSubCommand {

    private final PlayerDataStorage playerDataStorage;

    public TrustCommand(PlayerDataStorage playerDataStorage){
        this.playerDataStorage = playerDataStorage;
    }

    @Override
    public String getName() {
        return "trust";
    }

    @Override
    public String getDescription() {
        return Lang.UNTRUST_COMMAND_DESC.getDefault();
    }

    @Override
    public int getArguments() {
        return 0;
    }

    @Override
    public String getSyntax() {
        return "/tan trust <Town/Region/Nation> <Player>" ;
    }

    @Override
    public List<String> getTabCompleteSuggestions(Player player, String currentMessage, String[] args) {
        int nbArg = args.length;
        return switch (nbArg) {
            case 1 -> List.of("trust");
            case 2 -> List.of("town", "region", "nation");
            case 3 -> Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
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

        Optional<Territory> optionalTerritory = TerritoryUtil.getTerritoryFromArgs(tanPlayer, args[1]);
        if(optionalTerritory.isEmpty()){
            TanChatUtils.message(player, Lang.TERRITORY_NOT_FOUND);
            return;
        }
        Territory territory = optionalTerritory.get();
        if(territory.getRank(tanPlayer).hasPermission(RolePermission.MANAGE_CLAIM_SETTINGS)){
            TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(tanPlayer), NOT_ALLOWED);
            return;
        }
        ITanPlayer targetPlayer = playerDataStorage.get(Bukkit.getOfflinePlayer(args[2]));
        if(territory.isPlayerIn(targetPlayer)){
            TanChatUtils.message(player, Lang.INVITATION_ERROR_PLAYER_ALREADY_IN_TOWN.get(tanPlayer), NOT_ALLOWED);
            return;
        }
        PermissionManager chunkPermission = territory.getChunkSettings().getChunkPermissions();
        for (ChunkPermissionType chunkPermissionType : ChunkPermissionType.values()){
            chunkPermission.get(chunkPermissionType).getAuthorizedPlayers().add(targetPlayer.getID());
        }
        TanChatUtils.message(player, Lang.PLAYER_NOW_TRUSTED_IN_TERRITORY.get(targetPlayer.getNameStored(), territory.getColoredName()), MINOR_GOOD);
    }
}

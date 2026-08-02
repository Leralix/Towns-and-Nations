package org.leralix.tan.commands.player.territory;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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

import java.util.*;

import static org.leralix.lib.data.SoundEnum.MINOR_GOOD;
import static org.leralix.lib.data.SoundEnum.NOT_ALLOWED;

public class UntrustCommand extends PlayerSubCommand {

    private final PlayerDataStorage playerDataStorage;

    public UntrustCommand(PlayerDataStorage playerDataStorage){
        this.playerDataStorage = playerDataStorage;
    }

    @Override
    public String getName() {
        return "untrust";
    }

    @Override
    public String getDescription() {
        return Lang.TRUST_COMMAND_DESC.getDefault();
    }

    @Override
    public int getArguments() {
        return 0;
    }

    @Override
    public String getSyntax() {
        return "/tan untrust <Town/Region/Nation> <Player>" ;
    }

    @Override
    public List<String> getTabCompleteSuggestions(Player player, String currentMessage, String[] args) {
        int nbArg = args.length;
        return switch (nbArg) {
            case 1 -> List.of("untrust");
            case 2 -> List.of("town", "region", "nation");
            case 3 -> getAuthorizedPlayers(player, args);
            default -> Collections.emptyList();
        };
    }

    /**
     * Show only player names who can be affected by the command
     * @param player the player casting the command
     * @param args the two first argument of the command
     * @return The list of all player that can be untrusted
     */
    private List<String> getAuthorizedPlayers(Player player, String[] args) {
        Set<UUID> allPlayerID = new HashSet<>();

        ITanPlayer tanPlayer = playerDataStorage.get(player);
        Optional<Territory> optionalTerritory = TerritoryUtil.getTerritoryFromArgs(tanPlayer, args[1]);
        if(optionalTerritory.isEmpty()){
            return List.of();
        }
        Territory territory = optionalTerritory.get();
        if(territory.getRank(tanPlayer).hasPermission(RolePermission.MANAGE_CLAIM_SETTINGS)){
            return List.of();
        }

        PermissionManager chunkPermission = territory.getChunkSettings().getChunkPermissions();
        for (ChunkPermissionType chunkPermissionType : ChunkPermissionType.values()){
            allPlayerID.addAll(chunkPermission.get(chunkPermissionType).getAuthorizedPlayers());
        }

        return allPlayerID.stream()
                .map(Bukkit::getOfflinePlayer)
                .map(OfflinePlayer::getName)
                .toList();
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
        PermissionManager chunkPermission = territory.getChunkSettings().getChunkPermissions();
        for (ChunkPermissionType chunkPermissionType : ChunkPermissionType.values()){
            chunkPermission.get(chunkPermissionType).getAuthorizedPlayers().remove(targetPlayer.getID());
        }
        TanChatUtils.message(player, Lang.PLAYER_NOW_UNTRUSTED_IN_TERRITORY.get(targetPlayer.getNameStored(), territory.getColoredName()), MINOR_GOOD);
    }
}

package org.tan.TownsAndNations.PlaceholderAPI;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.DataClass.territoryData.RegionData;
import org.tan.TownsAndNations.DataClass.territoryData.TownData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.storage.DataStorage.PlayerDataStorage;

public class PlaceHolderAPI extends PlaceholderExpansion {

    @Override
    @NotNull
    public String getAuthor() {
        return "Leralix";
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return "tan";
    }

    @Override
    @NotNull
    public String getVersion() {
        return TownsAndNations.getCurrentVersion().toString();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        PlayerData playerData = PlayerDataStorage.get(player.getUniqueId());
        TownsAndNations.getPluginLogger().info("onRequest: " + params);

        if (playerData == null) {
            return "Data not found"; // Gérer le cas où les données du joueur ne sont pas trouvées
        }

        if (params.equalsIgnoreCase("player_town_name")) {
            return playerData.haveTown() ? playerData.getTown().getName() : Lang.NO_TOWN.get();
        }
        else if (params.equalsIgnoreCase("player_town_resident_quantity")) {
            return playerData.haveTown() ? Integer.toString(playerData.getTown().getPlayerList().size()) : Lang.NO_TOWN.get();
        }
        else if (params.equalsIgnoreCase("player_town_chunk_quantity")) {
            return playerData.haveTown() ? Integer.toString(playerData.getTown().getNumberOfClaimedChunk()) : Lang.NO_TOWN.get();
        }
        else if (params.equalsIgnoreCase("player_town_balance")) {
            return playerData.haveTown() ? Integer.toString(playerData.getTown().getBalance()) : Lang.NO_TOWN.get();
        }
        else if (params.equalsIgnoreCase("player_town_rank_name")) {
            return playerData.haveTown() ? playerData.getTownRank().getName() : Lang.NO_TOWN.get();
        }
        else if (params.equalsIgnoreCase("player_town_rank_colored_name")) {
            return playerData.haveTown() ? playerData.getTownRank().getColoredName() : Lang.NO_TOWN.get();
        }
        else if (params.equalsIgnoreCase("player_region_name")) {
            return playerData.haveRegion() ? playerData.getRegion().getName() : Lang.NO_REGION.get();
        }
        else if (params.equalsIgnoreCase("player_region_resident_quantity")) {
            return playerData.haveRegion() ? Integer.toString(playerData.getRegion().getTotalPlayerCount()) : Lang.NO_REGION.get();
        }
        else if (params.equalsIgnoreCase("player_region_chunk_quantity")) {
            return playerData.haveRegion() ? Integer.toString(playerData.getRegion().getNumberOfClaimedChunk()) : Lang.NO_REGION.get();
        }
        else if (params.equalsIgnoreCase("player_region_balance")) {
            return playerData.haveRegion() ? Integer.toString(playerData.getRegion().getBalance()) : Lang.NO_REGION.get();
        }

        return null;
    }
}
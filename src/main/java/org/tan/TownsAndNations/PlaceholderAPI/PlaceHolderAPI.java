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
        return "Author";
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return "T&N";
    }

    @Override
    @NotNull
    public String getVersion() {
        return TownsAndNations.getCurrentVersion().toString();
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equalsIgnoreCase("player_town_name")) {
            PlayerData playerData = PlayerDataStorage.get(player.getUniqueId());
            if(!playerData.haveTown())
                return Lang.NO_TOWN.get();
            return playerData.getTown().getName();
        }
        else if (params.equalsIgnoreCase("player_town_resident_quantity")) {
            PlayerData playerData = PlayerDataStorage.get(player.getUniqueId());
            if(!playerData.haveTown())
                return Lang.NO_TOWN.get();

            TownData town = playerData.getTown();
            return Integer.toString(town.getPlayerIDList().size());
        }
        else if (params.equalsIgnoreCase("player_town_chunk_quantity")) {
            PlayerData playerData = PlayerDataStorage.get(player.getUniqueId());
            if(!playerData.haveTown())
                return Lang.NO_TOWN.get();

            TownData town = playerData.getTown();
            return Integer.toString(town.getNumberOfClaimedChunk());
        }
        else if (params.equalsIgnoreCase("player_town_balance")) {
            PlayerData playerData = PlayerDataStorage.get(player.getUniqueId());
            if(!playerData.haveTown())
                return Lang.NO_TOWN.get();

            TownData town = playerData.getTown();
            return Integer.toString(town.getBalance());
        }
        else if (params.equalsIgnoreCase("player_town_rank_name")) {
            PlayerData playerData = PlayerDataStorage.get(player.getUniqueId());
            if(!playerData.haveTown())
                return Lang.NO_TOWN.get();
            return playerData.getTownRank().getName();
        }
        else if (params.equalsIgnoreCase("player_town_rank_colored_name")) {
            PlayerData playerData = PlayerDataStorage.get(player.getUniqueId());
            if(!playerData.haveTown())
                return Lang.NO_TOWN.get();
            return playerData.getTownRank().getColoredName();
        }
        else if (params.equalsIgnoreCase("player_region_name")) {
            PlayerData playerData = PlayerDataStorage.get(player.getUniqueId());
            if(!playerData.haveRegion())
                return Lang.NO_REGION.get();
            return playerData.getRegion().getName();
        }
        else if (params.equalsIgnoreCase("player_region_resident_quantity")) {
            PlayerData playerData = PlayerDataStorage.get(player.getUniqueId());
            if(!playerData.haveRegion())
                return Lang.NO_REGION.get();
            RegionData regionData = playerData.getRegion();
            return Integer.toString(regionData.getTotalPlayerCount());
        }
        else if (params.equalsIgnoreCase("player_region_chunk_quantity")) {
            PlayerData playerData = PlayerDataStorage.get(player.getUniqueId());
            if(!playerData.haveRegion())
                return Lang.NO_REGION.get();
            RegionData regionData = playerData.getRegion();

            return Integer.toString(regionData.getNumberOfClaimedChunk());
        }
        else if (params.equalsIgnoreCase("player_region_balance")) {
            PlayerData playerData = PlayerDataStorage.get(player.getUniqueId());
            if(!playerData.haveRegion())
                return Lang.NO_REGION.get();
            RegionData regionData = playerData.getRegion();
            return Integer.toString(regionData.getBalance());
        }

        return null;
    }
}
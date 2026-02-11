package org.leralix.tan.api.external.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.api.external.papi.entries.*;
import org.leralix.tan.storage.LocalChatStorage;
import org.leralix.tan.storage.stored.NationDataStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;

import java.util.HashMap;
import java.util.Map;

public class PlaceHolderAPI extends PlaceholderExpansion {


    static final String PLACEHOLDER_NOT_FOUND = "[TAN] Placeholder not found";

    private final Map<String, PapiEntry> entries;

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
        return TownsAndNations.getPlugin().getCurrentVersion().toString();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    public PlaceHolderAPI(
            PlayerDataStorage playerDataStorage,
            TownDataStorage townDataStorage,
            RegionDataStorage regionDataStorage,
            NationDataStorage nationDataStorage,
            LocalChatStorage localChatStorage
    ) {
        entries = new HashMap<>();

        registerEntry(new GetFirstTerritoryIdWithName(playerDataStorage, townDataStorage, regionDataStorage, nationDataStorage));
        registerEntry(new OtherPlayerTownName(playerDataStorage, townDataStorage, regionDataStorage, nationDataStorage));
        registerEntry(new OtherPlayerTownTag(playerDataStorage, townDataStorage, regionDataStorage, nationDataStorage));
        registerEntry(new OtherPlayerRegionName(playerDataStorage, townDataStorage, regionDataStorage, nationDataStorage));
        registerEntry(new OtherPlayerNationName(playerDataStorage, townDataStorage, regionDataStorage, nationDataStorage));
        registerEntry(new OtherPlayerTownColoredName(playerDataStorage, townDataStorage, regionDataStorage, nationDataStorage));
        registerEntry(new OtherPlayerChatMode(playerDataStorage, townDataStorage, regionDataStorage, nationDataStorage, localChatStorage));
        registerEntry(new PlayerBalance(playerDataStorage, townDataStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerBiggerOverlordName(playerDataStorage, townDataStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerChatMode(playerDataStorage, townDataStorage, regionDataStorage, nationDataStorage, localChatStorage));
        registerEntry(new PlayerNameHaveTown(playerDataStorage, townDataStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerNameIsTownLeader(playerDataStorage, townDataStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerRegionBalance(playerDataStorage, townDataStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerRegionChunkActualQuantity(playerDataStorage, townDataStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerRegionName(playerDataStorage, townDataStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerNationBalance(playerDataStorage, townDataStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerNationChunkActualQuantity(playerDataStorage, townDataStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerNationName(playerDataStorage, townDataStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerNationRankColoredName(playerDataStorage, townDataStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerNationRankName(playerDataStorage, townDataStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerTownBalance(playerDataStorage, townDataStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerTownChunkActualQuantity(playerDataStorage, townDataStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerTownChunkMaxQuantity(playerDataStorage, townDataStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerTownColoredName(playerDataStorage, townDataStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerTownName(playerDataStorage, townDataStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerTownRankColoredName(playerDataStorage, townDataStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerTownRankName(playerDataStorage, townDataStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerTownRemainingQuantity(playerDataStorage, townDataStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerTownResidentQuantity(playerDataStorage, townDataStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerTownTag(playerDataStorage, townDataStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerColoredTownTag(playerDataStorage, townDataStorage, regionDataStorage, nationDataStorage));
        registerEntry(new TerritoryWithIdExist(playerDataStorage, townDataStorage, regionDataStorage, nationDataStorage));
        registerEntry(new TerritoryWithIdLeaderName(playerDataStorage, townDataStorage, regionDataStorage, nationDataStorage));
        registerEntry(new TerritoryWithNameExist(playerDataStorage, townDataStorage, regionDataStorage, nationDataStorage));
        registerEntry(new TerritoryWithNameLeaderName(playerDataStorage, townDataStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerRegionResidentQuantity(playerDataStorage, townDataStorage, regionDataStorage, nationDataStorage));

        registerEntry(new PlayerLocationChunkName(playerDataStorage, townDataStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerLocationChunkTypeName(playerDataStorage, townDataStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerLocationPropertyExist(playerDataStorage, townDataStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerLocationPropertyIsOwner(playerDataStorage, townDataStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerLocationPropertyName(playerDataStorage, townDataStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerLocationPvpEnabled(playerDataStorage, townDataStorage, regionDataStorage, nationDataStorage));
    }

    void registerEntry(PapiEntry playerBalance) {
        entries.put(playerBalance.getIdentifier(), playerBalance);
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {

        String paramIdentifier = removePlaceholder(params);

        if (entries.containsKey(paramIdentifier)) {
            return entries.get(paramIdentifier).getData(player, params);
        }

        return PLACEHOLDER_NOT_FOUND;
    }

    public String removePlaceholder(String params) {
        return params.replaceAll("\\{[^}]*}", "{}");
    }

}

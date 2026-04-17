package org.leralix.tan.api.external.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.api.external.papi.entries.*;
import org.leralix.tan.storage.LocalChatStorage;
import org.leralix.tan.storage.stored.NationStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionStorage;
import org.leralix.tan.storage.stored.TownStorage;

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
            TownStorage townStorage,
            RegionStorage regionDataStorage,
            NationStorage nationDataStorage,
            LocalChatStorage localChatStorage
    ) {
        entries = new HashMap<>();

        registerEntry(new GetFirstTerritoryIdWithName(playerDataStorage, townStorage, regionDataStorage, nationDataStorage));
        registerEntry(new OtherPlayerTownName(playerDataStorage, townStorage, regionDataStorage, nationDataStorage));
        registerEntry(new OtherPlayerTownTag(playerDataStorage, townStorage, regionDataStorage, nationDataStorage));
        registerEntry(new OtherPlayerRegionName(playerDataStorage, townStorage, regionDataStorage, nationDataStorage));
        registerEntry(new OtherPlayerNationName(playerDataStorage, townStorage, regionDataStorage, nationDataStorage));
        registerEntry(new OtherPlayerTownColoredName(playerDataStorage, townStorage, regionDataStorage, nationDataStorage));
        registerEntry(new OtherPlayerChatMode(playerDataStorage, townStorage, regionDataStorage, nationDataStorage, localChatStorage));
        registerEntry(new PlayerBalance(playerDataStorage, townStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerBiggerOverlordName(playerDataStorage, townStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerChatMode(playerDataStorage, townStorage, regionDataStorage, nationDataStorage, localChatStorage));
        registerEntry(new PlayerNameHaveTown(playerDataStorage, townStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerNameIsTownLeader(playerDataStorage, townStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerRegionBalance(playerDataStorage, townStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerRegionChunkActualQuantity(playerDataStorage, townStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerRegionName(playerDataStorage, townStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerNationBalance(playerDataStorage, townStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerNationChunkActualQuantity(playerDataStorage, townStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerNationName(playerDataStorage, townStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerNationRankColoredName(playerDataStorage, townStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerNationRankName(playerDataStorage, townStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerTownBalance(playerDataStorage, townStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerTownChunkActualQuantity(playerDataStorage, townStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerTownChunkMaxQuantity(playerDataStorage, townStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerTownColoredName(playerDataStorage, townStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerTownName(playerDataStorage, townStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerTownRankColoredName(playerDataStorage, townStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerTownRankName(playerDataStorage, townStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerTownRemainingQuantity(playerDataStorage, townStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerTownResidentQuantity(playerDataStorage, townStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerTownTag(playerDataStorage, townStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerColoredTownTag(playerDataStorage, townStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerColoredTownTagOrEmpty(playerDataStorage, townStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerColoredTownTagOrCustomText(playerDataStorage, townStorage, regionDataStorage, nationDataStorage));
        registerEntry(new TerritoryWithIdExist(playerDataStorage, townStorage, regionDataStorage, nationDataStorage));
        registerEntry(new TerritoryWithIdLeaderName(playerDataStorage, townStorage, regionDataStorage, nationDataStorage));
        registerEntry(new TerritoryWithNameExist(playerDataStorage, townStorage, regionDataStorage, nationDataStorage));
        registerEntry(new TerritoryWithNameLeaderName(playerDataStorage, townStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerRegionResidentQuantity(playerDataStorage, townStorage, regionDataStorage, nationDataStorage));

        registerEntry(new PlayerLocationChunkName(playerDataStorage, townStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerLocationChunkTypeName(playerDataStorage, townStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerLocationPropertyExist(playerDataStorage, townStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerLocationPropertyIsOwner(playerDataStorage, townStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerLocationPropertyName(playerDataStorage, townStorage, regionDataStorage, nationDataStorage));
        registerEntry(new PlayerLocationPvpEnabled(playerDataStorage, townStorage, regionDataStorage, nationDataStorage));
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

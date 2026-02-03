package org.leralix.tan.api.external.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.api.external.papi.entries.*;
import org.leralix.tan.storage.stored.PlayerDataStorage;

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

    public PlaceHolderAPI(PlayerDataStorage playerDataStorage) {
        entries = new HashMap<>();

        registerEntry(new GetFirstTerritoryIdWithName(playerDataStorage));
        registerEntry(new OtherPlayerTownName(playerDataStorage));
        registerEntry(new OtherPlayerTownTag(playerDataStorage));
        registerEntry(new OtherPlayerRegionName(playerDataStorage));
        registerEntry(new OtherPlayerNationName(playerDataStorage));
        registerEntry(new OtherPlayerTownColoredName(playerDataStorage));
        registerEntry(new OtherPlayerChatMode(playerDataStorage));
        registerEntry(new PlayerBalance());
        registerEntry(new PlayerBiggerOverlordName(playerDataStorage));
        registerEntry(new PlayerChatMode(playerDataStorage));
        registerEntry(new PlayerNameHaveTown(playerDataStorage));
        registerEntry(new PlayerNameIsTownLeader(playerDataStorage));
        registerEntry(new PlayerRegionBalance(playerDataStorage));
        registerEntry(new PlayerRegionChunkActualQuantity(playerDataStorage));
        registerEntry(new PlayerRegionName(playerDataStorage));
        registerEntry(new PlayerNationBalance(playerDataStorage));
        registerEntry(new PlayerNationChunkActualQuantity(playerDataStorage));
        registerEntry(new PlayerNationName(playerDataStorage));
        registerEntry(new PlayerNationRankColoredName(playerDataStorage));
        registerEntry(new PlayerNationRankName(playerDataStorage));
        registerEntry(new PlayerTownBalance(playerDataStorage));
        registerEntry(new PlayerTownChunkActualQuantity(playerDataStorage));
        registerEntry(new PlayerTownChunkMaxQuantity(playerDataStorage));
        registerEntry(new PlayerTownColoredName(playerDataStorage));
        registerEntry(new PlayerTownName(playerDataStorage));
        registerEntry(new PlayerTownRankColoredName(playerDataStorage));
        registerEntry(new PlayerTownRankName(playerDataStorage));
        registerEntry(new PlayerTownRemainingQuantity(playerDataStorage));
        registerEntry(new PlayerTownResidentQuantity(playerDataStorage));
        registerEntry(new PlayerTownTag(playerDataStorage));
        registerEntry(new PlayerColoredTownTag(playerDataStorage));
        registerEntry(new TerritoryWithIdExist(playerDataStorage));
        registerEntry(new TerritoryWithIdLeaderName(playerDataStorage));
        registerEntry(new TerritoryWithNameExist(playerDataStorage));
        registerEntry(new TerritoryWithNameLeaderName(playerDataStorage));
        registerEntry(new PlayerRegionResidentQuantity(playerDataStorage));

        registerEntry(new PlayerLocationChunkName());
        registerEntry(new PlayerLocationChunkTypeName());
        registerEntry(new PlayerLocationPropertyExist());
        registerEntry(new PlayerLocationPropertyIsOwner());
        registerEntry(new PlayerLocationPropertyName());
        registerEntry(new PlayerLocationPvpEnabled());
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

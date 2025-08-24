package org.leralix.tan.api.external.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.api.external.papi.entries.*;

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

    public PlaceHolderAPI() {
        entries = new HashMap<>();

        registerEntry(new GetFirstTerritoryIdWithName());
        registerEntry(new OtherPlayerTownName());
        registerEntry(new OtherPlayerTownTag());
        registerEntry(new OtherPlayerRegionName());
        registerEntry(new OtherPlayerChatMode());
        registerEntry(new PlayerBalance());
        registerEntry(new PlayerBiggerOverlordName());
        registerEntry(new PlayerChatMode());
        registerEntry(new PlayerNameHaveTown());
        registerEntry(new PlayerNameIsTownLeader());
        registerEntry(new PlayerRegionBalance());
        registerEntry(new PlayerRegionChunkActualQuantity());
        registerEntry(new PlayerRegionName());
        registerEntry(new PlayerTownBalance());
        registerEntry(new PlayerTownChunkActualQuantity());
        registerEntry(new PlayerTownChunkMaxQuantity());
        registerEntry(new PlayerTownName());
        registerEntry(new PlayerTownRankColoredName());
        registerEntry(new PlayerTownRankName());
        registerEntry(new PlayerTownRemainingQuantity());
        registerEntry(new PlayerTownResidentQuantity());
        registerEntry(new TerritoryWithIdExist());
        registerEntry(new TerritoryWithNameExist());
        registerEntry(new TerritoryWithNameLeaderName());
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

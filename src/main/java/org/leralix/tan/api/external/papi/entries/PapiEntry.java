package org.leralix.tan.api.external.papi.entries;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class PapiEntry {

    protected static final String TRUE = "TRUE";
    protected static final String FALSE = "FALSE";
    protected static final String PLAYER_NOT_FOUND = "[TAN] Player data not found";

    private final String identifier;

    protected PapiEntry(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public abstract String getData(OfflinePlayer player, @NotNull String params);

    protected static String[] extractValues(String input) {
        Pattern pattern = Pattern.compile("\\{(.*?)}");
        Matcher matcher = pattern.matcher(input);

        ArrayList<String> values = new ArrayList<>();

        while (matcher.find()) {
            values.add(matcher.group(1));
        }

        return values.toArray(new String[0]);
    }

    protected TerritoryData getTerritoryByName(String name) {
        for(TownData townData : TownDataStorage.getInstance().getAllAsync().join().values()){
            if(townData.getName().equalsIgnoreCase(name)){
                return townData;
            }
        }
        for(RegionData regionData : RegionDataStorage.getInstance().getAllAsync().join().values()){
            if(regionData.getName().equalsIgnoreCase(name)){
                return regionData;
            }
        }
        return null;
    }
}

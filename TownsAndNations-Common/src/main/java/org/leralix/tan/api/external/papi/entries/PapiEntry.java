package org.leralix.tan.api.external.papi.entries;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.data.territory.Nation;
import org.leralix.tan.data.territory.Region;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.data.territory.Town;
import org.leralix.tan.storage.stored.NationStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionStorage;
import org.leralix.tan.storage.stored.TownStorage;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class PapiEntry {

    protected static final String PLAYER_NOT_FOUND = "Player not found";
    protected static final String PROPERTY_NOT_FOUND = "Property not found";

    private final String identifier;

    protected final PlayerDataStorage playerDataStorage;
    protected final TownStorage townStorage;
    protected final RegionStorage regionDataStorage;
    protected final NationStorage nationDataStorage;

    protected PapiEntry(
            String identifier,
            PlayerDataStorage playerDataStorage,
            TownStorage townStorage,
            RegionStorage regionDataStorage,
            NationStorage nationDataStorage
    ) {
        this.identifier = identifier;
        this.playerDataStorage = playerDataStorage;
        this.townStorage = townStorage;
        this.regionDataStorage = regionDataStorage;
        this.nationDataStorage = nationDataStorage;
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

    protected Territory getTerritoryByName(String name) {
        for(Town townData : townStorage.getAll().values()){
            if(townData.getName().equalsIgnoreCase(name)){
                return townData;
            }
        }
        for(Region regionData : regionDataStorage.getAll().values()){
            if(regionData.getName().equalsIgnoreCase(name)){
                return regionData;
            }
        }
        for(Nation nationData : nationDataStorage.getAll().values()){
            if(nationData.getName().equalsIgnoreCase(name)){
                return nationData;
            }
        }
        return null;
    }
}

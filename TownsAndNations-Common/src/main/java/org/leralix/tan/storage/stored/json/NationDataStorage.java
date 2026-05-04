package org.leralix.tan.storage.stored.json;

import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.data.territory.Nation;
import org.leralix.tan.data.territory.NationData;
import org.leralix.tan.data.territory.Region;
import org.leralix.tan.data.territory.cosmetic.ICustomIcon;
import org.leralix.tan.storage.stored.NationStorage;
import org.leralix.tan.storage.stored.TerritoryStorage;
import org.leralix.tan.storage.typeadapter.IconAdapter;

import java.util.LinkedHashMap;

public class NationDataStorage extends TerritoryStorage<Nation> implements NationStorage {

    public NationDataStorage() {
        super("TAN - Nations.json",
                new TypeToken<LinkedHashMap<String, NationData>>() {}.getType(),
                new GsonBuilder()
                        .registerTypeAdapter(ICustomIcon.class, new IconAdapter())
                        .setPrettyPrinting()
                        .create());
    }

    @Override
    public Nation newNation(String name, @NotNull Region capital) {
        String nationID = generateNextID("N");
        NationData nationData = new NationData(nationID, name, capital.getLeaderData(), capital);

        put(nationID, nationData);
        capital.setOverlord(nationData);

        return nationData;
    }
}

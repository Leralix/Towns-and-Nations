package org.leralix.tan.storage.stored;

import org.jetbrains.annotations.NotNull;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.Nation;
import org.leralix.tan.data.territory.Region;

import java.util.Map;

public interface NationStorage extends IterritoryStorage{

    default Nation get(Region regionData){
        if(regionData == null){
            return null;
        }
        return get(regionData.getNationID());
    }

    default Nation get(ITanPlayer playerData){
        if(playerData == null){
            return null;
        }
        return get(playerData.getRegion());
    }

    Nation newNation(String name, @NotNull Region capital);

    Nation get(String nationID);

    boolean isNameUsed(String name);

    void delete(String id);

    Map<String, Nation> getAll();

    void save();
}

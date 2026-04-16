package org.leralix.tan.data.territory;

import org.leralix.tan.data.DbManager;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.storage.stored.database.DatabaseData;

import java.util.List;

public class NationDatabase extends TerritoryDatabase<Nation> implements DatabaseData<Nation>, Nation {

    private Nation data;

    public NationDatabase(Nation data, DbManager<Nation> manager) {
        super(manager, data);
        this.data = data;
    }

    @Override
    public void setData(Nation data) {
        setTerritoryData(data);
        this.data = data;
    }

    @Override
    public List<Territory> getSubjects() {
        return data.getSubjects();
    }

    @Override
    public void registerPlayer(ITanPlayer tanPlayer) {
        mutate(p -> p.registerPlayer(tanPlayer));
    }

    @Override
    public void unregisterPlayer(ITanPlayer tanPlayer) {
        mutate(p -> p.unregisterPlayer(tanPlayer));
    }

    @Override
    public void removeVassal(Territory territoryData) {
        mutate(p -> p.removeVassal(territoryData));
    }

    @Override
    public void setCapital(String regionID) {
        mutate(p -> p.setCapital(regionID));
    }
}

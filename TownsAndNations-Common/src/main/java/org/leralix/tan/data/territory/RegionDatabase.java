package org.leralix.tan.data.territory;

import org.leralix.tan.data.DbManager;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.storage.stored.database.DatabaseData;

import java.util.List;
import java.util.Optional;

public class RegionDatabase extends TerritoryDatabase<Region> implements DatabaseData<Region>, Region {

    private Region data;

    public RegionDatabase(Region data, DbManager<Region> manager) {
        super(manager, data);
        this.data = data;
    }

    @Override
    public void setData(Region data) {
        setTerritoryData(data);
        this.data = data;
    }

    @Override
    public void setCapital(String townID) {
        mutate(p -> p.setCapital(townID));
    }

    @Override
    public Optional<Nation> getNation() {
        return data.getNation();
    }

    @Override
    public String getNationID() {
        return data.getNationID();
    }

    @Override
    public List<Territory> getSubjects() {
        return data.getSubjects();
    }

    @Override
    public void registerPlayer(ITanPlayer tanPlayer) {
        mutate(r -> r.registerPlayer(tanPlayer));
    }

    @Override
    public void unregisterPlayer(ITanPlayer tanPlayer) {
        mutate(r -> r.unregisterPlayer(tanPlayer));
    }

    @Override
    public void removeVassal(Territory territoryData) {
        mutate(r -> r.removeVassal(territoryData));
    }
}

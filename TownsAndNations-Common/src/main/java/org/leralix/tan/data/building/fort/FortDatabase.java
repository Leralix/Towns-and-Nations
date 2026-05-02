package org.leralix.tan.data.building.fort;

import org.leralix.lib.position.Vector3D;
import org.leralix.tan.data.DbManager;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.storage.stored.database.DatabaseData;

import java.util.function.Consumer;

public class FortDatabase extends Fort implements DatabaseData<FortData> {


    private final DbManager<FortData> manager;

    private FortData data;

    public FortDatabase(FortData data, DbManager<FortData> manager){
        this.manager = manager;
        this.data = data;
    }

    @Override
    public String getID() {
        return data.getID();
    }

    @Override
    public String getName() {
        return data.getName();
    }

    @Override
    public Vector3D getPosition() {
        return data.getPosition();
    }

    @Override
    public Territory getOwner() {
        return data.getOwner();
    }

    @Override
    public Territory getOccupier() {
        return data.getOccupier();
    }

    @Override
    protected void setOccupierInternal(Territory newOwner) {
        mutate(f -> f.setOccupierInternal(newOwner));
    }

    @Override
    public void setOwner(Territory newOwner) {
        mutate(f -> f.setOwner(newOwner));
    }

    @Override
    public void setData(FortData fresh) {
        this.data = fresh;
    }

    private synchronized void mutate(Consumer<FortData> action) {
        action.accept(data);
        manager.save(data);
    }

}

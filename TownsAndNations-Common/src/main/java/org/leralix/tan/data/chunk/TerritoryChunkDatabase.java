package org.leralix.tan.data.chunk;

import org.bukkit.entity.Player;
import org.leralix.tan.data.DbManager;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.lang.LangType;
import org.tan.api.interfaces.territory.TanTerritory;

import java.util.function.Consumer;

public abstract class TerritoryChunkDatabase extends ChunkDatabase implements TerritoryChunk {

    private TerritoryChunk data;

    private final DbManager<IClaimedChunk> databaseManager;

    protected TerritoryChunkDatabase(DbManager<IClaimedChunk> databaseManager, TerritoryChunk territoryChunk) {
        super(territoryChunk, databaseManager);
        this.data = territoryChunk;
        this.databaseManager = databaseManager;
    }

    @Override
    public void setData(IClaimedChunk data) {
        this.data = (TerritoryChunk) data;
    }

    @Override
    public Territory getOwnerInternal() {
        return data.getOwnerInternal();
    }

    @Override
    public Territory getOccupierInternal() {
        return data.getOccupierInternal();
    }

    @Override
    public String getOccupierID() {
        return data.getOccupierID();
    }

    @Override
    public void setOccupierID(String occupierID) {
        mutateTerritory(chunk -> chunk.setOccupierID(occupierID));
    }

    @Override
    public void liberate() {
        mutateTerritory(TerritoryChunk::liberate);
    }

    @Override
    public boolean isOccupied() {
        return data.isOccupied();
    }

    @Override
    public void unclaimChunk(Player player, ITanPlayer tanPlayer, LangType langType) {
        mutateTerritory(chunk -> chunk.unclaimChunk(player, tanPlayer, langType));
    }

    @Override
    public TanTerritory getOwner() {
        return data.getOwner();
    }

    @Override
    public String getOwnerID() {
        return data.getOwnerID();
    }

    @Override
    public TanTerritory getOccupier() {
        return data.getOccupier();
    }


    protected void mutateTerritory(Consumer<TerritoryChunk> mutator) {
        mutator.accept(data);
        databaseManager.save(data);
    }
}

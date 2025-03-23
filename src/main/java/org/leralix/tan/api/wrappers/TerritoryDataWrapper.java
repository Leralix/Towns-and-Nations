package org.leralix.tan.api.wrappers;

import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.tan.api.interfaces.TanClaimedChunk;
import org.tan.api.interfaces.TanPlayer;
import org.tan.api.interfaces.TanTerritory;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class TerritoryDataWrapper implements TanTerritory {

    private final TerritoryData territoryData;

    protected TerritoryDataWrapper(TerritoryData territoryData){
        this.territoryData = territoryData;
    }

    public static TanTerritory of(TerritoryData territoryData) {
        if(territoryData == null){
            return null;
        }
        return new TerritoryDataWrapper(territoryData);
    }

    @Override
    public UUID getUUID() {
        return UUID.fromString(territoryData.getID());
    }

    @Override
    public String getName() {
        return territoryData.getName();
    }

    @Override
    public void setName(String newName) {
        territoryData.rename(newName);
    }

    @Override
    public String getDescription() {
        return territoryData.getDescription();
    }

    @Override
    public void setDescription(String s) {
        territoryData.setDescription(s);
    }

    @Override
    public TanPlayer getOwner() {
        return PlayerDataWrapper.of(territoryData.getLeaderData());
    }

    @Override
    public UUID getOwnerUUID() {
        return getOwner().getUUID();
    }

    @Override
    public Long getCreationDate() {
        return territoryData.getCreationDate();
    }

    @Override
    public ItemStack getIcon() {
        return territoryData.getIcon();
    }

    @Override
    public Color getColor() {
        return Color.fromRGB(territoryData.getChunkColorCode());
    }

    @Override
    public void setColor(Color color) {
        territoryData.setChunkColor(color.asRGB());
    }

    @Override
    public int getNumberOfClaimedChunk() {
        return getClaimedChunks().size();
    }

    @Override
    public Collection<TanPlayer> getMembers() {
        return territoryData.getPlayerDataList().stream()
                .map(PlayerDataWrapper::of)
                .toList();
    }

    @Override
    public Collection<TanTerritory> getVassals() {
        return territoryData.getVassals().stream()
                .map(TerritoryDataWrapper::of)
                .toList();
    }

    @Override
    public boolean haveOverlord() {
        return territoryData.haveOverlord();
    }

    @Override
    public TanTerritory getOverlord() {
        return  TerritoryDataWrapper.of(territoryData.getOverlord());
    }

    @Override
    public Collection<TanClaimedChunk> getClaimedChunks() {
        return NewClaimedChunkStorage.getInstance().getAllChunkFrom(territoryData).stream()
                .map(p -> (TanClaimedChunk) p).toList();
    }
}

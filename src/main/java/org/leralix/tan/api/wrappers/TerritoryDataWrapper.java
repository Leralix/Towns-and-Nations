package org.leralix.tan.api.wrappers;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.tan.api.enums.ETownPermission;
import org.tan.api.interfaces.TanClaimedChunk;
import org.tan.api.interfaces.TanPlayer;
import org.tan.api.interfaces.TanTerritory;

import java.util.Collection;
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
    public String getID() {
        return territoryData.getID();
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
        return TanPlayerWrapper.of(territoryData.getLeaderData());
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
        return territoryData.getITanPlayerList().stream()
                .map(TanPlayerWrapper::of)
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
    public boolean canPlayerDoAction(TanPlayer tanPlayer, ETownPermission permission) {

        Player player = Bukkit.getPlayer(tanPlayer.getUUID());
        if (player == null) {
            return false; // Player is not online
        }
        RolePermission playerPermission = RolePermission.valueOf(permission.name());

        return territoryData.doesPlayerHavePermission(player, playerPermission);
    }

    @Override
    public Collection<TanClaimedChunk> getClaimedChunks() {
        return NewClaimedChunkStorage.getInstance().getAllChunkFrom(territoryData).stream()
                .map(ClaimedChunkWrapper::of)
                .toList();
    }
}

package org.leralix.tan.data.chunk;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.leralix.lib.position.Vector2D;
import org.leralix.lib.position.Vector3D;
import org.leralix.tan.data.DbManager;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.data.territory.permission.ChunkPermissionType;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.database.DatabaseData;
import org.tan.api.enums.EChunkPermission;
import org.tan.api.interfaces.TanPlayer;
import org.tan.api.interfaces.territory.TanTerritory;

import java.util.UUID;
import java.util.function.Consumer;

public abstract class ChunkDatabase implements DatabaseData<ChunkData>, IClaimedChunk {

    private final DbManager<ChunkData> manager;

    private ChunkData data;

    protected ChunkDatabase(ChunkData data, DbManager<ChunkData> manager) {
        this.data = data;
        this.manager = manager;
    }

    protected void setChunkData(ChunkData data) {
        this.data = data;
    }

    @Override
    public Vector2D getVector2D() {
        return data.getVector2D();
    }

    @Override
    public Vector2D getMiddleVector2D() {
        return data.getMiddleVector2D();
    }

    @Override
    public int getX() {
        return data.getX();
    }

    @Override
    public int getMiddleX() {
        return data.getMiddleX();
    }

    @Override
    public int getZ() {
        return data.getZ();
    }

    @Override
    public int getMiddleZ() {
        return data.getMiddleZ();
    }

    @Override
    public UUID getWorldUUID() {
        return data.getWorldUUID();
    }

    @Override
    public String getWorldID() {
        return data.getWorldID();
    }

    @Override
    public String getworldName() {
        return data.getworldName();
    }

    @Override
    public boolean canPlayerDo(Player player, ITanPlayer tanPlayer, ChunkPermissionType permissionType, Location location) {
        return data.canPlayerDo(player, tanPlayer, permissionType, location);
    }

    @Override
    public void playerEnterClaimedArea(Player player, ITanPlayer tanPlayer, boolean displayTerritoryColor) {
        data.playerEnterClaimedArea(player, tanPlayer, displayTerritoryColor);
    }

    @Override
    public String getName() {
        return data.getName();
    }

    @Override
    public boolean canEntitySpawn(EntityType entityType) {
        return data.canEntitySpawn(entityType);
    }

    @Override
    public World getWorld() {
        return data.getWorld();
    }

    @Override
    public TextComponent getMapIcon(LangType langType) {
        return data.getMapIcon(langType);
    }

    @Override
    public boolean canTerritoryClaim(Player player, Territory territoryData, LangType langType) {
        return data.canTerritoryClaim(player, territoryData, langType);
    }

    @Override
    public boolean canTerritoryClaim(Territory territoryData) {
        return data.canTerritoryClaim(territoryData);
    }

    @Override
    public boolean canClaim(TanTerritory territory) {
        return data.canClaim(territory);
    }

    @Override
    public void claim(TanTerritory tanTerritory) {
        mutate(claim -> claim.claim(tanTerritory));
    }

    @Override
    public boolean canExplosionGrief() {
        return data.canExplosionGrief();
    }

    @Override
    public boolean canFireGrief() {
        return data.canFireGrief();
    }

    @Override
    public boolean canPVPHappen() {
        return data.canPVPHappen();
    }

    @Override
    public boolean canHostileGrief() {
        return data.canHostileGrief();
    }

    @Override
    public boolean canVillagerGrief() {
        return data.canVillagerGrief();
    }

    @Override
    public boolean canPassiveGrief() {
        return data.canPassiveGrief();
    }

    @Override
    public Chunk getChunk() {
        return data.getChunk();
    }

    @Override
    public ChunkType getType() {
        return data.getType();
    }

    @Override
    public void notifyUpdate() {
        mutate(IClaimedChunk::notifyUpdate);
    }

    @Override
    public boolean containsPosition(Vector3D position) {
        return data.containsPosition(position);
    }

    @Override
    public boolean canBeGriefByExplosion() {
        return data.canBeGriefByExplosion();
    }

    @Override
    public boolean canBeGriefByFire() {
        return data.canBeGriefByFire();
    }

    @Override
    public boolean canPvpHappen() {
        return data.canPvpHappen();
    }

    @Override
    public boolean canPlayerDoAction(TanPlayer tanPlayer, EChunkPermission permission, Location location) {
        return data.canPlayerDoAction(tanPlayer, permission, location);
    }

    @Override
    public void unclaim() {
        mutate(IClaimedChunk::unclaim);
    }

    @Override
    public boolean isClaimed() {
        return data.isClaimed();
    }

    public synchronized void mutate(Consumer<IClaimedChunk> mutator) {
        mutator.accept(data);
        manager.save(data);
    }
}

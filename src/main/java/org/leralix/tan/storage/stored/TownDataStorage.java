package org.leralix.tan.storage.stored;

import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.property.AbstractOwner;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.dataclass.territory.cosmetic.ICustomIcon;
import org.leralix.tan.dataclass.territory.permission.RelationPermission;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.enums.permissions.ChunkPermissionType;
import org.leralix.tan.storage.typeadapter.EnumMapDeserializer;
import org.leralix.tan.storage.typeadapter.EnumMapKeyValueDeserializer;
import org.leralix.tan.storage.typeadapter.IconAdapter;
import org.leralix.tan.storage.typeadapter.OwnerDeserializer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

public class TownDataStorage extends DatabaseStorage<TownData>{

    private static final String TABLE_NAME = "tan_towns";
    private static TownDataStorage instance;

    private int newTownId;

    private TownDataStorage() {
        super(TABLE_NAME,
                TownData.class,
                new GsonBuilder()
                        .registerTypeAdapter(new TypeToken<Map<ChunkPermissionType, RelationPermission>>() {}.getType(), new EnumMapKeyValueDeserializer<>(ChunkPermissionType.class, RelationPermission.class))
                        .registerTypeAdapter(new TypeToken<Map<TownRelation, List<String>>>() {}.getType(),new EnumMapDeserializer<>(TownRelation.class, new TypeToken<List<String>>(){}.getType()))
                        .registerTypeAdapter(new TypeToken<List<RelationPermission>>() {}.getType(),new EnumMapDeserializer<>(RelationPermission.class, new TypeToken<List<String>>(){}.getType()))
                        .registerTypeAdapter(AbstractOwner.class, new OwnerDeserializer())
                        .registerTypeAdapter(ICustomIcon.class, new IconAdapter())
                        .setPrettyPrinting()
                        .create());
        loadNextTownId();
    }

    @Override
    protected void createTable() {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS %s (
                id VARCHAR(255) PRIMARY KEY,
                data TEXT NOT NULL
            )
        """.formatted(TABLE_NAME);

        try (Connection conn = getDatabase().getDataSource().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            TownsAndNations.getPlugin().getLogger().severe(
                "Error creating table " + TABLE_NAME + ": " + e.getMessage()
            );
        }
    }

    private void loadNextTownId() {
        newTownId = getDatabase().getNextTownId();
    }

    @Override
    public void reset() {
        instance = null;
    }


    public static TownDataStorage getInstance() {
        if (instance == null)
            instance = new TownDataStorage();
        return instance;
    }

    public TownData newTown(String townName, ITanPlayer tanPlayer){
        String townId = getNextTownID();
        TownData newTown = new TownData(townId, townName, tanPlayer);

        put(townId,newTown);
        return newTown;
    }

    private @NotNull String getNextTownID() {
        String townId = "T"+ newTownId;
        newTownId++;
        getDatabase().updateNextTownId(newTownId);
        return townId;
    }

    public TownData newTown(String townName){
        String townId = getNextTownID();

        TownData newTown = new TownData(townId, townName, null);

        put(townId,newTown);
        return newTown;
    }


    public void deleteTown(TownData townData) {
        delete(townData.getID());
    }


    public TownData get(ITanPlayer tanPlayer){
        return get(tanPlayer.getTownId());
    }

    public TownData get(Player player){
        return get(PlayerDataStorage.getInstance().get(player).getTownId());
    }


    public int getNumberOfTown() {
        return count();
    }

    public boolean isNameUsed(String townName){
        if (townName == null) {
            return false;
        }

        // Optimized: scan JSON data for name instead of deserializing all objects
        String selectSQL = "SELECT 1 FROM " + TABLE_NAME + " WHERE json_extract(data, '$.name') = ? LIMIT 1";

        try (Connection conn = getDatabase().getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(selectSQL)) {

            ps.setString(1, townName);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            // Fallback to the old method if json_extract is not supported
            TownsAndNations.getPlugin().getLogger().warning(
                "json_extract not supported, falling back to full scan: " + e.getMessage()
            );

            for (TownData town : getAll().values()){
                if(townName.equals(town.getName()))
                    return true;
            }
        }

        return false;
    }
}

package org.leralix.tan.storage.stored;

import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.dataclass.territory.cosmetic.ICustomIcon;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.typeadapter.EnumMapDeserializer;
import org.leralix.tan.storage.typeadapter.IconAdapter;
import org.leralix.tan.utils.file.FileUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class RegionDataStorage extends DatabaseStorage<RegionData> {

    private static final String TABLE_NAME = "tan_regions";
    private int nextID;
    private static RegionDataStorage instance;

    public static RegionDataStorage getInstance() {
        if(instance == null)
            instance = new RegionDataStorage();
        return instance;
    }

    private RegionDataStorage() {
        super(TABLE_NAME,
                RegionData.class,
                new GsonBuilder()
                        .registerTypeAdapter(new TypeToken<Map<TownRelation, List<String>>>() {}.getType(),new EnumMapDeserializer<>(TownRelation.class, new TypeToken<List<String>>(){}.getType()))
                        .registerTypeAdapter(ICustomIcon.class, new IconAdapter())
                        .setPrettyPrinting()
                        .create());
        loadNextID();
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

            // Migration: Add region_name column if it doesn't exist
            try (ResultSet rs = conn.getMetaData().getColumns(null, null, TABLE_NAME, "region_name")) {
                if (!rs.next()) {
                    stmt.executeUpdate("ALTER TABLE %s ADD COLUMN region_name VARCHAR(255) UNIQUE NULL".formatted(TABLE_NAME));
                    TownsAndNations.getPlugin().getLogger().info("Added region_name column to " + TABLE_NAME);
                }
            }

        } catch (SQLException e) {
            TownsAndNations.getPlugin().getLogger().severe(
                "Error creating table " + TABLE_NAME + ": " + e.getMessage()
            );
        }
    }

    @Override
    public void put(String id, RegionData obj) {
        if (id == null || obj == null) {
            return;
        }

        String jsonData = gson.toJson(obj, typeToken);
        String upsertSQL = "INSERT INTO " + tableName + " (id, region_name, data) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE region_name = VALUES(region_name), data = VALUES(data)";

        try (Connection conn = getDatabase().getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(upsertSQL)) {

            ps.setString(1, id);
            ps.setString(2, obj.getName()); // Set region_name
            ps.setString(3, jsonData);
            ps.executeUpdate();

            // Update cache
            if (cacheEnabled && cache != null) {
                synchronized (cache) {
                    cache.put(id, obj);
                }
            }

        } catch (SQLException e) {
            TownsAndNations.getPlugin().getLogger().severe(
                "Error storing " + typeClass.getSimpleName() + " with ID " + id + ": " + e.getMessage()
            );
        }
    }

    private void loadNextID() {
        nextID = getDatabase().getNextRegionId();
    }

    public RegionData createNewRegion(String name, TownData capital){

        ITanPlayer newLeader = capital.getLeaderData();

        String regionID = generateNextID();

        RegionData newRegion = new RegionData(regionID, name, newLeader);
        put(regionID, newRegion);
        capital.setOverlord(newRegion);

        FileUtil.addLineToHistory(Lang.REGION_CREATED_NEWSLETTER.get(newLeader.getNameStored(), name));
        return newRegion;
    }

    private @NotNull String generateNextID() {
        String regionID = "R"+nextID;
        nextID++;
        getDatabase().updateNextRegionId(nextID);
        return regionID;
    }

    public RegionData get(Player player){
        return get(PlayerDataStorage.getInstance().get(player));
    }
    public RegionData get(ITanPlayer tanPlayer){
        TownData town = TownDataStorage.getInstance().get(tanPlayer);
        if(town == null)
            return null;
        return town.getRegion();
    }

    public void deleteRegion(RegionData region){
        delete(region.getID());
    }

    public boolean isNameUsed(String name){
        for (RegionData region : getAll().values()){
            if(region.getName().equalsIgnoreCase(name))
                return true;
        }
        return false;
    }


    @Override
    public void reset() {
        instance = null;
    }
}

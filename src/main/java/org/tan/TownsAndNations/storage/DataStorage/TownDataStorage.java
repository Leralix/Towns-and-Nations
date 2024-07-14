package org.tan.TownsAndNations.storage.DataStorage;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import com.google.gson.internal.bind.DateTypeAdapter;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.*;
import org.tan.TownsAndNations.DataClass.territoryData.RegionData;
import org.tan.TownsAndNations.DataClass.territoryData.TownData;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.enums.TownChunkPermission;
import org.tan.TownsAndNations.enums.ChunkPermissionType;
import org.tan.TownsAndNations.enums.TownRelation;
import org.tan.TownsAndNations.enums.TownRolePermission;
import org.tan.TownsAndNations.utils.ConfigUtil;

import java.io.*;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.*;
import java.util.Date;

import static org.tan.TownsAndNations.TownsAndNations.isSQLEnabled;
import static org.tan.TownsAndNations.utils.TeamUtils.updateAllScoreboardColor;

public class TownDataStorage {

    private static LinkedHashMap<String, TownData> townDataMap = new LinkedHashMap<>();
    private static int newTownId = 1;
    private static Connection connection;

    public static TownData newTown(String townName, Player player){
        String townId = "T"+newTownId;
        String playerID = player.getUniqueId().toString();
        newTownId++;

        TownData newTown = new TownData(townId, townName, playerID);


        townDataMap.put(townId,newTown);
        saveStats();
        return newTown;
    }

    public static TownData newTown(String townName){
        String townId = "T"+newTownId;
        newTownId++;

        TownData newTown = new TownData(townId, townName, null);

        townDataMap.put(townId,newTown);
        saveStats();
        return newTown;
    }


    public static void removeTown(String TownId) {
        TownData townToDelete = get(TownId);

        NewClaimedChunkStorage.unclaimAllChunkFromTown(townToDelete); //Unclaim all chunk from town

        RegionData region = RegionDataStorage.get(townToDelete.getRegionID());
        if(region != null)
            region.removeTown(townToDelete);


        townToDelete.cancelAllRelation();   //Cancel all Relation between the deleted town and other town
        for(String playerID : townToDelete.getPlayerList()){ //Kick all Players from the deleted town
            townToDelete.removePlayer(PlayerDataStorage.get(playerID));
        }

        if(isSQLEnabled()) { //if SQL is enabled, some data need to be removed manually
            removeAllChunkPermissionsForTown(townToDelete.getID()); //Remove all chunk permission from the deleted town
            deleteAllRole(townToDelete.getID()); //Delete all role from the deleted town
            deleteRolePermissionFromTown(townToDelete.getID()); //Delete all role permission from the deleted town
            NewClaimedChunkStorage.unclaimAllChunkFromTown(townToDelete);  //Unclaim all chunk from the deleted town NOT WORKING RN
            removeTownUpgradeFromDB(townToDelete.getID()); //Delete all town upgrade from the deleted town
        }

        updateAllScoreboardColor();
        townDataMap.remove(TownId);
        saveStats();
    }

    public static LinkedHashMap<String, TownData> getTownMap() {
        return townDataMap;
    }


    public static TownData get(PlayerData playerData){
        return get(playerData.getTownId());
    }
    public static TownData get(Player player){
        return get(PlayerDataStorage.get(player).getTownId());
    }
    public static TownData get(String townId) {
        return townDataMap.get(townId);
    }


    public static int getNumberOfTown() {
        return townDataMap.size();
    }

    public static void loadStats() {

        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/TAN - Towns.json");
        if (!file.exists())
            return;

        Reader reader;
        try {
            reader = new FileReader(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateTypeAdapter())
                .registerTypeAdapter(Map.class, (JsonDeserializer<Map<String, Object>>) (json1, typeOfT, context) -> new Gson().fromJson(json1, typeOfT))
                .create();

        Type type = new TypeToken<LinkedHashMap<String, TownData>>() {}.getType();

        townDataMap = gson.fromJson(reader, type);




        int ID = 0;
        for (Map.Entry<String, TownData> entry : townDataMap.entrySet()) {
            String cle = entry.getKey();
            int newID =  Integer.parseInt(cle.substring(1));
            if(newID > ID)
                ID = newID;
        }
        newTownId = ID+1;

    }

    public static void saveStats() {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/TAN - Towns.json");
        file.getParentFile().mkdirs();
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (Writer writer = new FileWriter(file, false)) {
            gson.toJson(townDataMap, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public static int getNewTownId() {
        return newTownId;
    }

    public static void initialize(String host, String username, String password) {
        try {
            try (Statement statement = connection.createStatement()){
                String sql = "CREATE TABLE IF NOT EXISTS tan_town_data (" +
                        "town_key VARCHAR(255) PRIMARY KEY," +
                        "name VARCHAR(255)," +
                        "uuid_leader VARCHAR(255)," +
                        "townDefaultRank VARCHAR(255)," +
                        "Description VARCHAR(255)," +
                        "DateCreated VARCHAR(255)," +
                        "townIconMaterialCode VARCHAR(255)," +
                        "isRecruiting BOOLEAN," +
                        "balance INT," +
                        "taxRate INT," +
                        "color VARCHAR(255))";
                statement.executeUpdate(sql);
            }

            try (Statement statement = connection.createStatement()){
                String sql = "CREATE TABLE IF NOT EXISTS tan_player_current_town (" +
                        "town_key VARCHAR(255)," +
                        "player_id VARCHAR(255) PRIMARY KEY)";
                statement.executeUpdate(sql);
            }
            try (Statement statement = connection.createStatement()){
                String sql = "CREATE TABLE IF NOT EXISTS tan_player_town_application (" +
                        "town_key VARCHAR(255) PRIMARY KEY," +
                        "player_id VARCHAR(255))";
                statement.executeUpdate(sql);
            }
            try (Statement statement = connection.createStatement()){
                String sql = "CREATE TABLE IF NOT EXISTS tan_player_town_role (" +
                        "town_id VARCHAR(255)," +
                        "name VARCHAR(255)," +
                        "level VARCHAR(255)," +
                        "rankIconName VARCHAR(255)," +
                        "salary INT," +
                        "isPayingTaxes BOOLEAN," +
                        "PRIMARY KEY (town_id, name))";
                statement.executeUpdate(sql);
            }
            try (Statement statement = connection.createStatement()){
                String sql = "CREATE TABLE IF NOT EXISTS tan_town_upgrades (" +
                        "rank_key VARCHAR(255) PRIMARY KEY," +
                        "level VARCHAR(255)," +
                        "chunk_level VARCHAR(255)," +
                        "player_cap_level VARCHAR(255)," +
                        "town_spawn_bought BOOL)";
                statement.executeUpdate(sql);
            }
            try (Statement statement = connection.createStatement()){
                String sql = "CREATE TABLE IF NOT EXISTS tan_chunk_permissions (" +
                        "PermissionId INT AUTO_INCREMENT PRIMARY KEY," +
                        "TownId VARCHAR(255)," +
                        "PermissionType VARCHAR(255)," +
                        "PermissionValue VARCHAR(255))";
                statement.executeUpdate(sql);
            }
            try (Statement statement = connection.createStatement()){
                String sql = "CREATE TABLE IF NOT EXISTS tan_town_role_permissions(" +
                        "TownID  VARCHAR(255)," +
                        "RankId  VARCHAR(255)," +
                        "PermissionType  VARCHAR(255)," +
                        "IsGranted  VARCHAR(255)," +
                        "PRIMARY KEY (TownID, RankId,PermissionType))";
                statement.executeUpdate(sql);
            }
            try (Statement statement = connection.createStatement()){
                String sql = "CREATE TABLE IF NOT EXISTS tan_town_RELATION(" +
                        "town_1_id  VARCHAR(255)," +
                        "town_2_id  VARCHAR(255)," +
                        "relation_type  VARCHAR(255)," +
                        "PRIMARY KEY (town_1_id, town_2_id,relation_type))";
                statement.executeUpdate(sql);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }







    public static void removeTownUpgradeFromDB(String townID) {
        String sql = "DELETE FROM tan_town_upgrades WHERE rank_key = ? ";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, townID);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addPlayerJoinRequestToDB(String playerUUID, String townID) {
        String sql = "INSERT INTO tan_player_town_application (town_key, player_id) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, townID);
            ps.setString(2, playerUUID);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removePlayerJoinRequestFromDB(String playerUUID, String townID) {
        String sql = "DELETE FROM tan_player_town_application WHERE town_key = ? AND player_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, townID);
            ps.setString(2, playerUUID);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean isPlayerAlreadyAppliedFromDB(String playerUUID, String townID) {

        String sql = "SELECT COUNT(*) FROM tan_player_town_application WHERE town_key = ? AND player_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, townID);
            ps.setString(2, playerUUID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static HashSet<String> getAllPlayerApplicationFrom(String townID) {
        HashSet<String> requests = new HashSet<>();
        String sql = "SELECT player_id FROM tan_player_town_application WHERE town_key = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, townID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    requests.add(rs.getString("player_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    public static void createChunkPermissions(String townId) {

        String sql = "INSERT INTO tan_chunk_permissions (TownId, PermissionType, PermissionValue) VALUES (?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (ChunkPermissionType type : ChunkPermissionType.values()) {
                ps.setString(1, townId);
                ps.setString(2, type.toString());
                ps.setString(3, "TOWN");
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateChunkPermission(String townId, ChunkPermissionType permissionType, TownChunkPermission newPermission) {
        String sql = "UPDATE tan_chunk_permissions SET PermissionValue = ? WHERE TownId = ? AND PermissionType = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, newPermission.toString());
            ps.setString(2, townId);
            ps.setString(3, permissionType.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteChunkPermissionsFor(String townId) {
        String sql = "DELETE FROM tan_chunk_permissions WHERE TownId = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, townId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static TownChunkPermission getPermission(String townId, ChunkPermissionType permissionType) {
        String sql = "SELECT PermissionValue FROM tan_chunk_permissions WHERE TownId = ? AND PermissionType = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, townId);
            ps.setString(2, permissionType.toString());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String permissionValue = rs.getString("PermissionValue");
                    return TownChunkPermission.valueOf(permissionValue);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void createRole(String townID, TownRank townRank) {
        String sql = "INSERT INTO tan_player_town_role (town_id, name,level, rankIconName, salary, isPayingTaxes) VALUES (?,?,?,?,?,?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, townID);
            ps.setString(2, townRank.getName());
            ps.setString(3, townRank.getRankEnum().toString());
            ps.setString(4, townRank.getRankIconName());
            ps.setInt(5, townRank.getSalary());
            ps.setBoolean(6, townRank.isPayingTaxes());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        createRankPermissions(townID, townRank.getName());
    }
    public static TownRank getRole(String townId, String roleName) {


        String sql = "SELECT * FROM tan_player_town_role WHERE town_id = ? AND name = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, townId);
            ps.setString(2, roleName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new TownRank(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("level"),
                        rs.getString("rankIconName"),
                        rs.getBoolean("isPayingTaxes"),
                        rs.getInt("salary")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void updateRank(String townId, TownRank townRank) {
        String sql = "UPDATE tan_player_town_role SET level = ?, rankIconName = ?, salary = ?, isPayingTaxes = ?  WHERE town_id = ? AND name = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, townRank.getRankEnum().toString());
            ps.setString(2, townRank.getRankIconName());
            ps.setInt(3, townRank.getSalary());
            ps.setBoolean(4, townRank.isPayingTaxes());
            ps.setString(5, townId);
            ps.setString(6, townRank.getName());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateRank(String townId, String oldName, TownRank townRank) {
        String sql = "UPDATE tan_player_town_role SET name = ?, level = ?, rankIconName = ?, salary = ?, isPayingTaxes = ?  WHERE town_id = ? AND name = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, townRank.getName());
            ps.setString(2, townRank.getRankEnum().toString());
            ps.setString(3, townRank.getRankIconName());
            ps.setInt(4, townRank.getSalary());
            ps.setBoolean(5, townRank.isPayingTaxes());
            ps.setString(6, townId);
            ps.setString(7, oldName);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteRole(String townId, String roleName) {
        String sql = "DELETE FROM tan_player_town_role WHERE town_id = ? AND name = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, townId);
            ps.setString(2, roleName);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteAllRole(String townId) {
        String sql = "DELETE FROM tan_player_town_role WHERE town_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, townId);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteRolePermission(String townId, String rankName) {
        String sql = "DELETE FROM tan_town_role_permissions WHERE TownID = ? AND RankId = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, townId);
            ps.setString(2, rankName);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteRolePermissionFromTown(String townId) {
        String sql = "DELETE FROM tan_town_role_permissions WHERE TownID = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, townId);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getPlayerIdsByTownAndRank(String townId, String rankName) {
        List<String> playerIds = new ArrayList<>();
        String sql = "SELECT player_id FROM tan_player_data WHERE town_id = ? AND town_rank = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, townId);
            ps.setString(2, rankName);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    playerIds.add(rs.getString("player_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playerIds;
    }

    public static List<TownRank> getRanksByTownId(String townId) {
        List<TownRank> ranks = new ArrayList<>();
        String sql = "SELECT * FROM tan_player_town_role WHERE town_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, townId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    ranks.add(new TownRank(
                            rs.getInt("int"),
                            rs.getString("name"),
                            rs.getString("level"),
                            rs.getString("rankIconName"),
                            rs.getBoolean("isPayingTaxes"),
                            rs.getInt("salary"))
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ranks;
    }

    public static void createRankPermissions(String townId, String rankName) {

        String sql = "INSERT INTO tan_town_role_permissions (TownId, RankId, PermissionType, isGranted) VALUES (?, ?, ?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (TownRolePermission permission : TownRolePermission.values()) {
                ps.setString(1, townId);
                ps.setString(2, rankName);
                ps.setString(3, permission.toString());
                ps.setBoolean(4, false);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean getRankPermission(String townId, String rankName, TownRolePermission permission) {
        String sql = "SELECT isGranted FROM tan_town_role_permissions WHERE TownId = ? AND RankId = ? AND PermissionType = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, townId);
            ps.setString(2, rankName);
            ps.setString(3, permission.toString());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("isGranted");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void swapRankPermission(String townId, String rankName, TownRolePermission permission) {
        String sql = "UPDATE tan_town_role_permissions SET isGranted = NOT isGranted WHERE TownId = ? AND RankId = ? AND PermissionType = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, townId);
            ps.setString(2, rankName);
            ps.setString(3, permission.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void renameRankPermission(String townId, String oldRankName, String newRankName) {
        String sql = "UPDATE tan_town_role_permissions SET RankId = ? WHERE TownId = ? AND RankId = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, newRankName);
            ps.setString(2, townId);
            ps.setString(3, oldRankName);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addTownRelation(String town1Id, String town2Id, TownRelation relationType) {
        String sql = "INSERT INTO tan_town_relation (town_1_id, town_2_id, relation_type) VALUES (?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, town1Id);
            ps.setString(2, town2Id);
            ps.setString(3, relationType.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> getTownsRelatedTo(String townId, TownRelation relationType) {
        ArrayList<String> relatedTowns = new ArrayList<>();
        String sql = "SELECT town_1_id, town_2_id FROM tan_town_relation WHERE (town_1_id = ? OR town_2_id = ?) AND relation_type = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, townId);
            ps.setString(2, townId);
            ps.setString(3, relationType.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String relatedTownId = rs.getString("town_1_id").equals(townId) ? rs.getString("town_2_id") : rs.getString("town_1_id");
                    relatedTowns.add(relatedTownId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return relatedTowns;
    }

    public static void removeTownRelation(String townId1, String townId2, TownRelation relationType) {
        String sql = "DELETE FROM tan_town_relation WHERE " +
                "((town_1_id = ? AND town_2_id = ?) OR (town_1_id = ? AND town_2_id = ?)) AND relation_type = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, townId1);
            ps.setString(2, townId2);
            ps.setString(3, townId2);
            ps.setString(4, townId1);
            ps.setString(5, relationType.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeAllTownRelationWith(String townToDeleteID) {
        String sql = "DELETE FROM tan_town_relation WHERE town_1_id = ? ";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, townToDeleteID);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static TownRelation getRelationBetweenTowns(String townId1, String townId2) {
        TownRelation relationType = null;
        String sql = "SELECT relation_type FROM tan_town_relation WHERE " +
                "(town_1_id = ? AND town_2_id = ?) OR (town_1_id = ? AND town_2_id = ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, townId1);
            ps.setString(2, townId2);
            ps.setString(3, townId2);
            ps.setString(4, townId1);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    relationType = TownRelation.valueOf(rs.getString("relation_type"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return relationType; // Retourne null si aucune relation n'est trouvée
    }

    public static void removeAllChunkPermissionsForTown(String townId) {
        String sql = "DELETE FROM tan_chunk_permissions WHERE TownId = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, townId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int getNumberOfPlayerByRank(String townId, String rankName) {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM tan_player_data WHERE town_id = ? AND town_rank = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, townId);
            ps.setString(2, rankName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1); // Le premier et unique résultat sera le nombre de joueurs
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count;
    }

    public static boolean isNameUsed(String townName){
        if(ConfigUtil.getCustomConfig("config.yml").getBoolean("AllowNameDuplication",true))
            return false;
        
        for (TownData town : townDataMap.values()){
            if(townName.equals(town.getName()))
                return true;
        }
        return false;
    }

}

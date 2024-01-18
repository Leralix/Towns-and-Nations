package org.tan.TownsAndNations.storage;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.TownsAndNations;

import java.io.*;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.*;
import java.util.Date;

import static org.tan.TownsAndNations.TownsAndNations.isSqlEnable;

public class TownDataStorage {

    private static LinkedHashMap<String, TownData> townDataMap = new LinkedHashMap<>();
    private static int newTownId = 1;
    private static Connection connection;

    public static TownData newTown(String townName, Player player){
        String townId = "T"+newTownId;
        String playerID = player.getUniqueId().toString();
        newTownId++;

        TownData newTown = new TownData( townId, townName, playerID);

        if(isSqlEnable()){
            saveTownDataToDatabase(newTown);
            addPlayerToTownDatabase(townId, playerID);
        }
        else{
            townDataMap.put(townId,newTown);
            saveStats();
        }
        return newTown;
    }

    private static void saveTownDataToDatabase(TownData newTown) {

        String sql = "INSERT INTO tan_town_data (town_key, name, uuid_leader," +
                " townDefaultRank, Description, DateCreated, townIconMaterialCode, isRecruiting)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, newTown.getID());
            ps.setString(2, newTown.getName());
            ps.setString(3, newTown.getUuidLeader());
            ps.setString(4, newTown.getTownDefaultRank());
            ps.setString(5, newTown.getDescription());
            ps.setString(6, newTown.getDateCreated());
            ps.setString(7, newTown.getTownIconName());
            ps.setBoolean(8, newTown.isRecruiting());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addPlayerToTownDatabase(String townId, String playerUUID) {
        String sql = "INSERT INTO tan_player_current_town (town_key, player_id) VALUES (?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, townId);
            ps.setString(2, playerUUID);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removePlayerFromTownDatabase(String playerUUID) {
        String sql = "DELETE FROM tan_player_current_town WHERE  player_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, playerUUID);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void removeTown(String TownId) {
        if (isSqlEnable()) {
            removeTownFromDatabase(TownId);
        } else {
            removeTownFromMemory(TownId);
        }
    }

    private static void removeTownFromMemory(String TownId) {
        TownData townData = townDataMap.get(TownId);
        if (townData != null) {
            HashSet<String> array = townData.getPlayerList();
            for (String playerUUID : array) {
                PlayerDataStorage.get(playerUUID).setTownId(null);
            }
        }
        townDataMap.remove(TownId);
        saveStats();
    }

    private static void removeTownFromDatabase(String TownId) {
        // Supprimer les données de la ville de la table tan_town_data
        String sqlDeleteTown = "DELETE FROM tan_town_data WHERE town_key = ?";
        try (PreparedStatement psDeleteTown = connection.prepareStatement(sqlDeleteTown)) {
            psDeleteTown.setString(1, TownId);
            psDeleteTown.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Mettre à jour les données des joueurs liés à cette ville dans la base de données
        String sqlUpdatePlayers = "UPDATE tan_player_current_town SET town_key = NULL WHERE town_key = ?";
        try (PreparedStatement psUpdatePlayers = connection.prepareStatement(sqlUpdatePlayers)) {
            psUpdatePlayers.setString(1, TownId);
            psUpdatePlayers.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static LinkedHashMap<String, TownData> getTownList() {
        if (isSqlEnable()) {
            return getTownListFromDatabase();
        } else {
            return townDataMap;
        }
    }

    private static LinkedHashMap<String, TownData> getTownListFromDatabase() {
        LinkedHashMap<String, TownData> townList = new LinkedHashMap<>();
        String sql = "SELECT * FROM tan_town_data";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                TownData townData = new TownData(
                        rs.getString("town_key"),
                        rs.getString("name"),
                        rs.getString("uuid_leader")


                );
                townList.put(townData.getID(), townData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return townList;
    }
    public static TownData get(PlayerData playerData){
        return get(playerData.getTownId());
    }
    public static TownData get(Player player){
        return get(PlayerDataStorage.get(player.getUniqueId().toString()).getTownId());
    }
    public static TownData get(String townId) {
        if (isSqlEnable()) {
            return getTownDataFromDatabase(townId);
        } else {
            return townDataMap.get(townId);
        }
    }

    private static TownData getTownDataFromDatabase(String townId) {
        String sql = "SELECT * FROM tan_town_data WHERE town_key = ?"; // Assurez-vous que le nom de la colonne et de la table est correct

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, townId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new TownData(
                            rs.getString("town_key"),
                            rs.getString("name"),
                            rs.getString("uuid_leader"),
                            rs.getString("Description"),
                            rs.getString("DateCreated"),
                            rs.getString("townIconMaterialCode"),
                            rs.getString("townDefaultRank"),
                            rs.getBoolean("isRecruiting")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // Retourner null si aucune ville correspondante n'est trouvée
    }

    public static int getNumberOfTown() {
        if (isSqlEnable()) {
            return getNumberOfTownFromDatabase();
        } else {
            return townDataMap.size();
        }
    }

    private static int getNumberOfTownFromDatabase() {
        String sql = "SELECT COUNT(*) FROM tan_town_data";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
    public static void loadStats() {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateTypeAdapter())
                .create();

        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/TAN - Towns.json");
        if (file.exists()){
            Reader reader = null;
            try {
                reader = new FileReader(file);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
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

    public static void initialize() {
        try {
            String url = "jdbc:mysql://localhost:3306/minecraft";
            String username = "root";
            String password = "password";
            connection = DriverManager.getConnection(url, username, password);
            try (Statement statement = connection.createStatement()){
                String sql = "CREATE TABLE IF NOT EXISTS tan_town_data (" +
                        "town_key VARCHAR(255) PRIMARY KEY," +
                        "name VARCHAR(255)," +
                        "uuid_leader VARCHAR(255)," +
                        "townDefaultRank VARCHAR(255)," +
                        "Description VARCHAR(255)," +
                        "DateCreated VARCHAR(255)," +
                        "townIconMaterialCode VARCHAR(255)," +
                        "isRecruiting BOOLEAN)";
                statement.executeUpdate(sql);
            }

            try (Statement statement = connection.createStatement()){
                String sql = "CREATE TABLE IF NOT EXISTS tan_player_current_town (" +
                        "town_key VARCHAR(255) PRIMARY KEY," +
                        "player_id VARCHAR(255))";
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
                        "rank_key VARCHAR(255) PRIMARY KEY," +
                        "town_id VARCHAR(255)," +
                        "name VARCHAR(255)," +
                        "rankIconName VARCHAR(255)," +
                        "salary INT," +
                        "isPayingTaxes BOOLEAN)";
                statement.executeUpdate(sql);
            }



        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateTownData(TownData townData) {
        if (isSqlEnable() && townData != null) {
            String sql = "UPDATE tan_town_data SET name = ?, uuid_leader = ?, townDefaultRank = ?, " +
                    "Description = ?, DateCreated = ?, townIconMaterialCode = ?, isRecruiting = ? " +
                    "WHERE town_key = ?";

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, townData.getName());
                ps.setString(2, townData.getUuidLeader());
                ps.setString(3, townData.getTownDefaultRank());
                ps.setString(4, townData.getDescription());
                ps.setString(5, townData.getDateCreated());
                ps.setString(6, townData.getTownIconName());
                ps.setBoolean(7, townData.isRecruiting());
                ps.setString(8, townData.getID());
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}

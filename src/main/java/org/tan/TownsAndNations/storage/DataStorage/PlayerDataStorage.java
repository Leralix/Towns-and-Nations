package org.tan.TownsAndNations.storage.DataStorage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.DataClass.PlayerData;

import java.io.*;
import java.sql.*;
import java.util.*;

import static org.tan.TownsAndNations.TownsAndNations.isSqlEnable;

public class PlayerDataStorage {

    private static ArrayList<PlayerData> stats = new ArrayList<>();
    private static Connection connection;

    public static PlayerData createPlayerDataClass(Player p) {
        PlayerData stat = new PlayerData(p);

        if (isSqlEnable()) {
            savePlayerDataToDatabase(stat);
        } else {
            stats.add(stat);
            saveStats();
        }

        return stat;
    }

    private static void savePlayerDataToDatabase(PlayerData playerData) {
        // Exemple de requête SQL pour insérer les données du joueur
        // Adaptez cette requête selon la structure de votre base de données
        String sql = "INSERT INTO tan_player_data (player_id, player_name, balance,town_id,town_rank) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, playerData.getID()); // Assurez-vous que ces méthodes existent dans PlayerData
            ps.setString(2, playerData.getName());
            ps.setInt(3, playerData.getBalance());
            ps.setString(4, playerData.getTownId());
            ps.setString(5, playerData.getTownRankID());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteData(String uuid) {
        if (isSqlEnable()) {
            deleteDataFromDatabase(uuid);
        } else {
            for (PlayerData stat : stats) {
                if (stat.getID().equalsIgnoreCase(uuid)) {
                    stats.remove(stat);
                    break;
                }
            }
            saveStats();
        }
    }

    private static void deleteDataFromDatabase(String uuid) {
        String sql = "DELETE FROM tan_player_data WHERE player_id = ?"; // Assurez-vous que 'uuid' est le nom correct de la colonne

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, uuid);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static PlayerData get(OfflinePlayer player) {
        return get(player.getUniqueId().toString());
    }
    public static PlayerData get(Player player) {
        return get(player.getUniqueId().toString());
    }
    public static PlayerData get(UUID player) {
        return get(player.toString());
    }
    public static PlayerData get(String id){

        if(isSqlEnable()){
            return getFromDatabase(id);
        }
        else {
            for (PlayerData stat : stats) {
                if (stat.getID().equalsIgnoreCase(id)) {
                    return stat;
                }
            }
            return createPlayerDataClass(TownsAndNations.getPlugin().getServer().getPlayer(UUID.fromString(id)));
        }


    }
    private static PlayerData getFromDatabase(String id) {
        String sql = "SELECT * FROM tan_player_data WHERE player_id = ?"; // Assurez-vous que le nom de la colonne est correct

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new PlayerData(
                            rs.getString("player_id"),
                            rs.getString("player_name"),
                            rs.getInt("balance"),
                            rs.getString("town_id"),
                            rs.getString("town_rank")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Player does not exist in database (new player)
        PlayerData newPlayer = new PlayerData(TownsAndNations.getPlugin().getServer().getPlayer(UUID.fromString(id)));
        savePlayerDataToDatabase(newPlayer);

        return newPlayer;

    }



    public static List<PlayerData> getStats() {
        if (isSqlEnable()) {
            return getStatsFromDatabase();
        } else {
            return stats;
        }
    }

    private static List<PlayerData> getStatsFromDatabase() {
        List<PlayerData> playerDataList = new ArrayList<>();
        String sql = "SELECT * FROM tan_player_data"; // Assurez-vous que le nom de la table est correct

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                PlayerData playerData = new PlayerData(
                        rs.getString("player_id"),
                        rs.getString("player_name"),
                        rs.getInt("balance"),
                        rs.getString("town_id"),
                        rs.getString("town_rank")
                );
                playerDataList.add(playerData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return playerDataList;
    }

    public static void loadStats(){

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/TAN - Stats.json");
        if (file.exists()){
            Reader reader;
            try {
                reader = new FileReader(file);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            PlayerData[] n = gson.fromJson(reader, PlayerData[].class);
            stats = new ArrayList<>(Arrays.asList(n));

        }

    }

    public static void saveStats() {

        Gson gson = new Gson();
        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/TAN - Stats.json");
        file.getParentFile().mkdir();

        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Writer writer;
        try {
            writer = new FileWriter(file, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        gson.toJson(stats, writer);
        try {
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void initialize(String host, String username, String password) {
        try {
            connection = DriverManager.getConnection(host, username, password);
            try (Statement statement = connection.createStatement()){
                String sql = "CREATE TABLE IF NOT EXISTS " +
                        "tan_player_data (player_id VARCHAR(255) PRIMARY KEY," +
                        "player_name VARCHAR(255)," +
                        "balance INT," +
                        "town_id VARCHAR(255)," +
                        "town_rank VARCHAR(255))";
                statement.executeUpdate(sql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void updatePlayerDataInDatabase(PlayerData playerData) {
        String sql = "UPDATE tan_player_data SET player_name = ?, balance = ?, town_id = ?, town_rank = ? WHERE player_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, playerData.getName());
            ps.setInt(2, playerData.getBalance());
            ps.setString(3, playerData.getTownId());
            ps.setString(4, playerData.getTownRankID());
            ps.setString(5, playerData.getID());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
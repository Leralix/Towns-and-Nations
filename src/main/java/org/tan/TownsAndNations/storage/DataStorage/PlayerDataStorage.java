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
        stats.add(stat);
        saveStats();
        return stat;
    }

    public static void deleteData(String uuid) {
        for (PlayerData stat : stats) {
            if (stat.getID().equalsIgnoreCase(uuid)) {
                stats.remove(stat);
                break;
            }
        }
        saveStats();

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
        for (PlayerData stat : stats) {
            if (stat.getID().equalsIgnoreCase(id)) {
                return stat;
            }
        }
        return createPlayerDataClass(TownsAndNations.getPlugin().getServer().getPlayer(UUID.fromString(id)));
    }



    public static List<PlayerData> getLists() {
        return stats;
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


}
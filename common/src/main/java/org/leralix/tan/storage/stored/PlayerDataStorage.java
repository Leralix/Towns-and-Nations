package org.leralix.tan.storage.stored;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.PlayerData;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class PlayerDataStorage {
    private static final String ERROR_MESSAGE = "Error while creating player storage";

    private static Map<String, PlayerData> playerStorage = new HashMap<>();

    private PlayerDataStorage() {
        throw new IllegalStateException("Utility class");
    }

    public static PlayerData createPlayerDataClass(Player p) {
        PlayerData playerData = new PlayerData(p);
        return createPlayerDataClass(playerData);
    }
    public static PlayerData createPlayerDataClass(PlayerData p) {
        playerStorage.put(p.getID(), p);
        saveStats();
        return p;
    }

    public static void deleteData(String playerID) {
        playerStorage.remove(playerID);
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

        if(id == null)
            return null;

        PlayerData res = playerStorage.get(id);
        if(res != null)
            return res;

        Player newPlayer = TownsAndNations.getPlugin().getServer().getPlayer(UUID.fromString(id));
        if(newPlayer != null){
            return createPlayerDataClass(newPlayer);
        }
        return null;
    }



    public static Collection<PlayerData> getLists() {
        return playerStorage.values();
    }

    public static void loadStats(){

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/TAN - Players.json");
        if (file.exists()){
            Reader reader;
            try {
                reader = new FileReader(file);
            } catch (FileNotFoundException e) {
                TownsAndNations.getPlugin().getLogger().severe(ERROR_MESSAGE);
                return;
            }
            Type type = new TypeToken<HashMap<String, PlayerData>>() {}.getType();
            playerStorage = gson.fromJson(reader, type);

        }

    }
    public static void saveStats() {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File(TownsAndNations.getPlugin().getDataFolder().getAbsolutePath() + "/TAN - Players.json");
        file.getParentFile().mkdir();


        try {
             file.createNewFile();
        } catch (IOException e) {
            TownsAndNations.getPlugin().getLogger().severe(ERROR_MESSAGE);
            return;
        }
        Writer writer;
        try {
            writer = new FileWriter(file, false);
        } catch (IOException e) {
            TownsAndNations.getPlugin().getLogger().severe(ERROR_MESSAGE);
            return;
        }
        gson.toJson(playerStorage, writer);
        try {
            writer.flush();
        } catch (IOException e) {
            TownsAndNations.getPlugin().getLogger().severe(ERROR_MESSAGE);
            return;
        }
        try {
            writer.close();
        } catch (IOException e) {
            TownsAndNations.getPlugin().getLogger().severe(ERROR_MESSAGE);
        }

    }

}
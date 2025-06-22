package org.leralix.tan.storage.stored;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.NoPlayerData;
import org.leralix.tan.dataclass.PlayerData;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataStorage {

    private static final String ERROR_MESSAGE = "Error while creating player storage";

    private static Map<String, ITanPlayer> playerStorage = new HashMap<>();

    private static PlayerDataStorage instance;

    private static ITanPlayer NO_PLAYER;

    private PlayerDataStorage() {
        loadStats();
    }

    public static synchronized PlayerDataStorage getInstance() {
        if (instance == null) {
            instance = new PlayerDataStorage();
            NO_PLAYER = new NoPlayerData();
        }
        return instance;
    }


    public ITanPlayer register(Player p) {
        ITanPlayer tanPlayer = new PlayerData(p);
        return register(tanPlayer);
    }
    ITanPlayer register(ITanPlayer p) {
        playerStorage.put(p.getID(), p);
        saveStats();
        return p;
    }

    public ITanPlayer get(OfflinePlayer player) {
        return get(player.getUniqueId().toString());
    }

    public ITanPlayer get(Player player) {
        return get(player.getUniqueId().toString());
    }

    public ITanPlayer get(UUID playerID) {
        return get(playerID.toString());
    }

    public ITanPlayer get(String id){

        if(id == null)
            return NO_PLAYER;

        ITanPlayer res = playerStorage.get(id);
        if(res != null)
            return res;

        Player newPlayer = Bukkit.getPlayer(UUID.fromString(id));
        if(newPlayer != null){
            return register(newPlayer);
        }
        throw new RuntimeException("Error : Player ID [" + id + "] has not been found" );
    }


    public Collection<ITanPlayer> getAll() {
        return playerStorage.values();
    }

    public void loadStats(){

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
    public void saveStats() {

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
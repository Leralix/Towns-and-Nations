package org.leralix.tan.storage.stored;

import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.leralix.tan.utils.constants.Constants;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class PremiumStorage extends JsonStorage<Boolean> {

    private PremiumStorage() {
        super("Premium accounts.json",
                new TypeToken<HashMap<String, Boolean>>() {}.getType(),
                new GsonBuilder()
                        .setPrettyPrinting()
                        .create());
    }

    private static PremiumStorage instance;

    public static synchronized PremiumStorage getInstance() {
        if (instance == null) {
            instance = new PremiumStorage();
        }
        return instance;
    }

    public boolean isPremium(String playerName) {

        if(Constants.onlineMode()){
            return true;
        }

        if(playerName == null){
            return false;
        }

        String key = playerName.toLowerCase();

        if (dataMap.containsKey(key)) {
            return dataMap.get(key);
        }

        boolean premium = fetchPremium(playerName);
        dataMap.put(key, premium);
        return premium;
    }

    private boolean fetchPremium(String playerName) {

        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + playerName);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(2000);
            connection.setReadTimeout(2000);

            if (connection.getResponseCode() == 200) {
                try (InputStream in = connection.getInputStream()) {
                    String json = new String(in.readAllBytes(), StandardCharsets.UTF_8);
                    JsonObject obj = JsonParser.parseString(json).getAsJsonObject();

                    return obj.has("id") && obj.has("name");
                }
            }
        } catch (Exception ignored) {

        }
        return false;
    }

    @Override
    public void reset() {
        instance = null;
    }
}

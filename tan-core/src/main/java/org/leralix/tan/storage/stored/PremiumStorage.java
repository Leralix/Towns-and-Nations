package org.leralix.tan.storage.stored;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.utils.constants.Constants;

public class PremiumStorage extends DatabaseStorage<Boolean> {

  private static final String TABLE_NAME = "tan_premium_accounts";
  private static volatile PremiumStorage instance;

  private PremiumStorage() {
    super(TABLE_NAME, Boolean.class, new GsonBuilder().setPrettyPrinting().create());
  }

  @Override
  protected void createTable() {
    String createTableSQL =
        """
            CREATE TABLE IF NOT EXISTS %s (
                id VARCHAR(255) PRIMARY KEY,
                data TEXT NOT NULL
            )
        """
            .formatted(TABLE_NAME);

    try (Connection conn = getDatabase().getDataSource().getConnection();
        Statement stmt = conn.createStatement()) {
      stmt.execute(createTableSQL);
    } catch (SQLException e) {
      TownsAndNations.getPlugin()
          .getLogger()
          .severe("Error creating table " + TABLE_NAME + ": " + e.getMessage());
    }
  }

  public static PremiumStorage getInstance() {
    // Double-checked locking without initial synchronization (fast path)
    if (instance == null) {
      synchronized (PremiumStorage.class) {
        if (instance == null) {
          instance = new PremiumStorage();
        }
      }
    }
    return instance;
  }

  public boolean isPremium(String playerName) {

    if (Constants.onlineMode()) {
      return true;
    }

    if (playerName == null) {
      return false;
    }

    String key = playerName.toLowerCase();

    Boolean cachedValue = get(key).join();
    if (cachedValue != null) {
      return cachedValue;
    }

    boolean premium = fetchPremium(playerName);
    put(key, premium);
    return premium;
  }

  private boolean fetchPremium(String playerName) {

    HttpURLConnection connection = null;
    try {
      // P4.1: Use URI.toURL() instead of deprecated URL constructor
      URL url =
          java.net
              .URI
              .create("https://api.mojang.com/users/profiles/minecraft/" + playerName)
              .toURL();
      connection = (HttpURLConnection) url.openConnection();
      connection.setConnectTimeout(2000);
      connection.setReadTimeout(2000);

      if (connection.getResponseCode() == 200) {
        try (InputStream in = connection.getInputStream()) {
          String json = new String(in.readAllBytes(), StandardCharsets.UTF_8);
          JsonObject obj = JsonParser.parseString(json).getAsJsonObject();

          return obj.has("id") && obj.has("name");
        }
      }
    } catch (Exception e) {
      TownsAndNations.getPlugin()
          .getLogger()
          .fine("Could not verify premium status for player " + playerName + ": " + e.getMessage());
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
    return false;
  }

  @Override
  public void reset() {
    instance = null;
  }
}

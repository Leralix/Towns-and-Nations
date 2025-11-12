package org.leralix.tan.utils.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.leralix.tan.TownsAndNations;

/**
 * Manages cooldown configuration from cooldowns.yml.
 *
 * <p>This class loads cooldown values for various commands and provides easy access to them.
 *
 * @since 0.16.0
 */
public class CooldownConfig {

  private static CooldownConfig instance;
  private FileConfiguration config;

  private CooldownConfig() {
    loadConfig();
  }

  public static CooldownConfig getInstance() {
    if (instance == null) {
      instance = new CooldownConfig();
    }
    return instance;
  }

  /** Loads the cooldowns.yml configuration file. */
  private void loadConfig() {
    File configFile = new File(TownsAndNations.getPlugin().getDataFolder(), "cooldowns.yml");

    // Create file if it doesn't exist
    if (!configFile.exists()) {
      try {
        configFile.getParentFile().mkdirs();
        try (InputStream defaultConfig = TownsAndNations.getPlugin().getResource("cooldowns.yml")) {
          if (defaultConfig != null) {
            Files.copy(defaultConfig, configFile.toPath());
          }
        }
      } catch (IOException e) {
        TownsAndNations.getPlugin()
            .getLogger()
            .severe("Could not create cooldowns.yml: " + e.getMessage());
      }
    }

    // Load configuration
    this.config = YamlConfiguration.loadConfiguration(configFile);
  }

  /** Reloads the configuration from disk. */
  public void reload() {
    loadConfig();
  }

  /**
   * Gets a cooldown value in seconds.
   *
   * @param path The config path (e.g., "town.create")
   * @param defaultValue Default value if not found
   * @return Cooldown in seconds
   */
  public long getCooldown(String path, long defaultValue) {
    return config.getLong(path, defaultValue);
  }

  /**
   * Gets a cooldown value in seconds.
   *
   * @param path The config path (e.g., "town.create")
   * @return Cooldown in seconds, or 0 if not found
   */
  public long getCooldown(String path) {
    return getCooldown(path, 0L);
  }

  // Convenience methods for common cooldowns

  public long getTownCreateCooldown() {
    return getCooldown("town.create", 300);
  }

  public long getTownDeleteCooldown() {
    return getCooldown("town.delete", 60);
  }

  public long getTownRenameCooldown() {
    return getCooldown("town.rename", 600);
  }

  public long getTownInviteCooldown() {
    return getCooldown("town.invite", 5);
  }

  public long getTownKickCooldown() {
    return getCooldown("town.kick", 10);
  }

  public long getRegionCreateCooldown() {
    return getCooldown("region.create", 600);
  }

  public long getRegionDeleteCooldown() {
    return getCooldown("region.delete", 120);
  }

  public long getChunkClaimCooldown() {
    return getCooldown("chunk.claim", 2);
  }

  public long getChunkUnclaimCooldown() {
    return getCooldown("chunk.unclaim", 2);
  }

  public long getChunkAutoclaimCooldown() {
    return getCooldown("chunk.autoclaim", 1);
  }

  public long getWarDeclareCooldown() {
    return getCooldown("war.declare", 1800);
  }

  public long getDiplomacyAllyCooldown() {
    return getCooldown("diplomacy.ally", 60);
  }

  public long getDiplomacyEnemyCooldown() {
    return getCooldown("diplomacy.enemy", 60);
  }
}

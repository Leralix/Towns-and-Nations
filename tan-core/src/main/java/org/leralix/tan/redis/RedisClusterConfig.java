package org.leralix.tan.redis;

import java.util.logging.Logger;
import org.bukkit.configuration.file.FileConfiguration;

public class RedisClusterConfig {

  private static final Logger logger = Logger.getLogger(RedisClusterConfig.class.getName());

  public static JedisManager createRedisClient(FileConfiguration config) {
    String mode = config.getString("redis.mode", "single").toLowerCase();

    // Pour l'instant, seul le mode single est supporté avec Jedis
    // Le mode cluster nécessiterait JedisCluster
    if (!mode.equals("single")) {
      logger.warning(
          "Mode Redis '" + mode + "' non supporté avec Jedis. Utilisation du mode 'single'.");
    }

    return createSingleClient(config);
  }

  private static JedisManager createSingleClient(FileConfiguration config) {
    RedisServerConfig serverConfig = new RedisServerConfig(config);

    String username = config.getString("redis.username", null);
    String password = serverConfig.getPassword();

    if (username != null && !username.isEmpty()) {
      logger.info("Redis: ACL activé (username: " + username + ")");
    }

    if (password != null && !password.isEmpty()) {
      logger.info("Redis: Authentification activée (mot de passe fourni)");
    } else if (password != null && password.isEmpty()) {
      logger.info("Redis: Authentification avec mot de passe vide");
      password = ""; // Jedis gère "" comme auth vide
    } else {
      logger.info("Redis: Pas d'authentification (mot de passe non configuré)");
      password = null; // Jedis n'enverra pas AUTH si null
    }

    return new JedisManager(
        serverConfig.getHost(),
        serverConfig.getPort(),
        username,
        password,
        serverConfig.getDatabase(),
        serverConfig.getConnectionPoolSize());
  }
}

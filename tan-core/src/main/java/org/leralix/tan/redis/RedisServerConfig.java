package org.leralix.tan.redis;

import org.bukkit.configuration.file.FileConfiguration;

public class RedisServerConfig {

  private final String serverId;
  private final String host;
  private final int port;
  private final String password;
  private final int database;
  private final int timeout;
  private final int retryAttempts;
  private final int retryInterval;
  private final int subscriptionTimeout;
  private final int subscriptionConnectionPoolSize;
  private final int subscriptionsPerConnection;
  private final int connectionPoolSize;
  private final int connectionMinimumIdleSize;
  private final boolean keepAlive;
  private final int pingInterval;
  private final String globalChannel;
  private final String serverEventsChannel;
  private final String townSyncChannel;
  private final String playerSyncChannel;
  private final boolean heartbeatEnabled;
  private final int heartbeatInterval;
  private final int heartbeatTimeout;

  public RedisServerConfig(FileConfiguration config) {
    this.serverId = config.getString("redis.server-id", "server-" + System.currentTimeMillis());
    this.host = config.getString("redis.single.host", "localhost");
    this.port = config.getInt("redis.single.port", 6379);
    this.password = config.getString("redis.password");
    this.database = config.getInt("redis.database", 0);
    this.timeout = config.getInt("redis.connection.timeout", 3000);
    this.retryAttempts = config.getInt("redis.connection.retry-attempts", 3);
    this.retryInterval = config.getInt("redis.connection.retry-interval", 1500);
    this.subscriptionTimeout = config.getInt("redis.connection.subscription-timeout", 5000);
    this.subscriptionConnectionPoolSize =
        config.getInt("redis.connection.subscription-connection-pool-size", 50);
    this.subscriptionsPerConnection =
        config.getInt("redis.connection.subscriptions-per-connection", 5);
    this.connectionPoolSize = config.getInt("redis.connection.connection-pool-size", 64);
    this.connectionMinimumIdleSize =
        config.getInt("redis.connection.connection-minimum-idle-size", 10);
    this.keepAlive = config.getBoolean("redis.connection.keep-alive", true);
    this.pingInterval = config.getInt("redis.connection.ping-interval", 30000);
    this.globalChannel = config.getString("redis.channels.global", "tan:global");
    this.serverEventsChannel =
        config.getString("redis.channels.server-events", "tan:server-events");
    this.townSyncChannel = config.getString("redis.channels.town-sync", "tan:town-sync");
    this.playerSyncChannel = config.getString("redis.channels.player-sync", "tan:player-sync");
    this.heartbeatEnabled = config.getBoolean("redis.heartbeat.enabled", true);
    this.heartbeatInterval = config.getInt("redis.heartbeat.interval", 30);
    this.heartbeatTimeout = config.getInt("redis.heartbeat.timeout", 60);
  }

  public String getServerKey(String key) {
    return "tan:" + serverId + ":" + key;
  }

  public String getGlobalKey(String key) {
    return "tan:global:" + key;
  }

  public String getHeartbeatKey() {
    return "tan:heartbeat:" + serverId;
  }

  public String getActiveServersKey() {
    return "tan:active-servers";
  }

  public String getPlayerKey(String playerUuid) {
    return getServerKey("player:" + playerUuid);
  }

  public String getServerChannel() {
    return "tan:" + serverId;
  }

  public String getServerId() {
    return serverId;
  }

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  public String getPassword() {
    return password;
  }

  public int getDatabase() {
    return database;
  }

  public int getTimeout() {
    return timeout;
  }

  public int getRetryAttempts() {
    return retryAttempts;
  }

  public int getRetryInterval() {
    return retryInterval;
  }

  public int getSubscriptionTimeout() {
    return subscriptionTimeout;
  }

  public int getSubscriptionConnectionPoolSize() {
    return subscriptionConnectionPoolSize;
  }

  public int getSubscriptionsPerConnection() {
    return subscriptionsPerConnection;
  }

  public int getConnectionPoolSize() {
    return connectionPoolSize;
  }

  public int getConnectionMinimumIdleSize() {
    return connectionMinimumIdleSize;
  }

  public boolean isKeepAlive() {
    return keepAlive;
  }

  public int getPingInterval() {
    return pingInterval;
  }

  public String getGlobalChannel() {
    return globalChannel;
  }

  public String getServerEventsChannel() {
    return serverEventsChannel;
  }

  public String getTownSyncChannel() {
    return townSyncChannel;
  }

  public String getPlayerSyncChannel() {
    return playerSyncChannel;
  }

  public boolean isHeartbeatEnabled() {
    return heartbeatEnabled;
  }

  public int getHeartbeatInterval() {
    return heartbeatInterval;
  }

  public int getHeartbeatTimeout() {
    return heartbeatTimeout;
  }

  public String getRedisAddress() {
    return "redis://" + host + ":" + port;
  }

  @Override
  public String toString() {
    return "RedisServerConfig{"
        + "serverId='"
        + serverId
        + '\''
        + ", host='"
        + host
        + '\''
        + ", port="
        + port
        + ", database="
        + database
        + ", connectionPoolSize="
        + connectionPoolSize
        + ", subscriptionConnectionPoolSize="
        + subscriptionConnectionPoolSize
        + '}';
  }
}

package org.leralix.tan.redis;

import com.google.gson.Gson;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.leralix.tan.utils.CocoLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedisServerRegistry {

  private static final Logger LOGGER = LoggerFactory.getLogger(RedisServerRegistry.class);
  private static final Gson GSON = new Gson();

  private final JedisManager jedisManager;
  private final RedisServerConfig config;
  private final java.util.concurrent.ScheduledExecutorService scheduler;
  private java.util.concurrent.ScheduledFuture<?> heartbeatTask;

  public RedisServerRegistry(JedisManager jedisManager, RedisServerConfig config) {
    this.jedisManager = jedisManager;
    this.config = config;
    this.scheduler =
        java.util.concurrent.Executors.newScheduledThreadPool(
            1,
            r -> {
              Thread t = new Thread(r, "TaN-Redis-Heartbeat");
              t.setDaemon(true);
              return t;
            });
  }

  public void registerServer() {
    try {
      jedisManager.addToSet(config.getActiveServersKey(), config.getServerId());

      publishServerEvent(new ServerEvent(config.getServerId(), ServerEvent.Type.CONNECT));

      if (config.isHeartbeatEnabled()) {
        startHeartbeat();
      }
    } catch (Exception ex) {
      LOGGER.error(CocoLogger.error("Échec enregistrement serveur: " + ex.getMessage()), ex);
    }
  }

  public void unregisterServer() {
    try {
      if (heartbeatTask != null) {
        heartbeatTask.cancel(false);
        heartbeatTask = null;
      }

      jedisManager.removeFromSet(config.getActiveServersKey(), config.getServerId());
      jedisManager.delete(config.getHeartbeatKey());

      publishServerEvent(new ServerEvent(config.getServerId(), ServerEvent.Type.DISCONNECT));
    } catch (Exception ex) {
      LOGGER.error(CocoLogger.error("Échec désenregistrement serveur: " + ex.getMessage()), ex);
    } finally {
      scheduler.shutdown();
    }
  }

  private void startHeartbeat() {
    int interval = config.getHeartbeatInterval();
    int timeout = config.getHeartbeatTimeout();

    heartbeatTask =
        scheduler.scheduleAtFixedRate(
            () -> {
              try {
                long timestamp = System.currentTimeMillis();
                jedisManager.set(config.getHeartbeatKey(), String.valueOf(timestamp), timeout);
              } catch (Exception ex) {
              }
            },
            0,
            interval,
            TimeUnit.SECONDS);
  }

  public Set<String> getActiveServers() {
    try {
      return jedisManager.getSetMembers(config.getActiveServersKey());
    } catch (Exception ex) {
      LOGGER.error(CocoLogger.error("Échec lecture serveurs actifs: " + ex.getMessage()));
      return java.util.Collections.emptySet();
    }
  }

  public boolean isServerOnline(String serverId) {
    try {
      String heartbeatKey = "tan:heartbeat:" + serverId;
      String lastHeartbeatStr = jedisManager.get(heartbeatKey);

      if (lastHeartbeatStr == null) {
        return false;
      }

      long lastHeartbeat = Long.parseLong(lastHeartbeatStr);

      long now = System.currentTimeMillis();
      long timeout = config.getHeartbeatTimeout() * 1000L;

      return (now - lastHeartbeat) < timeout;
    } catch (Exception ex) {
      LOGGER.error(CocoLogger.error("Échec vérification heartbeat: " + ex.getMessage()));
      return false;
    }
  }

  public Long getLastHeartbeat(String serverId) {
    try {
      String heartbeatKey = "tan:heartbeat:" + serverId;
      String lastHeartbeatStr = jedisManager.get(heartbeatKey);

      if (lastHeartbeatStr == null) {
        return null;
      }

      return Long.parseLong(lastHeartbeatStr);
    } catch (Exception ex) {
      LOGGER.error(CocoLogger.error("Échec lecture heartbeat: " + ex.getMessage()));
      return null;
    }
  }

  private void publishServerEvent(ServerEvent event) {
    try {
      jedisManager.publishJson(config.getServerEventsChannel(), event);
    } catch (Exception ex) {
    }
  }

  public void addServerEventListener(ServerEventListener listener) {
    try {
      jedisManager.subscribe(
          config.getServerEventsChannel(),
          message -> {
            try {
              ServerEvent event = GSON.fromJson(message, ServerEvent.class);
              if (!event.getServerId().equals(config.getServerId())) {
                listener.onServerEvent(event);
              }
            } catch (Exception ex) {
              LOGGER.error(CocoLogger.error("Échec traitement event: " + ex.getMessage()));
            }
          });
    } catch (Exception ex) {
      LOGGER.error(CocoLogger.error("Échec enregistrement listener: " + ex.getMessage()));
    }
  }

  public static class ServerEvent implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private final String serverId;
    private final Type type;
    private final long timestamp;

    public enum Type {
      CONNECT,
      DISCONNECT,
      HEARTBEAT
    }

    public ServerEvent(String serverId, Type type) {
      this.serverId = serverId;
      this.type = type;
      this.timestamp = System.currentTimeMillis();
    }

    public String getServerId() {
      return serverId;
    }

    public Type getType() {
      return type;
    }

    public long getTimestamp() {
      return timestamp;
    }
  }

  @FunctionalInterface
  public interface ServerEventListener {
    void onServerEvent(ServerEvent event);
  }
}

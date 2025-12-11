package org.leralix.tan.commands.admin.redis;

import java.util.Set;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.redis.RedisServerRegistry;
import org.leralix.tan.utils.CocoLogger;

public class RedisAdminCommand implements CommandExecutor {

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (args.length < 2 || !args[0].equalsIgnoreCase("redis")) {
      return false;
    }

    String subCommand = args[1].toLowerCase();

    switch (subCommand) {
      case "list":
        handleList(sender);
        return true;

      case "info":
        if (args.length < 3) {
          sender.sendMessage(CocoLogger.error("Usage: /tan redis info <serveur-id>"));
          return true;
        }
        handleInfo(sender, args[2]);
        return true;

      case "status":
        handleStatus(sender);
        return true;

      default:
        sender.sendMessage(CocoLogger.warning("Sous-commande inconnue: " + subCommand));
        sender.sendMessage(CocoLogger.info("Commandes disponibles:"));
        sender.sendMessage(CocoLogger.info("  /tan redis list - Liste des serveurs"));
        sender.sendMessage(CocoLogger.info("  /tan redis info <id> - Info serveur"));
        sender.sendMessage(CocoLogger.info("  /tan redis status - Statut Redis"));
        return true;
    }
  }

  private void handleList(CommandSender sender) {
    RedisServerRegistry registry = TownsAndNations.getPlugin().getRedisServerRegistry();

    if (registry == null) {
      sender.sendMessage(CocoLogger.error("❌ Redis non initialisé"));
      return;
    }

    Set<String> activeServers = registry.getActiveServers();

    sender.sendMessage(CocoLogger.boxed("SERVEURS REDIS ACTIFS", CocoLogger.BRIGHT_CYAN));
    sender.sendMessage("");

    if (activeServers.isEmpty()) {
      sender.sendMessage(CocoLogger.warning("Aucun serveur trouvé"));
      return;
    }

    String currentServerId = TownsAndNations.getPlugin().getRedisServerConfig().getServerId();

    for (String serverId : activeServers) {
      boolean isOnline = registry.isServerOnline(serverId);
      Long lastHeartbeat = registry.getLastHeartbeat(serverId);

      String statusIcon = isOnline ? "🟢" : "🔴";
      String currentMarker = serverId.equals(currentServerId) ? " [VOUS]" : "";

      sender.sendMessage(
          CocoLogger.info(String.format("  %s %s%s", statusIcon, serverId, currentMarker)));

      if (lastHeartbeat != null && isOnline) {
        long lag = System.currentTimeMillis() - lastHeartbeat;
        sender.sendMessage(CocoLogger.info(String.format("     └─ Heartbeat: %dms ago", lag)));
      } else if (!isOnline) {
        sender.sendMessage(CocoLogger.warning("     └─ OFFLINE"));
      }
    }

    sender.sendMessage("");
    sender.sendMessage(CocoLogger.success("Total: " + activeServers.size() + " serveur(s)"));
  }

  private void handleInfo(CommandSender sender, String serverId) {
    RedisServerRegistry registry = TownsAndNations.getPlugin().getRedisServerRegistry();

    if (registry == null) {
      sender.sendMessage(CocoLogger.error("❌ Redis non initialisé"));
      return;
    }

    boolean isOnline = registry.isServerOnline(serverId);
    Long lastHeartbeat = registry.getLastHeartbeat(serverId);

    sender.sendMessage(CocoLogger.boxed("INFO SERVEUR: " + serverId, CocoLogger.BRIGHT_CYAN));
    sender.sendMessage("");

    sender.sendMessage(CocoLogger.info("  Statut: " + (isOnline ? "🟢 ONLINE" : "🔴 OFFLINE")));

    if (lastHeartbeat != null) {
      long lag = System.currentTimeMillis() - lastHeartbeat;
      long seconds = lag / 1000;
      sender.sendMessage(CocoLogger.info("  Dernier heartbeat: " + seconds + "s ago"));

      java.time.Instant instant = java.time.Instant.ofEpochMilli(lastHeartbeat);
      java.time.LocalDateTime dateTime =
          java.time.LocalDateTime.ofInstant(instant, java.time.ZoneId.systemDefault());
      sender.sendMessage(CocoLogger.info("  Timestamp: " + dateTime.toString()));
    } else {
      sender.sendMessage(CocoLogger.warning("  Aucun heartbeat trouvé"));
    }

    sender.sendMessage("");

    String currentServerId = TownsAndNations.getPlugin().getRedisServerConfig().getServerId();
    if (serverId.equals(currentServerId)) {
      sender.sendMessage(CocoLogger.success("✓ Ceci est VOTRE serveur"));
    }
  }

  private void handleStatus(CommandSender sender) {
    boolean redisEnabled = TownsAndNations.getPlugin().getRedisClient() != null;

    sender.sendMessage(CocoLogger.boxed("STATUT REDIS", CocoLogger.BRIGHT_CYAN));
    sender.sendMessage("");

    if (!redisEnabled) {
      sender.sendMessage(CocoLogger.error("❌ Redis désactivé"));
      return;
    }

    var config = TownsAndNations.getPlugin().getRedisServerConfig();
    var registry = TownsAndNations.getPlugin().getRedisServerRegistry();

    sender.sendMessage(CocoLogger.success("✓ Redis actif"));
    sender.sendMessage("");
    sender.sendMessage(CocoLogger.info("  Server ID: " + config.getServerId()));
    sender.sendMessage(CocoLogger.info("  Host: " + config.getHost() + ":" + config.getPort()));
    sender.sendMessage(CocoLogger.info("  Database: " + config.getDatabase()));
    sender.sendMessage(CocoLogger.info("  Pool Size: " + config.getConnectionPoolSize()));
    sender.sendMessage(
        CocoLogger.info("  Sub Pool: " + config.getSubscriptionConnectionPoolSize()));

    if (config.isHeartbeatEnabled()) {
      sender.sendMessage(CocoLogger.info("  Heartbeat: " + config.getHeartbeatInterval() + "s"));
    }

    sender.sendMessage("");

    Set<String> activeServers = registry.getActiveServers();
    sender.sendMessage(CocoLogger.success("Serveurs connectés: " + activeServers.size()));
  }
}

package org.leralix.tan.utils.constants;

import org.bukkit.configuration.ConfigurationSection;

public class DatabaseConstants {

  private String dbType;
  private String host;
  private int port;
  private String name;
  private String user;
  private String password;

  public DatabaseConstants(ConfigurationSection configurationSection) {

    dbType = configurationSection.getString("type", "sqlite");
    host = configurationSection.getString("host", "localhost");
    port = configurationSection.getInt("port", 3306);
    name = configurationSection.getString("name", "towns_and_nations");
    user = configurationSection.getString("user", "root");
    password = configurationSection.getString("password", "");
  }

  public String getDbType() {
    return dbType;
  }

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  public String getName() {
    return name;
  }

  public String getUser() {
    return user;
  }

  public String getPassword() {
    return password;
  }
}

package org.leralix.tan.economy;

import org.leralix.tan.dataclass.ITanPlayer;

public abstract class AbstractTanEcon {

  public abstract double getBalance(ITanPlayer tanPlayer);

  public abstract boolean has(ITanPlayer tanPlayer, double amount);

  public abstract void withdrawPlayer(ITanPlayer tanPlayer, double amount);

  public abstract void depositPlayer(ITanPlayer s, double amount);

  public abstract String getMoneyIcon();
}

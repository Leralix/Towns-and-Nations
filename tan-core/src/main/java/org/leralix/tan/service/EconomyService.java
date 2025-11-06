package org.leralix.tan.service;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServiceRegisterEvent;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.economy.TanEconomyExternal;

public class EconomyService implements Listener {

  @EventHandler
  public void onServiceRegister(ServiceRegisterEvent event) {
    if (event.getProvider().getProvider() instanceof Economy economy) {
      if (EconomyUtil.isStandalone()) {
        EconomyUtil.register(new TanEconomyExternal(economy));
      }
    }
  }
}

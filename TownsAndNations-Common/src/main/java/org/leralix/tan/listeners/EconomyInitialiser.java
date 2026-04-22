package org.leralix.tan.listeners;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServiceRegisterEvent;
import org.leralix.tan.utils.economy.EconomyUtil;
import org.leralix.tan.utils.economy.TanEconomyExternal;

public class EconomyInitialiser implements Listener {

    @EventHandler
    public void onServiceRegister(ServiceRegisterEvent event) {
        if (event.getProvider().getProvider() instanceof Economy economy && EconomyUtil.isStandalone()) {
            EconomyUtil.register(new TanEconomyExternal(economy));
        }

    }

}

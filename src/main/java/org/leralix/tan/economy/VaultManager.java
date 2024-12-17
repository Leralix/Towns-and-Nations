package org.leralix.tan.economy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.utils.config.ConfigTag;
import org.leralix.tan.utils.config.ConfigUtil;

import java.util.logging.Level;
import java.util.logging.Logger;

public class VaultManager {

    public static void setupVault() {
        AbstractTanEcon tanEcon;
        Logger logger = Bukkit.getLogger();


        if(ConfigUtil.getCustomConfig(ConfigTag.MAIN).getBoolean("UseTanEconomy",true)){
            TanEconomyVault tanEconomyVault = new TanEconomyVault();
            EconomyUtil.setEconomy(tanEconomyVault);
            Bukkit.getServicesManager().register(Economy.class, tanEconomyVault, TownsAndNations.getPlugin(), ServicePriority.Normal);
            logger.log(Level.INFO,"[TaN] -Vault is detected, registering TaN Economy");
        }
        else{
            RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp == null) {
                logger.log(Level.INFO,"[TaN] -No active vault economy. Running standalone");
                EconomyUtil.setEconomy(new TanEconomyStandalone());
                return;
            }
            tanEcon = new TanEconomyExternal(rsp.getProvider());
            EconomyUtil.setEconomy(tanEcon);
            logger.log(Level.INFO,"[TaN] -Vault is detected, using {0} as economy", rsp.getProvider().getName());
        }
    }
}

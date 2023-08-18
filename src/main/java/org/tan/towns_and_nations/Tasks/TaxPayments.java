package org.tan.towns_and_nations.Tasks;


import org.bukkit.scheduler.BukkitRunnable;
import org.tan.towns_and_nations.DataClass.PlayerDataClass;
import org.tan.towns_and_nations.DataClass.TownDataClass;
import org.tan.towns_and_nations.TownsAndNations;
import org.tan.towns_and_nations.storage.PlayerStatStorage;
import org.tan.towns_and_nations.storage.TownDataStorage;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class TaxPayments {




    public static void scheduleMidnightTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Calendar calendar = new GregorianCalendar();
                if (calendar.get(Calendar.HOUR_OF_DAY) == 0 && calendar.get(Calendar.MINUTE) == 0) {
                    TaxPayment();
                }
            }
        }.runTaskTimer(TownsAndNations.getPlugin(), 0L, 1200L); // Exécute toutes les 1200 ticks (1 minute en temps Minecraft)
    }


    public static void TaxPayment() {
        TownsAndNations.getPluginLogger().info("Commande executée a minuit!");


        for (PlayerDataClass playerStat : PlayerStatStorage.getStats()){

            if (!playerStat.haveTown()) continue;
            TownDataClass playerTown = TownDataStorage.getTown(playerStat);
            if (!playerTown.getRank(playerStat.getTownRank()).isPayingTaxes()) continue;
            int tax = playerTown.getTreasury().getFlatTax();

            if(playerStat.getBalance() < tax){
                playerStat.removeFromBalance(tax);
                playerTown.getTreasury().addToBalance(tax);
            }
        }
    }


}

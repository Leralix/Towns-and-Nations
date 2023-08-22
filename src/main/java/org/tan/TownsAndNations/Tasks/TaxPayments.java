package org.tan.TownsAndNations.Tasks;


import org.bukkit.scheduler.BukkitRunnable;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.storage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.TownDataStorage;
import org.tan.TownsAndNations.utils.ChatUtils;

import java.util.Calendar;
import java.util.GregorianCalendar;

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


        for (PlayerData playerStat : PlayerDataStorage.getStats()){

            if (!playerStat.haveTown()) continue;
            TownData playerTown = TownDataStorage.get(playerStat);
            if (!playerTown.getRank(playerStat.getTownRank()).isPayingTaxes()) continue;
            int tax = playerTown.getTreasury().getFlatTax();

            if(playerStat.getBalance() > tax){
                playerStat.removeFromBalance(tax);
                playerTown.getTreasury().addToBalance(tax);

                TownsAndNations.getPluginLogger().info(playerStat.getName() + " has paid " + tax + "$ to the town " + playerTown.getName());
            }
            else{
                TownsAndNations.getPluginLogger().info(playerStat.getName() + " has not enough money to pay " + tax + "$ to the town " + playerTown.getName());
            }


        }

        TownsAndNations.getPluginLogger().info(ChatUtils.getTANString() + Lang.DAILY_TAXES_SUCCESS_LOG.getTranslation());

    }


}

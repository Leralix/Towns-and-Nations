package org.tan.TownsAndNations.Tasks;


import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.DataClass.TownRank;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.storage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.TownDataStorage;
import org.tan.TownsAndNations.utils.ArchiveUtil;
import org.tan.TownsAndNations.utils.ChatUtils;
import org.tan.TownsAndNations.utils.ConfigUtil;
import org.tan.TownsAndNations.utils.EconomyUtil;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

public class DailyTasks {

    public static void scheduleMidnightTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Calendar calendar = new GregorianCalendar();
                if (calendar.get(Calendar.HOUR_OF_DAY) == 0 && calendar.get(Calendar.MINUTE) == 0) {
                    TaxPayment();
                    ChunkPayment();
                    SalaryPayment();
                    ArchiveUtil.archiveFiles();

                }
            }
        }.runTaskTimer(TownsAndNations.getPlugin(), 0L, 1200L); // Exécute toutes les 1200 ticks (1 minute en temps Minecraft)
    }


    public static void TaxPayment() {


        for (PlayerData playerStat : PlayerDataStorage.getStats()){
            OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(UUID.fromString(playerStat.getUuid()));



            if (!playerStat.haveTown()) continue;
            TownData playerTown = TownDataStorage.get(playerStat);
            if (!playerTown.getRank(playerStat.getTownRankID()).isPayingTaxes()) continue;
            int tax = playerTown.getFlatTax();

            if(EconomyUtil.getBalance(offlinePlayer) > tax){
                EconomyUtil.removeFromBalance(offlinePlayer,tax);
                playerTown.addToBalance(tax);
                playerTown.getTreasury().addTaxHistory(playerStat.getName(), playerStat.getUuid(), tax);
                //TownsAndNations.getPluginLogger().info(playerStat.getName() + " has paid " + tax + "$ to the town " + playerTown.getName());
            }
            else{
                //TownsAndNations.getPluginLogger().info(playerStat.getName() + " has not enough money to pay " + tax + "$ to the town " + playerTown.getName());
                playerTown.getTreasury().addTaxHistory(playerStat.getName(), playerStat.getUuid(), -1);
            }
        }

        TownsAndNations.getPluginLogger().info(ChatUtils.getTANString() + Lang.DAILY_TAXES_SUCCESS_LOG.get());

    }
    public static void ChunkPayment(){

        float upkeepCost = ConfigUtil.getCustomConfig("config.yml").getInt("ChunkUpkeepCost");

        for(TownData town : TownDataStorage.getTownList().values()){

            int numberClaimedChunk = town.getNumberOfClaimedChunk();
            int totalUpkeep = (int) ( numberClaimedChunk * upkeepCost/10);

            town.removeToBalance(totalUpkeep);
            town.getTreasury().addChunkHistory(numberClaimedChunk,totalUpkeep);
        }
    }

    public static void SalaryPayment(){

        for (TownData town: TownDataStorage.getTownList().values()){
            //Loop through each rank, only paying if everyone of the rank can be paid
            for (TownRank rank : town.getTownRanks().values()){

                int rankSalary = rank.getSalary();
                List<String> playerIdList = rank.getPlayers();
                int costOfSalary = playerIdList.size() * rankSalary;

                if(rankSalary == 0 || costOfSalary > town.getBalance() ){
                    continue;
                }

                town.removeToBalance(costOfSalary);
                for(String playerId : playerIdList){
                    PlayerData player = PlayerDataStorage.get(playerId);
                    player.addToBalance(rankSalary);
                }
            }

        }
    }


}

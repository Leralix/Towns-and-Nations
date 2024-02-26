package org.tan.TownsAndNations.Tasks;


import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.DataClass.RegionData;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.DataClass.TownRank;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.storage.DataStorage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.DataStorage.RegionDataStorage;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;
import org.tan.TownsAndNations.utils.ChatUtils;
import org.tan.TownsAndNations.utils.ConfigUtil;
import org.tan.TownsAndNations.utils.EconomyUtil;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import static org.tan.TownsAndNations.utils.ArchiveUtil.archiveFiles;

public class DailyTasks {

    public static void scheduleMidnightTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Calendar calendar = new GregorianCalendar();
                if (calendar.get(Calendar.HOUR_OF_DAY) == 0 && calendar.get(Calendar.MINUTE) == 0) {
                    TownTaxPayment();
                    RegionTaxPayment();
                    ChunkPayment();
                    SalaryPayment();
                    archiveFiles();
                    ClearOldTaxes();
                }
            }
        }.runTaskTimer(TownsAndNations.getPlugin(), 0L, 1200L); // Exécute toutes les 1200 ticks (1 minute en temps Minecraft)
    }

    public static void RegionTaxPayment() {

        for(RegionData regionData: RegionDataStorage.getAllRegions()){

            for(String townID : regionData.getTownsID()){
                TownData town = TownDataStorage.get(townID);
                if(town == null) continue;
                if(town.getBalance() < regionData.getTaxRate()){
                    regionData.getTaxHistory().add(town.getName(), townID, -1);
                    continue;
                }
                town.removeToBalance(regionData.getTaxRate());
                regionData.addBalance(regionData.getTaxRate());
                regionData.getTaxHistory().add(town.getName(), townID, regionData.getTaxRate());

            }
        }

    }


    public static void TownTaxPayment() {


        for (PlayerData playerStat : PlayerDataStorage.getStats()){
            OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(UUID.fromString(playerStat.getUuid()));

            if (!playerStat.haveTown()) continue;
            TownData playerTown = TownDataStorage.get(playerStat);
            if (!playerTown.getRank(playerStat.getTownRankID()).isPayingTaxes()) continue;
            int tax = playerTown.getFlatTax();

            if(EconomyUtil.getBalance(offlinePlayer) > tax){
                EconomyUtil.removeFromBalance(offlinePlayer,tax);
                playerTown.addToBalance(tax);
                playerTown.getTaxHistory().add(playerStat.getName(), playerStat.getUuid(), tax);
                //TownsAndNations.getPluginLogger().info(playerStat.getName() + " has paid " + tax + "$ to the town " + playerTown.getName());
            }
            else{
                //TownsAndNations.getPluginLogger().info(playerStat.getName() + " has not enough money to pay " + tax + "$ to the town " + playerTown.getName());
                playerTown.getTaxHistory().add(playerStat.getName(), playerStat.getUuid(), -1);
            }
        }

        TownsAndNations.getPluginLogger().info(ChatUtils.getTANString() + Lang.DAILY_TAXES_SUCCESS_LOG.get());

    }
    public static void ChunkPayment(){

        float upkeepCost = ConfigUtil.getCustomConfig("config.yml").getInt("ChunkUpkeepCost");

        for(TownData town : TownDataStorage.getTownMap().values()){

            int numberClaimedChunk = town.getNumberOfClaimedChunk();
            int totalUpkeep = (int) ( numberClaimedChunk * upkeepCost/10);

            town.removeToBalance(totalUpkeep);
            town.getChunkHistory().add(numberClaimedChunk,totalUpkeep);
        }
    }

    public static void SalaryPayment(){

        for (TownData town: TownDataStorage.getTownMap().values()){
            //Loop through each rank, only paying if everyone of the rank can be paid
            for (TownRank rank : town.getTownRanks()){

                int rankSalary = rank.getSalary();
                List<String> playerIdList = rank.getPlayers(town.getID());
                int costOfSalary = playerIdList.size() * rankSalary;

                if(rankSalary == 0 || costOfSalary > town.getBalance() ){
                    continue;
                }

                town.removeToBalance(costOfSalary);
                for(String playerId : playerIdList){
                    PlayerData player = PlayerDataStorage.get(playerId);
                    player.addToBalance(rankSalary);
                    town.getSalaryHistory().add(player.getUuid(), -costOfSalary);

                }
            }

        }
    }

    public static void ClearOldTaxes() {
        int timeBeforeClearing = ConfigUtil.getCustomConfig("config.yml").getInt("TimeBeforeClearingTaxHistory",30);
        int TimeBeforeClearingChunk = ConfigUtil.getCustomConfig("config.yml").getInt("TimeBeforeClearingChunkHistory",30);
        int timeBeforeClearingDonation = ConfigUtil.getCustomConfig("config.yml").getInt("NumberOfDonationBeforeClearing",100);
        int timeBeforeClearingMisc = ConfigUtil.getCustomConfig("config.yml").getInt("NumberOfMiscPurchaseBeforeClearing",100);


        for (TownData town : TownDataStorage.getTownMap().values()) {


            town.getTaxHistory().clearHistory(timeBeforeClearing);
            town.getChunkHistory().clearHistory(TimeBeforeClearingChunk);

            town.getDonationHistory().clearHistory(timeBeforeClearingDonation);
            town.getMiscellaneousHistory().clearHistory(timeBeforeClearingMisc);
        }
    }


}

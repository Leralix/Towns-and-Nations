package org.tan.TownsAndNations.Tasks;


import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;
import org.tan.TownsAndNations.DataClass.*;
import org.tan.TownsAndNations.DataClass.territoryData.RegionData;
import org.tan.TownsAndNations.DataClass.territoryData.TownData;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.storage.DataStorage.LandmarkStorage;
import org.tan.TownsAndNations.storage.DataStorage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.DataStorage.RegionDataStorage;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;
import org.tan.TownsAndNations.utils.ArchiveUtil;
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

                int minute = ConfigUtil.getCustomConfig("config.yml").getInt("taxHourTime",0);
                int hour = ConfigUtil.getCustomConfig("config.yml").getInt("taxMinuteTime",0);

                if (calendar.get(Calendar.HOUR_OF_DAY) == hour && calendar.get(Calendar.MINUTE) == minute) {
                    executeMidnightTasks();
                }
            }
        }.runTaskTimer(TownsAndNations.getPlugin(), 0L, 1200L); // Exécute toutes les 1200 ticks (1 minute en temps Minecraft)
    }

    public static void executeMidnightTasks() {
        TownTaxPayment();
        RegionTaxPayment();
        ChunkPayment();
        SalaryPayment();

        PropertyRent();

        ClearOldTaxes();
        LandmarkStorage.generateAllRessources();
        ArchiveUtil.archiveFiles();

    }

    private static void PropertyRent() {
        for (TownData town : TownDataStorage.getTownMap().values()) {
            for (PropertyData property : town.getPropertyDataList()) {
                if (property.isRented()) {
                    property.payRent();
                }
            }
        }
    }

    public static void RegionTaxPayment() {

        for(RegionData regionData: RegionDataStorage.getAllRegions()){

            for(String townID : regionData.getSubjectsID()){
                TownData town = TownDataStorage.get(townID);
                if(town == null) continue;
                if(town.getBalance() < regionData.getTaxRate()){
                    regionData.getTaxHistory().add(town.getName(), townID, -1);
                    continue;
                }
                town.removeFromBalance(regionData.getTaxRate());
                regionData.addBalance(regionData.getTaxRate());
                regionData.getTaxHistory().add(town.getName(), townID, regionData.getTaxRate());

            }
        }

    }


    public static void TownTaxPayment() {


        for (PlayerData playerStat : PlayerDataStorage.getLists()){
            OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(UUID.fromString(playerStat.getID()));

            if (!playerStat.haveTown()) continue;
            TownData playerTown = TownDataStorage.get(playerStat);
            if (!playerStat.getTownRank().isPayingTaxes()) continue;
            int tax = playerTown.getFlatTax();

            if(EconomyUtil.getBalance(offlinePlayer) > tax){
                EconomyUtil.removeFromBalance(offlinePlayer,tax);
                playerTown.addToBalance(tax);
                playerTown.getTaxHistory().add(playerStat.getName(), playerStat.getID(), tax);
            }
            else{
                playerTown.getTaxHistory().add(playerStat.getName(), playerStat.getID(), -1);
            }
        }
    }
    public static void ChunkPayment(){

        float upkeepCost = ConfigUtil.getCustomConfig("config.yml").getInt("TownChunkUpkeepCost");

        for(TownData town : TownDataStorage.getTownMap().values()){

            int numberClaimedChunk = town.getNumberOfClaimedChunk();
            int totalUpkeep = (int) ( numberClaimedChunk * upkeepCost/10);

            town.removeFromBalance(totalUpkeep);
            town.getChunkHistory().add(numberClaimedChunk,totalUpkeep);
        }
    }
    public static void SalaryPayment(){

        for (TownData town: TownDataStorage.getTownMap().values()){
            //Loop through each rank, only paying if everyone of the rank can be paid
            for (TownRank rank : town.getRanks()){

                int rankSalary = rank.getSalary();
                List<String> playerIdList = rank.getPlayers(town.getID());
                int costOfSalary = playerIdList.size() * rankSalary;

                if(rankSalary == 0 || costOfSalary > town.getBalance() ){
                    continue;
                }

                town.removeFromBalance(costOfSalary);
                for(String playerId : playerIdList){
                    PlayerData player = PlayerDataStorage.get(playerId);
                    player.addToBalance(rankSalary);
                    town.getSalaryHistory().add(player.getID(), -costOfSalary);

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

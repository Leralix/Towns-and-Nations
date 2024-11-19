package org.leralix.tan.tasks;


import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;
import org.leralix.tan.dataclass.*;
import org.leralix.tan.dataclass.newhistory.SalaryTransactionHistory;
import org.leralix.tan.dataclass.territory.ITerritoryData;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.newsletter.NewsletterStorage;
import org.leralix.tan.storage.stored.LandmarkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.ArchiveUtil;
import org.leralix.tan.utils.config.ConfigTag;
import org.leralix.tan.utils.config.ConfigUtil;
import org.leralix.tan.economy.EconomyUtil;

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

                int minute = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("taxHourTime",0);
                int hour = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("taxMinuteTime",0);

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
        NewsletterStorage.clearOldNewsletters();
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
            for(ITerritoryData town : regionData.getVassals()){
                if(town == null) continue;
                if(town.getBalance() < regionData.getTax()){
                    regionData.getTaxHistory().add(town.getName(), town.getID(), -1);
                    continue;
                }
                town.removeFromBalance(regionData.getTax());
                regionData.addToBalance(regionData.getTax());
                regionData.getTaxHistory().add(town.getName(), town.getID(), regionData.getTax());

            }
        }

    }


    public static void TownTaxPayment() {
        for (PlayerData playerStat : PlayerDataStorage.getLists()){
            OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(UUID.fromString(playerStat.getID()));

            if (!playerStat.haveTown()) continue;
            TownData playerTown = TownDataStorage.get(playerStat);
            if (!playerStat.getTownRank().isPayingTaxes()) continue;
            double tax = playerTown.getFlatTax();

            if(EconomyUtil.getBalance(offlinePlayer) > tax){
                EconomyUtil.removeFromBalance(offlinePlayer,tax);
                playerTown.addToBalance(tax);
                playerTown.getTaxHistory().add(playerStat.getName(), playerStat.getID(), tax);
                TownsAndNations.getPlugin().getDatabaseHandler().addTransactionHistory(new SalaryTransactionHistory(playerTown,playerStat,tax));
            }
            else{
                TownsAndNations.getPlugin().getDatabaseHandler().addTransactionHistory(new SalaryTransactionHistory(playerTown,playerStat,-1));
            }
        }
    }
    public static void ChunkPayment(){

        float upkeepCost = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("TownChunkUpkeepCost");

        for(TownData town : TownDataStorage.getTownMap().values()){

            int numberClaimedChunk = town.getNumberOfClaimedChunk();
            int totalUpkeep = (int) ( numberClaimedChunk * upkeepCost/10);
            if (totalUpkeep > town.getBalance()){
                town.removeFromBalance(town.getBalance()); //TODO: Destroy chunk when treasury cannot pay chunks
                continue;
            }
            town.removeFromBalance(totalUpkeep);
            town.getChunkHistory().add(numberClaimedChunk,totalUpkeep);
        }
    }
    public static void SalaryPayment(){

        for (TownData town: TownDataStorage.getTownMap().values()){
            //Loop through each rank, only paying if everyone of the rank can be paid
            for (RankData rank : town.getAllRanks()){
                int rankSalary = rank.getSalary();
                List<String> playerIdList = rank.getPlayersID();
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
        int timeBeforeClearing = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("TimeBeforeClearingTaxHistory",30);
        int TimeBeforeClearingChunk = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("TimeBeforeClearingChunkHistory",30);
        int timeBeforeClearingDonation = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("NumberOfDonationBeforeClearing",100);
        int timeBeforeClearingMisc = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("NumberOfMiscPurchaseBeforeClearing",100);


        for (TownData town : TownDataStorage.getTownMap().values()) {


            town.getTaxHistory().clearHistory(timeBeforeClearing);
            town.getChunkHistory().clearHistory(TimeBeforeClearingChunk);

            town.getDonationHistory().clearHistory(timeBeforeClearingDonation);
            town.getMiscellaneousHistory().clearHistory(timeBeforeClearingMisc);
            town.getSalaryHistory().clearHistory(timeBeforeClearing);
        }
    }
}

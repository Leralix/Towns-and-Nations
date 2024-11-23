package org.leralix.tan.tasks;


import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;
import org.leralix.tan.dataclass.*;
import org.leralix.tan.dataclass.newhistory.*;
import org.leralix.tan.dataclass.territory.ITerritoryData;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.territory.economy.SubjectTaxLine;
import org.leralix.tan.newsletter.NewsletterStorage;
import org.leralix.tan.storage.database.DatabaseHandler;
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
        for(TownData town : TownDataStorage.getTownMap().values()){
            town.executeTasks();
        }

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
                    double tax = regionData.getTax();
                    if(town.getBalance() < tax){
                        TownsAndNations.getPlugin().getDatabaseHandler().addTransactionHistory(new SubjectTaxHistory(regionData,town,-1));
                    }
                    else {
                        town.removeFromBalance(tax);
                        regionData.addToBalance(tax);
                        TownsAndNations.getPlugin().getDatabaseHandler().addTransactionHistory(new SubjectTaxHistory(regionData,town,tax));
                    }
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
                TownsAndNations.getPlugin().getDatabaseHandler().addTransactionHistory(new PlayerTaxHistory(playerTown,playerStat,tax));
            }
            else{
                TownsAndNations.getPlugin().getDatabaseHandler().addTransactionHistory(new PlayerTaxHistory(playerTown,playerStat,-1));
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
            TownsAndNations.getPlugin().getDatabaseHandler().addTransactionHistory(new ChunkPaymentHistory(town,totalUpkeep));
        }
    }
    public static void SalaryPayment(){

        for (TownData town: TownDataStorage.getTownMap().values()){
            //Loop through each rank, only paying if everyone of the rank can be paid
            for (RankData rank : town.getAllRanks()){
                int rankSalary = rank.getSalary();
                List<String> playerIdList = rank.getPlayersID();
                double costOfSalary = playerIdList.size() * rankSalary;

                if(rankSalary == 0 || costOfSalary > town.getBalance() ){
                    continue;
                }
                town.removeFromBalance(costOfSalary);
                for(String playerId : playerIdList){
                    PlayerData playerData = PlayerDataStorage.get(playerId);
                    playerData.addToBalance(rankSalary);
                    TownsAndNations.getPlugin().getDatabaseHandler().addTransactionHistory(new SalaryPaymentHistory(town, String.valueOf(rank.getID()), costOfSalary));
                }
            }

        }
    }

    public static void ClearOldTaxes() {
        int timeBeforeClearingDonation = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("NumberOfDonationBeforeClearing",90);
        int timeBeforeClearingHistory = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("TimeBeforeClearingTaxHistory",90);
        int timeBeforeClearingSalary = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("TimeBeforeClearingSalaryHistory",90);
        int timeBeforeClearingMisc = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("NumberOfMiscPurchaseBeforeClearing",90);
        int TimeBeforeClearingChunk = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("TimeBeforeClearingChunkHistory",90);

        DatabaseHandler databaseHandler = TownsAndNations.getPlugin().getDatabaseHandler();
        databaseHandler.deleteOldHistory(timeBeforeClearingDonation, TransactionHistoryEnum.DONATION);
        databaseHandler.deleteOldHistory(timeBeforeClearingHistory, TransactionHistoryEnum.PLAYER_TAX);
        databaseHandler.deleteOldHistory(timeBeforeClearingHistory, TransactionHistoryEnum.SUBJECT_TAX);
        databaseHandler.deleteOldHistory(timeBeforeClearingSalary, TransactionHistoryEnum.SALARY);
        databaseHandler.deleteOldHistory(timeBeforeClearingMisc, TransactionHistoryEnum.MISCELLANEOUS);
        databaseHandler.deleteOldHistory(TimeBeforeClearingChunk, TransactionHistoryEnum.CHUNK_SPENDING);

    }
}

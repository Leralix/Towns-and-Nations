package org.leralix.tan.tasks;


import org.bukkit.scheduler.BukkitRunnable;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.PropertyData;
import org.leralix.tan.dataclass.newhistory.TransactionHistoryEnum;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.storage.database.DatabaseHandler;
import org.leralix.tan.storage.stored.LandmarkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.newsletter.NewsletterStorage;
import org.leralix.tan.utils.ArchiveUtil;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class DailyTasks {
    private DailyTasks() {
        throw new IllegalStateException("Utility class");
    }

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
        propertyRent();

        for(TownData town : TownDataStorage.getTownMap().values()){
            town.executeTasks();
        }
        for(RegionData regionData : RegionDataStorage.getAll()){
            regionData.executeTasks();
        }

        clearOldTaxes();
        updatePlayerUsernames();

        NewsletterStorage.clearOldNewsletters();
        LandmarkStorage.generateAllRessources();
        ArchiveUtil.archiveFiles();
    }

    private static void updatePlayerUsernames() {
        for(PlayerData player : PlayerDataStorage.getInstance().getAll()){
            player.clearName();
        }
    }

    private static void propertyRent() {
        for (TownData town : TownDataStorage.getTownMap().values()) {
            for (PropertyData property : town.getPropertyDataList()) {
                if (property.isRented()) {
                    property.payRent();
                }
            }
        }
    }


    public static void clearOldTaxes() {
        int timeBeforeClearingDonation = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("NumberOfDonationBeforeClearing",90);
        int timeBeforeClearingHistory = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("TimeBeforeClearingTaxHistory",90);
        int timeBeforeClearingSalary = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("TimeBeforeClearingSalaryHistory",90);
        int timeBeforeClearingMisc = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("NumberOfMiscPurchaseBeforeClearing",90);
        int timeBeforeClearingChunk = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("TimeBeforeClearingChunkHistory",90);

        DatabaseHandler databaseHandler = TownsAndNations.getPlugin().getDatabaseHandler();
        databaseHandler.deleteOldHistory(timeBeforeClearingDonation, TransactionHistoryEnum.DONATION);
        databaseHandler.deleteOldHistory(timeBeforeClearingHistory, TransactionHistoryEnum.PLAYER_TAX);
        databaseHandler.deleteOldHistory(timeBeforeClearingHistory, TransactionHistoryEnum.SUBJECT_TAX);
        databaseHandler.deleteOldHistory(timeBeforeClearingHistory, TransactionHistoryEnum.PROPERTY_RENT_TAX);
        databaseHandler.deleteOldHistory(timeBeforeClearingHistory, TransactionHistoryEnum.PROPERTY_BUY_TAX);
        databaseHandler.deleteOldHistory(timeBeforeClearingSalary, TransactionHistoryEnum.SALARY);
        databaseHandler.deleteOldHistory(timeBeforeClearingMisc, TransactionHistoryEnum.MISCELLANEOUS);
        databaseHandler.deleteOldHistory(timeBeforeClearingChunk, TransactionHistoryEnum.CHUNK_SPENDING);

    }
}

package org.leralix.tan.tasks;


import org.bukkit.scheduler.BukkitRunnable;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.PropertyData;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.events.newsletter.NewsletterStorage;
import org.leralix.tan.storage.stored.LandmarkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.file.ArchiveUtil;

import java.util.Calendar;
import java.util.GregorianCalendar;
import org.leralix.tan.storage.database.transactions.TransactionManager;
import org.leralix.tan.utils.constants.Constants;

public class DailyTasks {

    private final Calendar calendar;

    private final int hourTime;
    private final int minuteTime;

    public DailyTasks(int hourTime, int minuteTime) {
        this.hourTime = hourTime;
        this.minuteTime = minuteTime;
        this.calendar = new GregorianCalendar();
    }

    public void scheduleMidnightTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (calendar.get(Calendar.HOUR_OF_DAY) == hourTime && calendar.get(Calendar.MINUTE) == minuteTime) {
                    executeMidnightTasks();
                }
            }
        }.runTaskTimer(TownsAndNations.getPlugin(), 0L, 1200L); // Execute every 1200 ticks (1 minute)
    }

    public static void executeMidnightTasks() {
        propertyRent();

        for(TownData town : TownDataStorage.getInstance().getAll().values()){
            town.executeTasks();
        }
        for(RegionData regionData : RegionDataStorage.getInstance().getAll().values()){
            regionData.executeTasks();
        }

        clearOldTransaction();
        updatePlayerUsernames();

        NewsletterStorage.getInstance().clearOldNewsletters();
        if (ConfigUtil.getCustomConfig(ConfigTag.MAIN).getBoolean("enableMidnightGenerateResource", true)) {
          LandmarkStorage.getInstance().generateAllResources();
        }
        ArchiveUtil.archiveFiles();
    }

    private static void updatePlayerUsernames() {
        for(ITanPlayer player : PlayerDataStorage.getInstance().getAll().values()){
            player.clearName();
        }
    }

    private static void propertyRent() {
        for (TownData town : TownDataStorage.getInstance().getAll().values()) {
            for (PropertyData property : town.getProperties()) {
                if (property.isRented()) {
                    property.payRent();
                }
            }
        }
    }


    public static void clearOldTransaction() {
        TransactionManager.getInstance().deleteOldTransactions(Constants.getNbDaysBeforeTransactionDeletion());
    }
}

package org.leralix.tan.tasks;


import org.bukkit.scheduler.BukkitRunnable;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.building.property.PropertyData;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.RegionData;
import org.leralix.tan.data.territory.TownData;
import org.leralix.tan.events.newsletter.NewsletterStorage;
import org.leralix.tan.storage.database.transactions.TransactionManager;
import org.leralix.tan.storage.stored.*;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.file.ArchiveUtil;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class DailyTasks {

    private final PlayerDataStorage playerDataStorage;
    private final int hourTime;
    private final int minuteTime;

    public DailyTasks(PlayerDataStorage playerDataStorage, int hourTime, int minuteTime) {
        this.playerDataStorage = playerDataStorage;
        this.hourTime = hourTime;
        this.minuteTime = minuteTime;
    }

    public void scheduleMidnightTask() {
        new BukkitRunnable() {
            @Override
            public void run() {

                Calendar calendar = new GregorianCalendar();

                if (calendar.get(Calendar.HOUR_OF_DAY) == hourTime && calendar.get(Calendar.MINUTE) == minuteTime) {
                    executeMidnightTasks();
                }
            }
        }.runTaskTimer(TownsAndNations.getPlugin(), 0L, 1200L); // Execute every 1200 ticks (1 minute)
    }

    public void executeMidnightTasks() {
        propertyRent();

        for(TownData town : TownDataStorage.getInstance().getAll().values()){
            town.executeTasks();
        }
        for(RegionData regionData : RegionDataStorage.getInstance().getAll().values()){
            regionData.executeTasks();
        }

        TributePlayerDailyStorage.getInstance().resetDaily();
        TributeVassalDailyStorage.getInstance().resetDaily();

        clearOldTransaction();
        updatePlayerUsernames();

        NewsletterStorage.getInstance().clearOldNewsletters();
        if (ConfigUtil.getCustomConfig(ConfigTag.MAIN).getBoolean("enableMidnightGenerateResource", true)) {
          LandmarkStorage.getInstance().generateAllResources();
        }
        ArchiveUtil.archiveFiles();
    }

    private void updatePlayerUsernames() {
        for(ITanPlayer player : playerDataStorage.getAll().values()){
            player.clearName();
        }
    }

    private void propertyRent() {
        for (TownData town : TownDataStorage.getInstance().getAll().values()) {
            for (PropertyData property : town.getPropertiesInternal()) {
                if (property.isRented()) {
                    property.payRent();
                }
            }
        }
    }


    public static void clearOldTransaction() {
        TransactionManager.getInstance().deleteOldTransactions(Constants.getNbDaysBeforeClearningTransactions());
    }
}

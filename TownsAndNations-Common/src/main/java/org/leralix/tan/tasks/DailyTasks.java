package org.leralix.tan.tasks;


import org.bukkit.scheduler.BukkitRunnable;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.building.property.PropertyData;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.Region;
import org.leralix.tan.data.territory.Town;
import org.leralix.tan.events.newsletter.NewsletterStorage;
import org.leralix.tan.storage.database.transactions.TransactionManager;
import org.leralix.tan.storage.stored.PlayerDataStorage;
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

        for(Town town : TownsAndNations.getPlugin().getTownStorage().getAll().values()){
            town.executeTasks();
        }
        for(Region regionData : TownsAndNations.getPlugin().getRegionStorage().getAll().values()){
            regionData.executeTasks();
        }

        clearOldTransaction();
        updatePlayerUsernames();

        NewsletterStorage.getInstance().clearOldNewsletters();
        TownsAndNations.getPlugin().getLandmarkStorage().generateAllResources();
        ArchiveUtil.archiveFiles();
    }

    private void updatePlayerUsernames() {
        for(ITanPlayer player : playerDataStorage.getAllPlayers()){
            player.clearName();
        }
    }

    private void propertyRent() {
        for (Town town : TownsAndNations.getPlugin().getTownStorage().getAll().values()) {
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

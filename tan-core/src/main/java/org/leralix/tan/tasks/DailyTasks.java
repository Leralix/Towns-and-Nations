package org.leralix.tan.tasks;

import java.util.Calendar;
import java.util.GregorianCalendar;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.PropertyData;
import org.leralix.tan.dataclass.newhistory.TransactionHistoryEnum;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.events.newsletter.NewsletterStorage;
import org.leralix.tan.storage.database.DatabaseHandler;
import org.leralix.tan.storage.stored.LandmarkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.FoliaScheduler;
import org.leralix.tan.utils.file.ArchiveUtil;

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
    FoliaScheduler.runTaskTimer(
        TownsAndNations.getPlugin(),
        () -> {
          if (calendar.get(Calendar.HOUR_OF_DAY) == hourTime
              && calendar.get(Calendar.MINUTE) == minuteTime) {
            executeMidnightTasks();
          }
        },
        1L,
        1200L);
  }

  public static void executeMidnightTasks() {
    FoliaScheduler.runTaskAsynchronously(
        TownsAndNations.getPlugin(),
        () -> {
          propertyRent();

          TownDataStorage.getInstance()
              .processBatches(
                  100,
                  batch -> {
                    for (TownData town : batch.values()) {
                      town.executeTasks();
                    }
                  })
              .join();

          RegionDataStorage.getInstance()
              .processBatches(
                  100,
                  batch -> {
                    for (RegionData regionData : batch.values()) {
                      regionData.executeTasks();
                    }
                  })
              .join();

          clearOldTaxes();
          updatePlayerUsernames();

          NewsletterStorage.getInstance().clearOldNewsletters();
          if (ConfigUtil.getCustomConfig(ConfigTag.MAIN)
              .getBoolean("enableMidnightGenerateResource", true)) {
            LandmarkStorage.getInstance().generateAllResources();
          }
          ArchiveUtil.archiveFiles();

          TownsAndNations.getPlugin().getLogger().info("Daily tasks completed successfully");
        });
  }

  private static void updatePlayerUsernames() {
    PlayerDataStorage.getInstance()
        .processBatches(
            200,
            batch -> {
              for (ITanPlayer player : batch.values()) {
                player.clearName();
              }
            })
        .join();
  }

  private static void propertyRent() {
    TownDataStorage.getInstance()
        .processBatches(
            100,
            batch -> {
              for (TownData town : batch.values()) {
                for (PropertyData property : town.getProperties()) {
                  if (property.isRented()) {
                    property.payRent();
                  }
                }
              }
            })
        .join();
  }

  public static void clearOldTaxes() {
    int timeBeforeClearingDonation =
        ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("NumberOfDonationBeforeClearing", 90);
    int timeBeforeClearingHistory =
        ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("TimeBeforeClearingTaxHistory", 90);
    int timeBeforeClearingSalary =
        ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("TimeBeforeClearingSalaryHistory", 90);
    int timeBeforeClearingMisc =
        ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("NumberOfMiscPurchaseBeforeClearing", 90);
    int timeBeforeClearingChunk =
        ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("TimeBeforeClearingChunkHistory", 90);

    DatabaseHandler databaseHandler = TownsAndNations.getPlugin().getDatabaseHandler();
    databaseHandler.deleteOldHistory(timeBeforeClearingDonation, TransactionHistoryEnum.DONATION);
    databaseHandler.deleteOldHistory(timeBeforeClearingHistory, TransactionHistoryEnum.PLAYER_TAX);
    databaseHandler.deleteOldHistory(timeBeforeClearingHistory, TransactionHistoryEnum.SUBJECT_TAX);
    databaseHandler.deleteOldHistory(
        timeBeforeClearingHistory, TransactionHistoryEnum.PROPERTY_RENT_TAX);
    databaseHandler.deleteOldHistory(
        timeBeforeClearingHistory, TransactionHistoryEnum.PROPERTY_BUY_TAX);
    databaseHandler.deleteOldHistory(timeBeforeClearingSalary, TransactionHistoryEnum.SALARY);
    databaseHandler.deleteOldHistory(timeBeforeClearingMisc, TransactionHistoryEnum.MISCELLANEOUS);
    databaseHandler.deleteOldHistory(
        timeBeforeClearingChunk, TransactionHistoryEnum.CHUNK_SPENDING);
  }
}

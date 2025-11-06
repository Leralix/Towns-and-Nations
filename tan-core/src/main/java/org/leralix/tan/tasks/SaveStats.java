package org.leralix.tan.tasks;

import org.leralix.tan.TownsAndNations;

public class SaveStats {

  private SaveStats() {
    throw new IllegalStateException("Utility class");
  }

  public static void startSchedule() {
    // Note: With DatabaseStorage, auto-save is no longer needed
    // Each put() operation writes directly to the database
    // This schedule is kept for compatibility but does nothing
    TownsAndNations.getPlugin()
        .getLogger()
        .info(
            "SaveStats: Using DatabaseStorage - auto-save on every operation, no periodic save needed");
  }

  public static void saveAll() {
    // Note: With DatabaseStorage, this method is obsolete
    // All data is already persisted in the database on every put() call
    // No action needed - keeping method for backward compatibility
    TownsAndNations.getPlugin()
        .getLogger()
        .fine(
            "SaveStats.saveAll() called but not needed with DatabaseStorage (auto-saves on every operation)");
  }
}

package org.leralix.tan.tasks;

import org.bukkit.scheduler.BukkitRunnable;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.storage.stored.ClaimStorage;
import org.leralix.tan.storage.stored.FortStorage;
import org.leralix.tan.storage.stored.WarStorage;

public class StartOfServerTask {

    public static void registerTasks(FortStorage fortStorage, ClaimStorage claimStorage, WarStorage warStorage){
        new BukkitRunnable() {
            @Override
            public void run() {
                fortStorage.checkValidWorlds();
                claimStorage.checkValidWorlds();
                warStorage.updateAttacks();

            }
        }.runTask(TownsAndNations.getPlugin());
    }




}

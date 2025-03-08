package org.leralix.tan.dataclass.wars;

import org.bukkit.Bukkit;
import org.leralix.tan.dataclass.territory.StrongholdData;
import org.leralix.tan.TownsAndNations;

public class StrongholdListener {
    private final CurrentAttack currentAttack;
    private final StrongholdData strongholdData;
    int taskID;



    public StrongholdListener(CurrentAttack currentAttack, StrongholdData strongholdData) {
        this.currentAttack = currentAttack;
        this.strongholdData = strongholdData;
        this.taskID = Bukkit.getScheduler().runTaskTimer(TownsAndNations.getPlugin(), this::scanStronghold, 0L, 200L).getTaskId();
    }

    public void scanStronghold(){
        strongholdData.updateControl(currentAttack);

        currentAttack.updateControl();
        strongholdData.broadcastControl();
        currentAttack.addScoreOfStronghold();

    }

    public void stop(){
        Bukkit.getScheduler().cancelTask(taskID);
    }


}

package org.leralix.tan.war.attack;

import org.leralix.tan.war.PlannedAttack;

public class InfiniteCurrentAttack extends CurrentAttack{

    /**
     * Constructor of CurrentAttack
     *
     * @param plannedAttack the planned attack data
     */
    public InfiniteCurrentAttack(PlannedAttack plannedAttack) {
        super(plannedAttack);
    }

    @Override
    protected void updateBossBar() {
        bossBar.setTitle(attackData.getWar().getName());
        bossBar.setProgress(1.0);
    }

    @Override
    protected boolean shouldContinue() {
        return true;
    }
}

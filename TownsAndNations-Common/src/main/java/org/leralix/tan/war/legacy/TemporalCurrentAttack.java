package org.leralix.tan.war.legacy;

import org.leralix.tan.war.PlannedAttack;

public class TemporalCurrentAttack extends CurrentAttack {


    /**
     * total time, in tick
     */
    private final long totalTime;
    /**
     * Remaining time, in tick
     */
    private long remaining;

    /**
     * Constructor of CurrentAttack
     *
     * @param plannedAttack the planned attack data
     * @param endTime       the end time in epoch milliseconds
     */
    public TemporalCurrentAttack(PlannedAttack plannedAttack, long endTime) {
        super(plannedAttack);

        //Conversion from ms to ticks
        this.totalTime = (endTime - System.currentTimeMillis()) / 50;
        this.remaining = totalTime;
    }

    @Override
    protected void updateBossBar() {
        long hours = remaining / 72000;
        long minutes = (remaining % 72000) / 1200;
        long seconds = (remaining % 1200) / 20;
        String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);

        bossBar.setTitle(timeString);
        bossBar.setProgress((double) (totalTime - remaining) / totalTime);
    }

    @Override
    protected boolean shouldContinue()  {
        remaining--;
        return remaining >= 0;
    }
}

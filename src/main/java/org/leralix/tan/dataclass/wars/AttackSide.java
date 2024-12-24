package org.leralix.tan.dataclass.wars;

import org.leralix.tan.lang.Lang;

public enum AttackSide {
    ATTACKER(Lang.BOSS_BAR_STRONGHOLD_HELD_BY_ATTACKERS.get()),
    DEFENDER(Lang.BOSS_BAR_STRONGHOLD_HELD_BY_DEFENDERS.get()),
    CONTESTED(Lang.BOSS_BAR_STRONGHOLD_CONTESTED.get());

    private final String bossBarMessage;

    AttackSide(String bossBarMessage){
        this.bossBarMessage = bossBarMessage;
    }

    public String getBossBarMessage() {
        return bossBarMessage;
    }
}

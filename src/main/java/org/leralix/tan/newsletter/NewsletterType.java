package org.leralix.tan.newsletter;

import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;

public enum NewsletterType {

    TOWN_CREATION,
    TOWN_DELETION,
    PLAYER_APPLICATION,
    PLAYER_JOIN_TOWN,
    REGION_CREATION,
    REGION_DISBAND,
    TOWN_JOIN_REGION_PROPOSAL,
    TOWN_JOIN_REGION_ACCEPTED,
    TOWN_LEAVE_REGION,
    DIPLOMACY_PROPOSAL,
    DIPLOMACY_ACCEPT,
    ATTACK_DECLARED;


    private EventScope newsletter;
    private EventScope broadcast;


    public static void init(){
        for (NewsletterType type : values()) {
            type.newsletter = EventScope.valueOf(ConfigUtil.getCustomConfig(ConfigTag.MAIN).getString("events." +  type.name() + ".BROADCAST", "NONE"));
            type.broadcast =  EventScope.valueOf(ConfigUtil.getCustomConfig(ConfigTag.MAIN).getString("events." +  type.name() + ".NEWSLETTER", "NONE"));
        }
    }

    public EventScope getNewsletterScope() {
        return newsletter;
    }

    public EventScope getBroadcastGlobal() {
        return broadcast;
    }
}

package org.leralix.tan.newsletter;

import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;

public enum NewsletterType {

    TOWN_CREATED,
    TOWN_DELETED,
    PLAYER_APPLICATION,
    PLAYER_JOIN_TOWN,
    REGION_CREATED,
    REGION_DELETED,

    TERRITORY_VASSAL_PROPOSAL,
    TERRITORY_VASSAL_ACCEPTED,
    TERRITORY_VASSAL_FORCED,
    TERRITORY_NOW_INDEPENDENT,

    DIPLOMACY_PROPOSAL,
    DIPLOMACY_ACCEPTED,

    ATTACK_DECLARED,
    ATTACK_CANCELLED,
    ATTACK_ENDED;


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

package org.leralix.tan.events.newsletter;

public enum NewsletterType {

    TOWN_CREATED("player_create_town_newsletter"),
    TOWN_DELETED("player_delete_town_newsletter"),
    PLAYER_APPLICATION("player_application_newsletter"),
    PLAYER_JOIN_TOWN("player_join_town_newsletter"),
    REGION_CREATED("player_create_region_newsletter"),
    REGION_DELETED("player_delete_region_newsletter"),
    NATION_CREATED("player_create_nation_newsletter"),
    NATION_DELETED("player_delete_nation_newsletter"),
    TERRITORY_VASSAL_PROPOSAL("territory_vassal_proposal_newsletter"),
    TERRITORY_VASSAL_ACCEPTED("territory_vassal_accepted_newsletter"),
    TERRITORY_VASSAL_FORCED("territory_vassal_forced_newsletter"),
    TERRITORY_VASSAL_INDEPENDENT("territory_vassal_independent_newsletter"),
    DIPLOMACY_PROPOSAL("diplomacy_proposal_newsletter"),
    DIPLOMACY_ACCEPTED("diplomacy_accepted_newsletter"),
    LANDMARK_CLAIMED("landmark_claimed_newsletter"),
    LANDMARK_UNCLAIMED("landmark_unclaimed_newsletter"),
    WAR_DECLARED("war_declared_newsletter"),
    ATTACK_DECLARED("attack_declared_newsletter"),
    ATTACK_CANCELLED("attack_cancelled_newsletter"),
    ATTACK_ENDED("attack_ended_newsletter"),
    WAR_ENDED("war_ended_newsletter");

    private final String databaseName;

    NewsletterType(String databaseName){
        this.databaseName = databaseName;
    }

    public static boolean isValidEnumValue(String value) {
        for (NewsletterType type : NewsletterType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }

    public String getDatabaseName() {
        return databaseName;
    }
}

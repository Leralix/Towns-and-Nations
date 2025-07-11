package org.leralix.tan.events.newsletter;

import org.leralix.tan.events.newsletter.news.*;
import org.tan.api.events.*;

public class NewsletterEvents implements TanListener {

    private final NewsletterStorage newsletterStorage;

    public NewsletterEvents() {
        this.newsletterStorage = NewsletterStorage.getInstance();
    }

    @EventHandler
    public void onAttackDeclared(AttackDeclaredEvent event) {
        newsletterStorage.register(new AttackDeclaredNewsletter(event.getAttackerTerritory(), event.getDefenderTerritory()));
    }

    @EventHandler
    public void onAttackWonByAttacker(AttackWonByAttackerEvent event) {
        newsletterStorage.register(new AttackWonByAttackerNewsletter(event.getAttackerTerritory(), event.getDefenderTerritory()));
    }

    @EventHandler
    public void onAttackWonByDefender(AttackWonByAttackerEvent event) {
        newsletterStorage.register(new AttackWonByDefenderNewsletter(event.getAttackerTerritory(), event.getDefenderTerritory()));
    }

    @EventHandler
    public void onDefenderAcceptDemands(AttackCancelledByDefenderEvent event) {
        newsletterStorage.register(new AttackCancelledByDefenderNewsletter(event.getDefenderTerritory(), event.getAttackerTerritory()));
    }

    @EventHandler
    public void onDiplomacyProposalAccepted(DiplomacyProposalAcceptedEvent event) {

    }

    @EventHandler
    public void onDiplomacyProposal(DiplomacyProposalEvent event) {

    }

    @EventHandler
    public void onPlayerJoinTown(PlayerJoinTownEvent event) {
        newsletterStorage.register(new PlayerJoinTownNews(event.getPlayer(), event.getTown()));
    }

    @EventHandler
    public void onPlayerRequestJoinTown(PlayerJoinRequestEvent event){
        newsletterStorage.register(new PlayerJoinRequestNews(event.getPlayer(), event.getTown()));
    }

    @EventHandler
    public void onRegionCreated(RegionCreatedEvent event) {
        newsletterStorage.register(new RegionCreationNews(event.getRegion(), event.getExecutor()));
    }

    @EventHandler
    public void onRegionDeleted(RegionDeletedEvent event) {
        newsletterStorage.register(new RegionDeletedNews(event.getRegion(), event.getExecutor()));
    }

    @EventHandler
    public void onTerritoryIndependence(TerritoryIndependenceEvent event) {
        newsletterStorage.register(new TerritoryIndependentNews(event.getTerritory(), event.getFormerOverlord()));
    }

    @EventHandler
    public void  onTerritoryVassalAccepted(TerritoryVassalAcceptedEvent event) {
        newsletterStorage.register(new TerritoryVassalAcceptedNews(event.getNewOverlord(), event.getTerritory()));
    }

    @EventHandler
    public void onTerritoryVassalForced(TerritoryVassalForcedEvent event) {
        newsletterStorage.register(new TerritoryVassalForcedNews(event.getNewOverlord(), event.getTerritory()));
    }

    public void onTerritoryVassalProposal(TerritoryVassalProposalEvent event) {
        newsletterStorage.register(new TerritoryVassalProposalNews(event.getPotentialOverlord(), event.getTerritory()));
    }

    @EventHandler
    public void onTownCreated(TownCreatedEvent event) {
        newsletterStorage.register(new TownCreatedNews(event.getTown(), event.getExecutor()));
    }

    @EventHandler
    public void onTownDeleted(TownDeletedEvent event) {
        newsletterStorage.register(new TownDeletedNews(event.getTown(), event.getExecutor()));
    }
}
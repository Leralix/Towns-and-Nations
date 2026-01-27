package org.leralix.tan.events.events;

import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.events.InternalEvent;
import org.tan.api.enums.EDiplomacyState;
import org.tan.api.events.DiplomacyProposalEvent;
import org.tan.api.interfaces.TanTerritory;

public class DiplomacyProposalInternalEvent extends InternalEvent implements DiplomacyProposalEvent {


    private final TanTerritory proposedTerritory;
    private final TanTerritory acceptingTerritory;

    private final TownRelation proposedRelation;

    public DiplomacyProposalInternalEvent(
            TanTerritory proposedTerritory,
            TanTerritory acceptingTerritory,
            TownRelation proposedRelation) {
        this.proposedTerritory = proposedTerritory;
        this.acceptingTerritory = acceptingTerritory;
        this.proposedRelation = proposedRelation;
    }

    @Override
    public TanTerritory getProposingTerritory() {
        return proposedTerritory;
    }

    @Override
    public TanTerritory getReceivingTerritory() {
        return acceptingTerritory;
    }

    @Override
    public EDiplomacyState getProposedDiplomacy() {
        return proposedRelation.toAPI();
    }
}

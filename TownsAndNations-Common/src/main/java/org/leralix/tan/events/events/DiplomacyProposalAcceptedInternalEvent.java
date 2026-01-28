package org.leralix.tan.events.events;

import org.leralix.tan.data.territory.relation.TownRelation;
import org.leralix.tan.events.InternalEvent;
import org.tan.api.enums.EDiplomacyState;
import org.tan.api.events.DiplomacyProposalAcceptedEvent;
import org.tan.api.interfaces.TanTerritory;

public class DiplomacyProposalAcceptedInternalEvent extends InternalEvent implements DiplomacyProposalAcceptedEvent {


    private final TanTerritory proposedTerritory;
    private final TanTerritory acceptingTerritory;

    private final TownRelation oldRelation;
    private final TownRelation newRelation;

    public DiplomacyProposalAcceptedInternalEvent(
            TanTerritory proposedTerritory,
            TanTerritory acceptingTerritory,
            TownRelation oldRelation,
            TownRelation newRelation
    ) {
        this.proposedTerritory = proposedTerritory;
        this.acceptingTerritory = acceptingTerritory;
        this.oldRelation = oldRelation;
        this.newRelation = newRelation;
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
    public EDiplomacyState getNewDiplomacy() {
        return newRelation.toAPI();
    }

    @Override
    public EDiplomacyState getOldDiplomacy() {
        return oldRelation.toAPI();
    }

    @Override
    public boolean isSuperior() {
        return newRelation.isSuperiorTo(oldRelation);
    }


}

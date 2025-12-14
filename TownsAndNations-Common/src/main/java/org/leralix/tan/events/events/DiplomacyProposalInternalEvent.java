package org.leralix.tan.events.events;

import org.leralix.tan.api.internal.wrappers.TerritoryDataWrapper;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.events.InternalEvent;
import org.tan.api.enums.EDiplomacyState;
import org.tan.api.events.DiplomacyProposalEvent;
import org.tan.api.interfaces.TanTerritory;

public class DiplomacyProposalInternalEvent extends InternalEvent implements DiplomacyProposalEvent {


    private final TerritoryData proposedTerritory;
    private final TerritoryData acceptingTerritory;

    private final TownRelation proposedRelation;

    public DiplomacyProposalInternalEvent(TerritoryData proposedTerritory, TerritoryData acceptingTerritory,
                                          TownRelation proposedRelation) {
        this.proposedTerritory = proposedTerritory;
        this.acceptingTerritory = acceptingTerritory;
        this.proposedRelation = proposedRelation;
    }

    @Override
    public TanTerritory getProposingTerritory() {
        return TerritoryDataWrapper.of(proposedTerritory);
    }

    @Override
    public TanTerritory getReceivingTerritory() {
        return TerritoryDataWrapper.of(acceptingTerritory);
    }

    @Override
    public EDiplomacyState getProposedDiplomacy() {
        return proposedRelation.toAPI();
    }
}

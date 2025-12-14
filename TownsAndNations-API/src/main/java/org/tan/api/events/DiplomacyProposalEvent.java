package org.tan.api.events;

import org.tan.api.enums.EDiplomacyState;
import org.tan.api.interfaces.TanTerritory;

public interface DiplomacyProposalEvent extends TanEvent {


    TanTerritory getProposingTerritory();

    TanTerritory getReceivingTerritory();

    EDiplomacyState getProposedDiplomacy();
}

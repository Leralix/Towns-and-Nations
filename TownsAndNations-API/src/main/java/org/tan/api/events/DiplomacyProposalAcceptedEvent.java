package org.tan.api.events;

import org.tan.api.enums.EDiplomacyState;
import org.tan.api.interfaces.TanTerritory;

public interface DiplomacyProposalAcceptedEvent extends TanEvent {


    TanTerritory getProposingTerritory();

    TanTerritory getReceivingTerritory();

    EDiplomacyState getNewDiplomacy();

    EDiplomacyState getOldDiplomacy();

    boolean isSuperior();

}

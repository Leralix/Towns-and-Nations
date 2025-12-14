package org.leralix.tan.war.info;

import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.timezone.TimeZoneEnum;
import org.tan.api.interfaces.war.TanAttackStatus;

import java.util.List;

public abstract class AttackResult implements TanAttackStatus {

    /**
     * Used for deserialisation
     */
    private final String type = getClass().getSimpleName();

    public abstract List<FilledLang> getResultLines(LangType langType, TimeZoneEnum timeZone);


}

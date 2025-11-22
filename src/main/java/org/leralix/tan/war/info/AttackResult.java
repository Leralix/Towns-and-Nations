package org.leralix.tan.war.info;

import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.timezone.TimeZoneEnum;

import java.util.List;

public abstract class AttackResult {

    /**
     * Used for deserialisation
     */
    private final String type = getClass().getSimpleName();

    public abstract List<FilledLang> getResultLines(LangType langType, TimeZoneEnum timeZone);


}

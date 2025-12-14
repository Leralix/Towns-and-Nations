package org.leralix.tan.war.info;

import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.timezone.TimeZoneEnum;
import org.leralix.tan.timezone.TimeZoneManager;

import java.time.Instant;
import java.util.List;

public class AttackResultCancelled extends AttackResult {

    /**
     * The date time of when the attack has been cancelled, in epoch milliseconds
     */
    private final long cancelledDateTime;

    public AttackResultCancelled(){
        this.cancelledDateTime = System.currentTimeMillis();
    }

    @Override
    public List<FilledLang> getResultLines(LangType langType, TimeZoneEnum timeZone) {

        FilledLang exactTimeStart = TimeZoneManager.getInstance().formatDate(Instant.ofEpochMilli(cancelledDateTime), timeZone, langType.getLocale());

        return List.of(
                Lang.ATTACK_ICON_CANCELLED.get(),
                Lang.ATTACK_ICON_CANCELLED_DATE.get(exactTimeStart.get(langType))
        );
    }
}

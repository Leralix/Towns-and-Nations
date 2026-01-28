package org.leralix.tan.war.info;

import org.leralix.tan.data.timezone.TimeZoneEnum;
import org.leralix.tan.data.timezone.TimeZoneManager;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.text.DateUtil;

import java.time.Instant;
import java.util.List;

public class AttackNotYetStarted extends AttackResult {

    /**
     * The start date in epoch milliseconds
     */
    private final long startDateTime;


    public AttackNotYetStarted(long startDateTime) {
        this.startDateTime = startDateTime;
    }

    @Override
    public List<FilledLang> getResultLines(LangType langType, TimeZoneEnum timeZone) {

        FilledLang exactTimeStart = TimeZoneManager.getInstance().formatDate(Instant.ofEpochMilli(startDateTime), timeZone, langType.getLocale());

        long dateOfStart = startDateTime - System.currentTimeMillis();

        return List.of(
                Lang.ATTACK_ICON_NOT_YET_STARTED.get(),
                Lang.ATTACK_ICON_NOT_YET_STARTED_START_DATE.get(DateUtil.getDateStringFromMilli(dateOfStart), exactTimeStart.get(langType)),
                Lang.ATTACK_ICON_NOT_YET_STARTED_DURATION_DATE.get(DateUtil.getDateStringFromMinutes(Constants.getAttackDuration()))
        );
    }
}

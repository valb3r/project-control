package com.valb3r.projectcontrol.service.analyze;

import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.Locale;

@UtilityClass
public class DateUtil {

    public Instant weekStart(Instant ofTime) {
        if (null == ofTime) {
            return null;
        }

        return ofTime.truncatedTo(ChronoUnit.DAYS).with(WeekFields.of(Locale.UK).dayOfWeek(), 1);
    }

    public Instant weekEnd(Instant ofTime) {
        if (null == ofTime) {
            return null;
        }

        return ofTime.truncatedTo(ChronoUnit.DAYS).with(WeekFields.of(Locale.UK).dayOfWeek(), 7);
    }
}

package com.valb3r.projectcontrol.service.analyze;

import lombok.experimental.UtilityClass;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneOffset;

@UtilityClass
public class DateUtil {

    // marking static for non-star imports
    public static Instant weekStart(Instant ofTime) {
        if (null == ofTime) {
            return null;
        }

        return ofTime.atZone(ZoneOffset.UTC)
                .toLocalDate()
                .with(DayOfWeek.MONDAY)
                .atStartOfDay()
                .toInstant(ZoneOffset.UTC);
    }

    // marking static for non-star imports
    public static Instant weekEnd(Instant ofTime) {
        if (null == ofTime) {
            return null;
        }

        return ofTime.atZone(ZoneOffset.UTC)
                .toLocalDate()
                .with(DayOfWeek.SUNDAY)
                .atTime(23, 59, 59)
                .toInstant(ZoneOffset.UTC);
    }
}

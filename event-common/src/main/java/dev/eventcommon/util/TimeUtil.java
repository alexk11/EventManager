package dev.eventcommon.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


@UtilityClass
public class TimeUtil {

    public static LocalDateTime getNow() {
        return LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }
}

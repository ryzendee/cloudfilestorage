package com.app.cloudfilestorage.unit.utils;

import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static com.app.cloudfilestorage.utils.DateFormatterUtil.formatZonedDateTime;
import static org.assertj.core.api.Assertions.assertThat;

public class DateFormatterUtilTest {

    @Test
    void formatZonedDateTime_withCurrentTime_returnsFormattedTime() {
        ZonedDateTime zonedDateTime = ZonedDateTime.of(2024, 7, 18, 10, 30, 0, 0, ZoneId.of("UTC"));

        String formattedDate = formatZonedDateTime(zonedDateTime);

        assertThat(formattedDate).isEqualTo("18-07-2024 10:30");
    }
}

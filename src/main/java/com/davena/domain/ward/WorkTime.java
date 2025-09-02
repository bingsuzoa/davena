package com.davena.domain.ward;

import java.time.LocalTime;

public record WorkTime(
        LocalTime start,
        LocalTime end
) {
}

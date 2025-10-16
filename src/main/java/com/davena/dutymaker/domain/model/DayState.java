package com.davena.dutymaker.domain.model;

import java.util.List;

public record DayState(
        List<TeamState> teamStates
) {
}

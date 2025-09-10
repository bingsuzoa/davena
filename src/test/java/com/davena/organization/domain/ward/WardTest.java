package com.davena.organization.domain.ward;

import com.davena.organization.application.dto.ward.shift.ShiftDto;
import com.davena.organization.domain.model.ward.DayType;
import com.davena.organization.domain.model.ward.Shift;
import com.davena.organization.domain.model.ward.Ward;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WardTest {

    @Test
    @DisplayName("Ward 생성 시 기본 Team, Grade, Shift(Off) 생성되는지 확인")
    void Ward_생성_시_기본_Team_Grade_Shift_생성되는지_확인() {
        UUID hospitalId = UUID.randomUUID();
        UUID supervisorId = UUID.randomUUID();

        Ward ward = Ward.create(hospitalId, supervisorId, "외상 병동", UUID.randomUUID().toString());
        Assertions.assertEquals(ward.getGrades().getFirst().getName(), Ward.DEFAULT_GRADE);
        Assertions.assertEquals(ward.getTeams().getFirst().getName(), Ward.DEFAULT_TEAM);

        Map<DayType, List<Shift>> shifts = ward.getShifts();
        List<Shift> weekDayShifts = shifts.get(DayType.WEEKDAY);
        List<Shift> weekEndShifts = shifts.get(DayType.WEEKEND);

        Assertions.assertEquals(weekDayShifts.size(), 4);
        Assertions.assertEquals(weekEndShifts.size(), 4);
    }

    @Test
    @DisplayName("Ward에 Team 추가하기")
    void addTeam() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());
        ward.addNewTeam("B팀");
        Assertions.assertEquals(ward.getTeams().size(), 2);
    }

    /// ///예외 테스트
    @Test
    @DisplayName("존재하는 Team명으로 이름 생성할 경우 예외 발생")
    void addTeam_duplicate_team_name() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상 병동", UUID.randomUUID().toString());
        ward.addNewTeam("B팀");
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ward.addNewTeam("B팀");
        });
    }
}

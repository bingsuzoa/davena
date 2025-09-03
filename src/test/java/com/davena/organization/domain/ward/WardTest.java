package com.davena.organization.domain.ward;

import com.davena.organization.domain.model.hospital.HospitalId;
import com.davena.organization.domain.model.user.UserId;
import com.davena.organization.domain.model.ward.Ward;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class WardTest {

    @Test
    @DisplayName("Ward 생성 시 기본 Team, Grade, Shift(Off) 생성되는지 확인")
    void Ward_생성_시_기본_Team_Grade_Shift_생성되는지_확인() {
        HospitalId hospitalId = new HospitalId(UUID.randomUUID());
        UserId supervisorId = new UserId(UUID.randomUUID());

        Ward ward = Ward.create(hospitalId, supervisorId, "외상 병동");
        Assertions.assertEquals(ward.getGrades().getFirst().getName(), Ward.DEFAULT_GRADE);
        Assertions.assertEquals(ward.getTeams().getFirst().getName(), Ward.DEFAULT_TEAM);
        Assertions.assertEquals(ward.getShifts().getFirst().getName(), Ward.OFF);
    }
}

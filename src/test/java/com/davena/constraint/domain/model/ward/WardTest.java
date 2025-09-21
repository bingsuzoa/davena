package com.davena.constraint.domain.model.ward;

import com.davena.organization.domain.model.ward.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.davena.organization.domain.model.ward.Team.CAN_NOT_BLANK_TEAM_NAME;
import static com.davena.organization.domain.model.ward.Team.CAN_NOT_EXCEED_10_TEAM_NAME;
import static com.davena.organization.domain.model.ward.Ward.*;

public class WardTest {

    /// ///해피 테스트
    @Test
    @DisplayName("병동 생성 테스트")
    void create() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상병동", UUID.randomUUID().toString());
    }

    @Test
    @DisplayName("병동 생성 시 기본 근무 조건 생성 테스트")
    void create_기본_조건_생성() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상병동", UUID.randomUUID().toString());
        List<Shift> shifts = ward.getShifts();
        List<Team> teams = ward.getTeams();
        Team defaultTeam = teams.getFirst();
        List<Grade> grades = ward.getGrades();
        Map<UUID, Map<UUID, Integer>> requirements = ward.getRequirements();

        Assertions.assertEquals(8, shifts.size());
        Assertions.assertEquals(1, teams.size());
        Assertions.assertEquals(defaultTeam.isDefault(), true);
        Assertions.assertEquals(1, grades.size());

        Map<UUID, Integer> defaultTeamRequirements = requirements.get(defaultTeam.getId());
        Assertions.assertEquals(8, defaultTeamRequirements.size());
    }

    @Test
    @DisplayName("병동에서 팀 추가하는 테스트")
    void addNewTeam() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상병동", UUID.randomUUID().toString());
        UUID teamId = ward.addNewTeam("bTeam");
        Assertions.assertEquals(ward.getTeams().size(), 2);
    }

    @Test
    @DisplayName("Grade 추가 테스트")
    void addNewGrade() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상병동", UUID.randomUUID().toString());
        UUID gradeId = ward.addNewGrade("bGrade");
        Assertions.assertEquals(ward.getGrades().size(), 2);
    }

    @Test
    @DisplayName("Shift 추가 테스트")
    void addNewShift() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상병동", UUID.randomUUID().toString());
        UUID shiftId = ward.addNewShift(DayType.WEEKDAY, "day", 7, 30, 14, 0);
        Assertions.assertEquals(9, ward.getShifts().size());
    }

    @Test
    @DisplayName("Team 삭제 테스트")
    void deleteTeam() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상병동", UUID.randomUUID().toString());
        UUID bTeamId = ward.addNewTeam("bTeam");
        ward.deleteTeam(bTeamId);

        for (Team team : ward.getTeams()) {
            Assertions.assertNotEquals(team.getId(), bTeamId);
        }
    }

    @Test
    @DisplayName("Grade 삭제 테스트")
    void deleteGrade() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상병동", UUID.randomUUID().toString());
        UUID gradeId = ward.addNewGrade("bGrade");
        ward.deleteGrade(gradeId);

        for (Grade grade : ward.getGrades()) {
            Assertions.assertNotEquals(grade.getId(), gradeId);
        }
    }

    @Test
    @DisplayName("Shift 삭제 테스트")
    void deleteNewShift() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상병동", UUID.randomUUID().toString());
        UUID shiftId = ward.addNewShift(DayType.WEEKDAY, "day", 7, 30, 14, 0);
        ward.deleteShift(shiftId);

        for (Shift shift : ward.getShifts()) {
            Assertions.assertNotEquals(shift.getId(), shiftId);
        }
    }

    /// ///예외 테스트
    @ParameterizedTest
    @ValueSource(strings = {"외상 병동", " 외상병동", "외상병동 "})
    @DisplayName("병동 이름에 공백이 포함되면 예외가 발생한다")
    void create_공백(String value) {
        IllegalArgumentException e = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Ward.create(UUID.randomUUID(), UUID.randomUUID(), value, UUID.randomUUID().toString())
        );
        Assertions.assertEquals(CAN_NOT_BLANK_WARD_NAME, e.getMessage());
    }

    @Test
    @DisplayName("10글자 초과하면 예외가 발생한다.")
    void create_이름_초과() {
        IllegalArgumentException e = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상병동1023418998", UUID.randomUUID().toString())
        );
        Assertions.assertEquals(CAN_NOT_EXCEED_10_WARD_NAME, e.getMessage());
    }

    @Test
    @DisplayName("팀 생성 시 이름 중복이면 예외가 발생한다.")
    void addNewTeam_이름_중복() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상병동", UUID.randomUUID().toString());
        IllegalArgumentException e = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> ward.addNewTeam(Team.DEFAULT_TEAM)
        );
        Assertions.assertEquals(Ward.ALREADY_EXIST_TEAM_NAME, e.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"b Team", "  bTeam", "bTeam "})
    @DisplayName("팀 이름에 공백이 포함되면 예외가 발생한다")
    void addNewTeam_공백(String value) {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상병동", UUID.randomUUID().toString());
        IllegalArgumentException e = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> ward.addNewTeam(value)
        );
        Assertions.assertEquals(CAN_NOT_BLANK_TEAM_NAME, e.getMessage());
    }

    @Test
    @DisplayName("10글자 초과하면 예외가 발생한다.")
    void addNewTeam_이름_초과() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상병동", UUID.randomUUID().toString());
        IllegalArgumentException e = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> ward.addNewTeam("djlf;;fjlkejflkapfjlakjf")
        );
        Assertions.assertEquals(CAN_NOT_EXCEED_10_TEAM_NAME, e.getMessage());
    }

    @Test
    @DisplayName("Grade 생성 시 이름 중복이면 예외가 발생한다.")
    void addNewGrade_이름_중복() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상병동", UUID.randomUUID().toString());
        IllegalArgumentException e = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> ward.addNewGrade(Grade.DEFAULT_GRADE)
        );
        Assertions.assertEquals(Ward.ALREADY_EXIST_GRADE_NAME, e.getMessage());
    }

    @Test
    @DisplayName("Grade 생성 시 이름 중복이면 예외가 발생한다.")
    void addNewShift_이름_중복() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상병동", UUID.randomUUID().toString());
        IllegalArgumentException e = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> ward.addNewShift(DayType.WEEKDAY, Shift.Off, 7, 30, 14, 0)
        );
        Assertions.assertEquals(Ward.ALREADY_EXIST_SHIFT_NAME, e.getMessage());
    }

    @Test
    @DisplayName("default Team 삭제 시 예외")
    void deleteTeam_예외() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상병동", UUID.randomUUID().toString());

        IllegalArgumentException e = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> ward.deleteTeam(ward.getDefaultTeamId())
        );
        Assertions.assertEquals(Team.CAN_NOT_REMOVE_DEFAULT_TEAM, e.getMessage());
    }

    @Test
    @DisplayName("default Grade 삭제 시 예외")
    void deleteGrade_예외() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상병동", UUID.randomUUID().toString());
        UUID gradeId = ward.getGrades().getFirst().getId();

        IllegalArgumentException e = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> ward.deleteGrade(gradeId)
        );
        Assertions.assertEquals(Grade.CAN_NOT_REMOVE_DEFAULT_GRADE, e.getMessage());
    }

    @Test
    @DisplayName("off 삭제 시 예외")
    void deleteShift_예외() {
        Ward ward = Ward.create(UUID.randomUUID(), UUID.randomUUID(), "외상병동", UUID.randomUUID().toString());

        Shift defaultShift = null;
        for (Shift shift : ward.getShifts()) {
            if (shift.isOff()) {
                defaultShift = shift;
                break;
            }
        }

        final UUID defaultOFF = defaultShift.getId();
        IllegalArgumentException e = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> ward.deleteShift(defaultOFF)
        );
        Assertions.assertEquals(CAN_NOT_DELETE_OFF, e.getMessage());
    }
}

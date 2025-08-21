package com.davena.dutymaker.domain.schedule;

import com.davena.dutymaker.domain.organization.Hospital;
import com.davena.dutymaker.domain.organization.Ward;
import com.davena.dutymaker.domain.organization.member.Member;
import com.davena.dutymaker.repository.MemberRepository;
import com.davena.dutymaker.repository.ScheduleRepository;
import com.davena.dutymaker.repository.WardRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
public class ScheduleTest {

    @Autowired
    private EntityManager em;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private WardRepository wardRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;

    /// ///해피 테스트
    @Test
    @DisplayName("한 병동에 월 Schedule 객체는 한 개만 가능")
    void schedule_객체_확인() {
        Hospital hospital = new Hospital();
        em.persist(hospital);
        Member supervisor = memberRepository.save(new Member("김간호", "김간호", "01011112222", "1234"));
        Ward ward = wardRepository.save(new Ward(hospital, supervisor, "외상 병동"));
        Schedule schedule = scheduleRepository.save(new Schedule(ward, "2025-09"));
    }

    /// ///예외 테스트
    @Test
    @DisplayName("한 병동에 동일한 월의 Schedule 객체 생성 시 예외 발생")
    void 동일한_월의_Schedule객체_생성_불가() {
        Hospital hospital = new Hospital();
        em.persist(hospital);
        Member supervisor = memberRepository.save(new Member("김간호", "김간호", "01011112222", "1234"));
        Ward ward = wardRepository.save(new Ward(hospital, supervisor, "외상 병동"));
        Schedule schedule = scheduleRepository.save(new Schedule(ward, "2025-09"));
        Assertions.assertThrows(DataIntegrityViolationException.class,() -> {
            scheduleRepository.save(new Schedule(ward, "2025-09"));
        });
    }
}

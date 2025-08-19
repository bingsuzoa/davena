package com.davena.dutymaker.service;

import com.davena.dutymaker.api.dto.ward.WardRequest;
import com.davena.dutymaker.domain.organization.Hospital;
import com.davena.dutymaker.domain.organization.Ward;
import com.davena.dutymaker.domain.organization.member.Member;
import com.davena.dutymaker.domain.shiftRequirement.ShiftType;
import com.davena.dutymaker.repository.HospitalRepository;
import com.davena.dutymaker.repository.MemberRepository;
import com.davena.dutymaker.repository.ShiftTypeRepository;
import com.davena.dutymaker.repository.WardRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@Import({
        WardService.class,
        ShiftTypeService.class
})
@ActiveProfiles("test")
public class WardServiceTest {

    @Autowired
    private EntityManager em;
    @Autowired
    private WardRepository wardRepository;
    @Autowired
    private HospitalRepository hospitalRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ShiftTypeRepository shiftRepository;
    @Autowired
    private WardService wardService;

    @Test
    @DisplayName("병동이 생성될 때 기본 OFF ShiftType 객체 생성되는지 확인")
    void 병동_생성_시_기본_OFF_생성_확인() {
        Hospital hospital = hospitalRepository.save(new Hospital());
        Member supervisor = memberRepository.save(new Member("김간호", "김간호", "01011112222", "1234"));
        WardRequest wardRequest = new WardRequest(hospital.getId(), "외상 병동");
        Ward ward = wardService.createWardAndOffType(supervisor.getId(), wardRequest);
        Assertions.assertNotNull(shiftRepository.findByWardIdAndName(ward.getId(), ShiftType.OFF));
    }
}

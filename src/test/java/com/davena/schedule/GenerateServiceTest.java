package com.davena.schedule;


import com.davena.common.MemberService;
import com.davena.common.WardService;
import com.davena.constraint.domain.model.HolidayRequest;
import com.davena.constraint.domain.model.Member;
import com.davena.constraint.domain.model.UnavailShiftRequest;
import com.davena.constraint.domain.port.HolidayRepository;
import com.davena.constraint.domain.port.MemberRepository;
import com.davena.constraint.domain.port.UnavailShiftRepository;
import com.davena.organization.domain.model.user.User;
import com.davena.organization.domain.model.ward.DayType;
import com.davena.organization.domain.model.ward.Shift;
import com.davena.organization.domain.model.ward.Ward;
import com.davena.organization.domain.port.UserRepository;
import com.davena.organization.domain.port.WardRepository;
import com.davena.schedule.application.dto.GenerateRequest;
import com.davena.schedule.domain.model.Schedule;
import com.davena.schedule.domain.model.canididate.Candidate;
import com.davena.schedule.domain.model.canididate.Cell;
import com.davena.schedule.domain.port.CellRepository;
import com.davena.schedule.domain.port.ScheduleRepository;
import com.davena.schedule.domain.service.GenerateService;
import com.davena.schedule.domain.service.HardPolicy;
import com.davena.schedule.domain.service.MemberStateService;
import com.davena.schedule.domain.service.TeamStateService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        JpaRepositoriesAutoConfiguration.class
})
public class GenerateServiceTest {

    @Autowired
    private GenerateService generateService;
    @Autowired
    private MemberStateService memberStateService;
    @Autowired
    private TeamStateService teamStateService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private HardPolicy hardPolicy;
    @Autowired
    private WardService wardService;
    @MockBean
    private ScheduleRepository scheduleRepository;
    @MockBean
    private CellRepository cellRepository;
    @MockBean
    private UnavailShiftRepository unavailShiftRepository;
    @MockBean
    private HolidayRepository holidayRepository;
    @MockBean
    private MemberRepository memberRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private WardRepository wardRepository;

    @Test
    @DisplayName("근무표 생성")
    void 근무표_생성() {
        User user1 = User.create("이진이", "dlwlsdl", "dlwlsdl", "01011112222");
        Ward ward = Ward.create(UUID.randomUUID(), user1.getId(), "외상병동", UUID.randomUUID().toString());
        UUID aTeamId = ward.getDefaultTeamId();
        UUID bTeamId = ward.addNewTeam("bTeam");

        UUID grade1 = ward.getGrades().getFirst().getId();
        UUID grade2 = ward.addNewGrade("grade2");
        UUID grade3 = ward.addNewGrade("grade3");

        List<Shift> shifts = ward.getShifts();
        Shift dayOfWeekday = ward.getShiftByName(DayType.WEEKDAY, Shift.Day);
        Shift eveOfWeekday = ward.getShiftByName(DayType.WEEKDAY, Shift.Eve);
        Shift nigOfWeekday = ward.getShiftByName(DayType.WEEKDAY, Shift.Nig);
        Shift offOfWeekday = ward.getShiftByName(DayType.WEEKDAY, Shift.Off);
        Shift dayOfWeekend = ward.getShiftByName(DayType.WEEKEND, Shift.Day);
        Shift eveOfWeekend = ward.getShiftByName(DayType.WEEKEND, Shift.Eve);
        Shift nigOfWeekend = ward.getShiftByName(DayType.WEEKEND, Shift.Nig);
        Shift offOfWeekend = ward.getShiftByName(DayType.WEEKEND, Shift.Off);

        ward.updateRequirement(aTeamId, dayOfWeekday.getId(), 2);
        ward.updateRequirement(aTeamId, eveOfWeekday.getId(), 1);
        ward.updateRequirement(aTeamId, nigOfWeekday.getId(), 1);
        ward.updateRequirement(aTeamId, dayOfWeekend.getId(), 1);
        ward.updateRequirement(aTeamId, eveOfWeekend.getId(), 1);
        ward.updateRequirement(aTeamId, nigOfWeekend.getId(), 1);

        ward.updateRequirement(bTeamId, dayOfWeekday.getId(), 2);
        ward.updateRequirement(bTeamId, eveOfWeekday.getId(), 1);
        ward.updateRequirement(bTeamId, nigOfWeekday.getId(), 1);
        ward.updateRequirement(bTeamId, dayOfWeekend.getId(), 1);
        ward.updateRequirement(bTeamId, eveOfWeekend.getId(), 1);
        ward.updateRequirement(bTeamId, nigOfWeekend.getId(), 1);
        ///
        User user2 = User.create("여나림", "duskfla", "duskfla", "01011112223");
        Member member2 = new Member(user2.getId(), ward.getId(), user2.getName());
        member2.updateTeam(aTeamId);
        member2.initPossibleShifts(shifts);
        member2.updateIsPossibleOfShift(eveOfWeekday.getId(), false);
        member2.updateIsPossibleOfShift(nigOfWeekday.getId(), false);
        member2.updateIsPossibleOfShift(eveOfWeekend.getId(), false);
        member2.updateIsPossibleOfShift(nigOfWeekend.getId(), false);

        member2.updateGrade(grade1);
        member2.updateCanCharge(true);
        member2.updateRank(1);


        List<HolidayRequest> m2Holidays = new ArrayList<>();
        m2Holidays.add(new HolidayRequest(UUID.randomUUID(), user2.getId(), 2025, 9, LocalDate.of(2025, 9, 10), ""));
        m2Holidays.add(new HolidayRequest(UUID.randomUUID(), user2.getId(), 2025, 9, LocalDate.of(2025, 9, 6), ""));
        m2Holidays.add(new HolidayRequest(UUID.randomUUID(), user2.getId(), 2025, 9, LocalDate.of(2025, 9, 7), ""));
        m2Holidays.add(new HolidayRequest(UUID.randomUUID(), user2.getId(), 2025, 9, LocalDate.of(2025, 9, 12), ""));
        when(memberRepository.findByUserId(user2.getId())).thenReturn(Optional.of(member2));
        when(holidayRepository.findByMemberIdAndYearAndMonth(user2.getId(), 2025, 9)).thenReturn(new ArrayList<>());
        when(unavailShiftRepository.findByMemberIdAndYearAndMonth(user2.getId(), 2025, 9)).thenReturn(new ArrayList<>());

        /// 혜준
        User user3 = User.create("김혜준", "rlagPwns", "rlagPwns", "01011112224");
        Member member3 = new Member(user3.getId(), ward.getId(), user3.getName());
        member3.updateTeam(aTeamId);
        member3.initPossibleShifts(shifts);

        member3.updateGrade(grade1);
        member3.updateCanCharge(true);
        member3.updateRank(2);

        List<HolidayRequest> m3Holidays = new ArrayList<>();
        m3Holidays.add(new HolidayRequest(UUID.randomUUID(), user3.getId(), 2025, 9, LocalDate.of(2025, 9, 6), ""));
        m3Holidays.add(new HolidayRequest(UUID.randomUUID(), user3.getId(), 2025, 9, LocalDate.of(2025, 9, 7), ""));
        m3Holidays.add(new HolidayRequest(UUID.randomUUID(), user3.getId(), 2025, 9, LocalDate.of(2025, 9, 8), ""));
        m3Holidays.add(new HolidayRequest(UUID.randomUUID(), user3.getId(), 2025, 9, LocalDate.of(2025, 9, 9), ""));
        m3Holidays.add(new HolidayRequest(UUID.randomUUID(), user3.getId(), 2025, 9, LocalDate.of(2025, 9, 10), ""));
        m3Holidays.add(new HolidayRequest(UUID.randomUUID(), user3.getId(), 2025, 9, LocalDate.of(2025, 9, 11), ""));
        when(memberRepository.findByUserId(user3.getId())).thenReturn(Optional.of(member3));
        when(holidayRepository.findByMemberIdAndYearAndMonth(user3.getId(), 2025, 9)).thenReturn(new ArrayList<>());
        when(unavailShiftRepository.findByMemberIdAndYearAndMonth(user3.getId(), 2025, 9)).thenReturn(new ArrayList<>());

        /// 지민
        User user4 = User.create("최지민", "chlwlals", "chlwlals", "01011112225");
        Member member4 = new Member(user4.getId(), ward.getId(), user4.getName());
        member4.updateTeam(aTeamId);
        member4.initPossibleShifts(shifts);
        member4.updateIsPossibleOfShift(eveOfWeekday.getId(), false);
        member4.updateIsPossibleOfShift(nigOfWeekday.getId(), false);
        member4.updateIsPossibleOfShift(eveOfWeekend.getId(), false);
        member4.updateIsPossibleOfShift(nigOfWeekend.getId(), false);
        when(memberRepository.findByUserId(user4.getId())).thenReturn(Optional.of(member4));
        when(holidayRepository.findByMemberIdAndYearAndMonth(user4.getId(), 2025, 9)).thenReturn(new ArrayList<>());
        when(unavailShiftRepository.findByMemberIdAndYearAndMonth(user4.getId(), 2025, 9)).thenReturn(new ArrayList<>());


        member4.updateGrade(grade1);
        member4.updateCanCharge(true);
        member4.updateRank(2);

        /// 수아
        User user5 = User.create("최수아", "chltndk", "chltndk", "01011112226");
        Member member5 = new Member(user5.getId(), ward.getId(), user5.getName());
        member5.updateTeam(aTeamId);
        member5.initPossibleShifts(shifts);

        member5.updateGrade(grade2);
        member5.updateCanCharge(false);

        List<HolidayRequest> m5Holidays = new ArrayList<>();
        List<UnavailShiftRequest> m5UnavailRequests = new ArrayList<>();
        m5Holidays.add(new HolidayRequest(UUID.randomUUID(), user5.getId(), 2025, 9, LocalDate.of(2025, 9, 6), ""));
        m5Holidays.add(new HolidayRequest(UUID.randomUUID(), user5.getId(), 2025, 9, LocalDate.of(2025, 9, 7), ""));
        m5Holidays.add(new HolidayRequest(UUID.randomUUID(), user5.getId(), 2025, 9, LocalDate.of(2025, 9, 8), ""));
        m5UnavailRequests.add(new UnavailShiftRequest(UUID.randomUUID(), user5.getId(), 2025, 9, LocalDate.of(2025, 9, 5), nigOfWeekday.getId(), ""));
        when(memberRepository.findByUserId(user5.getId())).thenReturn(Optional.of(member5));
        when(holidayRepository.findByMemberIdAndYearAndMonth(user5.getId(), 2025, 9)).thenReturn(m5Holidays);
        when(unavailShiftRepository.findByMemberIdAndYearAndMonth(user5.getId(), 2025, 9)).thenReturn(m5UnavailRequests);

        /// 세희
        User user6 = User.create("오세희", "dhtpgml", "dhtpgml", "01011112227");
        Member member6 = new Member(user6.getId(), ward.getId(), user6.getName());
        member6.updateTeam(aTeamId);
        member6.initPossibleShifts(shifts);

        member6.updateGrade(grade3);
        member6.updateCanCharge(false);

        List<HolidayRequest> m6Holidays = new ArrayList<>();
        m6Holidays.add(new HolidayRequest(UUID.randomUUID(), user6.getId(), 2025, 9, LocalDate.of(2025, 9, 5), ""));
        m6Holidays.add(new HolidayRequest(UUID.randomUUID(), user6.getId(), 2025, 9, LocalDate.of(2025, 9, 12), ""));
        when(memberRepository.findByUserId(user6.getId())).thenReturn(Optional.of(member6));
        when(holidayRepository.findByMemberIdAndYearAndMonth(user6.getId(), 2025, 9)).thenReturn(new ArrayList<>());
        when(unavailShiftRepository.findByMemberIdAndYearAndMonth(user6.getId(), 2025, 9)).thenReturn(new ArrayList<>());

        /// ///상민
        User user7 = User.create("김상민", "rlatkdals", "rlatkdals", "01011112228");
        Member member7 = new Member(user7.getId(), ward.getId(), user7.getName());
        member7.updateTeam(aTeamId);
        member7.initPossibleShifts(shifts);
        when(memberRepository.findByUserId(user7.getId())).thenReturn(Optional.of(member7));

        member7.updateGrade(grade3);
        member7.updateCanCharge(false);

        List<HolidayRequest> m7Holidays = new ArrayList<>();
        m7Holidays.add(new HolidayRequest(UUID.randomUUID(), user7.getId(), 2025, 9, LocalDate.of(2025, 9, 10), ""));
        m7Holidays.add(new HolidayRequest(UUID.randomUUID(), user7.getId(), 2025, 9, LocalDate.of(2025, 9, 11), ""));
        m7Holidays.add(new HolidayRequest(UUID.randomUUID(), user7.getId(), 2025, 9, LocalDate.of(2025, 9, 12), ""));
        when(holidayRepository.findByMemberIdAndYearAndMonth(user7.getId(), 2025, 9)).thenReturn(m7Holidays);
        when(unavailShiftRepository.findByMemberIdAndYearAndMonth(user7.getId(), 2025, 9)).thenReturn(new ArrayList<>());

        /// ///주연
        User user8 = User.create("서주연", "tjwndus", "tjwndus", "01011112229");
        Member member8 = new Member(user8.getId(), ward.getId(), user8.getName());
        member8.updateTeam(aTeamId);
        member8.initPossibleShifts(shifts);
        when(memberRepository.findByUserId(user8.getId())).thenReturn(Optional.of(member8));

        member8.updateGrade(grade3);
        member8.updateCanCharge(false);

        when(holidayRepository.findByMemberIdAndYearAndMonth(user8.getId(), 2025, 9)).thenReturn(new ArrayList<>());
        when(unavailShiftRepository.findByMemberIdAndYearAndMonth(user8.getId(), 2025, 9)).thenReturn(new ArrayList<>());


        /// ///소윤
        User user9 = User.create("김소윤", "rlathdbs", "rlathdbs", "01011113333");
        Member member9 = new Member(user9.getId(), ward.getId(), user9.getName());
        member9.updateTeam(aTeamId);
        member9.initPossibleShifts(shifts);
        when(memberRepository.findByUserId(user9.getId())).thenReturn(Optional.of(member9));

        member9.updateGrade(grade2);
        member9.updateCanCharge(false);

        when(holidayRepository.findByMemberIdAndYearAndMonth(user9.getId(), 2025, 9)).thenReturn(new ArrayList<>());
        when(unavailShiftRepository.findByMemberIdAndYearAndMonth(user9.getId(), 2025, 9)).thenReturn(new ArrayList<>());


        /// ///세음
        User user10 = User.create("조세음", "whtpdma", "whtpdma", "01011113334");
        Member member10 = new Member(user10.getId(), ward.getId(), user10.getName());
        member10.updateTeam(aTeamId);
        member10.initPossibleShifts(shifts);
        when(memberRepository.findByUserId(user10.getId())).thenReturn(Optional.of(member10));

        member10.updateGrade(grade3);
        member10.updateCanCharge(false);

        List<HolidayRequest> m10Holidays = new ArrayList<>();
        m10Holidays.add(new HolidayRequest(UUID.randomUUID(), user10.getId(), 2025, 9, LocalDate.of(2025, 9, 10), ""));
        m10Holidays.add(new HolidayRequest(UUID.randomUUID(), user10.getId(), 2025, 9, LocalDate.of(2025, 9, 11), ""));
        when(holidayRepository.findByMemberIdAndYearAndMonth(user10.getId(), 2025, 9)).thenReturn(new ArrayList<>());
        when(unavailShiftRepository.findByMemberIdAndYearAndMonth(user10.getId(), 2025, 9)).thenReturn(new ArrayList<>());


        /// ////정화
        User user11 = User.create("이정화", "dlwjdghk", "dlwjdghk", "01011113335");
        Member member11 = new Member(user11.getId(), ward.getId(), user11.getName());
        member11.updateTeam(bTeamId);
        member11.initPossibleShifts(shifts);
        member11.updateIsPossibleOfShift(eveOfWeekday.getId(), false);
        member11.updateIsPossibleOfShift(nigOfWeekday.getId(), false);
        member11.updateIsPossibleOfShift(eveOfWeekend.getId(), false);
        member11.updateIsPossibleOfShift(nigOfWeekend.getId(), false);
        when(memberRepository.findByUserId(user11.getId())).thenReturn(Optional.of(member11));

        member11.updateGrade(grade1);
        member11.updateCanCharge(true);
        member11.updateRank(1);

        List<HolidayRequest> m11Holidays = new ArrayList<>();
        m11Holidays.add(new HolidayRequest(UUID.randomUUID(), user11.getId(), 2025, 9, LocalDate.of(2025, 9, 13), ""));
        m11Holidays.add(new HolidayRequest(UUID.randomUUID(), user11.getId(), 2025, 9, LocalDate.of(2025, 9, 15), ""));
        m11Holidays.add(new HolidayRequest(UUID.randomUUID(), user11.getId(), 2025, 9, LocalDate.of(2025, 9, 19), ""));
        m11Holidays.add(new HolidayRequest(UUID.randomUUID(), user11.getId(), 2025, 9, LocalDate.of(2025, 9, 20), ""));
        when(holidayRepository.findByMemberIdAndYearAndMonth(user11.getId(), 2025, 9)).thenReturn(new ArrayList<>());
        when(unavailShiftRepository.findByMemberIdAndYearAndMonth(user11.getId(), 2025, 9)).thenReturn(new ArrayList<>());


        /// ///선우
        User user12 = User.create("임선우", "dlatjsdn", "dlatjsdn", "01011113336");
        Member member12 = new Member(user12.getId(), ward.getId(), user12.getName());
        member12.updateTeam(bTeamId);
        member12.initPossibleShifts(shifts);
        when(memberRepository.findByUserId(user12.getId())).thenReturn(Optional.of(member12));

        member12.updateGrade(grade1);
        member12.updateCanCharge(true);
        member12.updateRank(2);

        List<HolidayRequest> m12Holidays = new ArrayList<>();
        m12Holidays.add(new HolidayRequest(UUID.randomUUID(), user12.getId(), 2025, 9, LocalDate.of(2025, 9, 19), ""));
        m12Holidays.add(new HolidayRequest(UUID.randomUUID(), user12.getId(), 2025, 9, LocalDate.of(2025, 9, 20), ""));
        m12Holidays.add(new HolidayRequest(UUID.randomUUID(), user12.getId(), 2025, 9, LocalDate.of(2025, 9, 21), ""));
        m12Holidays.add(new HolidayRequest(UUID.randomUUID(), user12.getId(), 2025, 9, LocalDate.of(2025, 9, 22), ""));
        when(holidayRepository.findByMemberIdAndYearAndMonth(user12.getId(), 2025, 9)).thenReturn(new ArrayList<>());
        when(unavailShiftRepository.findByMemberIdAndYearAndMonth(user12.getId(), 2025, 9)).thenReturn(new ArrayList<>());


        /// ///규복
        User user13 = User.create("이규복", "dlrbqhr", "dlrbqhr", "01011113337");
        Member member13 = new Member(user13.getId(), ward.getId(), user13.getName());
        member13.updateTeam(bTeamId);
        member13.initPossibleShifts(shifts);
        when(memberRepository.findByUserId(user13.getId())).thenReturn(Optional.of(member13));

        member13.updateGrade(grade1);
        member13.updateCanCharge(true);
        member13.updateRank(2);

        when(holidayRepository.findByMemberIdAndYearAndMonth(user13.getId(), 2025, 9)).thenReturn(new ArrayList<>());
        when(unavailShiftRepository.findByMemberIdAndYearAndMonth(user13.getId(), 2025, 9)).thenReturn(new ArrayList<>());


        /// ///준범
        User user14 = User.create("이준범", "dlwnsqja", "dlwnsqja", "01011113338");
        Member member14 = new Member(user14.getId(), ward.getId(), user14.getName());
        member14.updateTeam(bTeamId);
        member14.initPossibleShifts(shifts);
        when(memberRepository.findByUserId(user14.getId())).thenReturn(Optional.of(member14));

        member14.updateGrade(grade2);
        member14.updateCanCharge(false);

        List<HolidayRequest> m14Holidays = new ArrayList<>();
        m14Holidays.add(new HolidayRequest(UUID.randomUUID(), user14.getId(), 2025, 9, LocalDate.of(2025, 9, 13), ""));
        when(holidayRepository.findByMemberIdAndYearAndMonth(user14.getId(), 2025, 9)).thenReturn(new ArrayList<>());
        when(unavailShiftRepository.findByMemberIdAndYearAndMonth(user14.getId(), 2025, 9)).thenReturn(List.of(new UnavailShiftRequest(UUID.randomUUID(), user14.getId(), 2025, 9, LocalDate.of(2025, 9, 12), nigOfWeekday.getId(), "")));


        /// ///아영
        User user15 = User.create("조아영", "whdkdud", "whdkdud", "01011113339");
        Member member15 = new Member(user15.getId(), ward.getId(), user15.getName());
        member15.updateTeam(bTeamId);
        member15.initPossibleShifts(shifts);
        when(memberRepository.findByUserId(user15.getId())).thenReturn(Optional.of(member15));
        when(holidayRepository.findByMemberIdAndYearAndMonth(user15.getId(), 2025, 9)).thenReturn(new ArrayList<>());
        when(unavailShiftRepository.findByMemberIdAndYearAndMonth(user15.getId(), 2025, 9)).thenReturn(new ArrayList<>());

        member15.updateGrade(grade3);
        member15.updateCanCharge(false);

        /// ///경민
        User user16 = User.create("이경민", "dlrudals", "dlrudals", "01011114440");
        Member member16 = new Member(user16.getId(), ward.getId(), user16.getName());
        member16.updateTeam(bTeamId);
        member16.initPossibleShifts(shifts);
        when(memberRepository.findByUserId(user16.getId())).thenReturn(Optional.of(member16));
        when(holidayRepository.findByMemberIdAndYearAndMonth(user16.getId(), 2025, 9)).thenReturn(new ArrayList<>());
        when(unavailShiftRepository.findByMemberIdAndYearAndMonth(user16.getId(), 2025, 9)).thenReturn(new ArrayList<>());

        member16.updateGrade(grade2);
        member16.updateCanCharge(false);

        /// ///석희
        User user17 = User.create("한석희", "gkstjrgml", "gkstrjgml", "01011114441");
        Member member17 = new Member(user17.getId(), ward.getId(), user17.getName());
        member17.updateTeam(bTeamId);
        member17.initPossibleShifts(shifts);
        when(memberRepository.findByUserId(user17.getId())).thenReturn(Optional.of(member17));

        member17.updateGrade(grade2);
        member17.updateCanCharge(false);

        List<HolidayRequest> m17Holidays = new ArrayList<>();
        m17Holidays.add(new HolidayRequest(UUID.randomUUID(), user17.getId(), 2025, 9, LocalDate.of(2025, 9, 21), ""));
        m17Holidays.add(new HolidayRequest(UUID.randomUUID(), user17.getId(), 2025, 9, LocalDate.of(2025, 9, 22), ""));
        when(holidayRepository.findByMemberIdAndYearAndMonth(user17.getId(), 2025, 9)).thenReturn(new ArrayList<>());
        when(unavailShiftRepository.findByMemberIdAndYearAndMonth(user17.getId(), 2025, 9)).thenReturn(new ArrayList<>());

        /// ///지은
        User user18 = User.create("이지은", "dlwlsdms", "dlwldms", "01011114442");
        Member member18 = new Member(user18.getId(), ward.getId(), user18.getName());
        member18.updateTeam(bTeamId);
        member18.initPossibleShifts(shifts);
        when(memberRepository.findByUserId(user18.getId())).thenReturn(Optional.of(member18));
        List<HolidayRequest> m18Holidays = new ArrayList<>();
        m18Holidays.add(new HolidayRequest(UUID.randomUUID(), user18.getId(), 2025, 9, LocalDate.of(2025, 9, 20), ""));
        when(holidayRepository.findByMemberIdAndYearAndMonth(user18.getId(), 2025, 9)).thenReturn(new ArrayList<>());
        when(unavailShiftRepository.findByMemberIdAndYearAndMonth(user18.getId(), 2025, 9)).thenReturn(new ArrayList<>());


        member18.updateGrade(grade2);
        member18.updateCanCharge(false);

        /// ///영화
        User user19 = User.create("권영화", "rnjsdudghk", "rnjsdudghk", "01011114443");
        Member member19 = new Member(user19.getId(), ward.getId(), user19.getName());
        member19.updateTeam(bTeamId);
        member19.initPossibleShifts(shifts);
        when(memberRepository.findByUserId(user19.getId())).thenReturn(Optional.of(member19));
        when(memberRepository.findByUserId(user19.getId())).thenReturn(Optional.of(member19));
        List<HolidayRequest> m19Holidays = new ArrayList<>();
        m19Holidays.add(new HolidayRequest(UUID.randomUUID(), user19.getId(), 2025, 9, LocalDate.of(2025, 9, 19), ""));
        m19Holidays.add(new HolidayRequest(UUID.randomUUID(), user19.getId(), 2025, 9, LocalDate.of(2025, 9, 20), ""));
        m19Holidays.add(new HolidayRequest(UUID.randomUUID(), user19.getId(), 2025, 9, LocalDate.of(2025, 9, 21), ""));
        m19Holidays.add(new HolidayRequest(UUID.randomUUID(), user19.getId(), 2025, 9, LocalDate.of(2025, 9, 22), ""));
        when(holidayRepository.findByMemberIdAndYearAndMonth(user19.getId(), 2025, 9)).thenReturn(new ArrayList<>());
        when(unavailShiftRepository.findByMemberIdAndYearAndMonth(user19.getId(), 2025, 9)).thenReturn(new ArrayList<>());

        member19.updateGrade(grade3);
        member19.updateCanCharge(false);

        /// ////수지
        User user20 = User.create("전수지", "wjstnwl", "wjstnwl", "01011114444");
        Member member20 = new Member(user20.getId(), ward.getId(), user20.getName());
        member20.updateTeam(bTeamId);
        member20.initPossibleShifts(shifts);
        when(memberRepository.findByUserId(user20.getId())).thenReturn(Optional.of(member20));
        when(holidayRepository.findByMemberIdAndYearAndMonth(user20.getId(), 2025, 9)).thenReturn(new ArrayList<>());
        when(unavailShiftRepository.findByMemberIdAndYearAndMonth(user20.getId(), 2025, 9)).thenReturn(new ArrayList<>());

        when(wardRepository.findById(any())).thenReturn(Optional.of(ward));

        member20.updateGrade(grade3);
        member20.updateCanCharge(false);

        /// ///8월 확정 스케줄
        Schedule lastMonthSchedule = new Schedule(ward.getId(), YearMonth.of(2025, 8));
        Candidate finalizedCandidate = new Candidate(lastMonthSchedule.getId());
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user2.getId(), 24, offOfWeekend.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user2.getId(), 25, offOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user2.getId(), 26, dayOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user2.getId(), 27, dayOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user2.getId(), 28, dayOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user2.getId(), 29, dayOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user2.getId(), 30, offOfWeekend.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user2.getId(), 31, offOfWeekend.getId()));

        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user3.getId(), 24, nigOfWeekend.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user3.getId(), 25, nigOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user3.getId(), 26, offOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user3.getId(), 27, offOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user3.getId(), 28, dayOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user3.getId(), 29, dayOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user3.getId(), 30, dayOfWeekend.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user3.getId(), 31, offOfWeekend.getId()));

        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user4.getId(), 24, offOfWeekend.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user4.getId(), 25, offOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user4.getId(), 26, offOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user4.getId(), 27, offOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user4.getId(), 28, offOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user4.getId(), 29, offOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user4.getId(), 30, offOfWeekend.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user4.getId(), 31, offOfWeekend.getId()));

        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user5.getId(), 24, eveOfWeekend.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user5.getId(), 25, eveOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user5.getId(), 26, nigOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user5.getId(), 27, nigOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user5.getId(), 28, nigOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user5.getId(), 29, offOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user5.getId(), 30, offOfWeekend.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user5.getId(), 31, dayOfWeekend.getId()));

        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user6.getId(), 24, nigOfWeekend.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user6.getId(), 25, nigOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user6.getId(), 26, offOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user6.getId(), 27, offOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user6.getId(), 28, offOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user6.getId(), 29, dayOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user6.getId(), 30, dayOfWeekend.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user6.getId(), 31, eveOfWeekend.getId()));

        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user7.getId(), 24, offOfWeekend.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user7.getId(), 25, offOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user7.getId(), 26, offOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user7.getId(), 27, offOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user7.getId(), 28, eveOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user7.getId(), 29, nigOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user7.getId(), 30, nigOfWeekend.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user7.getId(), 31, nigOfWeekend.getId()));

        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user8.getId(), 24, dayOfWeekend.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user8.getId(), 25, dayOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user8.getId(), 26, dayOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user8.getId(), 27, eveOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user8.getId(), 28, offOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user8.getId(), 29, eveOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user8.getId(), 30, eveOfWeekend.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user8.getId(), 31, nigOfWeekend.getId()));

        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user9.getId(), 24, offOfWeekend.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user9.getId(), 25, dayOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user9.getId(), 26, dayOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user9.getId(), 27, dayOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user9.getId(), 28, offOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user9.getId(), 29, offOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user9.getId(), 30, offOfWeekend.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user9.getId(), 31, offOfWeekend.getId()));

        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user10.getId(), 24, eveOfWeekend.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user10.getId(), 25, eveOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user10.getId(), 26, nigOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user10.getId(), 27, nigOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user10.getId(), 28, nigOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user10.getId(), 29, offOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user10.getId(), 30, offOfWeekend.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user10.getId(), 31, dayOfWeekend.getId()));

        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user11.getId(), 24, offOfWeekend.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user11.getId(), 25, dayOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user11.getId(), 26, dayOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user11.getId(), 27, dayOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user11.getId(), 28, offOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user11.getId(), 29, offOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user11.getId(), 30, offOfWeekend.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user11.getId(), 31, offOfWeekend.getId()));

        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user12.getId(), 24, offOfWeekend.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user12.getId(), 25, offOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user12.getId(), 26, eveOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user12.getId(), 27, eveOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user12.getId(), 28, eveOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user12.getId(), 29, eveOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user12.getId(), 30, nigOfWeekend.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user12.getId(), 31, nigOfWeekend.getId()));

        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user13.getId(), 24, dayOfWeekend.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user13.getId(), 25, dayOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user13.getId(), 26, dayOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user13.getId(), 27, dayOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user13.getId(), 28, dayOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user13.getId(), 29, dayOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user13.getId(), 30, offOfWeekend.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user13.getId(), 31, offOfWeekend.getId()));

        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user14.getId(), 24, offOfWeekend.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user14.getId(), 25, offOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user14.getId(), 26, offOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user14.getId(), 27, dayOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user14.getId(), 28, dayOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user14.getId(), 29, dayOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user14.getId(), 30, offOfWeekend.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user14.getId(), 31, offOfWeekend.getId()));

        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user15.getId(), 24, nigOfWeekend.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user15.getId(), 25, nigOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user15.getId(), 26, nigOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user15.getId(), 27, offOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user15.getId(), 28, offOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user15.getId(), 29, offOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user15.getId(), 30, dayOfWeekend.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user15.getId(), 31, offOfWeekend.getId()));

        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user16.getId(), 24, offOfWeekend.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user16.getId(), 25, offOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user16.getId(), 26, offOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user16.getId(), 27, offOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user16.getId(), 28, dayOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user16.getId(), 29, offOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user16.getId(), 30, offOfWeekend.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user16.getId(), 31, eveOfWeekend.getId()));

        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user17.getId(), 24, offOfWeekend.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user17.getId(), 25, eveOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user17.getId(), 26, eveOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user17.getId(), 27, offOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user17.getId(), 28, offOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user17.getId(), 29, eveOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user17.getId(), 30, eveOfWeekend.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user17.getId(), 31, offOfWeekend.getId()));

        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user18.getId(), 24, offOfWeekend.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user18.getId(), 25, dayOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user18.getId(), 26, eveOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user18.getId(), 27, eveOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user18.getId(), 28, eveOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user18.getId(), 29, eveOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user18.getId(), 30, eveOfWeekend.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user18.getId(), 31, offOfWeekend.getId()));

        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user19.getId(), 24, eveOfWeekend.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user19.getId(), 25, offOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user19.getId(), 26, offOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user19.getId(), 27, nigOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user19.getId(), 28, nigOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user19.getId(), 29, nigOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user19.getId(), 30, offOfWeekend.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user19.getId(), 31, offOfWeekend.getId()));

        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user20.getId(), 24, offOfWeekend.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user20.getId(), 25, eveOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user20.getId(), 26, eveOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user20.getId(), 27, eveOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user20.getId(), 28, eveOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user20.getId(), 29, offOfWeekday.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user20.getId(), 30, offOfWeekend.getId()));
        finalizedCandidate.addCell(new Cell(UUID.randomUUID(), finalizedCandidate.getId(), user20.getId(), 31, eveOfWeekend.getId()));
        when(scheduleRepository.getScheduleByWardIdAndYearAndMonth(ward.getId(), 2025, 8)).thenReturn(Optional.of(lastMonthSchedule));
        when(cellRepository.findByCandidateId(any())).thenReturn(finalizedCandidate.getCells());

        Schedule thisMonthSchedule = new Schedule(ward.getId(), YearMonth.of(2025, 9));
        List<Candidate> candidates = generateService.generate(new GenerateRequest(ward.getId(), thisMonthSchedule.getId(), 2025, 9));
        assertThat(candidates).isNotEmpty();
        Assertions.assertEquals(5, candidates.size());
    }
}

package com.davena;

import com.davena.constraint.domain.model.HolidayRequest;
import com.davena.constraint.domain.model.Member;
import com.davena.constraint.domain.model.UnavailShiftRequest;
import com.davena.constraint.domain.port.HolidayRepository;
import com.davena.constraint.domain.port.MemberRepository;
import com.davena.constraint.domain.port.UnavailShiftRepository;
import com.davena.organization.domain.model.user.User;
import com.davena.organization.domain.model.ward.Ward;
import com.davena.organization.domain.port.UserRepository;
import com.davena.organization.domain.port.WardRepository;
import com.davena.schedule.domain.model.Schedule;
import com.davena.schedule.domain.model.canididate.Candidate;
import com.davena.schedule.domain.model.canididate.Cell;
import com.davena.schedule.domain.port.CandidateRepository;
import com.davena.schedule.domain.port.CellRepository;
import com.davena.schedule.domain.port.ScheduleRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Configuration
public class DummyConfig {

    @Bean
    public HolidayRepository holidayRepository() {
        return new HolidayRepository() {
            @Override
            public HolidayRequest save(HolidayRequest request) {
                return null;
            }

            @Override
            public UUID delete(UUID requestId) {
                return null;
            }

            @Override
            public List<HolidayRequest> findByWardIdAndYearAndMonth(UUID wardId, int year, int month) {
                return List.of();
            }

            @Override
            public List<HolidayRequest> findByMemberIdAndYearAndMonth(UUID memberId, int year, int month) {
                return List.of();
            }

            @Override
            public Optional<HolidayRequest> findByMemberIdAndRequestDay(UUID memberId, LocalDate requestDay) {
                return Optional.empty();
            }
        };
    }

    @Bean
    public MemberRepository memberRepository() {
        return new MemberRepository() {
            @Override
            public Member save(Member member) {
                return null;
            }

            @Override
            public Optional<Member> findByUserId(UUID userId) {
                return Optional.empty();
            }

            @Override
            public List<Member> findAllByWardId(UUID wardId) {
                return List.of();
            }

            @Override
            public List<Member> findByWardIdAndGradeId(UUID wardId, UUID gradeId) {
                return List.of();
            }

            @Override
            public List<Member> findByWardIdAndTeamId(UUID wardId, UUID teamId) {
                return List.of();
            }

            @Override
            public List<Member> findChargeMembersOfWardAndTeam(UUID wardId, UUID teamId) {
                return List.of();
            }
        };
    }

    @Bean
    public UnavailShiftRepository unavailShiftRepository() {
        return new UnavailShiftRepository() {
            @Override
            public UnavailShiftRequest save(UnavailShiftRequest request) {
                return null;
            }

            @Override
            public UUID delete(UUID requestId) {
                return null;
            }

            @Override
            public List<UnavailShiftRequest> findByWardIdAndYearAndMonth(UUID wardId, int year, int month) {
                return List.of();
            }

            @Override
            public List<UnavailShiftRequest> findByMemberIdAndYearAndMonth(UUID memberId, int year, int month) {
                return List.of();
            }

            @Override
            public Optional<UnavailShiftRequest> findByMemberIdAndShiftIdAndRequestDay(UUID memberId, UUID shiftId, LocalDate requestDay) {
                return Optional.empty();
            }

            @Override
            public List<UnavailShiftRequest> findByMemberIdAndRequestDay(UUID memberId, LocalDate requestDay) {
                return List.of();
            }
        };
    }
    @Bean
    public UserRepository userRepository() {
        return new UserRepository() {
            @Override
            public User save(User user) {
                return null;
            }

            @Override
            public Optional<User> findById(UUID userId) {
                return Optional.empty();
            }

            @Override
            public List<User> findAllById(List<UUID> userIds) {
                return List.of();
            }
        };
    }

    @Bean
    public WardRepository wardRepository() {
        return new WardRepository() {
            @Override
            public Ward save(Ward ward) {
                return null;
            }

            @Override
            public Optional<Ward> findByToken(String token) {
                return Optional.empty();
            }

            @Override
            public Optional<Ward> findById(UUID wardId) {
                return Optional.empty();
            }
        };
    }

    @Bean
    public CandidateRepository candidateRepository() {
        return new CandidateRepository() {
            @Override
            public Optional<Candidate> findById(UUID candidateId) {
                return Optional.empty();
            }
        };
    }

    @Bean
    public CellRepository cellRepository() {
        return new CellRepository() {
            @Override
            public List<Cell> findByCandidateId(UUID candidateId) {
                return List.of();
            }
        };
    }

    @Bean
    public ScheduleRepository scheduleRepository() {
        return new ScheduleRepository() {
            @Override
            public Optional<Schedule> getScheduleByWardIdAndYearAndMonth(UUID wardId, int year, int month) {
                return Optional.empty();
            }
        };
    }
}

package com.davena.common;

import com.davena.constraint.domain.model.Member;
import com.davena.constraint.domain.port.MemberRepository;
import com.davena.organization.domain.model.user.User;
import com.davena.organization.domain.model.ward.Ward;
import com.davena.organization.domain.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final UserRepository userRepository;
    private final MemberRepository memberRepository;

    public static final String NOT_EXIST_USER = "존재하지 않는 회원입니다.";
    public static final String ALREADY_REGISTERED_MEMBER = "이미 병동에 가입된 회원입니다.";
    public static final String AT_LEAST_ONE_MEMBER = "최소 한 명 이상의 멤버가 있어야 합니다.";
    public static final String NOT_CONTAINS_ALL_MEMBER = "병동의 전체 멤버가 포함되지 않았습니다.";

    public User getUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(NOT_EXIST_USER));
    }

    public Member save(Member member) {
        return memberRepository.save(member);
    }

    public Member getMember(UUID userId) {
        return memberRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException(ALREADY_REGISTERED_MEMBER));
    }

    public List<Member> getMembersOfGrade(UUID wardId, UUID gradeId) {
        return memberRepository.findByWardIdAndGradeId(wardId, gradeId);
    }

    public List<Member> getMembersOfTeam(UUID wardId, UUID teamId) {
        return memberRepository.findByWardIdAndTeamId(wardId, teamId);
    }

    public boolean isAlreadyExistMember(UUID userId) {
        Optional<Member> optionalMember = memberRepository.findByUserId(userId);
        if(optionalMember.isEmpty()) {
            return false;
        }
        return true;
    }

    public List<Member> getAllMembersOfWard(UUID wardId) {
        return memberRepository.findAllByWardId(wardId);
    }

    public void validateAtLeastOneMember(Map<UUID, List<UUID>> groupMembers) {
        if (groupMembers.values().stream().anyMatch(List::isEmpty)) {
            throw new IllegalArgumentException(AT_LEAST_ONE_MEMBER);
        }
    }

    public void validateContainAllMembers(Ward ward, Map<UUID, List<UUID>> groupMembers) {
        Set<UUID> dtoMembers = groupMembers.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toSet());
        if (!dtoMembers.equals(ward.getUsers())) {
            throw new IllegalArgumentException(NOT_CONTAINS_ALL_MEMBER);
        }
    }
}

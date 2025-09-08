package com.davena.organization.domain.service;

import com.davena.organization.domain.model.ward.Ward;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class MembersValidator {

    public static final String AT_LEAST_ONE_MEMBER = "최소 한 명 이상의 멤버가 있어야 합니다.";
    public static final String NOT_CONTAINS_ALL_MEMBER = "병동의 전체 멤버가 포함되지 않았습니다.";

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

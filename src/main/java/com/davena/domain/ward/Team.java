package com.davena.domain.ward;

import com.davena.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Table(uniqueConstraints = @UniqueConstraint(name = "uk_team_ward_name", columnNames = {"ward_id", "name"}))
@AttributeOverride(name = "id", column = @Column(name = "team_id"))
public class Team extends BaseEntity {

    protected Team() {

    }

    public Team(
            Ward ward,
            String name,
            boolean isDefault
    ) {
        this.ward = ward;
        this.name = name;
        this.isDefault = isDefault;
    }

    public static final String DEFAULT_TEAM_NAME = "a팀";
    public static final String NOT_EXIST_TEAM = "해당 병동에는 존재하지 않는 팀입니다.";
    private static final String ALREADY_APPROVED_MEMBER = "이미 팀에 존재하는 멤버입니다.";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ward_id", nullable = false)
    private Ward ward;

    @Column(nullable = false, length = 10)
    private String name;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault;

    @ElementCollection
    @CollectionTable(name = "team_members", joinColumns = @JoinColumn(name = "team_id"))
    @Column(name = "member_id")
    private Set<Long> members = new HashSet<>();

    public static Team createDefault(Ward ward) {
        return new Team(ward, DEFAULT_TEAM_NAME, true);
    }

    public static Team createNormal(Ward ward, String name) {
        return new Team(ward, name, false);
    }

    public void approveMember(Long memberId) {
        if(members.contains(memberId)) {
            throw new IllegalArgumentException(ALREADY_APPROVED_MEMBER);
        }
        members.add(memberId);
    }
}

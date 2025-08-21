package com.davena.dutymaker.domain.shiftRequirement;

import com.davena.dutymaker.domain.BaseEntity;
import com.davena.dutymaker.domain.organization.team.Team;
import com.davena.dutymaker.domain.organization.Ward;
import com.davena.dutymaker.domain.policy.DayType;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(uniqueConstraints = @UniqueConstraint(
        name = "uk_rule_team_daytype_shifttype",
        columnNames = {"team_id", "day_type", "shift_type_id"}
))
public class RequirementRule extends BaseEntity {

    protected RequirementRule() {

    }

    public RequirementRule(
            Team team,
            DayType dayType,
            ShiftType shiftType,
            int required
    ) {
        this.team = team;
        this.dayType = dayType;
        this.shiftType = shiftType;
        this.required = required;
        team.addRequirementRule(this);
    }

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @Enumerated(EnumType.STRING)
    private DayType dayType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_type_id")
    private ShiftType shiftType;

    @Column(nullable = false)
    private int required;
}

package com.davena.domain.ward;

import com.davena.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(uniqueConstraints = @UniqueConstraint(name = "uk_grade_ward_name", columnNames = {"ward_id", "name"}))
@AttributeOverride(name = "id", column = @Column(name = "grade_id"))
public class Grade extends BaseEntity {

    private Grade() {

    }

    public Grade(
            Ward ward,
            String name,
            boolean isDefault
    ) {
        this.ward = ward;
        this.name = name;
        this.isDefault = isDefault;
    }

    public static final String DEFAULT_GRADE_NAME = "1단계";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ward_id", nullable = false)
    private Ward ward;

    @Column(nullable = false, length = 10)
    private String name;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault;

    public static Grade createDefault(Ward ward) {
        return new Grade(ward, DEFAULT_GRADE_NAME, true);
    }

    public static Grade createNormal(Ward ward, String name) {
        return new Grade(ward, name, false);
    }
}

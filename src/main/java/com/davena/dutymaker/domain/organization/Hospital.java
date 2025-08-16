package com.davena.dutymaker.domain.organization;

import com.davena.dutymaker.domain.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@AttributeOverride(name = "id", column = @Column(name = "hospital_id"))
public class Hospital extends BaseEntity {

    @OneToMany(mappedBy = "hospital")
    Set<Ward> wards = new HashSet<>();

    public void addWard(Ward ward) {
        wards.add(ward);
    }
}

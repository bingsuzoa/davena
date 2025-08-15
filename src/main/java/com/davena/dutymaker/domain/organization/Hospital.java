package com.davena.dutymaker.domain.organization;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AttributeOverride(name = "id", column = @Column(name = "hospital_id"))
public class Hospital {

    @OneToMany(mappedBy = "hospital")
    List<Group> groups = new ArrayList<>();

    public void addGroup(Group group) {
        groups.add(group);
    }
}

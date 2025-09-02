package com.davena.domain.hospital;

import com.davena.domain.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;

@Entity
@Getter
@AttributeOverride(name = "id", column = @Column(name = "hospital_id"))
public class Hospital extends BaseEntity {


}

package ru.hookaorder.backend.feature.roles.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.hookaorder.backend.feature.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Table(name = "roles")
@Entity
@RequiredArgsConstructor
public class RoleEntity extends BaseEntity {
    @Column(name = "role_name")
    @JsonProperty("role_name")
    private String roleName;

}

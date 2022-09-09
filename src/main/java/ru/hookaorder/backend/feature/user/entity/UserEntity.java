package ru.hookaorder.backend.feature.user.entity;

import lombok.Data;
import org.springframework.lang.Nullable;
import ru.hookaorder.backend.feature.BaseEntity;
import ru.hookaorder.backend.feature.roles.entity.RoleEntity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Collections;
import java.util.Set;

@Entity
@Data
@Table(name = "users")
public class UserEntity extends BaseEntity {
    @Column(name = "name")
    private String name;

    @Column(name = "email")
    @Pattern(regexp = "[^@ \\t\\r\\n]+@[^@ \\t\\r\\n]+\\.[^@ \\t\\r\\n]+")
    private String email;

    @Column(name = "phone", nullable = false, unique = true)
    @Pattern(regexp = "^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}$")
    private String phone;

    @NotBlank
    @Column(name = "password", nullable = false)
    private String password;

    @ManyToMany
    @JoinColumn
    private Set<RoleEntity> rolesSet;
}

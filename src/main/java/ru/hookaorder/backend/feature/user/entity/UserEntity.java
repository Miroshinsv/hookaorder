package ru.hookaorder.backend.feature.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.hookaorder.backend.feature.BaseEntity;
import ru.hookaorder.backend.feature.place.entity.PlaceEntity;
import ru.hookaorder.backend.feature.rating.entity.RatingEntity;
import ru.hookaorder.backend.feature.roles.entity.RoleEntity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Set;
import java.util.stream.DoubleStream;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@Table(name = "users")
public class UserEntity extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    @Pattern(regexp = "[^@ \\t\\r\\n]+@[^@ \\t\\r\\n]+\\.[^@ \\t\\r\\n]+")
    private String email;

    @Column(name = "phone", nullable = false, unique = true)
    @Pattern(regexp = "^[+]?[(]?[0-9]{3}[)]?[-\\s.]?[0-9]{3}[-\\s.]?[0-9]{4,6}$")
    private String phone;

    @NotBlank
    @Column(name = "password", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(name = "is_enabled")
    @JsonProperty(value = "is_enabled", access = JsonProperty.Access.READ_ONLY)
    private boolean isEnabled = true;

    @ManyToMany
    @JoinColumn
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty(value = "roles", access = JsonProperty.Access.READ_ONLY)
    private Set<RoleEntity> rolesSet = Collections.emptySet();

    @ManyToMany
    @JoinColumn
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty(value = "work_places", access = JsonProperty.Access.READ_ONLY)
    private Set<PlaceEntity> workPlaces = Collections.emptySet();

    @OneToMany
    @JsonIgnore
    private Set<RatingEntity> ratings = Collections.emptySet();

    @Transient
    @JsonProperty(value = "rating", access = JsonProperty.Access.READ_ONLY)
    private Double avgRating;

    @PostLoad
    public void calculateAvgRating() {
        avgRating = Double.parseDouble(new DecimalFormat("#.##").format(
                ratings.stream().flatMapToDouble((val) -> DoubleStream.of(val.getRatingValue())).average()
                        .orElse(0)).replace(",", "."));
    }
}

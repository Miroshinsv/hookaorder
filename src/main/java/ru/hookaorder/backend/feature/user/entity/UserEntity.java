package ru.hookaorder.backend.feature.user.entity;

import com.fasterxml.jackson.annotation.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
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
@EqualsAndHashCode(exclude = "workPlaces")
@Table(name = "users")
@JsonFilter("phoneFilter")
@Where(clause = "is_enabled=true AND deleted_at IS NULL")
@SQLDelete(sql = "UPDATE users set deleted_at = now()::timestamp where id=?")
public class UserEntity extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    @Pattern(regexp = "[^@ \\t\\r\\n]+@[^@ \\t\\r\\n]+\\.[^@ \\t\\r\\n]+")
    private String email;

    @Column(name = "phone", nullable = false, unique = true)
    @Pattern(regexp = "^[(]?[0-9]{3}[)]?[-\\s.]?[0-9]{3}[-\\s.]?[0-9]{4,6}$")
    private String phone;

    @NotBlank
    @Column(name = "password", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(name = "is_enabled")
    @JsonProperty(value = "is_enabled", access = JsonProperty.Access.READ_ONLY)
    private boolean isEnabled = true;

    @Column(name = "fcm_token")
    @JsonProperty(value = "fcm_token", access = JsonProperty.Access.WRITE_ONLY)
    private String fcmToken;

    @ManyToMany
    @JoinColumn
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty(value = "roles", access = JsonProperty.Access.READ_ONLY)
    private Set<RoleEntity> rolesSet = Collections.emptySet();

    @ManyToMany(mappedBy = "staff")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonIgnore
    private Set<PlaceEntity> workPlaces = Collections.emptySet();

    @OneToMany(cascade = CascadeType.ALL)
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
